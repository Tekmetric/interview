package com.interview.resource;

import com.interview.resource.mapper.UserResourceMapper;
import com.interview.resource.model.UserDto;
import com.interview.resource.model.UserToSaveDto;
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

    @PostMapping
    public ResponseEntity<UserDto> saveNewUser(@RequestBody UserToSaveDto toUpdate) {
        return ResponseEntity.ok(
                userResourceMapper.toDto(
                        userService.saveNew(
                                userResourceMapper.toDm(toUpdate))));
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto toUpdate) {
        return ResponseEntity.ok(
                userResourceMapper.toDto(
                        userService.update(
                                userResourceMapper.toDm(toUpdate))));
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