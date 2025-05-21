-- Book table for storing book metadata and available copies
CREATE TABLE IF NOT EXISTS book (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    copies_available INT NOT NULL DEFAULT 0
);
