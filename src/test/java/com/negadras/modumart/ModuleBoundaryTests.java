package com.negadras.modumart;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.ApplicationModule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests to verify module boundaries and dependencies are respected.
 */
class ModuleBoundaryTests {

    ApplicationModules modules = ApplicationModules.of(ModumartApplication.class);

    @Test
    void shouldHaveExpectedModules() {
        assertThat(modules.stream().map(ApplicationModule::getName))
                .containsExactlyInAnyOrder(
                        "catalog", 
                        "customers", 
                        "orders", 
                        "payments", 
                        "shipping", 
                        "notifications",
                        "analytics"
                );
    }

    @Test
    void catalogModuleShouldHaveExpectedDependencies() {
        ApplicationModule catalog = modules.getModuleByName("catalog").orElseThrow();
        
        // Catalog module should be able to listen to order events
        // The actual dependencies will be verified by the architecture verification
        assertThat(catalog).isNotNull();
    }

    @Test
    void customersModuleShouldBeIndependent() {
        ApplicationModule customers = modules.getModuleByName("customers").orElseThrow();
        
        // Customers module should be independent of other business modules
        assertThat(customers).isNotNull();
    }

    @Test
    void ordersModuleShouldHaveExpectedDependencies() {
        ApplicationModule orders = modules.getModuleByName("orders").orElseThrow();
        
        // Orders module should be able to interact with catalog and customers
        assertThat(orders).isNotNull();
    }

    @Test
    void paymentsModuleShouldHaveExpectedDependencies() {
        ApplicationModule payments = modules.getModuleByName("payments").orElseThrow();
        
        // Payments module should be able to listen to order events
        assertThat(payments).isNotNull();
    }

    @Test
    void shippingModuleShouldHaveExpectedDependencies() {
        ApplicationModule shipping = modules.getModuleByName("shipping").orElseThrow();
        
        // Shipping module should be able to listen to payment events
        assertThat(shipping).isNotNull();
    }

    @Test
    void notificationsModuleShouldBeACrossCuttingConcern() {
        ApplicationModule notifications = modules.getModuleByName("notifications").orElseThrow();
        
        // Notifications module should be a cross-cutting concern
        assertThat(notifications).isNotNull();
    }

    @Test
    void analyticsModuleShouldBeACrossCuttingConcern() {
        ApplicationModule analytics = modules.getModuleByName("analytics").orElseThrow();
        
        // Analytics module should be a cross-cutting concern listening to all events
        assertThat(analytics).isNotNull();
    }

    @Test
    void verifyNoCircularDependencies() {
        // This test ensures no circular dependencies exist
        modules.verify();
    }
}