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

    /**
     * Method for getting all players from the database
     *
     * @param query - used for filtering e.g. query=name:Alin
     * @return list of all players found
     */
    @Override
    public List<PlayerDto> findAll(String query) {
        Specification<Player> specification = buildSpecifications(
            getSearchCriteria(query).stream().map(PlayerSpecification::new).collect(Collectors.toList()))
            .orElse(null);

        return playerMapper.buildDtoList(playerRepository.findAll(specification, Sort.by("rank")));
    }

    /**
     * Find one player entity by id
     *
     * @param id - provided id for which the app should return data
     * @return a player object if found
     * @throws PlayerServiceException - if player with specified id is not found
     */
    @Override
    public PlayerDto findById(Long id) throws PlayerServiceException {
        Player player = playerRepository
            .findById(id).orElseThrow(() ->
                new PlayerServiceException(String.format("Player with id %s not found", id)));

        return playerMapper.buildDto(player);
    }

    /**
     * Create/Edit a new player based on provided dto
     *
     * @param playerDto - request data that will be saved
     * @return the newly created/updated player
     * @throws PlayerServiceException - if player is not unique based on name
     */
    @Override
    public PlayerDto save(PlayerDto playerDto) throws PlayerServiceException {
        Player entity = playerMapper.buildEntity(playerDto);
        if (entity.getId() == null && checkDuplicatePlayer(playerDto)) {
            throw new PlayerServiceException(String.format("Player name %s is not unique", playerDto.getName()));
        }

        return playerMapper.buildDto(playerRepository.save(entity));
    }

    /**
     * Delete layer by id
     *
     * @param id - id that is provided for deletion
     */
    @Override
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    /**
     * Check if a player is a duplicate based on name
     *
     * @param playerDto - used for validation
     * @return true is a player is same name is present
     */
    private boolean checkDuplicatePlayer(PlayerDto playerDto) {
        return playerRepository.findByName(playerDto.getName()).isPresent();
    }
}
