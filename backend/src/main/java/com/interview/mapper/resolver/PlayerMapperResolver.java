package com.interview.mapper.resolver;

import com.interview.dto.PlayerDto;
import com.interview.entity.Player;
import com.interview.repository.PlayerRepository;
import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapperResolver {
    @Autowired
    PlayerRepository playerRepository;

    @ObjectFactory
    public Player resolveByPlayerName(PlayerDto dto, @TargetType Class<Player> entityClass) {
        return this.playerRepository.findByName(dto.getName()).orElse(new Player());
    }
}

