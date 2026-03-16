package com.interview.controller;

import com.interview.error.RequestValidationException;
import com.interview.model.request.EnrollCustomerRequest;
import com.interview.model.request.GetCustomerRewardsAccountRequest;
import com.interview.model.request.UnenrollCustomerRequest;
import com.interview.service.RewardsEnrollmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/rewards")
public class RewardsEnrollmentController {

    private static final Logger logger = LoggerFactory.getLogger(RewardsEnrollmentController.class);
    private final RewardsEnrollmentService rewardsEnrollmentService;

    @Autowired
    public RewardsEnrollmentController(RewardsEnrollmentService rewardsEnrollmentService){
        this.rewardsEnrollmentService = rewardsEnrollmentService;
    }

    @GetMapping("/{customerId}")
    public String getCustomerRewardsAccount(@Valid @ModelAttribute GetCustomerRewardsAccountRequest request, BindingResult bindingResult) {
        logger.info("Received get customer rewards account request: {}", request);
        if(bindingResult.hasErrors()){
            logger.error("GetCustomerRewardsAccountRequest validation failed during parsing: {}", bindingResult);
            throw new RequestValidationException("Failed to parse GetCustomerRewardsAccountRequest");
        }
        return  rewardsEnrollmentService.getCustomerRewardsAccount(request);
    }

    @PostMapping(path = {"/{customerId}/enroll", "/{customerId}/enroll/{rewardsAccountId}"})
    public UUID enrollCustomer(@Valid @ModelAttribute EnrollCustomerRequest request, BindingResult bindingResult){
        logger.info("Received enrollment request: {}", request);
        if(bindingResult.hasErrors()){
            logger.error("EnrollCustomerRequest validation failed during parsing: {}", bindingResult);
            throw new RequestValidationException("Failed to parse EnrollCustomerRequest");
        }
        return rewardsEnrollmentService.enrollCustomer(request);
    }

    @DeleteMapping(path = "/{customerId}/unenroll")
    public UUID unenrollCustomer(@Valid @ModelAttribute UnenrollCustomerRequest request, BindingResult bindingResult){
        logger.info("Received unenroll request: {}", request);
        if(bindingResult.hasErrors()){
            logger.error("UnenrollCustomerRequest validation failed during parsing: {}", bindingResult);
            throw new RequestValidationException("Failed to parse UnenrollCustomerRequest");
        }
        return rewardsEnrollmentService.unenrollCustomer(request);
    }
}