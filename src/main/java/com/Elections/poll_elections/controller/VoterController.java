package com.Elections.poll_elections.controller;

import com.Elections.poll_elections.model.Voter;
import com.Elections.poll_elections.service.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voters")
@RequiredArgsConstructor
public class VoterController {

    private final VoterService voterService;

    @PostMapping
    public ResponseEntity<Voter> create(@RequestBody Voter voter) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voterService.create(voter));
    }

    @GetMapping
    public List<Voter> findAll() {
        return voterService.findAll();
    }

    @GetMapping("/{id}")
    public Voter findById(@PathVariable Long id) {
        return voterService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        voterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}