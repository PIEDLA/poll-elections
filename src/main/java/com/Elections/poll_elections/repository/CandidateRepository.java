package com.Elections.poll_elections.repository;

import com.Elections.poll_elections.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    boolean existsByNameIgnoreCase(String name);
}