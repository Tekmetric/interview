package com.interview.service.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
@RequiredArgsConstructor
public class ModelMapperProvider {

    private final ApplicationContext context;

    private final Map<Class<?>, ModelMapper<?, ?>> MODEL_MAPPERS_BY_IDENTIFIERS = new ConcurrentHashMap<>();

    public <InputRecord, DestinationModel> void mapTo(InputRecord inputRecord, DestinationModel destinationModel) {
        Class<?> destinationClass = destinationModel instanceof HibernateProxy ? destinationModel.getClass().getSuperclass() : destinationModel.getClass();

        ModelMapper<InputRecord, DestinationModel> modelMapper = retrieveMapper(inputRecord, (Class<DestinationModel>) destinationClass);

        modelMapper.mapTo(inputRecord, destinationModel);
    }

    public <InputRecord, DestinationModel> DestinationModel mapTo(InputRecord inputRecord, Class<DestinationModel> destinationDtoClass) {
        ModelMapper<InputRecord, DestinationModel> modelMapper = retrieveMapper(inputRecord, destinationDtoClass);

        return modelMapper.mapTo(inputRecord);
    }

    @SuppressWarnings("unchecked")
    private <SourceDto, DestinationDto> ModelMapper<SourceDto, DestinationDto> retrieveMapper(SourceDto sourceModel,
                                                                                                                   Class<DestinationDto> destinationDtoClass) {
        Class<?> sourceClass = sourceModel instanceof HibernateProxy ? sourceModel.getClass().getSuperclass() : sourceModel.getClass();

        ModelMapper<?, ?> modelMapper = MODEL_MAPPERS_BY_IDENTIFIERS.computeIfAbsent(sourceClass, this::loadBusinessMapper);

        if (Objects.isNull(modelMapper)) {
            throw new IllegalArgumentException(String.format("The DtoMapper doesn't have a register pair for <%s,%s> types", sourceModel.getClass(), destinationDtoClass));
        }
        return (ModelMapper<SourceDto, DestinationDto>) modelMapper;
    }

    @SuppressWarnings("rawtypes")
    private ModelMapper<?, ?> loadBusinessMapper(Class<?> sourceModelClass) {
        ModelMapper<?, ?> businessMapper = null;

        Map<String, ModelMapper> validatorMap = context.getBeansOfType(ModelMapper.class);
        if (MapUtils.isNotEmpty(validatorMap)) {
            //Check for multiple question:
            for (ModelMapper<?, ?> modelMapper : validatorMap.values()) {
                if (modelMapper.supports(sourceModelClass)) {
                    businessMapper = modelMapper;
                    break;
                }
            }
        }

        return businessMapper;
    }
}
