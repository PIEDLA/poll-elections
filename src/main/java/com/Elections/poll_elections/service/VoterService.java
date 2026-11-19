package com.Elections.poll_elections.service;

import com.Elections.poll_elections.exception.BadRequestException;
import com.Elections.poll_elections.exception.ConflictException;
import com.Elections.poll_elections.exception.ResourceNotFoundException;
import com.Elections.poll_elections.model.Voter;
import com.Elections.poll_elections.repository.CandidateRepository;
import com.Elections.poll_elections.repository.VoteRepository;
import com.Elections.poll_elections.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoterService {

    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;

    public Voter create(Voter voter) {
        if (voter.getName() == null || voter.getName().isBlank()) {
            throw new BadRequestException("El nombre del votante es obligatorio");
        }
        if (voter.getEmail() == null || voter.getEmail().isBlank()) {
            throw new BadRequestException("El email del votante es obligatorio");
        }
        if (voterRepository.existsByEmail(voter.getEmail())) {
            throw new ConflictException("Ya existe un votante con ese email");
        }
        if (candidateRepository.existsByNameIgnoreCase(voter.getName())) {
            throw new ConflictException("Esta persona ya está registrada como candidato");
        }

        voter.setId(null);
        voter.setHasVoted(false);   //
        return voterRepository.save(voter);

    }

    public List<Voter> findAll() {
        return voterRepository.findAll();
    }

    public Voter findById(Long id) {
        return voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Votante no encontrado con id " + id));
    }

    public void delete(Long id) {
        Voter voter = findById(id);
        if (voteRepository.existsByVoterId(id)) {
            throw new ConflictException("No se puede eliminar un votante que ya emitió su voto");
        }
        voterRepository.delete(voter);
    }
}