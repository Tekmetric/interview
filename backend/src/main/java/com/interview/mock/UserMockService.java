package com.interview.mock;

import com.interview.business.domain.AppUser;
import com.interview.business.services.users.UsersService;
import com.interview.business.services.users.dto.SignUpRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class UserMockService {
    private final List<String> UserAvatars = List.of(
            "https://randomuser.me/api/portraits/women/69.jpg",
            "https://randomuser.me/api/portraits/women/1.jpg",
            "https://randomuser.me/api/portraits/women/26.jpg",
            "https://randomuser.me/api/portraits/women/68.jpg",
            "https://randomuser.me/api/portraits/women/2.jpg",
            "https://randomuser.me/api/portraits/women/89.jpg"
    );


    private final UsersService usersService;

    public UserMockService(UsersService usersService) {
        this.usersService = usersService;
    }

    public List<AppUser> generateUsers(int times) {
        return IntStream.range(0, times).mapToObj((i) -> usersService.signUp(new SignUpRequest("Name " + i, "name_" + i + "@email.com", "strong_password", UserAvatars.get(new Random().nextInt(UserAvatars.size()))))).toList();
    }
}
