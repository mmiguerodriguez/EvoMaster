package org.evomaster.core.opensearch

import org.evomaster.client.java.controller.api.dto.database.execution.OpenSearchFailedQuery
import org.evomaster.client.java.controller.api.dto.database.execution.OpenSearchExecutionsDto

class OpenSearchExecution(val failedQueries: MutableList<OpenSearchFailedQuery>?) {
    companion object {
        fun fromDto(dto: OpenSearchExecutionsDto?): OpenSearchExecution {
            return OpenSearchExecution(dto?.failedQueries)
        }
    }
}