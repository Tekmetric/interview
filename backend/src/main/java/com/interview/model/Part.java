package com.interview.model;

import com.interview.entity.PartEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Accessors(chain = true)
public class Part {
    private Long id;
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int inventory;

    public static Part fromEntity(PartEntity entity) {
        if (entity == null) return null;
        return new Part()
                .setId(entity.getId())
                .setName(entity.getName())
                .setInventory(entity.getInventory());
    }

    public PartEntity toEntity() {
        return new PartEntity()
                .setId(this.id)
                .setName(this.name);
    }
}
