package com.interview;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithTest {

    ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void verifyModuleStructure() {
        // This will verify that all modules follow the architectural rules
        modules.verify();
    }

    @Test
    void createModuleDocumentation() throws Exception {
        // This creates documentation of the module structure
        new Documenter(modules)
            .writeDocumentation()
            .writeIndividualModulesAsPlantUml();
    }

    @Test
    void printModules() {
        // Print out the detected modules for verification
        modules.forEach(module -> {
            System.out.println("Module: " + module.getName());
            System.out.println("  Base package: " + module.getBasePackage());
            System.out.println("  Named interfaces: " + module.getNamedInterfaces());
            System.out.println();
        });
    }
}
