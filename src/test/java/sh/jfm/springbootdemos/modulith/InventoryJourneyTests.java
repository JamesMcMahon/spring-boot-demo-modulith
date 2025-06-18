package sh.jfm.springbootdemos.modulith;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import sh.jfm.springbootdemos.modulith.data.BookRepository;
import sh.jfm.springbootdemos.modulith.data.CopyRepository;
import sh.jfm.springbootdemos.modulith.model.Book;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

/// Full-stack journey tests for Inventory (copies)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryJourneyTests {

    @Autowired
    BookRepository bookRepo;
    @Autowired
    CopyRepository copyRepo;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        copyRepo.deleteAll();
        bookRepo.deleteAll();
        // Insert a book for copy to reference
        bookRepo.save(new Book(
                "9781416928171", "Bunnicula", "Deborah and James Howe"
        ));
    }

    @Test
    void addCopyAndDeleteCopyJourney() {
        final String isbn = "9781416928171";
        final String location = "Main Library";

        // POST /inventory/copies
        var copyId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                                {"id":null,"isbn":"%s","location":"%s"}
                                """.formatted(isbn, location))
                        .when()
                        .post("/inventory/copies")
                        .then()
                        .statusCode(201)
                        .extract()
                        .header("Location")
                        .replaceAll(".*/", ""); // get id from Location header

        assertThat(copyRepo.count()).isEqualTo(1);

        // GET /inventory/books/{isbn}/availability → 1
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/inventory/books/{isbn}/availability", isbn)
                .then()
                .statusCode(200)
                .body("available", equalTo(1));

        // DELETE /inventory/copies/{id}
        given()
                .when()
                .delete("/inventory/copies/{id}", copyId)
                .then()
                .statusCode(204);

        assertThat(copyRepo.count()).isEqualTo(0);

        // GET again → 0
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/inventory/books/{isbn}/availability", isbn)
                .then()
                .statusCode(200)
                .body("available", equalTo(0));
    }

    @Test
    void toggleAvailabilityJourney() {
        final String isbn = "9781416979708";
        final String location = "Branch A";

        // ensure book exists
        bookRepo.save(new Book(isbn, "Return to Howliday Inn", "Deborah and James Howe"));

        // create copy – capture ID from the Location header
        String copyId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                                {"id":null,"isbn":"%s","location":"%s"}
                                """.formatted(isbn, location))
                        .when()
                        .post("/inventory/copies")
                        .then()
                        .statusCode(201)
                        .extract()
                        .header("Location")
                        .replaceAll(".*/", "");

        // availability is 1
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/inventory/books/{isbn}/availability", isbn)
                .then()
                .statusCode(200)
                .body("available", equalTo(1));

        // toggle to unavailable
        given()
                .contentType(ContentType.JSON)
                .body("{\"available\":false}")
                .when()
                .patch("/inventory/copies/{id}", copyId)
                .then()
                .statusCode(204);

        // availability is now 0
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/inventory/books/{isbn}/availability", isbn)
                .then()
                .statusCode(200)
                .body("available", equalTo(0));
    }
}
