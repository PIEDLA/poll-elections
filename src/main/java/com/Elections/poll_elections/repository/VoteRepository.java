package com.Elections.poll_elections.repository;

import com.Elections.poll_elections.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVoterId(Long voterId);
    boolean existsByCandidateId(Long candidateId);
}