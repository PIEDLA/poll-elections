package com.Elections.poll_elections.service;

import com.Elections.poll_elections.dto.CandidateStats;
import com.Elections.poll_elections.dto.VoteStatistics;
import com.Elections.poll_elections.exception.ConflictException;
import com.Elections.poll_elections.exception.ResourceNotFoundException;
import com.Elections.poll_elections.model.Candidate;
import com.Elections.poll_elections.model.Vote;
import com.Elections.poll_elections.model.Voter;
import com.Elections.poll_elections.repository.CandidateRepository;
import com.Elections.poll_elections.repository.VoteRepository;
import com.Elections.poll_elections.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;

    @Transactional
    public Vote cast(Long voterId, Long candidateId) {
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new ResourceNotFoundException("Votante no encontrado con id " + voterId));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidato no encontrado con id " + candidateId));

        if (voter.getHasVoted()) {
            throw new ConflictException("El votante ya emitió su voto");
        }

        voter.setHasVoted(true);
        candidate.setVotes(candidate.getVotes() + 1);

        voterRepository.save(voter);
        candidateRepository.save(candidate);

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setCandidate(candidate);
        return voteRepository.save(vote);
    }

    public List<Vote> findAll() {
        return voteRepository.findAll();
    }

    public VoteStatistics getStatistics() {
        List<Candidate> candidates = candidateRepository.findAll();
        long totalVotes = candidates.stream()
                .mapToLong(Candidate::getVotes)
                .sum();

        List<CandidateStats> candidateStats = candidates.stream()
                .map(c -> new CandidateStats(
                        c.getId(),
                        c.getName(),
                        c.getParty(),
                        c.getVotes(),
                        percentage(c.getVotes(), totalVotes)))
                .toList();

        long votersWhoVoted = voterRepository.countByHasVotedTrue();

        return new VoteStatistics(totalVotes, votersWhoVoted, candidateStats);
    }

    // Redondea a 2 decimales
    private double percentage(long votes, long total) {
        if (total == 0) {
            return 0.0;
        }
        return Math.round(votes * 10000.0 / total) / 100.0;
    }
}