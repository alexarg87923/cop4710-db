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
-- -- Create a user and grant privileges
-- CREATE USER root WITH ENCRYPTED PASSWORD 'password';
-- GRANT ALL PRIVILEGES ON DATABASE lim TO root;
-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------
-- SQL script content to be executed from Java JDBC:
CREATE TABLE IF NOT EXISTS Author (
    AuthorID SERIAL PRIMARY KEY,
    AuthorName VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Genre (
    GenreID SERIAL PRIMARY KEY,
    GenreName VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Books (
    BookID SERIAL PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    AuthorID INT REFERENCES Author(AuthorID),
    GenreID INT REFERENCES Genre(GenreID),
    BookYear INT,
    Quantity INT NOT NULL
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

CREATE TABLE IF NOT EXISTS "User" (
    UserID SERIAL PRIMARY KEY,
    Email VARCHAR(255) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Name VARCHAR(255) NOT NULL,
    Phone VARCHAR(50),
    Address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Member (
    MemberID INT PRIMARY KEY,
    RegisterDate DATE DEFAULT CURRENT_DATE,
    CONSTRAINT fk_user FOREIGN KEY (MemberID) REFERENCES "User"(UserID)
);

CREATE TABLE IF NOT EXISTS Employee (
    EmployeeID INT PRIMARY KEY,
    Position VARCHAR(100),
    Salary DECIMAL(10, 2),
    CONSTRAINT fk_user FOREIGN KEY (EmployeeID) REFERENCES "User"(UserID)
);


-- Members
INSERT INTO Member (RegisterDate)
SELECT '2022-01-10' WHERE NOT EXISTS (SELECT 1 FROM Member WHERE RegisterDate = '2022-01-10');
INSERT INTO Member (RegisterDate)
SELECT '2022-01-15' WHERE NOT EXISTS (SELECT 1 FROM Member WHERE RegisterDate = '2022-01-15');
INSERT INTO Member (RegisterDate)
SELECT '2022-02-01' WHERE NOT EXISTS (SELECT 1 FROM Member WHERE RegisterDate = '2022-02-01');
INSERT INTO Member (RegisterDate)
SELECT '2022-03-05' WHERE NOT EXISTS (SELECT 1 FROM Member WHERE RegisterDate = '2022-03-05');
INSERT INTO Member (RegisterDate)
SELECT '2022-04-12' WHERE NOT EXISTS (SELECT 1 FROM Member WHERE RegisterDate = '2022-04-12');
INSERT INTO Member (RegisterDate)
SELECT '2022-05-20' WHERE NOT EXISTS (SELECT 1 FROM Member WHERE RegisterDate = '2022-05-20');

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

-- Employees
INSERT INTO Employee (Position, Salary)
SELECT 'Librarian', 55000.00 WHERE NOT EXISTS (SELECT 1 FROM Employee WHERE Position = 'Librarian' AND Salary = 55000.00);
INSERT INTO Employee (Position, Salary)
SELECT 'Assistant Librarian', 40000.00 WHERE NOT EXISTS (SELECT 1 FROM Employee WHERE Position = 'Assistant Librarian' AND Salary = 40000.00);
INSERT INTO Employee (Position, Salary)
SELECT 'Archivist', 45000.00 WHERE NOT EXISTS (SELECT 1 FROM Employee WHERE Position = 'Archivist' AND Salary = 45000.00);
INSERT INTO Employee (Position, Salary)
SELECT 'Book Keeper', 35000.00 WHERE NOT EXISTS (SELECT 1 FROM Employee WHERE Position = 'Book Keeper' AND Salary = 35000.00);
INSERT INTO Employee (Position, Salary)
SELECT 'Curator', 47000.00 WHERE NOT EXISTS (SELECT 1 FROM Employee WHERE Position = 'Curator' AND Salary = 47000.00);
INSERT INTO Employee (Position, Salary)
SELECT 'Security', 30000.00 WHERE NOT EXISTS (SELECT 1 FROM Employee WHERE Position = 'Security' AND Salary = 30000.00);

-- Books
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'Harry Potter and the Sorcerer''s Stone', 1, 1, 1997, 3 WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'Harry Potter and the Sorcerer''s Stone');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'A Game of Thrones', 2, 1, 1996, 5 WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'A Game of Thrones');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'The Hobbit', 3, 1, 1937, 4 WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'The Hobbit');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'American Gods', 4, 2, 2001, 2 WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'American Gods');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'The Shining', 5, 2, 1977, 4 WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'The Shining');
INSERT INTO Books (Title, AuthorID, GenreID, BookYear, Quantity)
SELECT 'Murder on the Orient Express', 6, 1, 1934, 3 WHERE NOT EXISTS (SELECT 1 FROM Books WHERE Title = 'Murder on the Orient Express');

-- Users
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID, MemberID)
SELECT 'John Doe', '123 Elm St', '555-1234', 'john.doe@email.com', 'password123', NULL, 1 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'john.doe@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID, MemberID)
SELECT 'Jane Smith', '456 Oak St', '555-5678', 'jane.smith@email.com', 'password123', NULL, 2 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'jane.smith@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID)
SELECT 'Bob Johnson', '789 Pine St', '555-9012', 'bob.johnson@email.com', 'password123', 1 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'bob.johnson@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID, MemberID)
SELECT 'Alice Johnson', '321 Maple St', '555-2345', 'alice.johnson@email.com', 'securePass123', NULL, 3 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'alice.johnson@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID, MemberID)
SELECT 'Carl Kent', '654 Willow St', '555-7890', 'carl.kent@email.com', 'securePass123', NULL, 4 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'carl.kent@email.com');
INSERT INTO "User" (Name, Address, Phone, Email, Password, EmployeeID)
SELECT 'Diana Prince', '987 Cedar St', '555-4567', 'diana.prince@email.com', 'securePass123', 2 WHERE NOT EXISTS (SELECT 1 FROM "User" WHERE Email = 'diana.prince@email.com');

-- Book Loans
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT 1, 1, '2022-03-01', NULL, '2022-03-15', 'N', 15 WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = 1 AND MemberID = 1 AND LoanDate = '2022-03-01');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT 2, 2, '2022-03-05', NULL, '2022-03-20', 'N', 15 WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = 2 AND MemberID = 2 AND LoanDate = '2022-03-05');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT 3, 1, '2022-03-10', NULL, '2022-03-25', 'N', 20 WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = 3 AND MemberID = 1 AND LoanDate = '2022-03-10');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT 4, 3, '2022-05-01', NULL, '2022-05-15', 'N', 10 WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = 4 AND MemberID = 3 AND LoanDate = '2022-05-01');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT 5, 4, '2022-05-03', '2022-05-18', '2022-05-17', 'Y', 12 WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = 5 AND MemberID = 4 AND LoanDate = '2022-05-03');
INSERT INTO BookLoans (BookID, MemberID, LoanDate, ReturnDate, DueDate, BookReturn, LoanPrice)
SELECT 6, 3, '2022-06-01', NULL, '2022-06-15', 'N', 8 WHERE NOT EXISTS (SELECT 1 FROM BookLoans WHERE BookID = 6 AND MemberID = 3 AND LoanDate = '2022-06-01');
