package sh.jfm.springbootdemos.modulith;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import sh.jfm.springbootdemos.modulith.catalog.BookRepository;
import sh.jfm.springbootdemos.modulith.inventory.CopyRepository;
import sh.jfm.springbootdemos.modulith.lending.PatronRepository;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.Matchers.equalTo;

@SuppressWarnings("SameParameterValue")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryJourneyTests {

    @Autowired
    private BookRepository books;
    @Autowired
    private CopyRepository copies;
    @Autowired
    private PatronRepository patrons;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        copies.deleteAll();
        books.deleteAll();
        patrons.deleteAll();
    }

    @Test
    void fullLibraryHappyPathJourney() {
        final var tddByExampleIsbn = "9780321146533";

        publisherAddsBookToCatalog(tddByExampleIsbn, "TDD by Example", "Kent Beck");
        librarianSeesBookDetails(tddByExampleIsbn, "TDD by Example", "Kent Beck");

        publisherUpdatesBookDetails(tddByExampleIsbn, "Test-Driven Development by Example", "Kent Beck");
        librarianSeesBookDetails(tddByExampleIsbn, "Test-Driven Development by Example", "Kent Beck");

        var copyId = librarianRegistersCopy(tddByExampleIsbn, "LOC-1");
        systemShowsAvailableCopies(tddByExampleIsbn, 1);

        var patronId = systemCreatesAPatron();
        patronBorrowsBook(patronId, tddByExampleIsbn, copyId);
        systemShowsAvailableCopies(tddByExampleIsbn, 0);

        patronReturnsBook(patronId, tddByExampleIsbn);
        systemShowsAvailableCopies(tddByExampleIsbn, 1);
    }

    private void publisherAddsBookToCatalog(String isbn, String title, String author) {
        given().contentType(ContentType.JSON)
                .body("""
                        {"isbn":"%s","title":"%s","author":"%s"}
                        """.formatted(isbn, title, author))
                .when().post("/catalog/books")
                .then().statusCode(201);
    }

    private void librarianSeesBookDetails(String isbn, String title, String author) {
        given().accept(ContentType.JSON)
                .when().get("/catalog/books/{isbn}", isbn)
                .then().statusCode(200)
                .body(jsonEquals("""
                        {"isbn":"%s","title":"%s","author":"%s"}
                        """.formatted(isbn, title, author)));
    }

    private void publisherUpdatesBookDetails(String isbn, String title, String author) {
        given().contentType(ContentType.JSON)
                .body("""
                        {"isbn":"%s","title":"%s","author":"%s"}
                        """.formatted(isbn, title, author))
                .when().patch("/catalog/books/{isbn}", isbn)
                .then().statusCode(204);
    }

    private long librarianRegistersCopy(String isbn, String location) {
        return given().contentType(ContentType.JSON)
                .body("""
                        {"isbn":"%s","location":"%s"}
                        """.formatted(isbn, location))
                .when().post("/inventory/copies")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    private void systemShowsAvailableCopies(String isbn, int expectedAvailable) {
        given().accept(ContentType.JSON)
                .when().get("/inventory/books/{isbn}/availability", isbn)
                .then().statusCode(200)
                .body("available", equalTo(expectedAvailable));
    }

    private long systemCreatesAPatron() {
        return given()
                .contentType(ContentType.JSON)
                .when().post("/lending/patrons")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    private void patronBorrowsBook(long patronId, String isbn, long expectedCopyId) {
        given().contentType(ContentType.JSON)
                .body("""
                        {"patronId":%d,"isbn":"%s"}
                        """.formatted(patronId, isbn))
                .when().post("/lending/loans")
                .then().statusCode(201)
                .body("isbn", equalTo(isbn))
                .body("copyId", equalTo((int) expectedCopyId));
    }

    private void patronReturnsBook(long patronId, String isbn) {
        given().contentType(ContentType.JSON)
                .body("""
                        {"patronId":%d,"isbn":"%s"}
                        """.formatted(patronId, isbn))
                .when().post("/lending/returns")
                .then().statusCode(204);
    }
}
