package org.evomaster.core.opensearch


import org.evomaster.core.search.action.Action
import org.evomaster.core.search.action.ActionResult

/**
 * OpenSearch insert action execution result
 */
class OpenSearchActionResult : ActionResult {

    constructor(sourceLocalId: String, stopping: Boolean = false) : super(sourceLocalId, stopping)
    constructor(other: OpenSearchActionResult) : super(other)

    companion object {
        const val INSERT_OPENSEARCH_EXECUTE_SUCCESSFULLY = "INSERT_OPENSEARCH_EXECUTE_SUCCESSFULLY"
    }

    override fun copy(): OpenSearchActionResult {
        return OpenSearchActionResult(this)
    }

    /**
     * @param success specifies whether the OpenSearch insert operation executed successfully
     */
    fun setInsertExecutionResult(success: Boolean) =
        addResultValue(INSERT_OPENSEARCH_EXECUTE_SUCCESSFULLY, success.toString())

    /**
     * @return OpenSearch insert execution result
     */
    fun getInsertExecutionResult() = getResultValue(INSERT_OPENSEARCH_EXECUTE_SUCCESSFULLY)?.toBoolean() ?: false

    override fun matchedType(action: Action): Boolean {
        return action is OpenSearchAction
    }
}