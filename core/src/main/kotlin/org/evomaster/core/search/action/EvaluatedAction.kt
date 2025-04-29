package org.evomaster.core.search.action

import org.evomaster.core.sql.SqlAction
import org.evomaster.core.sql.SqlActionResult
import org.evomaster.core.mongo.MongoDbAction
import org.evomaster.core.mongo.MongoDbActionResult
import org.evomaster.core.opensearch.OpenSearchAction
import org.evomaster.core.opensearch.OpenSearchActionResult


open class EvaluatedAction(val action: Action, val result: ActionResult){
    init{
        if(action.getLocalId() != result.sourceLocalId){
            throw IllegalArgumentException("Mismatch between action local id ${action.getLocalId()} and" +
                    " the source id ${result.sourceLocalId} in the associated result")
        }
    }
}


/**
 * specialized evaluated db action
 */
class EvaluatedDbAction(val sqlAction: SqlAction, val sqlResult: SqlActionResult) : EvaluatedAction(sqlAction, sqlResult)

class EvaluatedMongoDbAction(val mongoAction: MongoDbAction, val mongoResult: MongoDbActionResult) : EvaluatedAction(mongoAction, mongoResult)

class EvaluatedOpenSearchAction(val openSearchAction: OpenSearchAction, val openSearchResult: OpenSearchActionResult) : EvaluatedAction(openSearchAction, openSearchResult)
