package org.evomaster.client.java.controller.internal.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.evomaster.client.java.controller.api.dto.database.execution.MongoExecutionsDto;
import org.evomaster.client.java.controller.api.dto.database.execution.MongoFailedQuery;
import org.evomaster.client.java.controller.api.dto.database.execution.OpenSearchExecutionsDto;
import org.evomaster.client.java.controller.api.dto.database.execution.OpenSearchFailedQuery;
import org.evomaster.client.java.controller.internal.TaintHandlerExecutionTracer;
import org.evomaster.client.java.controller.mongo.MongoOperation;
import org.evomaster.client.java.controller.opensearch.OpenSearchHeuristicsCalculator;
import org.evomaster.client.java.controller.opensearch.OpenSearchOperation;
import org.evomaster.client.java.instrumentation.MongoCollectionSchema;
import org.evomaster.client.java.instrumentation.OpenSearchIndexSchema;
import org.evomaster.client.java.instrumentation.OpenSearchSearchCommand;
import org.evomaster.client.java.utils.SimpleLogger;

import org.opensearch.client.opensearch.core.SearchRequest;

public class OpenSearchHandler {

    public static final String OPENSEARCH_COLLECTION_CLASS_NAME = "org.opensearch.client.opensearch.core.SearchResponse";
    /**
     * Info about Find operations executed
     */
    private final List<OpenSearchSearchCommand> operations;

    /**
     * Whether to use execution's info or not
     */
    private volatile boolean extractOpenSearchExecution;

    /**
     * The heuristics based on the Mongo execution
     */
    private final List<OpenSearchCommandWithDistance> openSearchCommandWithDistances;

    /**
     * Whether to calculate heuristics based on execution or not
     */
    private volatile boolean calculateHeuristics;

    /**
     * Unsuccessful executed queries
     */
    private final List<OpenSearchOperation> emptyIndexes;

    /**
     * Info about schemas of the documents of the repository extracted from Spring framework.
     * Documents of the collection will be mapped to the Repository type
     */
    private final Map<String, String> indexSchemas;

    /**
     * Since we do not want to add a dependency to given Mongo version, we
     * are using an Object reference
     */
    private Object openSearchClient = null;

    private final OpenSearchHeuristicsCalculator calculator = new OpenSearchHeuristicsCalculator(new TaintHandlerExecutionTracer());

    public OpenSearchHandler() {
        openSearchCommandWithDistances = new ArrayList<>();
        operations = new ArrayList<>();
        emptyIndexes = new ArrayList<>();
        indexSchemas = new HashMap<>();
        extractOpenSearchExecution = true;
        calculateHeuristics = true;
    }

    public void reset() {
        operations.clear();
        openSearchCommandWithDistances.clear();
        emptyIndexes.clear();
    }

    public boolean isCalculateHeuristics() {
        return calculateHeuristics;
    }

    public boolean isExtractOpenSearchExecution() {
        return extractOpenSearchExecution;
    }

    public void setCalculateHeuristics(boolean calculateHeuristics) {
        this.calculateHeuristics = calculateHeuristics;
    }

    public void setExtractOpenSearchExecution(boolean extractOpenSearchExecution) {
        this.extractOpenSearchExecution = extractOpenSearchExecution;
    }

    public void handle(OpenSearchSearchCommand info) {
        if (extractOpenSearchExecution) {
            operations.add(info);
        }
    }

    public void handle(OpenSearchIndexSchema info) {
        if (extractOpenSearchExecution) {
            indexSchemas.put(info.getIndexName(), info.getIndexSchema());
        }
    }

    public List<OpenSearchCommandWithDistance> getEvaluatedOpenSearchCommands() {

        operations.stream().filter(info -> info.getQuery() != null).forEach(openSearchInfo -> {
            OpenSearchDistanceWithMetrics distanceWithMetrics = computeFindDistance(openSearchInfo);
            openSearchCommandWithDistances.add(new OpenSearchCommandWithDistance(openSearchInfo.getQuery(), distanceWithMetrics));
        });
        operations.clear();

        return openSearchCommandWithDistances;
    }

    public OpenSearchExecutionsDto getExecutionDto() {
        OpenSearchExecutionsDto dto = new OpenSearchExecutionsDto();
        dto.failedQueries = emptyIndexes.stream().map(this::extractRelevantInfo).collect(Collectors.toList());
        return dto;
    }

//    private static Class<?> getCollectionClass(Object collection) throws ClassNotFoundException {
//        // return the first class with class name: com.mongodb.client.MongoCollection
//        return Arrays.stream(collection.getClass().getInterfaces())
//            .filter(iface -> iface.getName().equals(MONGO_COLLECTION_CLASS_NAME))
//            .findFirst()
//            .orElseThrow(() -> new ClassNotFoundException("Could not find class " + MONGO_COLLECTION_CLASS_NAME));
//    }

    // TODO-MIGUE
    private Iterable<?> getDocuments(String indexName) {
        try {
            Class<?> openSearchClientClass = openSearchClient.getClass();
            Method searchMethod = openSearchClientClass.getMethod("search", SearchRequest.class, Class.class);
            Object searchResponse = searchMethod.invoke(openSearchClient, new SearchRequest.Builder().index(indexName).build(), Map.class);
            Class <?> searchResponseClass = searchResponse.getClass();

            Iterable<?> searchInterable = (Iterable<?>) searchResponseClass.getMethod("documents").invoke(searchResponse);
            return searchInterable;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Failed to retrieve an OpenSearch client instance", e);
        }
    }

    private OpenSearchDistanceWithMetrics computeFindDistance(OpenSearchSearchCommand info) {
        String indexName = info.getIndexName();

        Iterable<?> documents = getDocuments(indexName);
        boolean indexIsEmpty = !documents.iterator().hasNext();

        if (indexIsEmpty) {
            emptyIndexes.add(new OpenSearchOperation(info.getIndexName(), info.getQuery(), info.getDatabaseName(), info.getIndexType()));
        }

        double min = Double.MAX_VALUE;
        int numberOfEvaluatedDocuments = 0;
        for (Object doc : documents) {
            numberOfEvaluatedDocuments += 1;
            double findDistance;
            try {
                findDistance = calculator.computeExpression(info.getQuery(), doc);
            } catch (Exception ex) {
                SimpleLogger.uniqueWarn("Failed to compute find: " + info.getQuery() + " with data " + doc);
                findDistance = Double.MAX_VALUE;
            }

            if (findDistance == 0) {
                return new OpenSearchDistanceWithMetrics(0, numberOfEvaluatedDocuments);
            } else if (findDistance < min) {
                min = findDistance;
            }
        }
        return new OpenSearchDistanceWithMetrics(min, numberOfEvaluatedDocuments);
    }

    // TODO-MIGUE: Set document type
    private OpenSearchFailedQuery extractRelevantInfo(OpenSearchOperation operation) {
//        String documentsType;
//        if (collectionSchemaIsRegistered(operation.getCollectionName())) {
//            // We have to which class the documents of the collection will be mapped to
//            documentsType = collectionSchemas.get(operation.getCollectionName());
//        } else {
//            // Just using the documents type provided by the MongoCollection method
//            documentsType = operation.getDocumentsType();
//        }
        return new OpenSearchFailedQuery(operation.getIndexName(), "documentType");
    }

    public void setOpenSearchClient(Object openSearchClient) {
        this.openSearchClient = openSearchClient;
    }
}
