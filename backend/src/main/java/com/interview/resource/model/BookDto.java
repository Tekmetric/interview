package com.interview.resource.model;

import com.interview.repository.model.Genre;
import com.interview.resource.model.validation.ValidIsbn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class BookDto {

    private BookDto() {
    }

    @Schema(description = "Payload to create a new book")
    public record CreateRequest(

            @Schema(example = "Clean Code")
            @NotBlank(message = "Title is required")
            @Size(max = 255)
            String title,

            @Schema(example = "Robert C. Martin")
            @NotBlank(message = "Author is required")
            @Size(max = 255)
            String author,

            @Schema(example = "978-0132350884")
            @NotBlank(message = "ISBN is required")
            @ValidIsbn
            String isbn,

            @Schema(example = "TECHNOLOGY")
            @NotNull(message = "Genre is required")
            Genre genre,

            @Schema(example = "35.99")
            @NotNull(message = "Price is required")
            @DecimalMin(value = "0.00", message = "Price must be non-negative")
            @Digits(integer = 8, fraction = 2)
            BigDecimal price,

            @Schema(example = "2008-08-01")
            @NotNull(message = "Published date is required")
            @PastOrPresent(message = "Published date cannot be in the future")
            LocalDate publishedAt,

            @Size(max = 2000)
            String description

    ) {
    }

    @Schema(description = "Payload to fully replace an existing book. All fields required.")
    public record UpdateRequest(

            @NotBlank(message = "Title is required")
            @Size(max = 255)
            String title,

            @NotBlank(message = "Author is required")
            @Size(max = 255)
            String author,

            @NotBlank(message = "ISBN is required")
            @ValidIsbn
            String isbn,

            @NotNull(message = "Genre is required")
            Genre genre,

            @NotNull(message = "Price is required")
            @DecimalMin(value = "0.00")
            @Digits(integer = 8, fraction = 2)
            BigDecimal price,

            @NotNull(message = "Published date is required")
            @PastOrPresent
            LocalDate publishedAt,

            @Size(max = 2000)
            String description,

            @Schema(description = "Current version for optimistic concurrency control", example = "0")
            @NotNull(message = "Version is required for optimistic locking")
            Long version

    ) {
    }

    @Schema(description = "Book resource representation")
    public record BookResponse(
            UUID id,
            String title,
            String author,
            String isbn,
            Genre genre,
            BigDecimal price,
            LocalDate publishedAt,
            String description,
            @Schema(description = "Version counter — include in PUT requests to prevent concurrent overwrites")
            Long version,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

}