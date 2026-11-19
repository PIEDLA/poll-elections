package com.Elections.poll_elections.repository;

import com.Elections.poll_elections.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterRepository extends JpaRepository<Voter, Long> {
    boolean existsByEmail(String email);
    boolean existsByNameIgnoreCase(String name);
    long countByHasVotedTrue();
}