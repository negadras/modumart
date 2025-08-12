package com.negadras.modumart;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Tests to verify the modular architecture of the Modumart application.
 * These tests ensure proper module boundaries and dependencies.
 */
class ModulithArchitectureTests {

    ApplicationModules modules = ApplicationModules.of(ModumartApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }

    @Test
    void writeModulithDocumentation() {
        new Documenter(modules)
                .writeModuleCanvases();
    }
}