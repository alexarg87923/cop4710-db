-- Commands to be executed directly in PostgreSQL (these cannot be run from JDBC):

-- Create the database (run this part directly in your PostgreSQL admin tool or psql)
-- CREATE DATABASE lim;

-- Create a user and grant privileges
-- CREATE USER root WITH ENCRYPTED PASSWORD 'password';
-- GRANT ALL PRIVILEGES ON DATABASE lim TO root;

-- SQL script content to be executed from Java JDBC:

CREATE TABLE IF NOT EXISTS Member (
    MemberID SERIAL PRIMARY KEY,
    RegisterDate DATE
);

CREATE TABLE IF NOT EXISTS Author (
    AuthorID SERIAL PRIMARY KEY,
    AuthorName VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Genre (
    GenreID SERIAL PRIMARY KEY,
    GenreName VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Employee (
    EmployeeID SERIAL PRIMARY KEY,
    Position VARCHAR(255),
    Salary FLOAT
);

CREATE TABLE IF NOT EXISTS Books (
    BookID SERIAL PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    AuthorID INT REFERENCES Author(AuthorID),
    GenreID INT REFERENCES Genre(GenreID),
    BookYear INT,
    Quantity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS "User" (
    UserID SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Address VARCHAR(255),
    Phone VARCHAR(20),
    Email VARCHAR(255),
    Password VARCHAR(255),
    EmployeeID INT REFERENCES Employee(EmployeeID),
    MemberID INT REFERENCES Member(MemberID)
);

CREATE TABLE IF NOT EXISTS BookLoans (
    LoanID SERIAL PRIMARY KEY,
    BookID INT REFERENCES Books(BookID),
    MemberID INT REFERENCES Member(MemberID),
    LoanDate DATE,
    ReturnDate DATE,
    DueDate DATE,
    BookReturn CHAR(1),
    LoanPrice INT
);


INSERT INTO Member (RegisterDate) VALUES ('2022-01-10');
INSERT INTO Member (RegisterDate) VALUES ('2022-01-15');
INSERT INTO Member (RegisterDate) VALUES ('2022-02-01');
INSERT INTO Member (RegisterDate) VALUES ('2022-03-05');
INSERT INTO Member (RegisterDate) VALUES ('2022-04-12');
INSERT INTO Member (RegisterDate) VALUES ('2022-05-20');

INSERT INTO Author (AuthorName) VALUES ('J.K. Rowling');
INSERT INTO Author (AuthorName) VALUES ('George R.R. Martin');
INSERT INTO Author (AuthorName) VALUES ('J.R.R. Tolkien');
INSERT INTO Author (AuthorName) VALUES ('Neil Gaiman');
INSERT INTO Author (AuthorName) VALUES ('Stephen King');
INSERT INTO Author (AuthorName) VALUES ('Agatha Christie');

INSERT INTO Genre (GenreName) VALUES ('Fantasy');
INSERT INTO Genre (GenreName) VALUES ('Science Fiction');
INSERT INTO Genre (GenreName) VALUES ('Non-Fiction');
INSERT INTO Genre (GenreName) VALUES ('Mystery');
INSERT INTO Genre (GenreName) VALUES ('Thriller');
INSERT INTO Genre (GenreName) VALUES ('Historical Fiction');

INSERT INTO Employee (Position, Salary) VALUES ('Librarian', 55000.00);
INSERT INTO Employee (Position, Salary) VALUES ('Assistant Librarian', 40000.00);
INSERT INTO Employee (Position, Salary) VALUES ('Archivist', 45000.00);
INSERT INTO Employee (Position, Salary) VALUES ('Book Keeper', 35000.00);
INSERT INTO Employee (Position, Salary) VALUES ('Curator', 47000.00);
INSERT INTO Employee (Position, Salary) VALUES ('Security', 30000.00);

INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity) VALUES ('Harry Potter and the Sorcerer''s Stone', 1, 1, 1997, 3);
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity) VALUES ('A Game of Thrones', 2, 1, 1996, 5);
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity) VALUES ('The Hobbit', 3, 1, 1937, 4);
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity) VALUES ('American Gods', 4, 2, 2001, 2);
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity) VALUES ('The Shining', 5, 2, 1977, 4);
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity) VALUES ('Murder on the Orient Express', 6, 1, 1934, 3);

INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID, MemberID) VALUES ('John Doe', '123 Elm St', '555-1234', 'john.doe@email.com', 'password123', NULL, 1);
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID, MemberID) VALUES ('Jane Smith', '456 Oak St', '555-5678', 'jane.smith@email.com', 'password123', NULL, 2);
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID) VALUES ('Bob Johnson', '789 Pine St', '555-9012', 'bob.johnson@email.com', 'password123', 1);
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID, MemberID) VALUES ('Alice Johnson', '321 Maple St', '555-2345', 'alice.johnson@email.com', 'securePass123', NULL, 3);
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID, MemberID) VALUES ('Carl Kent', '654 Willow St', '555-7890', 'carl.kent@email.com', 'securePass123', NULL, 4);
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID) VALUES ('Diana Prince', '987 Cedar St', '555-4567', 'diana.prince@email.com', 'securePass123', 2);

INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice) VALUES (1, 1, '2022-03-01', NULL, '2022-03-15', 'N', 15);
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice) VALUES (2, 2, '2022-03-05', NULL, '2022-03-20', 'N', 15);
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice) VALUES (3, 1, '2022-03-10', NULL, '2022-03-25', 'N', 20);
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice) VALUES (4, 3, '2022-05-01', NULL, '2022-05-15', 'N', 10);
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice) VALUES (5, 4, '2022-05-03', '2022-05-18', '2022-05-17', 'Y', 12);
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice) VALUES (6, 3, '2022-06-01', NULL, '2022-06-15', 'N', 8);

