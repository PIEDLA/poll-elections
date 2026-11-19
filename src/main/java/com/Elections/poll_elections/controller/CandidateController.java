package com.Elections.poll_elections.controller;

import com.Elections.poll_elections.model.Candidate;
import com.Elections.poll_elections.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping
    public ResponseEntity<Candidate> create(@RequestBody Candidate candidate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.create(candidate));
    }

    @GetMapping
    public List<Candidate> findAll() {
        return candidateService.findAll();
    }

    @GetMapping("/{id}")
    public Candidate findById(@PathVariable Long id) {
        return candidateService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}