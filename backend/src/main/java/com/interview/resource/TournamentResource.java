package com.interview.resource;

import com.interview.dto.TournamentDto;
import com.interview.service.TournamentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/tournaments")
public class TournamentResource {
    private final TournamentService tournamentService;

    public TournamentResource(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping()
    public List<TournamentDto> getTournaments() {
        return tournamentService.findAll();
    }

}
