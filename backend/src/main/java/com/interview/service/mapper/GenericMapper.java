package com.interview.service.mapper;

public interface GenericMapper <T, U, V> {

    public T mapToEntity(U u);
    public U mapEntityToDTO(T t);
    public V mapEntityToPresentationDTO(T t);

}