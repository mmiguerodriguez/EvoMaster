package org.evomaster.core.output

import org.evomaster.core.search.action.EvaluatedOpenSearchAction

object OpenSearchWriter {

    /**
     * generate OpenSearch insert actions into test case based on [openSearchInitializationActions]
     * @param format is the format of tests to be generated
     * @param openSearchInitializationActions contains the db actions to be generated
     * @param lines is used to save generated textual lines with respects to [openSearchInitializationActions]
     * @param groupIndex specifies an index of a group of this [openSearchInitializationActions]
     * @param insertionVars is a list of previous variable names of the db actions (Pair.first) and corresponding results (Pair.second)
     * @param skipFailure specifies whether to skip failure tests
     */
    fun handleOpenSearchInitialization(
        format: OutputFormat,
        openSearchInitializationActions: List<EvaluatedOpenSearchAction>,
        lines: Lines,
        groupIndex: String = "",
        insertionVars: MutableList<Pair<String, String>>,
        skipFailure: Boolean
    ) {

    }
}