package com.interview.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class CleanArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.interview");
    }

    @Test
    void layeredArchitectureIsRespected() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Domain").definedBy("..domain..")
                .layer("Application").definedBy("..application..")
                .layer("Infrastructure").definedBy("..infrastructure..")
                .layer("Api").definedBy("..api..")
                .layer("Config").definedBy("..config..")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure", "Api", "Config")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Api", "Config")
                .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Config")
                .whereLayer("Api").mayOnlyBeAccessedByLayers("Config")
                .check(classes);
    }

    @Test
    void domainMustNotDependOnSpringOrJpa() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "javax.persistence..",
                        "jakarta.persistence..",
                        "org.hibernate.."
                );
        rule.check(classes);
    }

    @Test
    void applicationMustNotDependOnSpring() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "javax.persistence..",
                        "jakarta.persistence..",
                        "org.hibernate.."
                );
        rule.check(classes);
    }

    @Test
    void applicationMustNotDependOnInfrastructureOrApi() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure..",
                        "..api.."
                );
        rule.check(classes);
    }

    @Test
    void apiMustNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");
        rule.check(classes);
    }
}
