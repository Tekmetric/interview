package com.tekmetric;

import static com.tekmetric.validation.UserValidationUtil.validateUserList;

import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.repository.User;
import com.tekmetric.repository.UserDAO;
import com.tekmetric.repository.UserMapper;
import com.tekmetric.util.ValidationUtil;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPortalImpl implements UserPortal {
  private final UserDAO userDAO;

  @Override
  public UserModel getUserById(UUID id) {
    Optional<User> user = userDAO.findById(id);

    if (user.isEmpty()) {
      throw new UserNotFoundException("There is no user with id: " + id);
    }

    return UserMapper.toModel(user.get());
  }

  @Override
  public List<UserModel> getUsersById(List<UUID> ids) {
    List<User> fetchUsers = userDAO.findAllById(ids);

    validateUserList(ids, fetchUsers);
    return UserMapper.toModels(fetchUsers);
  }

  @Override
  public Page<UserModel> getAllUsers(Pageable pageable) {
    return userDAO.findAll(pageable).map(UserMapper::toModel);
  }

  @Override
  public UserModel updateUser(UUID id, UserUpdates updates) {
    var user =
        userDAO.findById(id).orElseThrow(() -> new UserNotFoundException("User not found: " + id));

    if (updates.getEmail() != null) {
      ValidationUtil.validateEmail(updates.getEmail());
      user.setEmail(updates.getEmail());
    }

    if (updates.getBirthDate() != null) {
      ValidationUtil.validateBirthDate(updates.getBirthDate());
      user.setBirthDate(updates.getBirthDate());
    }

    var saved = userDAO.save(user);
    return UserMapper.toModel(saved);
  }
}
