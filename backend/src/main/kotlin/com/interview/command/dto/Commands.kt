package com.interview.command.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateWidgetCommand(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    val name: String = "",

    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String = ""
)

data class UpdateWidgetCommand(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    var name: String = "",

    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    var description: String = ""
)
