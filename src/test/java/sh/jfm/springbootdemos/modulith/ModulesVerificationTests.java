package sh.jfm.springbootdemos.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/// Test class to verify modulith architecture boundaries
class ModulesVerificationTests {

    /// This test ensures that module dependencies and interactions
    /// comply with the defined architectural rules.
    @Test
    void verifiesModuleBoundaries() {
        ApplicationModules.of(LibraryApplication.class).verify();
    }
}
