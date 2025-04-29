package org.evomaster.client.java.controller.opensearch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.evomaster.client.java.controller.api.dto.database.operations.OpenSearchInsertionDto;
import org.evomaster.client.java.controller.api.dto.database.operations.OpenSearchInsertionResultsDto;
import org.evomaster.client.java.utils.SimpleLogger;
import org.opensearch.client.opensearch.core.IndexRequest;

public class OpenSearchScriptRunner {

    /**
     * Default constructor
     */
    public OpenSearchScriptRunner() {}

    /**
     * Execute the different OpenSearch insertions.
     *
     * @param connection a connection to the database (OpenSearchClient)
     * @param insertions the OpenSearch insertions to execute
     * @return a OpenSearchInsertionResultsDto
     */
    public static OpenSearchInsertionResultsDto executeInsert(Object connection, List<OpenSearchInsertionDto> insertions) {

        if (insertions == null || insertions.isEmpty()) {
            throw new IllegalArgumentException("No data to insert");
        }

        List<Boolean> openSearchResults = new ArrayList<>(Collections.nCopies(insertions.size(), false));

        for (int i = 0; i < insertions.size(); i++) {

            OpenSearchInsertionDto insertionDto = insertions.get(i);

            try {
                Object document = parseEJSON(insertionDto.data);
                indexDocument(connection, insertionDto.databaseName, insertionDto.index, document);
                openSearchResults.set(i, true);
                SimpleLogger.debug(insertionDto.data + " inserted into database: " + insertionDto.databaseName + " and index: " + insertionDto.index);
            } catch (Exception e) {
                final String errorMessage;

                if (e instanceof InvocationTargetException) {
                    InvocationTargetException  invocationTargetException = (InvocationTargetException)e;
                    Throwable innerException = invocationTargetException.getTargetException();
                    errorMessage = innerException.getMessage();
                } else {
                    errorMessage = e.getMessage();
                }

                String msg = "Failed to execute insertion with index " + i + " with OpenSearch. Error: " + errorMessage;
                throw new RuntimeException(msg, e);
            }
        }

        OpenSearchInsertionResultsDto insertionResultsDto = new OpenSearchInsertionResultsDto();
        insertionResultsDto.executionResults = openSearchResults;
        return insertionResultsDto;
    }

    // TODO-MIGUE: Modify insertions
    // How can we work with OpenSearch IndexRequest way of inserting stuff?
    // public <TDocument> IndexResponse index(IndexRequest<TDocument> request)
    private static void indexDocument(Object connection, String databaseName, String collectionName, Object document) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException,
        InstantiationException {
//        Object database = connection.getClass().getMethod("index", IndexRequest.class).invoke(connection, indexRequest);
//        Object collection = database.getClass().getMethod("getCollection", String.class).invoke(database, collectionName);
        IndexRequest<Object> indexRequest = new IndexRequest.Builder<>().document(document).build();
        Class<?> builder = Class.forName("org.opensearch.client.opensearch._types.RequestBase.IndexRequest.Builder");
        Object builderInstance = builder.getDeclaredConstructor().newInstance();
        Method documentMethod = builder.getMethod("document", Object.class);
        documentMethod.invoke(builderInstance, document);
        Method buildMethod = builder.getMethod("build");
        Object indexRequestInstance = buildMethod.invoke(builderInstance);

        Class.forName("org.opensearch.client.opensearch.OpenSearchClient").getMethod("index", Object.class).invoke(connection, indexRequestInstance);
    }

//    private static Object parseEJSON(String documentAsEJSON) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        Class<?> documentClass = Class.forName("org.bson.Document");
//        Object document = Class.forName("org.bson.Document").getDeclaredConstructor().newInstance();
//        document = documentClass.getMethod("parse", String.class).invoke(document, documentAsEJSON);
//        return document;
//    }
}
