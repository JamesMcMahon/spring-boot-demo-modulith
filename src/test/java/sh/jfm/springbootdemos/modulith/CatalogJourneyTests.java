package sh.jfm.springbootdemos.modulith;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import sh.jfm.springbootdemos.modulith.data.BookRepository;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.assertj.core.api.Assertions.assertThat;

/// Full-stack journey tests – boots the entire Spring Boot app on a random port
/// and exercises it via real HTTP.
/// Edge cases are moved to narrower tests to keep the scope focused.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogJourneyTests {

    @Autowired
    BookRepository repo;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        repo.deleteAll();
    }

    @Test
    void createAndGetRoundTrip() {
        final String isbn = "9780132350884";

        final String originalJson = """
                {"isbn":"%s","title":"Clean Code","author":"Robert C. Martin"}
                """.formatted(isbn);

        // POST  ────────────────────────────────────────────────
        given()
                .contentType(ContentType.JSON)
                .body(originalJson)
                .when()
                .post("/catalog/books")
                .then()
                .statusCode(201);

        assertThat(repo.count()).isEqualTo(1);

        // GET (initial) ───────────────────────────────────────
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/catalog/books/{isbn}", isbn)
                .then()
                .statusCode(200)
                .body(jsonEquals(originalJson));
    }

    @Test
    void patchThenGetUpdatesBook() {
        final String isbn = "9780132350884";

        // insert original book via API
        final String originalJson = """
                {"isbn":"%s","title":"Clean Code","author":"Robert C. Martin"}
                """.formatted(isbn);

        given()
                .contentType(ContentType.JSON)
                .body(originalJson)
                .when()
                .post("/catalog/books")
                .then()
                .statusCode(201);

        assertThat(repo.count()).isEqualTo(1);

        // update via PATCH
        final String revisedJson = """
                {"isbn":"%s","title":"Cleaner Code","author":"Bob Martin"}
                """.formatted(isbn);

        given()
                .contentType(ContentType.JSON)
                .body(revisedJson)
                .when()
                .patch("/catalog/books/{isbn}", isbn)
                .then()
                .statusCode(204);

        assertThat(repo.count()).isEqualTo(1);

        // GET should now return the revised JSON
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/catalog/books/{isbn}", isbn)
                .then()
                .statusCode(200)
                .body(jsonEquals(revisedJson));
    }
}
