package com.clouway.tablehistory.core

data class ArticleHistory(val articleId: Int, val operation: OPERATION, val description: String,
                          val unit: MEASURE_UNIT, val price: Float, val availability: Float)