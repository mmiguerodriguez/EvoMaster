package org.evomaster.client.java.controller.opensearch;

import org.evomaster.client.java.controller.mongo.operations.QueryOperation;
import org.evomaster.client.java.sql.internal.TaintHandler;

public class OpenSearchHeuristicsCalculator {

    public static final double MIN_DISTANCE_TO_TRUE_VALUE = 1.0;

    private final TaintHandler taintHandler;

    public OpenSearchHeuristicsCalculator() {
        this(null);
    }

    public OpenSearchHeuristicsCalculator(TaintHandler taintHandler) {
        this.taintHandler = taintHandler;
    }

    /**
     * Compute a "branch" distance heuristics.
     *
     * @param query the QUERY clause which we want to resolve as true
     * @param doc   a document in the database for which we want to calculate the distance
     * @return a branch distance, where 0 means that the document would make the QUERY resolve as true
     *
     * TODO-MIGUE: Define expression and how to calculate
     */
    public double computeExpression(Object query, Object doc) {
        return 1.0f;
//        QueryOperation operation = getOperation(query);
//        return calculateDistance(operation, doc);
    }

    public TaintHandler getTaintHandler() {
        return taintHandler;
    }
}
