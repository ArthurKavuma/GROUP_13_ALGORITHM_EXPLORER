package com.group13.algorithmexplorer.dto;

/**
 * Request and response DTOs for Module A: Recursion Lab (Fibonacci, Factorial, Tower of Hanoi).
 * Built without any {@code java.util} collection classes.
 */
public final class RecursionDtos {

    private RecursionDtos() {
    }

    public static class IntInputRequestDto {
        private Integer n;

        public IntInputRequestDto() {
        }

        public IntInputRequestDto(Integer n) {
            this.n = n;
        }

        public Integer getN() {
            return n;
        }

        public void setN(Integer n) {
            this.n = n;
        }
    }

    public static class MethodResultDto {
        private String methodKey;
        private String methodName;
        private boolean executed;
        private String skippedReason;
        private long result;
        private long operationsCount;
        private String operationsLabel;
        private long runningTimeNs;
        private String timeComplexity;
        private String spaceComplexity;
        private String[] traceSteps;

        public MethodResultDto() {
        }

        public MethodResultDto(
            String methodKey,
            String methodName,
            boolean executed,
            String skippedReason,
            long result,
            long operationsCount,
            String operationsLabel,
            long runningTimeNs,
            String timeComplexity,
            String spaceComplexity,
            String[] traceSteps
        ) {
            this.methodKey = methodKey;
            this.methodName = methodName;
            this.executed = executed;
            this.skippedReason = skippedReason;
            this.result = result;
            this.operationsCount = operationsCount;
            this.operationsLabel = operationsLabel;
            this.runningTimeNs = runningTimeNs;
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
            this.traceSteps = traceSteps != null ? traceSteps : new String[0];
        }

        public String getMethodKey() {
            return methodKey;
        }

        public void setMethodKey(String methodKey) {
            this.methodKey = methodKey;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public boolean isExecuted() {
            return executed;
        }

        public void setExecuted(boolean executed) {
            this.executed = executed;
        }

        public String getSkippedReason() {
            return skippedReason;
        }

        public void setSkippedReason(String skippedReason) {
            this.skippedReason = skippedReason;
        }

        public long getResult() {
            return result;
        }

        public void setResult(long result) {
            this.result = result;
        }

        public long getOperationsCount() {
            return operationsCount;
        }

        public void setOperationsCount(long operationsCount) {
            this.operationsCount = operationsCount;
        }

        public String getOperationsLabel() {
            return operationsLabel;
        }

        public void setOperationsLabel(String operationsLabel) {
            this.operationsLabel = operationsLabel;
        }

        public long getRunningTimeNs() {
            return runningTimeNs;
        }

        public void setRunningTimeNs(long runningTimeNs) {
            this.runningTimeNs = runningTimeNs;
        }

        public String getTimeComplexity() {
            return timeComplexity;
        }

        public void setTimeComplexity(String timeComplexity) {
            this.timeComplexity = timeComplexity;
        }

        public String getSpaceComplexity() {
            return spaceComplexity;
        }

        public void setSpaceComplexity(String spaceComplexity) {
            this.spaceComplexity = spaceComplexity;
        }

        public String[] getTraceSteps() {
            return traceSteps;
        }

        public void setTraceSteps(String[] traceSteps) {
            this.traceSteps = traceSteps;
        }
    }

    public static class FibonacciResponseDto {
        private int n;
        private int naiveSafeLimit;
        private String naiveLimitExplanation;
        private MethodResultDto naiveRecursion;
        private MethodResultDto memoizedRecursion;
        private MethodResultDto bottomUpDp;

        public FibonacciResponseDto() {
        }

        public FibonacciResponseDto(
            int n,
            int naiveSafeLimit,
            String naiveLimitExplanation,
            MethodResultDto naiveRecursion,
            MethodResultDto memoizedRecursion,
            MethodResultDto bottomUpDp
        ) {
            this.n = n;
            this.naiveSafeLimit = naiveSafeLimit;
            this.naiveLimitExplanation = naiveLimitExplanation;
            this.naiveRecursion = naiveRecursion;
            this.memoizedRecursion = memoizedRecursion;
            this.bottomUpDp = bottomUpDp;
        }

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }

