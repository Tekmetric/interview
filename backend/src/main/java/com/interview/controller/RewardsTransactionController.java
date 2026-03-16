package com.interview.controller;

import com.interview.error.RequestValidationException;
import com.interview.model.RewardsTransactionSummary;
import com.interview.model.request.GetRewardsActivityRequest;
import com.interview.model.request.PostTransactionRequest;
import com.interview.service.RewardsTransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class RewardsTransactionController {

    private static final Logger logger = LoggerFactory.getLogger(RewardsTransactionController.class);
    private final RewardsTransactionService rewardsTransactionService;

    @Autowired
    public RewardsTransactionController(RewardsTransactionService rewardsTransactionService){
        this.rewardsTransactionService = rewardsTransactionService;
    }

    // TODO: update this to accept customer id as the input instead of rewards account
    @GetMapping("/{rewardsAccountId}/summary")
    public List<RewardsTransactionSummary> getRedemptionActivity(@Valid @ModelAttribute GetRewardsActivityRequest request, BindingResult bindingResult){
        logger.info("Received get last rewards transaction request: {}", request);
        if(bindingResult.hasErrors()){
            logger.error("GetLastRewardsTransactionRequest validation failed during parsing: {}", bindingResult);
            throw new RequestValidationException("Failed to parse GetRedemptionActivityRequest");
        }
        return rewardsTransactionService.getRewardsActivity(request);
    }

    @PostMapping
    public UUID postTransaction(@Valid @RequestBody PostTransactionRequest request, BindingResult bindingResult){
        logger.info("Received post transaction request: {}", request);
        if(bindingResult.hasErrors()){
            logger.error("PostTransactionRequest validation failed during parsing: {}", bindingResult);
            throw new RequestValidationException("Failed to parse PostTransactionRequest");
        }
        return rewardsTransactionService.postTransaction(request);
    }


}
