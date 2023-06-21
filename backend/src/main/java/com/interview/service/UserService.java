package com.interview.service;

import com.interview.repository.UserRepository;
import com.interview.service.mapper.UserServiceMapper;
import com.interview.service.model.UserDm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    public static final int PAGE_NUMBER_DEFAULT = 0;
    public static final int PAGE_SIZE_DEFAULT = 32;
    private final DocumentService documentService;
    private final UserRepository userRepository;
    private final UserServiceMapper userServiceMapper;

    @Transactional(readOnly = true)
    public UserDm getBy(Long id) {
        return userServiceMapper.toDm(
                userRepository.findById(id).orElse(null));
    }

    @Transactional(readOnly = true)
    public List<UserDm> findAll(Integer page, Integer size, String searchBy) {
        return userServiceMapper.toUserDmList(
                userRepository.findAllBy(searchBy, getPageRequest(page, size)));
    }

    public UserDm saveNew(UserDm userDm) {
        return userServiceMapper.toDm(
                userRepository.save(
                        userServiceMapper.toEntity(userDm)));
    }

    public UserDm update(UserDm userDm) {
        if (userDm == null) {
            return null;
        }
        if (!CollectionUtils.isEmpty(userDm.getDocuments())) {
            userDm.setDocuments(documentService.saveNew(userDm.getDocuments(), userDm));
        }

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

    private static PageRequest getPageRequest(Integer page, Integer size) {
        int pageNumber = page == null ? PAGE_NUMBER_DEFAULT : page;
        int pageSize = size == null ? PAGE_SIZE_DEFAULT : size;
        return PageRequest.of(pageNumber, pageSize);
    }
}
