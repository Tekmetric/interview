package com.interview.service;

import com.interview.persistence.entity.CreditApplication;

public record ApplicationUnderReviewEvent(CreditApplication application) {}
