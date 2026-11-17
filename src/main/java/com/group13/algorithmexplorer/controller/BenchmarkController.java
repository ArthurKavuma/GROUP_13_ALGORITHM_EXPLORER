package com.group13.algorithmexplorer.controller;

import com.group13.algorithmexplorer.benchmark.BenchmarkService;
import com.group13.algorithmexplorer.dto.BenchmarkDtos.BenchmarkRequestDto;
import com.group13.algorithmexplorer.dto.BenchmarkDtos.BenchmarkResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Module D: Time Complexity Analysis & Experimental Benchmarking.
 */
@RestController
@RequestMapping("/api/benchmark")
@CrossOrigin(origins = "*")
public class BenchmarkController {

    private final BenchmarkService benchmarkService = new BenchmarkService();

    @PostMapping("/run")
    public ResponseEntity<BenchmarkResponseDto> runBenchmark(
        @RequestBody(required = false) BenchmarkRequestDto request
    ) {
        int reps = (request != null && request.getRepetitions() != null) ? request.getRepetitions() : 3;
        boolean includeLarge = request == null || request.isIncludeLargeN();
        return ResponseEntity.ok(benchmarkService.runSuite(reps, includeLarge));
    }
}
