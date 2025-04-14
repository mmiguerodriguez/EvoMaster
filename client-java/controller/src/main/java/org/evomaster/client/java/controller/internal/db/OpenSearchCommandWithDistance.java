package org.evomaster.client.java.controller.internal.db;

public class OpenSearchCommandWithDistance {
    private final Object openSearchCommand;

    private final OpenSearchDistanceWithMetrics openSearchDistanceWithMetrics;

    public OpenSearchCommandWithDistance(Object openSearchCommand, OpenSearchDistanceWithMetrics openSearchDistanceWithMetrics) {
        this.openSearchCommand = openSearchCommand;
        this.openSearchDistanceWithMetrics = openSearchDistanceWithMetrics;
    }

    public Object getOpenSearchCommand() {
        return openSearchCommand;
    }

    public OpenSearchDistanceWithMetrics getOpenSearchDistanceWithMetrics() {
        return openSearchDistanceWithMetrics;
    }
}
