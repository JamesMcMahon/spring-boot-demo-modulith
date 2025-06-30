package sh.jfm.springbootdemos.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/// Generates Spring Modulith documentation.
class ModuleDocumentationTests {

    private static final ApplicationModules MODULES =
            ApplicationModules.of(LibraryApplication.class);

    @Test
    void generateDocumentation() {
        new Documenter(MODULES)
                .writeDocumentation();
    }
}
