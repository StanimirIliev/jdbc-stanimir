package com.clouway.queries

import java.io.FileReader
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*

class Queries(val server: String, val port: Int, val db: String, val username: String, val password: String) {

    private val con: Connection = DriverManager.getConnection("jdbc:mysql://$server:$port/$db", username, password)
    private val statement = con.createStatement()

    fun select(table: String, where: String?, like: String?,
               vararg columns: String = Array<String>(1) { "*" }): ResultSet {
        return statement.executeQuery("select ${columns.find { it == "*" } ?:
                columns.joinToString(", ")} " +
                "FROM $table ${if (where == null) "" else "WHERE $where ${like ?: ""}"}")
    }

    fun update(table: String, where: String?, columns: List<String>, newValues: List<String>): Boolean {
        if (columns.size != newValues.size) {
            throw IllegalArgumentException("Size of columns should be the same as the size of the newValues")
        }
        if (columns.size == 0) {
            return false//  There is nothing to update
        }
        var set = ""
        for (i in columns.indices) {
            set += "${columns[i]} = \"${newValues[i]}\""
            if (i < columns.size - 1) {
                set += ", "
            }
        }
        return statement.execute("update $table SET $set ${if (where == null) "" else "WHERE $where"}")
    }

    fun delete(table: String, where: String): Boolean {
        return statement.execute("delete FROM $table WHERE $where")
    }

    fun insert(table: String, values: List<String>, columns: List<String> = listOf()): Boolean {
        if (columns.size != 0 && columns.size != values.size) {
            throw IllegalArgumentException("Size of columns should be the same as the size of the values")
        }
        if (values.size == 0) {
            return false//  Nothing to insert
        }
        var quotedValues = ArrayList<String>()
        values.forEach { quotedValues.add("\"$it\"") }
        return statement.execute("insert INTO $table ${if (columns.size == 0) "" else
            "(${columns.joinToString(", ")})"} VALUES (${quotedValues.joinToString(", ")})")
    }

    fun drop(table: String): Boolean {
        return statement.execute("drop TABLE $table")
    }

    fun alter(table: String, operation: Operation, column: String, options: String = ""): Boolean {
        return statement.execute("alter TABLE $table $operation $column " +
                if (operation != Operation.DROP) options else "")
    }

    fun customScript(script: String): Boolean {
        return statement.execute(script)
    }
}

fun main(args: Array<String>) {
    val queries = Queries("127.0.0.1", 3306, "testdb", "root", "1234")
    queries.customScript(FileReader("schema/schema.sql").readText())//  Creates new table Persons

    println("Insert some entries")
    queries.insert("Persons", listOf("Stanimir", "Iliev", "20", "Zelena Livada"),
            listOf("FirstName", "LastName", "Age", "Address"))
    queries.insert("Persons", listOf("Georgi", "Vasilev", "20", "Vidima"),
            listOf("FirstName", "LastName", "Age", "Address"))
    println(resultToString(queries.select("Persons", null, null)))

    println("Update LastName of the second entry")
    queries.update("Persons", "ID=2", listOf("LastName"), listOf("Georgiev"))
    println(resultToString(queries.select("Persons", null, null)))

    println("Delete second entry")
    queries.delete("Persons", "ID=2")
    println(resultToString(queries.select("Persons", null, null)))

    println("Delete column Address")
    queries.alter("Persons", Operation.DROP, "Address")
    println(resultToString(queries.select("Persons", null, null)))

    queries.drop("Persons")
}

private fun resultToString(result: ResultSet):String {
    val rsmd = result.getMetaData()
    val columnsNumber = rsmd.getColumnCount()
    var output = ""
    while (result.next()) {
        for (i in 1..columnsNumber) {
            if (i > 1){
                output += ",  "
            }
            val columnValue = result.getString(i)
            output += rsmd.getColumnName(i) + ": " + columnValue
        }
        output += "\n"
    }
    return output
}