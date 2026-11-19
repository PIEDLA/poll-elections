package com.Elections.poll_elections.controller;

import com.Elections.poll_elections.dto.VoteRequest;
import com.Elections.poll_elections.dto.VoteStatistics;
import com.Elections.poll_elections.model.Vote;
import com.Elections.poll_elections.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Vote> cast(@RequestBody VoteRequest request) {
        Vote vote = voteService.cast(request.voterId(), request.candidateId());
        return ResponseEntity.status(HttpStatus.CREATED).body(vote);
    }

    @GetMapping
    public List<Vote> findAll() {
        return voteService.findAll();
    }

    @GetMapping("/statistics")
    public VoteStatistics statistics() {
        return voteService.getStatistics();
    }
}