package com.baulsupp.cooee.providers.google

data class Thread(val id: String, val snippet: String, val historyId: String)
data class ThreadList(val threads: List<Thread>, val nextPageToken: String?, val resultSizeEstimate: Int)
