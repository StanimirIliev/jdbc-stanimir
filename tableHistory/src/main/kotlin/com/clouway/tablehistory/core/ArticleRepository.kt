package com.clouway.tablehistory.core

interface ArticleRepository {
    fun register(article: Article)
    fun update(articleId: Int, description: String, unit: MEASURE_UNIT, price: Float, availability: Float)
    fun delete(articleId: Int)
}