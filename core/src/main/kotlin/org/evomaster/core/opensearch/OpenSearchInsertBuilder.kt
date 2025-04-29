package org.evomaster.core.opensearch

class OpenSearchInsertBuilder {
    fun createOpenSearchInsertionAction(database: String, index: String, documentsType: String): OpenSearchAction {
        return OpenSearchAction(database, index, documentsType)
    }
}