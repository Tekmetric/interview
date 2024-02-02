package com.interview.persistence;

import com.interview.mapper.CustomerMapper;
import com.interview.service.CustomerPersistence;
import com.interview.service.model.Customer;
import com.interview.service.model.CustomerIdentification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class CustomerPersistenceProvider implements CustomerPersistence {

    CustomerRepository customerRepository;
    CustomerMapper customerMapper;


    @Override
    @Transactional(readOnly = true)
    public Page<CustomerIdentification> findAllByPage(Pageable pageable) {
        return customerRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id).map( entity ->
                customerMapper.entityToModel(entity)
        ).or(Optional::empty);
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        var customerEntity = customerMapper.modelToEntity(customer);
        customerRepository.save(customerEntity);
        return customerMapper.entityToModel(customerRepository.save(customerEntity));
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }


    @Override
    @Transactional
    public Customer updateCustomer(Customer customer) {
        var updatedEntity = customerRepository.findById(customer.getId()).map( e ->
                customerRepository.save(customerMapper.modelToEntity(customer))
        );
        return customerMapper.entityToModel(updatedEntity.get());
    }


}
