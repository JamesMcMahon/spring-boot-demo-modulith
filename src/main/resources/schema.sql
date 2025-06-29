CREATE TABLE "books"
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn   VARCHAR(20)  NOT NULL UNIQUE,
    title  VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
);

CREATE TABLE "copies"
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn      VARCHAR(20)  NOT NULL,
    location  VARCHAR(255) NOT NULL,
    available BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_copies_books FOREIGN KEY (isbn) REFERENCES "books" (isbn) ON DELETE CASCADE
);

CREATE TABLE "patrons"
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL
);

CREATE TABLE "loans"
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    copy_id   BIGINT      NOT NULL,
    isbn      VARCHAR(20) NOT NULL,
    patron_id BIGINT      NOT NULL,
    due_date  DATE        NOT NULL,
    CONSTRAINT fk_loans_copies FOREIGN KEY (copy_id) REFERENCES "copies" (id) ON DELETE CASCADE,
    CONSTRAINT fk_loans_patrons FOREIGN KEY (patron_id) REFERENCES "patrons" (id) ON DELETE CASCADE
);
