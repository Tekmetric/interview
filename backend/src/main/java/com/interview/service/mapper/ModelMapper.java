package com.interview.service.mapper;

/**
 * Contract for mapping from a source type to a destination type.
 *
 * @param <Source>      source (input) type
 * @param <Destination> destination (output) type
 */
public interface ModelMapper<Source, Destination> {

    /**
     * Indicates whether this mapper can handle the given source class.
     *
     * @param clazz source class to test
     * @return true if supported, false otherwise
     */
    boolean supports(Class<?> clazz);

    /**
     * Creates and returns a mapped destination instance from the given source.
     *
     * @param source source object (non-null)
     * @return mapped destination instance (non-null)
     */
    Destination mapTo(Source source);

    /**
     * Maps the given source into an existing destination instance.
     *
     * @param source       source object (non-null)
     * @param destination  destination to mutate (non-null)
     */
    void mapTo(Source source, Destination destination);
}