        public int getNaiveSafeLimit() {
            return naiveSafeLimit;
        }

        public void setNaiveSafeLimit(int naiveSafeLimit) {
            this.naiveSafeLimit = naiveSafeLimit;
        }

        public String getNaiveLimitExplanation() {
            return naiveLimitExplanation;
        }

        public void setNaiveLimitExplanation(String naiveLimitExplanation) {
            this.naiveLimitExplanation = naiveLimitExplanation;
        }

        public MethodResultDto getNaiveRecursion() {
            return naiveRecursion;
        }

        public void setNaiveRecursion(MethodResultDto naiveRecursion) {
            this.naiveRecursion = naiveRecursion;
        }

        public MethodResultDto getMemoizedRecursion() {
            return memoizedRecursion;
        }

        public void setMemoizedRecursion(MethodResultDto memoizedRecursion) {
            this.memoizedRecursion = memoizedRecursion;
        }

        public MethodResultDto getBottomUpDp() {
            return bottomUpDp;
        }

        public void setBottomUpDp(MethodResultDto bottomUpDp) {
            this.bottomUpDp = bottomUpDp;
        }
    }

    public static class FactorialResponseDto {
        private int n;
        private MethodResultDto recursive;
        private MethodResultDto iterative;

        public FactorialResponseDto() {
        }

        public FactorialResponseDto(int n, MethodResultDto recursive, MethodResultDto iterative) {
            this.n = n;
            this.recursive = recursive;
            this.iterative = iterative;
        }

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }

        public MethodResultDto getRecursive() {
            return recursive;
        }

        public void setRecursive(MethodResultDto recursive) {
            this.recursive = recursive;
        }

        public MethodResultDto getIterative() {
            return iterative;
        }

