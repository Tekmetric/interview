package com.interview.api.service.impl;

import com.interview.api.mapper.UserMapper;
import com.interview.api.repository.UserRepository;
import com.interview.api.dto.UserDTO;
import com.interview.api.exception.RecordNotFoundException;
import com.interview.api.model.User;
import com.interview.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl() {
        userMapper = new UserMapper();
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        return userMapper.toUserDTO(userRepository.save(userMapper.toUser(userDTO)));
    }

    @Override
    public UserDTO getUser(String username) {
        User user = userRepository.findByUsername(username);
        if (null == user){
            throw new RecordNotFoundException("Couldn't find user with username: " + username);
        }
         return  userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (null == user){
            throw new RecordNotFoundException("Couldn't find user with id: " + id);
        }
        return  userMapper.toUserDTO(user);
    }

    @Override
    public Page<UserDTO> getUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = null;

        if (sortBy != null & sortDirection != null) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortDirection), sortBy);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }

        Page<User> users= userRepository.findAll(pageable);

       return users.map(userMapper::toUserDTO);
    }


    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        if (!userRepository.existsById(Long.parseLong(userDTO.getId()))) {
            throw new RecordNotFoundException("Update operation aborted. Couldn't find user with id: " + userDTO.getId());
        }

        User user = userMapper.toUser(userDTO);
        return userMapper.toUserDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (null == user){
            throw new RecordNotFoundException("Delete operation aborted. Couldn't find user with id: " + id);
        }
         userRepository.delete(user);
    }

}
