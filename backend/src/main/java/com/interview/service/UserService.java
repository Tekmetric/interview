package com.interview.service;

import com.interview.repository.UserRepository;
import com.interview.service.mapper.UserServiceMapper;
import com.interview.service.model.UserDm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserServiceMapper userServiceMapper;

    public UserDm getBy(Long id) {
        return userServiceMapper.toDm(
                userRepository.findById(id).orElse(null));
    }
    public List<UserDm> findAll() {
        return userServiceMapper.toUserDmList(
                userRepository.findAll());
    }

    public UserDm saveOrUpdate(UserDm userDm) {
        return userServiceMapper.toDm(
                userRepository.save(
                        userServiceMapper.toEntity(userDm)));
    }

    public void delete(long id) {
        UserDm foundUser = userServiceMapper.toDm(
                userRepository.findById(id).orElse(null));
        if (foundUser == null) {
            throw new EntityNotFoundException("User not found");
        }
        userRepository.delete(
                userServiceMapper.toEntity(foundUser));
    }
}
