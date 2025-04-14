package org.evomaster.client.java.instrumentation;

import java.io.Serializable;

/**
 * Info related to OPENSEARCH command execution.
 */
public class OpenSearchSearchCommand implements Serializable {
    /**
     * Name of the index that the operation was applied to
     */
    private final String indexName;

    /**
     * Name of the database that the operation was applied to
     */
    private final String databaseName;

    /**
     * Type of the documents of the index
     */
    private final String indexType;

    /**
     * Executed search query
     */
    private final Object bson;

    /**
     * If the operation was successfully executed
     */
    private final boolean successfullyExecuted;

    /**
     * Elapsed execution time
     */
    private final long executionTime;

    public OpenSearchSearchCommand(String databaseName, String indexName, String indexType, Object bson, boolean successfullyExecuted, long executionTime) {
        this.indexName = indexName;
        this.databaseName = databaseName;
        this.indexType = indexType;
        this.bson = bson;
        this.successfullyExecuted = successfullyExecuted;
        this.executionTime = executionTime;
    }

    public Object getQuery() {
        return bson;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexType() {
        return indexType;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
