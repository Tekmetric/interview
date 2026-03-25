package com.interview.model.audit;

import com.interview.model.Mechanic;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import static com.interview.model.audit.Action.*;

public class MechanicEntityListener {

    private final MechanicHistoryEntityBuilder mechanicHistoryEntityBuilder;

    public MechanicEntityListener(MechanicHistoryEntityBuilder mechanicHistoryEntityBuilder) {
        this.mechanicHistoryEntityBuilder = mechanicHistoryEntityBuilder;
    }

    @PostPersist
    public void postPersist(Mechanic target) {
        mechanicHistoryEntityBuilder.perform(target, INSERTED);
    }

    @PostUpdate
    public void postUpdate(Mechanic target) {
        mechanicHistoryEntityBuilder.perform(target, UPDATED);
    }

    @PostRemove
    public void postRemove(Mechanic target) {
        mechanicHistoryEntityBuilder.perform(target, DELETED);
    }

}
