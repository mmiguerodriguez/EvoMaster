package org.evomaster.client.java.controller.api.dto.database.execution;

public class OpenSearchFailedQuery {
    /**
     * The index to insert the document into.
     */
    private final String index;
    /**
     * The type of the new document. Should map the type of the documents of the index.
     */
    private String documentsType;

    public OpenSearchFailedQuery(String index, String documentsType) {
        this.index = index;
        this.documentsType = documentsType;
    }

    public OpenSearchFailedQuery() {
        this.index = "";
        this.documentsType = "";
    }

    public String getIndex() {
        return index;
    }

    public String getDocumentsType() {
        return documentsType;
    }
}
