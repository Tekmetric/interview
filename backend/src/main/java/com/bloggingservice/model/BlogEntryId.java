package com.bloggingservice.model;

import java.io.Serializable;
import java.util.UUID;

public record BlogEntryId(UUID id, String author) implements Serializable {}