package com.group13.algorithmexplorer.dto;

/**
 * Request and response DTOs for Module D: Time Complexity Analysis & Benchmarking.
 * Zero {@code java.util} collection imports.
 */
public final class BenchmarkDtos {

    private BenchmarkDtos() {
    }

    public static class BenchmarkRequestDto {
        private Integer repetitions;
        private boolean includeLargeN;

        public BenchmarkRequestDto() {
            this.repetitions = 3;
            this.includeLargeN = true;
        }

        public BenchmarkRequestDto(Integer repetitions, boolean includeLargeN) {
            this.repetitions = repetitions;
            this.includeLargeN = includeLargeN;
        }

        public Integer getRepetitions() {
            return repetitions;
        }

        public void setRepetitions(Integer repetitions) {
            this.repetitions = repetitions;
        }

        public boolean isIncludeLargeN() {
            return includeLargeN;
        }

        public void setIncludeLargeN(boolean includeLargeN) {
            this.includeLargeN = includeLargeN;
        }
    }

    public static class BenchmarkPointDto {
        private int n;
        private long averageTimeNs;
        private double averageTimeMs;
        private long minTimeNs;
        private long maxTimeNs;
        private long averageComparisons;
        private long averageSwapsOrMoves;
        private boolean executed;
        private String note;

        public BenchmarkPointDto() {
        }

        public BenchmarkPointDto(
            int n,
            long averageTimeNs,
            double averageTimeMs,
            long minTimeNs,
            long maxTimeNs,
            long averageComparisons,
            long averageSwapsOrMoves,
            boolean executed,
            String note
        ) {
            this.n = n;
            this.averageTimeNs = averageTimeNs;
            this.averageTimeMs = averageTimeMs;
            this.minTimeNs = minTimeNs;
            this.maxTimeNs = maxTimeNs;
            this.averageComparisons = averageComparisons;
            this.averageSwapsOrMoves = averageSwapsOrMoves;
            this.executed = executed;
            this.note = note;
        }

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }

        public long getAverageTimeNs() {
            return averageTimeNs;
        }

        public void setAverageTimeNs(long averageTimeNs) {
            this.averageTimeNs = averageTimeNs;
        }

        public double getAverageTimeMs() {
            return averageTimeMs;
        }

        public void setAverageTimeMs(double averageTimeMs) {
            this.averageTimeMs = averageTimeMs;
        }

        public long getMinTimeNs() {
            return minTimeNs;
        }

        public void setMinTimeNs(long minTimeNs) {
            this.minTimeNs = minTimeNs;
        }

        public long getMaxTimeNs() {
            return maxTimeNs;
        }

        public void setMaxTimeNs(long maxTimeNs) {
            this.maxTimeNs = maxTimeNs;
        }

        public long getAverageComparisons() {
            return averageComparisons;
        }

        public void setAverageComparisons(long averageComparisons) {
            this.averageComparisons = averageComparisons;
        }

        public long getAverageSwapsOrMoves() {
            return averageSwapsOrMoves;
        }

        public void setAverageSwapsOrMoves(long averageSwapsOrMoves) {
            this.averageSwapsOrMoves = averageSwapsOrMoves;
        }

        public boolean isExecuted() {
            return executed;
        }

        public void setExecuted(boolean executed) {
            this.executed = executed;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    public static class AlgorithmSeriesDto {
        private String algorithmKey;
        private String algorithmName;
        private String complexityGroup; // "QUADRATIC" or "NLOGN"
        private String bestCase;
        private String averageCase;
        private String worstCase;
        private BenchmarkPointDto[] points;

        public AlgorithmSeriesDto() {
        }

        public AlgorithmSeriesDto(
            String algorithmKey,
            String algorithmName,
            String complexityGroup,
            String bestCase,
            String averageCase,
            String worstCase,
            BenchmarkPointDto[] points
        ) {
            this.algorithmKey = algorithmKey;
            this.algorithmName = algorithmName;
            this.complexityGroup = complexityGroup;
            this.bestCase = bestCase;
            this.averageCase = averageCase;
            this.worstCase = worstCase;
            this.points = points;
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

        public String getComplexityGroup() {
            return complexityGroup;
        }

        public void setComplexityGroup(String complexityGroup) {
            this.complexityGroup = complexityGroup;
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

        public BenchmarkPointDto[] getPoints() {
            return points;
        }

        public void setPoints(BenchmarkPointDto[] points) {
            this.points = points;
        }
    }

    public static class DistributionBenchmarkDto {
        private String inputType; // "RANDOM", "SORTED", "REVERSE_SORTED"
        private String inputLabel;
        private int[] standardSizes;
        private int[] largeSizes;
        private AlgorithmSeriesDto[] series;

        public DistributionBenchmarkDto() {
        }

        public DistributionBenchmarkDto(
            String inputType,
            String inputLabel,
            int[] standardSizes,
            int[] largeSizes,
            AlgorithmSeriesDto[] series
        ) {
            this.inputType = inputType;
            this.inputLabel = inputLabel;
            this.standardSizes = standardSizes;
            this.largeSizes = largeSizes;
            this.series = series;
        }

        public String getInputType() {
            return inputType;
        }

        public void setInputType(String inputType) {
            this.inputType = inputType;
        }

        public String getInputLabel() {
            return inputLabel;
        }

        public void setInputLabel(String inputLabel) {
            this.inputLabel = inputLabel;
        }

        public int[] getStandardSizes() {
            return standardSizes;
        }

        public void setStandardSizes(int[] standardSizes) {
            this.standardSizes = standardSizes;
        }

        public int[] getLargeSizes() {
            return largeSizes;
        }

        public void setLargeSizes(int[] largeSizes) {
            this.largeSizes = largeSizes;
        }

        public AlgorithmSeriesDto[] getSeries() {
            return series;
        }

        public void setSeries(AlgorithmSeriesDto[] series) {
            this.series = series;
        }
    }

    public static class BenchmarkResponseDto {
        private int repetitions;
        private int[] standardSizes;
        private int[] largeSizes;
        private DistributionBenchmarkDto[] distributions;
        private String[] discussionPoints;

        public BenchmarkResponseDto() {
        }

        public BenchmarkResponseDto(
            int repetitions,
            int[] standardSizes,
            int[] largeSizes,
            DistributionBenchmarkDto[] distributions,
            String[] discussionPoints
        ) {
            this.repetitions = repetitions;
            this.standardSizes = standardSizes;
            this.largeSizes = largeSizes;
            this.distributions = distributions;
            this.discussionPoints = discussionPoints;
        }

        public int getRepetitions() {
            return repetitions;
        }

        public void setRepetitions(int repetitions) {
            this.repetitions = repetitions;
        }

        public int[] getStandardSizes() {
            return standardSizes;
        }

        public void setStandardSizes(int[] standardSizes) {
            this.standardSizes = standardSizes;
        }

        public int[] getLargeSizes() {
            return largeSizes;
        }

        public void setLargeSizes(int[] largeSizes) {
            this.largeSizes = largeSizes;
        }

        public DistributionBenchmarkDto[] getDistributions() {
            return distributions;
        }

        public void setDistributions(DistributionBenchmarkDto[] distributions) {
            this.distributions = distributions;
        }

        public String[] getDiscussionPoints() {
            return discussionPoints;
        }

        public void setDiscussionPoints(String[] discussionPoints) {
            this.discussionPoints = discussionPoints;
        }
    }
}
