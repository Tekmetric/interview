package com.interview.resource;

import com.interview.dto.CustomerDto;
import com.interview.dto.CustomerIdentificationDto;
import com.interview.dto.CustomerSortDto;
import com.interview.mapper.CustomerMapper;
import com.interview.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerResource {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;


    @GetMapping("/{id}")
    public CustomerDto getCustomerById(@PathVariable Long id) {
        return customerMapper.modelToDto(customerService.getCustomer(id));
    }

    @PostMapping
    public CustomerDto addCustomer(@RequestBody CustomerDto customer) {
        var customerModel = customerMapper.dtoToModel(customer);
        customerService.addCustomer(customerModel);
        return customerMapper.modelToDto(customerService.addCustomer(customerModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeCustomer(@PathVariable Long id) {
        customerService.removeCustomer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping(("/{id}"))
    public CustomerDto updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customer) {
        var customerModel = customerMapper.dtoToModel(customer);
        return customerMapper.modelToDto(customerService.updateCustomer(customerModel));
    }

    @GetMapping
    public Page<CustomerIdentificationDto> listCustomers(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int sizePerPage,
                                                         @RequestParam(defaultValue = "ID") CustomerSortDto sortField,
                                                         @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        Pageable pageable = PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName());
        return customerService.findAllByPage(pageable).map(customerMapper::modelToIdentificationDto);
    }


}
