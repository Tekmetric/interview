package com.interview.resource;

import com.interview.resource.mapper.UserResourceMapper;
import com.interview.resource.model.UserDto;
import com.interview.resource.model.UserToSaveOrUpdateDto;
import com.interview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final UserResourceMapper userResourceMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable("id") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        return ResponseEntity.ok(
                userResourceMapper.toDto(
                        userService.getBy(userId)));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(
                userResourceMapper.toUserDtoList(
                        userService.findAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long userId,
                                              @RequestBody UserToSaveOrUpdateDto toUpdate) {
        return ResponseEntity.ok(
                userResourceMapper.toDto(
                        userService.saveOrUpdate(
                                userResourceMapper.toDm(userId, toUpdate))));
    }

    @PostMapping
    public ResponseEntity<UserDto> saveNewUser(@RequestBody UserToSaveOrUpdateDto toUpdate) {
        return ResponseEntity.ok(
                userResourceMapper.toDto(
                        userService.saveOrUpdate(
                                userResourceMapper.toDm(null, toUpdate))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }
}