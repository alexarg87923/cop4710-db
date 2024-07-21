-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------
-- Create the database (run this part directly in your PostgreSQL admin tool or psql)
-- CREATE DATABASE lim;
-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------


-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------
-- Creating roles for members and employees
-- CREATE ROLE member_role NOINHERIT LOGIN PASSWORD 'member123';
-- CREATE ROLE employee_role NOINHERIT LOGIN PASSWORD 'employee123';
-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------



-- SQL script content to be executed from Java JDBC:
-- Create Author and Genre tables

DROP TABLE "User" cascade;
DROP TABLE author cascade;
DROP TABLE bookloans cascade;
DROP TABLE books cascade;
DROP TABLE employee cascade;
DROP TABLE genre cascade;
DROP TABLE member cascade;

CREATE TABLE IF NOT EXISTS Author (
    AuthorID SERIAL PRIMARY KEY,
    AuthorName VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Genre (
    GenreID SERIAL PRIMARY KEY,
    GenreName VARCHAR(255) NOT NULL
);

-- Create Books table
CREATE TABLE IF NOT EXISTS Books (
    BookID SERIAL PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    AuthorID INT REFERENCES Author(AuthorID) ON DELETE SET NULL,
    GenreID INT REFERENCES Genre(GenreID) ON DELETE SET NULL,
    BookYear INT,
    Quantity INT NOT NULL
);

-- Create User table without foreign key constraints
CREATE TABLE IF NOT EXISTS "User" (
    UserID SERIAL PRIMARY KEY,
    Email VARCHAR(255) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Name VARCHAR(255) NOT NULL,
    Phone VARCHAR(50),
    Address VARCHAR(255),
    MemberID INT,
    EmployeeID INT
);

-- Create Member and Employee tables
CREATE TABLE IF NOT EXISTS Member (
    MemberID INT PRIMARY KEY,
    RegisterDate DATE DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS Employee (
    EmployeeID INT PRIMARY KEY,
    Position VARCHAR(100),
    Salary DECIMAL(10, 2)
);

-- Alter User table to add foreign key constraints
ALTER TABLE "User"
ADD CONSTRAINT fk_member FOREIGN KEY (MemberID) REFERENCES Member(MemberID),
ADD CONSTRAINT fk_employee FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID);

-- Create BookLoans table
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

-- Authors
INSERT INTO Author (AuthorName)
SELECT 'J.K. Rowling' WHERE NOT EXISTS (SELECT 1 FROM Author WHERE AuthorName = 'J.K. Rowling');
INSERT INTO Author (AuthorName)
SELECT 'George R.R. Martin' WHERE NOT EXISTS (SELECT 1 FROM Author WHERE AuthorName = 'George R.R. Martin');
INSERT INTO Author (AuthorName)
SELECT 'J.R.R. Tolkien' WHERE NOT EXISTS (SELECT 1 FROM Author WHERE AuthorName = 'J.R.R. Tolkien');
INSERT INTO Author (AuthorName)
SELECT 'Neil Gaiman' WHERE NOT EXISTS (SELECT 1 FROM Author WHERE AuthorName = 'Neil Gaiman');
INSERT INTO Author (AuthorName)
SELECT 'Stephen King' WHERE NOT EXISTS (SELECT 1 FROM Author WHERE AuthorName = 'Stephen King');
INSERT INTO Author (AuthorName)
SELECT 'Agatha Christie' WHERE NOT EXISTS (SELECT 1 FROM Author WHERE AuthorName = 'Agatha Christie');

-- Genres
INSERT INTO Genre (GenreName)
SELECT 'Fantasy' WHERE NOT EXISTS (SELECT 1 FROM Genre WHERE GenreName = 'Fantasy');
INSERT INTO Genre (GenreName)
SELECT 'Science Fiction' WHERE NOT EXISTS (SELECT 1 FROM Genre WHERE GenreName = 'Science Fiction');
INSERT INTO Genre (GenreName)
SELECT 'Non-Fiction' WHERE NOT EXISTS (SELECT 1 FROM Genre WHERE GenreName = 'Non-Fiction');
INSERT INTO Genre (GenreName)
SELECT 'Mystery' WHERE NOT EXISTS (SELECT 1 FROM Genre WHERE GenreName = 'Mystery');
INSERT INTO Genre (GenreName)
SELECT 'Thriller' WHERE NOT EXISTS (SELECT 1 FROM Genre WHERE GenreName = 'Thriller');
INSERT INTO Genre (GenreName)
SELECT 'Historical Fiction' WHERE NOT EXISTS (SELECT 1 FROM Genre WHERE GenreName = 'Historical Fiction');

-- Members
INSERT INTO Member (MemberID)
SELECT 1 WHERE NOT EXISTS (SELECT 1 FROM Member WHERE MemberID = 1);
INSERT INTO Member (MemberID)
SELECT 2 WHERE NOT EXISTS (SELECT 1 FROM Member WHERE MemberID = 2);
INSERT INTO Member (MemberID)
SELECT 3 WHERE NOT EXISTS (SELECT 1 FROM Member WHERE MemberID = 3);
INSERT INTO Member (MemberID)
SELECT 4 WHERE NOT EXISTS (SELECT 1 FROM Member WHERE MemberID = 4);

-- Employees
INSERT INTO Employee (EmployeeID, Position, Salary)
SELECT 5, 'Librarian', 55000.00 WHERE NOT EXISTS (SELECT 1 FROM Employee WHERE EmployeeID = 5);
INSERT INTO Employee (EmployeeID, Position, Salary)
SELECT 6, 'Assistant Librarian', 40000.00 WHERE NOT EXISTS (SELECT 1 FROM Employee WHERE EmployeeID = 6);

-- Users
INSERT INTO "User" (Name, Address, Phone, Email, Password, MemberID)
SELECT 'John Doe', '123 Elm St', '555-1234', 'john.doe@email.com', 'password123', 1 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'john.doe@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, MemberID)
SELECT 'Jane Smith', '456 Oak St', '555-5678', 'jane.smith@email.com', 'password123', 2 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'jane.smith@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, MemberID)
SELECT 'Alice Johnson', '321 Maple St', '555-2345', 'alice.johnson@email.com', 'securePass123', 3 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'alice.johnson@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, MemberID)
SELECT 'Carl Kent', '654 Willow St', '555-7890', 'carl.kent@email.com', 'securePass123', 4 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'carl.kent@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID)
SELECT 'Bob Johnson', '789 Pine St', '555-9012', 'bob.johnson@email.com', 'password123', 5 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'bob.johnson@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID)
SELECT 'Diana Prince', '987 Cedar St', '555-4567', 'diana.prince@email.com', 'securePass123', 6 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'diana.prince@email.com');

