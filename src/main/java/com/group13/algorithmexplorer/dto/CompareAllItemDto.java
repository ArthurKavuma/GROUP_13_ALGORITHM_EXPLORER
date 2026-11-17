package com.group13.algorithmexplorer.dto;

/**
 * Single row in the Module C "Compare All" side-by-side sorting comparison table.
 */
public class CompareAllItemDto {

    private String algorithmKey;
    private String algorithmName;
    private int comparisons;
    private int swapsOrMoves;
    private long runningTimeNs;
    private String bestCase;
    private String averageCase;
    private String worstCase;
    private String extraSpace;
    private boolean stable;
    private int[] sortedOutput;

    public CompareAllItemDto() {
    }

    public CompareAllItemDto(
        String algorithmKey,
        String algorithmName,
        int comparisons,
        int swapsOrMoves,
        long runningTimeNs,
        String bestCase,
        String averageCase,
        String worstCase,
        String extraSpace,
        boolean stable,
        int[] sortedOutput
    ) {
        this.algorithmKey = algorithmKey;
        this.algorithmName = algorithmName;
        this.comparisons = comparisons;
        this.swapsOrMoves = swapsOrMoves;
        this.runningTimeNs = runningTimeNs;
        this.bestCase = bestCase;
        this.averageCase = averageCase;
        this.worstCase = worstCase;
        this.extraSpace = extraSpace;
        this.stable = stable;
        this.sortedOutput = sortedOutput;
    }

    public String getAlgorithmKey() {
        return algorithmKey;
    }

    public void setAlgorithmKey(String algorithmKey) {
        this.algorithmKey = algorithmKey;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public int getComparisons() {
        return comparisons;
    }

    public void setComparisons(int comparisons) {
        this.comparisons = comparisons;
    }

    public int getSwapsOrMoves() {
        return swapsOrMoves;
    }

    public void setSwapsOrMoves(int swapsOrMoves) {
        this.swapsOrMoves = swapsOrMoves;
    }

    public long getRunningTimeNs() {
        return runningTimeNs;
    }

    public void setRunningTimeNs(long runningTimeNs) {
        this.runningTimeNs = runningTimeNs;
    }

    public String getBestCase() {
        return bestCase;
    }

    public void setBestCase(String bestCase) {
        this.bestCase = bestCase;
    }

    public String getAverageCase() {
        return averageCase;
    }

    public void setAverageCase(String averageCase) {
        this.averageCase = averageCase;
    }

    public String getWorstCase() {
        return worstCase;
    }

    public void setWorstCase(String worstCase) {
        this.worstCase = worstCase;
    }

    public String getExtraSpace() {
        return extraSpace;
    }

    public void setExtraSpace(String extraSpace) {
        this.extraSpace = extraSpace;
    }

    public boolean isStable() {
        return stable;
    }

    public void setStable(boolean stable) {
        this.stable = stable;
    }

    public int[] getSortedOutput() {
        return sortedOutput;
    }

    public void setSortedOutput(int[] sortedOutput) {
        this.sortedOutput = sortedOutput;
    }
}
