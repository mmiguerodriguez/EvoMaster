package org.evomaster.client.java.controller.api.dto.database.operations;

public class OpenSearchInsertionDto {
    /**
     * The database to insert the document into.
     */
    // TODO-MIGUE: Database name?
    public String databaseName;
    /**
     * The index to insert the document into.
     */
    public String index;
    /**
     * The type of the new document. Should map the type of the documents of the index.
     */
    public String data;
}
