package sh.jfm.springbootdemos.modulith.lending;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import sh.jfm.springbootdemos.modulith.catalog.Book;
import sh.jfm.springbootdemos.modulith.catalog.BookRepository;
import sh.jfm.springbootdemos.modulith.inventory.Copy;
import sh.jfm.springbootdemos.modulith.inventory.CopyRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LendingJourneyTests {

    @LocalServerPort
    private int port;

    @Autowired
    private BookRepository books;
    @Autowired
    private CopyRepository copies;
    @Autowired
    private PatronRepository patrons;

    private long patronId;

    @BeforeEach
    void setUp() {
        // wipe data left by other @SpringBootTest classes sharing the context
        copies.deleteAll();
        books.deleteAll();
        patrons.deleteAll();

        // Arrange a book, a copy and a patron using repositories
        books.save(new Book(null, "123", "Bone: The Great Cow Race", "Jeff Smith"));
        copies.save(new Copy(null, "123", "Boneville", true));
        patronId = patrons.save(new Patron(null)).id();

        RestAssured.port = port;
    }

    @Test
    void borrowThenReturnJourney() {
        final String isbn = "123";

        // borrow ───────────────────────────────────────────────
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"patronId":%d,"isbn":"%s"}
                        """.formatted(patronId, isbn))
                .when()
                .post("/lending/loans")
                .then()
                .statusCode(201)
                .body("isbn", equalTo(isbn));

        // active loans shows one entry ─────────────────────────
        given()
                .when()
                .get("/lending/patrons/{id}/loans", patronId)
                .then()
                .statusCode(200)
                .body("loans.size()", equalTo(1));

        // return ───────────────────────────────────────────────
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"patronId":%d,"isbn":"%s"}
                        """.formatted(patronId, isbn))
                .when()
                .post("/lending/returns")
                .then()
                .statusCode(204);

        // active loans now empty ───────────────────────────────
        given()
                .when()
                .get("/lending/patrons/{id}/loans", patronId)
                .then()
                .statusCode(200)
                .body("loans.size()", equalTo(0));
    }
}
