package com.interview.data.repository;

import com.interview.data.entity.UserEntity;
import com.interview.data.repository.jpa.UserJpaRepository;
import com.interview.model.ShoppingList;
import com.interview.model.User;
import com.interview.model.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final ShoppingListRepository shoppingListRepository;

    @Autowired
    public UserRepository(UserJpaRepository userJpaRepository, ShoppingListRepository shoppingListRepository) {
        this.userJpaRepository = userJpaRepository;
        this.shoppingListRepository = shoppingListRepository;
    }

    public User create(UserRequest userRequest) {
        User user = new User(userRequest);
        Assert.isTrue(user.isValid(), "User must have first and last name");
        UserEntity userEntity = userJpaRepository.save(new UserEntity(user));
        return new User(userEntity.getId(), userEntity.getFirstName(), userEntity.getLastName(), List.of());
    }

    public User getById(Long userId) {
        UserEntity userEntity =  userJpaRepository.getById(userId);
        List<ShoppingList> shoppingLists = shoppingListRepository.getByUserId(userId);

        return toUser(userEntity, shoppingLists);
    }

    private User toUser(UserEntity userEntity, List<ShoppingList> shoppingLists) {
        return new User(userEntity.getId(), userEntity.getFirstName(), userEntity.getLastName(), shoppingLists);
    }

    public void deleteById(Long userId) {
        shoppingListRepository.deleteByUserId(userId);
        userJpaRepository.deleteById(userId);
    }

    public User update(Long userId, UserRequest userUpdateRequest) {
        UserEntity userEntity = userJpaRepository.getById(userId);

        updateUserEntity(userEntity, userUpdateRequest);
        userEntity = userJpaRepository.save(userEntity);

        List<ShoppingList> shoppingLists = shoppingListRepository.getByUserId(userId);

        return toUser(userEntity, shoppingLists);
    }

    private void updateUserEntity(UserEntity userEntity, UserRequest userUpdateRequest) {
        if(userUpdateRequest.getFirstName() != null) {
            userEntity.setFirstName(userUpdateRequest.getFirstName());
        }
        if(userUpdateRequest.getLastName() != null) {
            userEntity.setLastName(userUpdateRequest.getLastName());
        }
    }

    public List<User> getAll() {
        return userJpaRepository.findAll().stream().map(this::toUser).toList();
    }

    private User toUser(UserEntity userEntity) {
        List<ShoppingList> shoppingLists = shoppingListRepository.getByUserId(userEntity.getId());
        return toUser(userEntity, shoppingLists);
    }
}
