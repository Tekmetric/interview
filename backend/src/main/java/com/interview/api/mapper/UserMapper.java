package com.interview.api.mapper;

import com.interview.api.dto.UserDTO;
import com.interview.api.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Task model and DTO.
 */
public class UserMapper {

    private final TypeMap<UserDTO, User> dtoToModelMapper;
    private final TypeMap<User, UserDTO> modelToDtoMapper;

    public UserMapper() {
        ModelMapper modelMapper = new ModelMapper();

        dtoToModelMapper = modelMapper.createTypeMap(UserDTO.class, User.class);

        modelToDtoMapper = modelMapper.createTypeMap(User.class, UserDTO.class);
        //sensitive information, only confronted during auth process and only on the server
        modelToDtoMapper.addMappings(mapper -> mapper.skip(UserDTO::setPassword));
    }

    public User toUser(UserDTO userDTO){
        return (userDTO == null) ? null : dtoToModelMapper.map(userDTO);
    }

    public  UserDTO toUserDTO(User user){
        return (user == null) ? null : modelToDtoMapper.map(user);
    }

    public List<User> toUsers(List<UserDTO> userDTOs){
        if (userDTOs == null) return null;

        return userDTOs.stream()
                .map(dtoToModelMapper::map)
                .collect(Collectors.toList());
    }

    public List<UserDTO> toUserDTOs(List<User> users){
        if (users == null) return null;

        return users.stream()
                .map(modelToDtoMapper::map)
                .collect(Collectors.toList());
    }
}
