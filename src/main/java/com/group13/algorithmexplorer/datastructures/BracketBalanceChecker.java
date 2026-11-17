package com.group13.algorithmexplorer.datastructures;

import com.group13.algorithmexplorer.dto.DataStructureDtos.BracketCheckResponseDto;
import com.group13.algorithmexplorer.dto.DataStructureDtos.BracketStepDto;

/**
 * Module B Stack Application — Bracket Expression Balance Checker.
 * <p>
 * Uses {@link FixedArrayStack} to verify whether an expression such as {@code { ( [ ] ) }}
 * has balanced brackets, recording the stack state after every symbol processed.
 * </p>
 * <ul>
 *   <li><b>Time Complexity:</b> O(n) where n is the length of the expression</li>
 *   <li><b>Space Complexity:</b> O(n) for the fixed-capacity stack</li>
 * </ul>
 */
public class BracketBalanceChecker {

    /**
     * Evaluates {@code expression} symbol by symbol using {@link FixedArrayStack},
     * recording the stack contents after each symbol.
     *
     * @param expression input string containing brackets such as {@code { ( [ ] ) }}
     * @return step-by-step trace and final balanced/unbalanced verdict
     */
    public BracketCheckResponseDto check(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a non-empty bracket expression (e.g. { ( [ ] ) }).");
        }
        if (expression.length() > 64) {
            throw new IllegalArgumentException("Expression length must be at most 64 characters.");
        }

        int cap = Math.min(32, Math.max(1, expression.length()));
        FixedArrayStack stack = new FixedArrayStack(cap);
        CustomQueue<BracketStepDto> stepsQueue = new CustomQueue<>();

        long startNs = System.nanoTime();
        boolean balanced = true;
        String finalReason = "All opening brackets were matched in correct LIFO order and the stack is empty at the end.";
        int operations = 0;
        int stepNo = 0;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (Character.isWhitespace(ch)) {
                continue;
            }
            stepNo++;
            operations++;

            if (isOpening(ch)) {
                if (stack.isFull()) {
                    balanced = false;
                    finalReason = "Stack overflow while pushing opening bracket '" + ch + "' at position " + i + ".";
                    stepsQueue.enqueue(new BracketStepDto(
                        stepNo,
                        String.valueOf(ch),
                        "Push '" + ch + "' -> Stack Overflow!",
                        stackToStrings(stack),
                        false
                    ));
                    break;
                }
                stack.push(ch);
                stepsQueue.enqueue(new BracketStepDto(
                    stepNo,
                    String.valueOf(ch),
                    "Opening bracket '" + ch + "' -> pushed onto stack",
                    stackToStrings(stack),
                    true
                ));
            } else if (isClosing(ch)) {
                if (stack.isEmpty()) {
                    balanced = false;
                    finalReason = "Unbalanced: Encountered closing bracket '" + ch
                        + "' when the stack was empty (no matching opening bracket).";
                    stepsQueue.enqueue(new BracketStepDto(
                        stepNo,
                        String.valueOf(ch),
                        "Closing bracket '" + ch + "' -> Error: Stack is empty! (Underflow)",
                        stackToStrings(stack),
                        false
                    ));
                    break;
                }
                char topChar = (char) stack.pop();
                operations++;
                if (matches(topChar, ch)) {
                    stepsQueue.enqueue(new BracketStepDto(
                        stepNo,
                        String.valueOf(ch),
                        "Closing bracket '" + ch + "' -> popped '" + topChar + "' (matches!)",
                        stackToStrings(stack),
                        true
                    ));
                } else {
                    balanced = false;
                    finalReason = "Unbalanced: Closing bracket '" + ch + "' does not match top of stack '"
                        + topChar + "' (expected '" + expectedClose(topChar) + "').";
                    stepsQueue.enqueue(new BracketStepDto(
                        stepNo,
                        String.valueOf(ch),
                        "Closing bracket '" + ch + "' -> popped '" + topChar + "' (MISMATCH! Expected '"
                            + expectedClose(topChar) + "')",
                        stackToStrings(stack),
                        false
                    ));
                    break;
                }
            } else {
                stepsQueue.enqueue(new BracketStepDto(
                    stepNo,
                    String.valueOf(ch),
                    "Non-bracket symbol '" + ch + "' -> ignored (stack unchanged)",
                    stackToStrings(stack),
                    true
                ));
            }
        }

        if (balanced && !stack.isEmpty()) {
            balanced = false;
            char unclosed = (char) stack.peek();
            finalReason = "Unbalanced: Expression ended with " + stack.size()
                + " unclosed opening bracket(s) still on the stack (top is '" + unclosed + "').";
        }

        long elapsedNs = System.nanoTime() - startNs;

        int totalSteps = stepsQueue.size();
        BracketStepDto[] steps = new BracketStepDto[totalSteps];
        for (int i = 0; i < totalSteps; i++) {
            steps[i] = stepsQueue.dequeue();
        }

        return new BracketCheckResponseDto(
            expression,
            balanced,
            finalReason,
            operations,
            elapsedNs,
            steps
        );
    }

    private static boolean isOpening(char c) {
        return c == '(' || c == '[' || c == '{';
    }

    private static boolean isClosing(char c) {
        return c == ')' || c == ']' || c == '}';
    }

    private static boolean matches(char open, char close) {
        return (open == '(' && close == ')')
            || (open == '[' && close == ']')
            || (open == '{' && close == '}');
    }

    private static char expectedClose(char open) {
        if (open == '(') return ')';
        if (open == '[') return ']';
        return '}';
    }

    private static String[] stackToStrings(FixedArrayStack stack) {
        int[] raw = stack.display();
        String[] out = new String[raw.length];
        for (int i = 0; i < raw.length; i++) {
            out[i] = String.valueOf((char) raw[i]);
        }
        return out;
    }
}