-- Books
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'Harry Potter and the Sorcerer''s Stone', (SELECT AuthorID FROM Author WHERE AuthorName = 'J.K. Rowling'), (SELECT GenreID FROM Genre WHERE GenreName = 'Fantasy'), 1997, 3
WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'Harry Potter and the Sorcerer''s Stone');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'A Game of Thrones', (SELECT AuthorID FROM Author WHERE AuthorName = 'George R.R. Martin'), (SELECT GenreID FROM Genre WHERE GenreName = 'Fantasy'), 1996, 5
WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'A Game of Thrones');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'The Hobbit', (SELECT AuthorID FROM Author WHERE AuthorName = 'J.R.R. Tolkien'), (SELECT GenreID FROM Genre WHERE GenreName = 'Fantasy'), 1937, 4
WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'The Hobbit');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'American Gods', (SELECT AuthorID FROM Author WHERE AuthorName = 'Neil Gaiman'), (SELECT GenreID FROM Genre WHERE GenreName = 'Science Fiction'), 2001, 2
WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'American Gods');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'The Shining', (SELECT AuthorID FROM Author WHERE AuthorName = 'Stephen King'), (SELECT GenreID FROM Genre WHERE GenreName = 'Thriller'), 1977, 4
WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'The Shining');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'Murder on the Orient Express', (SELECT AuthorID FROM Author WHERE AuthorName = 'Agatha Christie'), (SELECT GenreID FROM Genre WHERE GenreName = 'Mystery'), 1934, 3
WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'Murder on the Orient Express');

