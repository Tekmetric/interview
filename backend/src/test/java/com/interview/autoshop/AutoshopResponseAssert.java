package com.interview.autoshop;

import com.interview.autoshop.dto.AutoshopResponse;
import java.util.Objects;
import org.assertj.core.api.AbstractAssert;

public class AutoshopResponseAssert
        extends AbstractAssert<AutoshopResponseAssert, AutoshopResponse> {

    private AutoshopResponseAssert(AutoshopResponse actual) {
        super(actual, AutoshopResponseAssert.class);
    }

    public static AutoshopResponseAssert assertThatResponse(AutoshopResponse actual) {
        return new AutoshopResponseAssert(actual);
    }

    public AutoshopResponseAssert hasId() {
        isNotNull();
        if (actual.getId() == null) {
            failWithMessage("Expected id to be set but was null");
        }
        return this;
    }

    public AutoshopResponseAssert hasName(String expected) {
        isNotNull();
        if (!Objects.equals(actual.getName(), expected)) {
            failWithMessage("Expected name <%s> but was <%s>", expected, actual.getName());
        }
        return this;
    }

    public AutoshopResponseAssert hasTimestamps() {
        isNotNull();
        if (actual.getCreatedAt() == null || actual.getUpdatedAt() == null) {
            failWithMessage("Expected createdAt and updatedAt to be populated");
        }
        return this;
    }
}
