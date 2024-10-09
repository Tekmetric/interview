package com.interview.dto;

import com.interview.model.Cat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
public class CatDTO {
    private final String name;
    private final Integer age;
    private final String furColor;
    @Nullable
    private final String tagLine;

    public Cat toUpdatedCat(Long id) {
        return new Cat(id, name, age, furColor, tagLine);
    }

    public static CatDTO fromCat(Cat cat) {
        return new CatDTO(cat.getName(), cat.getAge(), cat.getFurColor(), cat.getTagLine());
    }
}
