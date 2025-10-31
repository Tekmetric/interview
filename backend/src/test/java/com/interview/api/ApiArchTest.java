package com.interview.api;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

public class ApiArchTest {
    @Test
    public void ApiParametersAreNoDbModels() {
        final JavaClasses importedClasses = new ClassFileImporter().importPackages("com.interview.api");

        final ArchRule rule = methods()
                .that()
                .areMetaAnnotatedWith(RequestMapping.class)
                .should(notUseDbModel());

        rule.check(importedClasses);
    }

    private ArchCondition<? super JavaMethod> notUseDbModel() {
        return new ArchCondition<>("have parameters that are not db models") {
            @Override
            public void check(JavaMethod javaMethod, ConditionEvents conditionEvents) {
                final Optional<Integer> dbModelParameterIndex = javaMethod.getParameters().stream()
                        .filter(t -> t.getType().getName().startsWith("com.interview.dao.model"))
                        .map(JavaParameter::getIndex)
                        .findAny();
                final String message = dbModelParameterIndex
                        .map(i -> "parameter " + i + " in " + javaMethod.getDescription() + " is a db model")
                        .orElse(null);
                conditionEvents.add(new SimpleConditionEvent(javaMethod, message == null, message));
            }
        };
    }
}
