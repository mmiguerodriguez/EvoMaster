package org.evomaster.core.opensearch

import org.evomaster.core.problem.rest.RestActionBuilderV3
import org.evomaster.core.problem.rest.RestActionBuilderV3.createGeneForDTO
import org.evomaster.core.search.action.EnvironmentAction
import org.evomaster.core.search.action.Action
import org.evomaster.core.search.gene.Gene
import org.evomaster.core.search.gene.ObjectGene
import org.evomaster.core.search.gene.collection.MapGene
import org.evomaster.core.search.gene.mongo.ObjectIdGene
import java.util.*

// TODO-MIGUE: Define DB ACTION
class OpenSearchAction(
    /**
     * The database to insert document into
     */
    val database: String,
    /**
     * The index to insert document into
     */
    val index: String,
    /**
     * The type of the new document. Should map the type of the documents of the index
     */
    val documentsType: String,
    computedGenes: List<Gene>? = null
) : EnvironmentAction(listOf()) {

    private val genes: List<Gene> = (computedGenes ?: computeGenes()).also { addChildren(it) }

    private fun computeGenes(): List<Gene> {
        val documentsTypeName = documentsType.substringBefore(":").drop(1).dropLast(1)
        val gene = createGeneForDTO(
            documentsTypeName, documentsType, RestActionBuilderV3.Options(invalidData = false)
        )
        val fixedFields = when (gene) {
            is ObjectGene -> {
                gene.fixedFields.filter { fixedFieldGene -> fixedFieldGene.name != "_id" }
            }

            is MapGene<*,*> -> {
                gene.getAllElements().filter { pairGene -> pairGene.first.name != "_id" }
            }

            else -> {
                throw IllegalArgumentException("Cannot obtain fixed fields from gene ${gene.javaClass}")
            }
        }

        return Collections.singletonList(
            ObjectGene(
                gene.name,
                fixedFields + (Collections.singletonList(ObjectIdGene("_id")))))

    }

    override fun getName(): String {
        return "OPENSEARCH_Insert_${database}_${index}_${documentsType}"
    }

    override fun seeTopGenes(): List<Gene> {
        return genes
    }

    override fun copyContent(): Action {
        return OpenSearchAction(database, index, documentsType, genes.map(Gene::copy))
    }
}