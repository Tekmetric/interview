package com.interview.dto;

import com.interview.model.Cat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
public class CreateCatDTO {
    private final String name;
    private final Integer age;
    private final String furColor;
    @Nullable
    private final String tagLine;

    public Cat toNewCat() {
        return new Cat(null, name, age, furColor, tagLine);
    }
}
