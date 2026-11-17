package com.group13.algorithmexplorer.dto;

/**
 * Request and response DTOs for Module B: Data Structures Lab
 * (Stack, Bracket Checker, Linear Queue, Circular Queue, Singly Linked List, Doubly Linked List).
 * Zero {@code java.util} collection imports.
 */
public final class DataStructureDtos {

    private DataStructureDtos() {
    }

    public static class DsOperationRequestDto {
        private String operation;
        private Integer value;
        private Integer target;
        private Integer capacity;

        public DsOperationRequestDto() {
        }

        public DsOperationRequestDto(String operation, Integer value, Integer target, Integer capacity) {
            this.operation = operation;
            this.value = value;
            this.target = target;
            this.capacity = capacity;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Integer getTarget() {
            return target;
        }

        public void setTarget(Integer target) {
            this.target = target;
        }

        public Integer getCapacity() {
            return capacity;
        }

        public void setCapacity(Integer capacity) {
            this.capacity = capacity;
        }
    }

    public static class StackStateResponseDto {
        private String operation;
        private boolean success;
        private String message;
        private int capacity;
        private int size;
        private int topIndex;
        private boolean empty;
        private boolean full;
        private Integer[] slots;
        private int[] elements;
        private int basicOperations;
        private long runningTimeNs;

        public StackStateResponseDto() {
        }

        public StackStateResponseDto(
            String operation,
            boolean success,
            String message,
            int capacity,
            int size,
            int topIndex,
            boolean empty,
            boolean full,
            Integer[] slots,
            int[] elements,
            int basicOperations,
            long runningTimeNs
        ) {
            this.operation = operation;
            this.success = success;
            this.message = message;
            this.capacity = capacity;
            this.size = size;
            this.topIndex = topIndex;
            this.empty = empty;
            this.full = full;
            this.slots = slots;
            this.elements = elements;
            this.basicOperations = basicOperations;
            this.runningTimeNs = runningTimeNs;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTopIndex() {
            return topIndex;
        }

        public void setTopIndex(int topIndex) {
            this.topIndex = topIndex;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }

        public boolean isFull() {
            return full;
        }

        public void setFull(boolean full) {
            this.full = full;
        }

        public Integer[] getSlots() {
            return slots;
        }

        public void setSlots(Integer[] slots) {
            this.slots = slots;
        }

        public int[] getElements() {
            return elements;
        }

        public void setElements(int[] elements) {
            this.elements = elements;
        }

        public int getBasicOperations() {
            return basicOperations;
        }

        public void setBasicOperations(int basicOperations) {
            this.basicOperations = basicOperations;
        }

        public long getRunningTimeNs() {
            return runningTimeNs;
        }

        public void setRunningTimeNs(long runningTimeNs) {
            this.runningTimeNs = runningTimeNs;
        }
    }

    public static class QueueStateResponseDto {
        private String queueType;
        private String operation;
        private boolean success;
        private String message;
        private int capacity;
        private int size;
        private int frontIndex;
        private int rearIndex;
        private boolean empty;
        private boolean full;
        private boolean falseFull;
        private String explanation;
        private Integer[] slots;
        private int[] elements;
        private int basicOperations;
        private long runningTimeNs;

        public QueueStateResponseDto() {
        }

        public QueueStateResponseDto(
            String queueType,
            String operation,
            boolean success,
            String message,
            int capacity,
            int size,
            int frontIndex,
            int rearIndex,
            boolean empty,
            boolean full,
            boolean falseFull,
            String explanation,
            Integer[] slots,
            int[] elements,
            int basicOperations,
            long runningTimeNs
        ) {
            this.queueType = queueType;
            this.operation = operation;
            this.success = success;
            this.message = message;
            this.capacity = capacity;
            this.size = size;
            this.frontIndex = frontIndex;
            this.rearIndex = rearIndex;
            this.empty = empty;
            this.full = full;
            this.falseFull = falseFull;
            this.explanation = explanation;
            this.slots = slots;
            this.elements = elements;
            this.basicOperations = basicOperations;
            this.runningTimeNs = runningTimeNs;
        }

        public String getQueueType() {
            return queueType;
        }

