package org.evomaster.core.opensearch

import org.evomaster.client.java.controller.api.dto.database.operations.*;
import org.evomaster.core.search.gene.utils.GeneUtils

object OpenSearchActionTransformer {

    fun transform(actions: List<OpenSearchAction>) : OpenSearchDatabaseCommandDto {

        val insertionDtos = mutableListOf<OpenSearchInsertionDto>()

        for (action in actions) {
            val genes = action.seeTopGenes().first()

            val insertionDto = OpenSearchInsertionDto().apply {
                databaseName = action.database
                index = action.index
                data = genes.getValueAsPrintableString(mode = GeneUtils.EscapeMode.EJSON)
            }

            insertionDtos.add(insertionDto)
        }

        return OpenSearchDatabaseCommandDto().apply { this.insertions = insertionDtos }
    }
}