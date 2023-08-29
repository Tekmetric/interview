package com.interview.resource;

import com.interview.dto.PlayerDto;
import com.interview.exception.PlayerServiceException;
import com.interview.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/players")
public class PlayerResource {
    private final PlayerService playerService;

    public PlayerResource(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping()
    public List<PlayerDto> getPlayers(@RequestParam(name = "query", required = false) String query) {
        return playerService.findAll(query);
    }

    @GetMapping("/{id}")
    public PlayerDto getPlayer(@PathVariable Long id) throws PlayerServiceException {
        return playerService.findPlayer(id);
    }

    @PostMapping()
    public PlayerDto savePlayer(@RequestBody PlayerDto playerDto) throws PlayerServiceException {
        return playerService.save(playerDto);
    }

    @DeleteMapping(value = "/{id}")
    public void deletePlayer(@PathVariable Long id) {
        playerService.delete(id);
    }

}
