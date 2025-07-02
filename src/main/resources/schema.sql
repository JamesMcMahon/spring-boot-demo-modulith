CREATE TABLE "books"
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn   VARCHAR(20)  NOT NULL UNIQUE,
    title  VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
);

-- This is a duplicate table to capture add books events from the catalog domain.
-- This is pointless if the app were to remain a single database.
-- With the goal of evolving into multiple databases, this allows use to decouple the Inventory database from
-- the Catalog database
CREATE TABLE "available_isbns"
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE "copies"
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn      VARCHAR(20)  NOT NULL,
    location  VARCHAR(255) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_copies_available_isbns FOREIGN KEY (isbn) REFERENCES "available_isbns" (isbn) ON DELETE CASCADE
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
    CONSTRAINT fk_loans_patrons FOREIGN KEY (patron_id) REFERENCES "patrons" (id) ON DELETE CASCADE
);
