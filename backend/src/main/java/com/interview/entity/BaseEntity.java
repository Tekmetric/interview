package com.interview.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.ProxyUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Instant createdAt = Instant.now();

    @Version
    private Instant updatedAt;

    private Instant deletedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (ProxyUtils.getUserClass(o.getClass()) != this.getClass()) return false;
        return Objects.equals(id, ((BaseEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
