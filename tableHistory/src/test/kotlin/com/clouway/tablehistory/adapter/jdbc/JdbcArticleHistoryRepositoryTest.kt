package com.clouway.tablehistory.adapter.jdbc

import com.clouway.tablehistory.core.Article
import com.clouway.tablehistory.core.ArticleHistory
import com.clouway.tablehistory.core.MEASURE_UNIT
import com.clouway.tablehistory.core.OPERATION
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.FileReader
import java.sql.DriverManager

class JdbcArticleHistoryRepositoryTest {

    val con = DriverManager.getConnection("jdbc:mysql://${System.getenv("DB_HOST")}/" +
            System.getenv("DB_TABLE"), System.getenv("DB_USER"), System.getenv("DB_PASS"))
    val articleRepo = JdbcArticleRepository(con, "Articles", "ArticlesHistory")
    val articleHistoryRepo = JdbcArticleHistoryRepository(con, "ArticlesHistory")

    @Before
    fun setUp() {
        val statement = con.createStatement()
        statement.execute("DROP TABLE IF EXISTS Articles")
        statement.execute("DROP TABLE IF EXISTS ArticlesHistory")
        statement.execute(FileReader("schema/Article.sql").readText())
        statement.execute(FileReader("schema/ArticleHistory.sql").readText())
    }

    @Test
    fun getArticleFromHistoryThatWasUpdated() {
        val article = Article(1, "Potatoes", MEASURE_UNIT.KILOGRAM, 3.85f, 30f)
        articleRepo.register(article)
        articleRepo.update(article.id, article.description, article.unit, article.price, 120f)
        val articleHistory = ArticleHistory(1, OPERATION.MODIFIED, "Potatoes",
                MEASURE_UNIT.KILOGRAM, 3.85f, 30f)
        assertThat(articleHistoryRepo.get(1, 1).last(), `is`(equalTo(articleHistory)))
    }

    @Test
    fun getArticleFromHistoryThatWasDeleted() {
        val article = Article(1, "Potatoes", MEASURE_UNIT.KILOGRAM, 3.85f, 30f)
        articleRepo.register(article)
        articleRepo.delete(article.id)
        val articleHistory = ArticleHistory(1, OPERATION.DELETED, "Potatoes",
                MEASURE_UNIT.KILOGRAM, 3.85f, 30f)
        assertThat(articleHistoryRepo.get(1, 1).last(), `is`(equalTo(articleHistory)))
    }

    @Test
    fun getArticlesFromTheFirstPageOfHistory_EntriesAreMoreThanPageSize() {
        val article = Article(1, "Potatoes", MEASURE_UNIT.KILOGRAM, 3.85f, 30f)
        articleRepo.register(article)
        articleRepo.update(article.id, article.description, article.unit, article.price,120f)
        articleRepo.update(article.id, article.description, article.unit, article.price,80f)
        articleRepo.update(article.id, article.description, article.unit, article.price,60f)
        val articleHistory1 = ArticleHistory(1, OPERATION.MODIFIED, "Potatoes",
                MEASURE_UNIT.KILOGRAM, 3.85f, 30f)
        val articleHistory2 = ArticleHistory(1, OPERATION.MODIFIED, "Potatoes",
                MEASURE_UNIT.KILOGRAM, 3.85f, 120f)
        assertThat(articleHistoryRepo.get(2, 1), `is`(equalTo(listOf(articleHistory1,
                articleHistory2))))
    }

    @Test
    fun getArticlesFromTheFirstPageOfHistory_EntriesAreLessThanPageSize() {
        val article = Article(1, "Potatoes", MEASURE_UNIT.KILOGRAM, 3.85f, 30f)
        articleRepo.register(article)
        articleRepo.update(article.id, article.description, article.unit, article.price,120f)
        assertThat(articleHistoryRepo.get(10, 1), `is`(equalTo(listOf())))
    }

    @Test
    fun getArticlesFromSpecificPageOfHistory_NoEntriesAtThatPage() {
        assertThat(articleHistoryRepo.get(2, 10).size, `is`(equalTo(0)))
    }

    @Test
    fun getArticlesFromSpecificPageOfHistory() {
        val article = Article(1, "Potatoes", MEASURE_UNIT.KILOGRAM, 3.85f, 30f)
        articleRepo.register(article)
        articleRepo.update(article.id, article.description, article.unit, article.price,120f)
        articleRepo.update(article.id, article.description, article.unit, article.price,80f)
        val articleHistory1 = ArticleHistory(1, OPERATION.MODIFIED, "Potatoes",
                MEASURE_UNIT.KILOGRAM, 3.85f, 30f)
        val articleHistory2 = ArticleHistory(1, OPERATION.MODIFIED, "Potatoes",
                MEASURE_UNIT.KILOGRAM, 3.85f, 120f)
        assertThat(articleHistoryRepo.get(2, 1), `is`(equalTo(listOf(articleHistory1, articleHistory2))))
    }
}