package com.group13.algorithmexplorer.dto;

/**
 * Top-level response payload returned by {@code POST /api/sort}.
 * Matches the {@code TrackingObject} contract in {@code app.js}.
 */
public class TrackingObjectDto {

    private String algorithm;
    private String dataStructure;
    private int[] input;
    private int[] output;
    private MetricsDto metrics;
    private StepDto[] steps;

    public TrackingObjectDto() {
    }

    public TrackingObjectDto(
        String algorithm,
        String dataStructure,
        int[] input,
        int[] output,
        MetricsDto metrics,
        StepDto[] steps
    ) {
        this.algorithm = algorithm;
        this.dataStructure = dataStructure;
        this.input = input;
        this.output = output;
        this.metrics = metrics;
        this.steps = steps;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(String dataStructure) {
        this.dataStructure = dataStructure;
    }

    public int[] getInput() {
        return input;
    }

    public void setInput(int[] input) {
        this.input = input;
    }

    public int[] getOutput() {
        return output;
    }

    public void setOutput(int[] output) {
        this.output = output;
    }

    public MetricsDto getMetrics() {
        return metrics;
    }

    public void setMetrics(MetricsDto metrics) {
        this.metrics = metrics;
    }

    public StepDto[] getSteps() {
        return steps;
    }

    public void setSteps(StepDto[] steps) {
        this.steps = steps;
    }
}

