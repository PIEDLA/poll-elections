package com.Elections.poll_elections.service;

import com.Elections.poll_elections.exception.BadRequestException;
import com.Elections.poll_elections.exception.ConflictException;
import com.Elections.poll_elections.exception.ResourceNotFoundException;
import com.Elections.poll_elections.model.Candidate;
import com.Elections.poll_elections.repository.CandidateRepository;
import com.Elections.poll_elections.repository.VoteRepository;
import com.Elections.poll_elections.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final VoterRepository voterRepository;
    private final VoteRepository voteRepository;

    public Candidate create(Candidate candidate) {
        if (candidate.getName() == null || candidate.getName().isBlank()) {
            throw new BadRequestException("El nombre del candidato es obligatorio");
        }
        // Regla: un candidato no puede ser votante
        if (voterRepository.existsByNameIgnoreCase(candidate.getName())) {
            throw new ConflictException("Esta persona ya está registrada como votante");
        }

        candidate.setId(null);
        candidate.setVotes(0);      // Los votos siempre empiezan en 0
        return candidateRepository.save(candidate);
    }

    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    public Candidate findById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidato no encontrado con id " + id));
    }

    public void delete(Long id) {
        Candidate candidate = findById(id);
        if (voteRepository.existsByCandidateId(id)) {
            throw new ConflictException("No se puede eliminar un candidato que ya tiene votos");
        }
        candidateRepository.delete(candidate);
    }
}