        public void setIterative(MethodResultDto iterative) {
            this.iterative = iterative;
        }
    }

    public static class HanoiRequestDto {
        private Integer disks;
        private String sourcePeg;
        private String destinationPeg;
        private String auxiliaryPeg;

        public HanoiRequestDto() {
        }

        public HanoiRequestDto(Integer disks, String sourcePeg, String destinationPeg, String auxiliaryPeg) {
            this.disks = disks;
            this.sourcePeg = sourcePeg;
            this.destinationPeg = destinationPeg;
            this.auxiliaryPeg = auxiliaryPeg;
        }

        public Integer getDisks() {
            return disks;
        }

        public void setDisks(Integer disks) {
            this.disks = disks;
        }

        public String getSourcePeg() {
            return sourcePeg;
        }

        public void setSourcePeg(String sourcePeg) {
            this.sourcePeg = sourcePeg;
        }

        public String getDestinationPeg() {
            return destinationPeg;
        }

        public void setDestinationPeg(String destinationPeg) {
            this.destinationPeg = destinationPeg;
        }

        public String getAuxiliaryPeg() {
            return auxiliaryPeg;
        }

        public void setAuxiliaryPeg(String auxiliaryPeg) {
            this.auxiliaryPeg = auxiliaryPeg;
        }
    }

    public static class HanoiStepDto {
        private int moveNumber;
        private int disk;
        private String fromPeg;
        private String toPeg;
        private String description;
        private int[] pegA;
        private int[] pegB;
        private int[] pegC;

        public HanoiStepDto() {
        }

        public HanoiStepDto(
            int moveNumber,
            int disk,
            String fromPeg,
            String toPeg,
            String description,
            int[] pegA,
            int[] pegB,
            int[] pegC
        ) {
            this.moveNumber = moveNumber;
            this.disk = disk;
            this.fromPeg = fromPeg;
            this.toPeg = toPeg;
            this.description = description;
            this.pegA = pegA;
            this.pegB = pegB;
            this.pegC = pegC;
        }

        public int getMoveNumber() {
            return moveNumber;
        }

        public void setMoveNumber(int moveNumber) {
            this.moveNumber = moveNumber;
        }

        public int getDisk() {
            return disk;
        }

        public void setDisk(int disk) {
            this.disk = disk;
        }

        public String getFromPeg() {
            return fromPeg;
        }

        public void setFromPeg(String fromPeg) {
            this.fromPeg = fromPeg;
        }

        public String getToPeg() {
            return toPeg;
        }

        public void setToPeg(String toPeg) {
            this.toPeg = toPeg;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int[] getPegA() {
            return pegA;
        }

        public void setPegA(int[] pegA) {
            this.pegA = pegA;
        }

        public int[] getPegB() {
            return pegB;
        }

        public void setPegB(int[] pegB) {
            this.pegB = pegB;
        }

        public int[] getPegC() {
            return pegC;
        }

        public void setPegC(int[] pegC) {
            this.pegC = pegC;
        }
    }

    public static class HanoiResponseDto {
        private int disks;
        private String sourcePeg;
        private String destinationPeg;
        private String auxiliaryPeg;
        private int totalMoves;
        private int formulaExpectedMoves;
        private boolean matchesFormula;
        private String formulaConfirmation;
        private long recursiveCalls;
        private long runningTimeNs;
        private HanoiStepDto[] steps;

        public HanoiResponseDto() {
        }

        public HanoiResponseDto(
            int disks,
            String sourcePeg,
            String destinationPeg,
            String auxiliaryPeg,
            int totalMoves,
            int formulaExpectedMoves,
            boolean matchesFormula,
            String formulaConfirmation,
            long recursiveCalls,
            long runningTimeNs,
            HanoiStepDto[] steps
        ) {
            this.disks = disks;
            this.sourcePeg = sourcePeg;
            this.destinationPeg = destinationPeg;
            this.auxiliaryPeg = auxiliaryPeg;
            this.totalMoves = totalMoves;
            this.formulaExpectedMoves = formulaExpectedMoves;
            this.matchesFormula = matchesFormula;
            this.formulaConfirmation = formulaConfirmation;
            this.recursiveCalls = recursiveCalls;
            this.runningTimeNs = runningTimeNs;
            this.steps = steps;
        }

        public int getDisks() {
            return disks;
        }

        public void setDisks(int disks) {
            this.disks = disks;
        }

        public String getSourcePeg() {
            return sourcePeg;
        }

        public void setSourcePeg(String sourcePeg) {
            this.sourcePeg = sourcePeg;
        }

        public String getDestinationPeg() {
            return destinationPeg;
        }

        public void setDestinationPeg(String destinationPeg) {
            this.destinationPeg = destinationPeg;
        }

        public String getAuxiliaryPeg() {
            return auxiliaryPeg;
        }

        public void setAuxiliaryPeg(String auxiliaryPeg) {
            this.auxiliaryPeg = auxiliaryPeg;
        }

        public int getTotalMoves() {
            return totalMoves;
        }

        public void setTotalMoves(int totalMoves) {
            this.totalMoves = totalMoves;
        }

        public int getFormulaExpectedMoves() {
            return formulaExpectedMoves;
        }

        public void setFormulaExpectedMoves(int formulaExpectedMoves) {
            this.formulaExpectedMoves = formulaExpectedMoves;
        }

        public boolean isMatchesFormula() {
            return matchesFormula;
        }

        public void setMatchesFormula(boolean matchesFormula) {
            this.matchesFormula = matchesFormula;
        }

        public String getFormulaConfirmation() {
            return formulaConfirmation;
        }

        public void setFormulaConfirmation(String formulaConfirmation) {
            this.formulaConfirmation = formulaConfirmation;
        }

        public long getRecursiveCalls() {
            return recursiveCalls;
        }

        public void setRecursiveCalls(long recursiveCalls) {
            this.recursiveCalls = recursiveCalls;
        }

        public long getRunningTimeNs() {
            return runningTimeNs;
        }

        public void setRunningTimeNs(long runningTimeNs) {
            this.runningTimeNs = runningTimeNs;
        }

        public HanoiStepDto[] getSteps() {
            return steps;
        }

        public void setSteps(HanoiStepDto[] steps) {
            this.steps = steps;
        }
    }
}
