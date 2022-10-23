package com.interview.domain.service.common.mapper;

import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import com.interview.domain.model.common.AbstractAuditingEntity;
import com.interview.domain.service.common.AbstractCRUDService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@ExcludeFromLoggingAspect
public class DomainServiceMapper implements ApplicationListener<ContextRefreshedEvent> {
    private Map<Class<?>, AbstractCRUDService<?, ?, ?>> serviceMappers = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final Collection<AbstractCRUDService> services = event.getApplicationContext().getBeansOfType(AbstractCRUDService.class).values();
        for (AbstractCRUDService<?, ?, ?> crudService : services) {
            serviceMappers.putIfAbsent(crudService.getEntityClass(), crudService);
        }
    }

    public AbstractCRUDService<?, ?, ?> getAbstractCRUDService(Class<? extends AbstractAuditingEntity<?>> entityClass) {
        return serviceMappers.get(entityClass);
    }

}
