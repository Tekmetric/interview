package com.interview.service.impl;

import com.interview.dto.PlayerDto;
import com.interview.entity.Player;
import com.interview.exception.PlayerServiceException;
import com.interview.mapper.PlayerMapper;
import com.interview.repository.PlayerRepository;
import com.interview.service.PlayerService;
import com.interview.specification.PlayerSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.interview.util.SpecificationUtil.buildSpecifications;
import static com.interview.util.SpecificationUtil.getSearchCriteria;


@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public PlayerServiceImpl(PlayerRepository playerRepository,
                             PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    @Override
    public List<PlayerDto> findAll(String query) {
        Specification<Player> specification = buildSpecifications(
            getSearchCriteria(query).stream().map(PlayerSpecification::new).collect(Collectors.toList()))
            .orElse(null);

        return playerMapper.buildDtoList(playerRepository.findAll(specification, Sort.by("rank")));
    }

    @Override
    public PlayerDto findPlayer(Long id) throws PlayerServiceException {
        Player playerOptional = playerRepository
            .findById(id).orElseThrow(() ->
                new PlayerServiceException(String.format("Player with specific id %s not found", id)));

        return playerMapper.buildDto(playerOptional);
    }

    @Override
    public PlayerDto save(PlayerDto playerDto) throws PlayerServiceException {
        Player entity = playerMapper.buildEntity(playerDto);
        if (entity.getId() == null && checkDuplicatePlayer(playerDto)) {
            throw new PlayerServiceException(String.format("Player name %s is not unique", playerDto.getName()));
        }

        return playerMapper.buildDto(playerRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    public boolean checkDuplicatePlayer(PlayerDto playerDto) {
        return playerRepository.findByName(playerDto.getName()).isPresent();
    }
}
