CREATE TABLE "books"
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn  VARCHAR(20)  NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
);

create table "copies"
(
    id       bigint generated always as identity primary key,
    isbn     varchar(20)  not null,
    location varchar(255) not null,
    constraint fk_copies_books foreign key (isbn) references "books" (isbn)
);