-- Book Loans
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT (SELECT BookID FROM Books WHERE Title = 'Harry Potter and the Sorcerer''s Stone'), 1, '2022-03-01', NULL, '2022-03-15', 'N', 15
WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = (SELECT BookID FROM Books WHERE Title = 'Harry Potter and the Sorcerer''s Stone') AND MemberID = 1 AND LoanDate = '2022-03-01');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT (SELECT BookID FROM Books WHERE Title = 'A Game of Thrones'), 2, '2022-03-05', NULL, '2022-03-20', 'N', 15
WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = (SELECT BookID FROM Books WHERE Title = 'A Game of Thrones') AND MemberID = 2 AND LoanDate = '2022-03-05');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT (SELECT BookID FROM Books WHERE Title = 'The Hobbit'), 1, '2022-03-10', NULL, '2022-03-25', 'N', 20
WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = (SELECT BookID FROM Books WHERE Title = 'The Hobbit') AND MemberID = 1 AND LoanDate = '2022-03-10');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT (SELECT BookID FROM Books WHERE Title = 'American Gods'), 3, '2022-05-01', NULL, '2022-05-15', 'N', 10
WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = (SELECT BookID FROM Books WHERE Title = 'American Gods') AND MemberID = 3 AND LoanDate = '2022-05-01');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT (SELECT BookID FROM Books WHERE Title = 'The Shining'), 4, '2022-05-03', '2022-05-18', '2022-05-17', 'Y', 12
WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = (SELECT BookID FROM Books WHERE Title = 'The Shining') AND MemberID = 4 AND LoanDate = '2022-05-03');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT (SELECT BookID FROM Books WHERE Title = 'Murder on the Orient Express'), 3, '2022-06-01', NULL, '2022-06-15', 'N', 8
WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = (SELECT BookID FROM Books WHERE Title = 'Murder on the Orient Express') AND MemberID = 3 AND LoanDate = '2022-06-01');

CREATE OR REPLACE PROCEDURE get_available_books(OUT ref_result REFCURSOR)
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE NOTICE 'Opening cursor for available books';
    OPEN ref_result FOR
        SELECT b.BookID, b.Title
        FROM Books b
        LEFT JOIN BookLoans bl ON b.BookID = bl.BookID AND bl.BookReturn = 'N'
        GROUP BY b.BookID, b.Title
        HAVING COUNT(bl.BookID) = 0;
    RAISE NOTICE 'Cursor opened';
END;
$$;

CREATE VIEW loan_records AS
SELECT 
    bl.LoanID,
    bl.BookID,
    b.Title AS BookTitle,
    bl.MemberID,
    u.Name AS MemberName,
    bl.LoanDate,
    bl.ReturnDate,
    bl.DueDate,
    bl.BookReturn,
    bl.LoanPrice
FROM 
    BookLoans bl
JOIN 
    Books b ON bl.BookID = b.BookID
JOIN 
    Member m ON bl.MemberID = m.MemberID
JOIN 
    "User" u ON m.MemberID = u.MemberID;

GRANT CONNECT ON DATABASE lim TO cop4710;
GRANT USAGE ON SCHEMA public TO cop4710;
GRANT CREATE ON SCHEMA public TO cop4710;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO cop4710;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO cop4710;

GRANT CONNECT ON DATABASE lim TO member_role;
GRANT USAGE ON SCHEMA public TO member_role;
GRANT SELECT ON TABLE Books, Author, Genre, BookLoans TO member_role;

GRANT CONNECT ON DATABASE lim TO employee_role;
GRANT USAGE ON SCHEMA public TO employee_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE Books, Author, Genre, "User", Member, Employee, BookLoans TO employee_role;
GRANT SELECT, UPDATE ON TABLE Books TO employee_role;
GRANT SELECT ON loan_records TO employee_role;
