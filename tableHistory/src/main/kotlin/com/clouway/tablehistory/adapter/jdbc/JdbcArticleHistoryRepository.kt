package com.clouway.tablehistory.adapter.jdbc

import com.clouway.tablehistory.core.Article
import com.clouway.tablehistory.core.ArticleHistory
import com.clouway.tablehistory.core.ArticleHistoryRepository
import com.clouway.tablehistory.core.OPERATION
import java.sql.Connection

class JdbcArticleHistoryRepository(val con: Connection, val table: String): ArticleHistoryRepository {

    override fun get(pageSize: Int, page: Int): List<ArticleHistory> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT ArticleId, Operation, Description, Unit, Price," +
                "Availability FROM $table")
        val allHistory = ArrayList<ArticleHistory>()
        while(result.next()) {
            allHistory.add(ArticleHistory(result.getInt("ArticleId"),
            enumValueOf(result.getString("Operation")), result.getString("Description"),
                    enumValueOf(result.getString("Unit")), result.getFloat("Price"),
                    result.getFloat("Availability")))
        }
        if(page > allHistory.size / pageSize || page <= 0 || pageSize <= 0) {
            return listOf()
        }
        val fromIndex = (page - 1) * pageSize
        val toIndex = fromIndex + pageSize
        return allHistory.subList(fromIndex, if(toIndex > allHistory.size) allHistory.size else toIndex)
    }

    override fun onUpdate(articleBeforeChange: Article, operation: OPERATION) {
        val statement = con.createStatement()
        statement.execute("INSERT INTO $table(ArticleId, Operation, Description, Unit, Price, Availability) " +
                "VALUES(${articleBeforeChange.id}, '$operation', '${articleBeforeChange.description}'," +
                "'${articleBeforeChange.unit}', ${articleBeforeChange.price}, ${articleBeforeChange.availability})")
    }
}