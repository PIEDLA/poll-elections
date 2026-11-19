package com.Elections.poll_elections.dto;

import java.util.List;

public record VoteStatistics(
        long totalVotes,
        long votersWhoVoted,
        List<CandidateStats> candidates
) {
}