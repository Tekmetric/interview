package com.interview.model.audit;

import com.interview.model.Mechanic;
import com.interview.model.audit.util.BeanUtil;
import com.interview.repository.MechanicHistoryRepository;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class MechanicHistoryEntityBuilder {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void perform(Mechanic mechanic, Action action) {
        MechanicHistoryRepository repository = BeanUtil.getBean(MechanicHistoryRepository.class);
        MechanicHistory mechanicHistory = extractHistory(mechanic);
        enrichWithAuditInformation(mechanicHistory, action);
        repository.save(mechanicHistory);
    }

    private MechanicHistory extractHistory(Mechanic mechanic) {
        return MechanicHistory.builder()
                .mechanicId(mechanic.getId())
                .firstName(mechanic.getFirstName())
                .lastName(mechanic.getLastName())
                .email(mechanic.getEmail())
                .phoneNumber(mechanic.getPhoneNumber())
                .role(mechanic.getRole())
                .mechanicShopId(mechanic.getMechanicShop().getId())
                .build();
    }

    private void enrichWithAuditInformation(MechanicHistory mechanicHistory, Action action) {
        mechanicHistory.setAction(action);
        mechanicHistory.setCreationDate(LocalDateTime.now());
        mechanicHistory.setModifiedBy("current user");

    }
}
