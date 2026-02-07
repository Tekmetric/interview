package com.tekmetric;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserPortal {
  UserModel getUserById(UUID id);

  List<UserModel> getUsersById(List<UUID> ids);

  Page<UserModel> getAllUsers(Pageable pageable);

  UserModel updateUser(UUID id, UserUpdates updates);
}
