package com.clouway.tablehistory.adapter.jdbc

import com.clouway.tablehistory.core.Article
import com.clouway.tablehistory.core.ArticleRepository
import com.clouway.tablehistory.core.MEASURE_UNIT
import com.clouway.tablehistory.core.OPERATION
import java.sql.Connection

class JdbcArticleRepository(val con: Connection, val table: String, val historyTable: String): ArticleRepository {
    override fun register(article: Article) {
        val statement = con.createStatement()
        statement.execute("INSERT INTO $table VALUES(${article.id}, '${article.description}'," +
                "'${article.unit}', ${article.price}, ${article.availability})")
    }

    override fun update(articleId: Int, description: String, unit: MEASURE_UNIT, price: Float, availability: Float) {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE Id = $articleId")
        if(!result.next()) {
            return//    There is no article to update
        }
        val articleBeforeChange = Article(result.getInt("Id"),
                result.getString("Description"), enumValueOf(result.getString("Unit")),
                result.getFloat("Price"), result.getFloat("Availability"))
        statement.execute("UPDATE $table SET Id = $articleId, Description = '$description'," +
                "Unit = '$unit', Price =  $price, Availability =  $availability " +
                "WHERE Id = $articleId")
        JdbcArticleHistoryRepository(con, historyTable).onUpdate(articleBeforeChange, OPERATION.MODIFIED)
    }

    override fun delete(articleId: Int) {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE Id = $articleId")
        if(!result.next()) {
            return//    This article is already deleted
        }
        JdbcArticleHistoryRepository(con, historyTable).onUpdate(Article(result.getInt("Id"),
                result.getString("Description"), enumValueOf(result.getString("Unit")),
                result.getFloat("Price"), result.getFloat("Availability")), OPERATION.DELETED)
        statement.execute("DELETE FROM $table WHERE Id = $articleId")
    }
}