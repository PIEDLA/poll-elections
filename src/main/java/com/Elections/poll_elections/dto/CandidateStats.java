package com.Elections.poll_elections.dto;

public record CandidateStats(
        Long candidateId,
        String name,
        String party,
        int votes,
        double percentage
) {
}