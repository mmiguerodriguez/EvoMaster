package org.evomaster.client.java.controller.internal.db;

public class OpenSearchDistanceWithMetrics {
    private final double openSearchDistance;

    private final int numberOfEvaluatedDocuments;

    public OpenSearchDistanceWithMetrics(double openSearchDistance, int numberOfEvaluatedDocuments) {
        if (openSearchDistance < 0) {
            throw new IllegalArgumentException("openSearchDistance must be non-negative but value is " + openSearchDistance);
        }

        if (numberOfEvaluatedDocuments < 0) {
            throw new IllegalArgumentException("numberOfEvaluatedDocuments must be non-negative but value is " + numberOfEvaluatedDocuments);
        }

        this.openSearchDistance = openSearchDistance;
        this.numberOfEvaluatedDocuments = numberOfEvaluatedDocuments;
    }

    public double getOpenSearchDistance() {
        return openSearchDistance;
    }

    public int getNumberOfEvaluatedDocuments() {
        return numberOfEvaluatedDocuments;
    }
}
