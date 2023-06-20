package com.interview.resource;

import com.interview.resource.mapper.UserResourceMapper;
import com.interview.resource.model.UserDto;
import com.interview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final UserResourceMapper userResourceMapper;

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userResourceMapper.toUserDtoList(userService.findAllUsers());
    }
}