        public void setQueueType(String queueType) {
            this.queueType = queueType;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getFrontIndex() {
            return frontIndex;
        }

        public void setFrontIndex(int frontIndex) {
            this.frontIndex = frontIndex;
        }

        public int getRearIndex() {
            return rearIndex;
        }

        public void setRearIndex(int rearIndex) {
            this.rearIndex = rearIndex;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }

        public boolean isFull() {
            return full;
        }

        public void setFull(boolean full) {
            this.full = full;
        }

        public boolean isFalseFull() {
            return falseFull;
        }

        public void setFalseFull(boolean falseFull) {
            this.falseFull = falseFull;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public Integer[] getSlots() {
            return slots;
        }

        public void setSlots(Integer[] slots) {
            this.slots = slots;
        }

        public int[] getElements() {
            return elements;
        }

        public void setElements(int[] elements) {
            this.elements = elements;
        }

        public int getBasicOperations() {
            return basicOperations;
        }

        public void setBasicOperations(int basicOperations) {
            this.basicOperations = basicOperations;
        }

        public long getRunningTimeNs() {
            return runningTimeNs;
        }

        public void setRunningTimeNs(long runningTimeNs) {
            this.runningTimeNs = runningTimeNs;
        }
    }

    public static class LinkedListStateResponseDto {
        private String listType;
        private String operation;
        private boolean success;
        private String message;
        private int size;
        private int highlightedIndex;
        private int[] forwardElements;
        private int[] backwardElements;
        private int basicOperations;
        private long runningTimeNs;

        public LinkedListStateResponseDto() {
        }

        public LinkedListStateResponseDto(
            String listType,
            String operation,
            boolean success,
            String message,
            int size,
            int highlightedIndex,
            int[] forwardElements,
            int[] backwardElements,
            int basicOperations,
            long runningTimeNs
        ) {
            this.listType = listType;
            this.operation = operation;
            this.success = success;
            this.message = message;
            this.size = size;
            this.highlightedIndex = highlightedIndex;
            this.forwardElements = forwardElements;
            this.backwardElements = backwardElements;
            this.basicOperations = basicOperations;
            this.runningTimeNs = runningTimeNs;
        }

        public String getListType() {
            return listType;
        }

        public void setListType(String listType) {
            this.listType = listType;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getHighlightedIndex() {
            return highlightedIndex;
        }

        public void setHighlightedIndex(int highlightedIndex) {
            this.highlightedIndex = highlightedIndex;
        }

        public int[] getForwardElements() {
            return forwardElements;
        }

        public void setForwardElements(int[] forwardElements) {
            this.forwardElements = forwardElements;
        }

        public int[] getBackwardElements() {
            return backwardElements;
        }

        public void setBackwardElements(int[] backwardElements) {
            this.backwardElements = backwardElements;
        }

        public int getBasicOperations() {
            return basicOperations;
        }

        public void setBasicOperations(int basicOperations) {
            this.basicOperations = basicOperations;
        }

        public long getRunningTimeNs() {
            return runningTimeNs;
        }

        public void setRunningTimeNs(long runningTimeNs) {
            this.runningTimeNs = runningTimeNs;
        }
    }

    public static class BracketCheckRequestDto {
        private String expression;

        public BracketCheckRequestDto() {
        }

        public BracketCheckRequestDto(String expression) {
            this.expression = expression;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }

    public static class BracketStepDto {
        private int stepNumber;
        private String symbol;
        private String action;
        private String[] stackAfterSymbol;
        private boolean validSoFar;

        public BracketStepDto() {
        }

        public BracketStepDto(
            int stepNumber,
            String symbol,
            String action,
            String[] stackAfterSymbol,
            boolean validSoFar
        ) {
            this.stepNumber = stepNumber;
            this.symbol = symbol;
            this.action = action;
            this.stackAfterSymbol = stackAfterSymbol;
            this.validSoFar = validSoFar;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String[] getStackAfterSymbol() {
            return stackAfterSymbol;
        }

        public void setStackAfterSymbol(String[] stackAfterSymbol) {
            this.stackAfterSymbol = stackAfterSymbol;
        }

        public boolean isValidSoFar() {
            return validSoFar;
        }

        public void setValidSoFar(boolean validSoFar) {
            this.validSoFar = validSoFar;
        }
    }

    public static class BracketCheckResponseDto {
        private String expression;
        private boolean balanced;
        private String message;
        private int operationsCount;
        private long runningTimeNs;
        private BracketStepDto[] steps;

        public BracketCheckResponseDto() {
        }

        public BracketCheckResponseDto(
            String expression,
            boolean balanced,
            String message,
            int operationsCount,
            long runningTimeNs,
            BracketStepDto[] steps
        ) {
            this.expression = expression;
            this.balanced = balanced;
            this.message = message;
            this.operationsCount = operationsCount;
            this.runningTimeNs = runningTimeNs;
            this.steps = steps;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public boolean isBalanced() {
            return balanced;
        }

        public void setBalanced(boolean balanced) {
            this.balanced = balanced;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getOperationsCount() {
            return operationsCount;
        }

        public void setOperationsCount(int operationsCount) {
            this.operationsCount = operationsCount;
        }

        public long getRunningTimeNs() {
            return runningTimeNs;
        }

        public void setRunningTimeNs(long runningTimeNs) {
            this.runningTimeNs = runningTimeNs;
        }

        public BracketStepDto[] getSteps() {
            return steps;
        }

        public void setSteps(BracketStepDto[] steps) {
            this.steps = steps;
        }
    }
}
