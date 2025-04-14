package org.evomaster.client.java.controller.opensearch;

public class OpenSearchOperation {
    /**
     * Executed search query
     * TODO-MIGUE: Should be an implementation of class {@link org.evomaster.client.java.controller.mongo.MongoOperation#queryClass}
     */
    private final Object query;

    /**
     * Name of the index that the operation was applied to
     */
    private final String indexName;

    /**
     * Name of the database that the operation was applied to
     */
    private final String databaseName;

    /**
     * Type of the documents of the collection
     */
    private final String documentsType;

    // TODO-MIGUE: JSON
    private final String queryClass = "org.bson.conversions.Bson";

    public OpenSearchOperation(String indexName, Object query, String databaseName, String documentsType) {
        this.indexName = indexName;
        this.databaseName = databaseName;
        this.documentsType = documentsType;
        this.query = query;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public Object getQuery() {
        return query;
    }

    public String getIndexType() {
        return documentsType;
    }
}
