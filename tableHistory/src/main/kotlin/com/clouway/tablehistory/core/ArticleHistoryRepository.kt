package com.clouway.tablehistory.core

interface ArticleHistoryRepository {
    fun get(pageSize: Int, page: Int): List<ArticleHistory>
    fun onUpdate(articleBeforeChange: Article, operation: OPERATION)
}