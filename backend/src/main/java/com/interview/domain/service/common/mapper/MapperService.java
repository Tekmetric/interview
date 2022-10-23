package com.interview.domain.service.common.mapper;

import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import com.interview.application.rest.v1.common.PathConstants;
import com.interview.application.rest.v1.common.dto.AbstractAuditingDto;
import com.interview.domain.exception.ApplicationException;
import com.interview.domain.exception.ErrorCode;
import com.interview.domain.exception.ErrorDetail;
import com.interview.domain.model.common.AbstractAuditingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.lang.reflect.ParameterizedType;
import java.util.*;

@Component
@ExcludeFromLoggingAspect
public class MapperService implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(MapperService.class);
    private final Map<Class<?>, Mapper<?, ?>> availableMappers = new HashMap<>();
    private final Map<Class<? extends AbstractAuditingDto<?>>, Class<? extends AbstractAuditingEntity<?>>> dtoToEntityMap = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Registers all available mappers
        final Collection<Mapper> mappers = event.getApplicationContext().getBeansOfType(Mapper.class).values();
        final List<Class<?>> domainClasses = getModelClasses();
        for (final Class<?> domainClass : domainClasses) {
            mappers.stream().filter(mapper -> match(mapper, domainClass)).forEach(mapper -> availableMappers.put(domainClass, mapper));
        }
    }

    public Mapper getMapperService(Class<?> entityClass) {
        return availableMappers.get(entityClass);
    }

    private List<Class<?>> getModelClasses() {
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            List<Class<?>> candidates = new ArrayList<>();
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage() + PathConstants.SEPARATOR + "**/*.class";
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                }
            }
            return candidates;
        } catch (Exception e) {
            LOG.error("Error appear while initializing the mapper", e);
            throw new ApplicationException(new ErrorDetail(ErrorCode.INTERNAL_ERROR));
        }
    }

    private String resolveBasePackage() {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders("com.interview.domain.model"));
    }

    /**
     * Matches the mapper to the domain object class based on their names.
     */
    @SuppressWarnings("unchecked")
    private boolean match(Mapper<?, ?> mapper, Class<?> modelClass) {
        if (!StringUtils.isEmpty(modelClass.getSimpleName())) {
            ParameterizedType mapperInterface;
            try {
                mapperInterface = (ParameterizedType) ((Class<?>) mapper.getClass().getGenericInterfaces()[0]).getGenericInterfaces()[0];
            } catch (Exception ex) {
                mapperInterface = (ParameterizedType) ((Class<?>) mapper.getClass().getSuperclass().getGenericInterfaces()[0]).getGenericInterfaces()[0];
            }
            Class mapperModelClass = (Class) mapperInterface.getActualTypeArguments()[0];
            Class<? extends AbstractAuditingDto<?>> dtoModelClass = (Class<? extends AbstractAuditingDto<?>>) mapperInterface.getActualTypeArguments()[1];
            dtoToEntityMap.putIfAbsent(dtoModelClass, (Class<? extends AbstractAuditingEntity<?>>) mapperModelClass);
            return mapperModelClass.getName().equals(modelClass.getName());
        } else {
            return false;
        }
    }
}
