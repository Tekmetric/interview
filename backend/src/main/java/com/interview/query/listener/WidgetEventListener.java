package com.interview.query.listener;

import com.interview.common.entity.Widget;
import com.interview.common.events.WidgetCreatedEvent;
import com.interview.common.events.WidgetDeletedEvent;
import com.interview.common.events.WidgetUpdatedEvent;
import com.interview.query.repository.WidgetQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WidgetEventListener {

    private static final Logger log = LoggerFactory.getLogger(WidgetEventListener.class);
    private final WidgetQueryRepository widgetQueryRepository;

    @PersistenceContext(unitName = "query")
    private EntityManager entityManager;

    @Autowired
    public WidgetEventListener(WidgetQueryRepository widgetQueryRepository) {
        this.widgetQueryRepository = widgetQueryRepository;
    }

    @ApplicationModuleListener
    @Transactional(value = "queryTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void on(WidgetCreatedEvent event) {
        log.info("Synchronizing created widget to query database: id={}", event.getId());

        entityManager.createNativeQuery(
            "INSERT INTO widgets (id, name, description) VALUES (?1, ?2, ?3)")
            .setParameter(1, event.getId())
            .setParameter(2, event.getName())
            .setParameter(3, event.getDescription())
            .executeUpdate();

        log.info("Widget created in query database: id={}", event.getId());
    }

    @ApplicationModuleListener
    public void on(WidgetUpdatedEvent event) {
        log.info("Synchronizing updated widget to query database: id={}", event.getId());

        widgetQueryRepository.findById(event.getId()).ifPresent(widget -> {
            widget.setName(event.getName());
            widget.setDescription(event.getDescription());
            widgetQueryRepository.save(widget);
            log.info("Widget updated in query database: id={}", event.getId());
        });
    }

    @ApplicationModuleListener
    public void on(WidgetDeletedEvent event) {
        log.info("Synchronizing deleted widget to query database: id={}", event.getId());

        widgetQueryRepository.deleteById(event.getId());

        log.info("Widget deleted from query database: id={}", event.getId());
    }
}
