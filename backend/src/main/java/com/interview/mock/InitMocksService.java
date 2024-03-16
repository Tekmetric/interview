package com.interview.mock;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InitMocksService {

    @Value("${app.mocks.enable}")
    private boolean enableMocks;

    @Value("${app.mocks.users:2}")
    private int totalUsers;

    @Value("${app.mocks.recipes:10}")
    private int recipesPerUser;

    @Value("${app.mocks.reviews:1}")
    private int reviewsPerUserPerRecipe;

    private final UserMockService userMockService;
    private final RecipeMocksService recipeMocksService;
    private final ReviewMocksService reviewMocksService;

    public InitMocksService(UserMockService userMockService, RecipeMocksService recipeMocksService, ReviewMocksService reviewMocksService) {
        this.userMockService = userMockService;
        this.recipeMocksService = recipeMocksService;
        this.reviewMocksService = reviewMocksService;
    }

    @PostConstruct
    public void initMocks() {
        if (!enableMocks) return;

        final var users = userMockService.generateUsers(totalUsers);
        final var recipes = recipeMocksService.generateRecipes(users, recipesPerUser);
        final var reviews = reviewMocksService.generateReviews(users, recipes, reviewsPerUserPerRecipe);

        System.out.println("=============================================");
        System.out.println("Total Users: " + users.size());
        System.out.println("Total Recipes: " + recipes.size());
        System.out.println("Total Reviews: " + reviews.size());
        System.out.println("=============================================");
    }
}
