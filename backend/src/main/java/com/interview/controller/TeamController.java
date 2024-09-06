package com.interview.controller;

import com.interview.dto.TeamDto;
import com.interview.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("")
    private List<TeamDto> getAll() {
        return teamService.getAll();
    }
}
