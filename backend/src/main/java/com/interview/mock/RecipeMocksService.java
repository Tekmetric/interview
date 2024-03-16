package com.interview.mock;

import com.interview.business.domain.AppUser;
import com.interview.business.domain.Recipe;
import com.interview.business.services.recipes.RecipesService;
import com.interview.business.services.recipes.dto.RecipeCreateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class RecipeMocksService {

    private final String Recipe_Description = """
            # Lists
                       
            1. Make my changes
                1. Fix bug
                2. Improve formatting
                    - Make the headings bigger
            2. Push my commits to GitHub
            3. Open a pull request
                * Describe my changes
                * Mention all the members of my team
                    * Ask for feedback
                        
            Colons can be used to align columns.
                        
            | Tables        | Are           | Cool  |
            | ------------- |:-------------:| -----:|
            | col 3 is      | right-aligned | $1600 |
            | col 2 is      | centered      |   $12 |
            | zebra stripes | are neat      |    $1 |
            """;

    private final List<String> RecipeImages = List.of(
            "https://www.allrecipes.com/thmb/-GwMqBUSLiOtOqyciATxhVB5ZPk=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/7519daffodil-cakeKim4x3-23c99ea170b84cf9a80a4be4026b1683.jpg",
            "https://www.allrecipes.com/thmb/iyfZNNm7WSl-1HVUzWjF9SpRST8=/0x512/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/8551641-CopycatCosmicBrownie_DDMFS-248-4x3-566596741ece4186b38d600960c75502.jpg",
            "https://www.allrecipes.com/thmb/BVIa5dKfGQlpQJ_epc6wH6Vm990=/0x512/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/IMG_8145_Chocolate-Mousse-for-Beginners-4x3-cropped-757ae43035ff48cc8bc9ccffbd6cf3b7.jpg",
            "https://www.allrecipes.com/thmb/DY07x-FSIQKnS8aT_pPuhzStfYQ=/0x512/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/8408560-7-Layer-Dessert-Dip-4x3-257-a8f1ba76035b43909541653d5c2ee3f4.jpg",
            "https://www.allrecipes.com/thmb/o12mmdP9HaPD53z_5OHIuuMjdMY=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/8363632-sweet-potato-brownies-ddmfs-3x4-11043-911444b05a96439db63b5d75dd5dd537.jpg",
            "https://www.allrecipes.com/thmb/hqVKTsuLxfBeuN4D1lq0ZJa-Llo=/0x512/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/8363654-sweet-potato-dump-cake-ddmfs-3x4-10722-2592297c1363477397fe08fa38977660.jpg"
    );

    private final RecipesService recipesService;

    public RecipeMocksService(RecipesService recipesService) {
        this.recipesService = recipesService;
    }

    public List<Recipe> generateRecipes(List<AppUser> users, int timesPerUser) {
        return users.stream().flatMap(user ->
                IntStream.range(0, timesPerUser).mapToObj((i) -> recipesService.createRecipe(user.id, new RecipeCreateRequest(
                        "Interesting Title " + i,
                        Recipe_Description,
                        RecipeImages.get(new Random().nextInt(RecipeImages.size())),
                        new Random().nextInt(90) + 1,
                        Recipe.MealType.values()[new Random().nextInt(Recipe.MealType.values().length)]
                )))
        ).toList();
    }
}
