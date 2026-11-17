package com.group13.algorithmexplorer.controller;

import com.group13.algorithmexplorer.dto.RecursionDtos.FactorialResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.FibonacciResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.HanoiRequestDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.HanoiResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.IntInputRequestDto;
import com.group13.algorithmexplorer.recursion.FactorialService;
import com.group13.algorithmexplorer.recursion.FibonacciService;
import com.group13.algorithmexplorer.recursion.TowerOfHanoiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Module A: Recursion Lab.
 * Exposes endpoints for Fibonacci (3 methods), Factorial (2 methods), and animated Tower of Hanoi.
 */
@RestController
@RequestMapping("/api/recursion")
@CrossOrigin(origins = "*")
public class RecursionController {

    private final FibonacciService fibonacciService = new FibonacciService();
    private final FactorialService factorialService = new FactorialService();
    private final TowerOfHanoiService hanoiService = new TowerOfHanoiService();

    @PostMapping("/fibonacci")
    public ResponseEntity<FibonacciResponseDto> fibonacci(@RequestBody IntInputRequestDto request) {
        if (request == null || request.getN() == null) {
            throw new IllegalArgumentException("Please enter a valid integer n for Fibonacci.");
        }
        return ResponseEntity.ok(fibonacciService.computeAll(request.getN()));
    }

    @PostMapping("/factorial")
    public ResponseEntity<FactorialResponseDto> factorial(@RequestBody IntInputRequestDto request) {
        if (request == null || request.getN() == null) {
            throw new IllegalArgumentException("Please enter a valid non-negative integer n for Factorial.");
        }
        return ResponseEntity.ok(factorialService.computeAll(request.getN()));
    }

    @PostMapping("/hanoi")
    public ResponseEntity<HanoiResponseDto> hanoi(@RequestBody HanoiRequestDto request) {
        if (request == null || request.getDisks() == null) {
            throw new IllegalArgumentException("Please specify the number of disks (1 to 8) for Tower of Hanoi.");
        }
        return ResponseEntity.ok(hanoiService.solve(
            request.getDisks(),
            request.getSourcePeg(),
            request.getDestinationPeg(),
            request.getAuxiliaryPeg()
        ));
    }
}
