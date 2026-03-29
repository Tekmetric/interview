package com.interview.resource.service;

import com.interview.resource.dto.AutoShopRequest;
import com.interview.resource.dto.AutoShopResponse;
import com.interview.resource.entity.AutoShop;
import com.interview.resource.mapper.AutoShopMapper;
import com.interview.resource.repository.IAutoShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class AutoShopService implements IAutoShopService {

    private final IAutoShopRepository repository;
    private final AutoShopMapper mapper;

    public AutoShopService(IAutoShopRepository repository, AutoShopMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<AutoShopResponse> getAll() {
        List<AutoShop> autoShops = repository.findAll();
        return mapper.toListResponse(autoShops);
    }

    @Transactional(readOnly = true)
    public AutoShopResponse getById(Long id) {
        AutoShop autoShop = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AutoShop not found"));
        return mapper.toResponse(autoShop);
    }

    public AutoShopResponse create(AutoShopRequest autoShop) {
        AutoShop newAutoShop = repository.save(mapper.toEntity(autoShop));
        return mapper.toResponse(newAutoShop);
    }

    public AutoShopResponse update(Long id, AutoShopRequest autoShop) {
        AutoShop currentShop = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AutoShop not found"));
        currentShop.setName(autoShop.getName());
        currentShop.setAddress(autoShop.getAddress());
        currentShop.setCity(autoShop.getCity());
        currentShop.setState(autoShop.getState());
        currentShop.setContractYears(autoShop.getContractYears());
        AutoShop autoShop1 = repository.save(currentShop);
        return mapper.toResponse(autoShop1);
    }

    public void delete(Long id) {
        AutoShop currentShop = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AutoShop not found"));
        repository.delete(currentShop);
    }

}
