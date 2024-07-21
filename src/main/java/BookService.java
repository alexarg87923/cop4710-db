import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;
import java.sql.Types;

public class BookService {
    private Scanner input;
    private Connection conn;
    private AuthorService authorService;
    private GenreService genreService;

    public BookService(Scanner input, Connection conn, AuthorService authorService, GenreService genreService) {
        this.input = input;
        this.conn = conn;
        this.authorService = authorService;
        this.genreService = genreService;
    }

	public void addBook() {
		System.out.println("\n======== Add a New Book ========");
		System.out.print("Title (or 'cancel' to exit): ");
		String title = input.nextLine();
	
		if (title.equalsIgnoreCase("cancel")) {
			return;
		}
	
		int authorId = handleNewAuthor();
		if (authorId == -1) {
			System.out.println("Cancelled or failed to get a valid author ID.");
			return;
		}
	
		int genreId = handleNewGenre();
		if (genreId == -1) {
			System.out.println("Cancelled or failed to get a valid genre ID.");
			return;
		}
	
		System.out.print("Year of Publication (or 'cancel' to exit): ");
		String yearStr = input.nextLine();
		if (yearStr.equalsIgnoreCase("cancel")) {
			return;
		}
	
		try {
			int year = Integer.parseInt(yearStr);
			System.out.print("Quantity (or 'cancel' to exit): ");
			String quantityStr = input.nextLine();
			if (quantityStr.equalsIgnoreCase("cancel")) {
				return;
			}
	
			try {
				int quantity = Integer.parseInt(quantityStr);
				try {
					PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (title, authorid, genreid, bookyear, quantity) VALUES (?, ?, ?, ?, ?)");
					stmt.setString(1, title);
					stmt.setInt(2, authorId);
					stmt.setInt(3, genreId);
					stmt.setInt(4, year);
					stmt.setInt(5, quantity);
					stmt.executeUpdate();
					System.out.println("Book added successfully!");
				} catch (SQLException e) {
					System.out.println("Failed to add book: " + e.getMessage());
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input for quantity. Please enter a valid number.");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input for year. Please enter a valid number.");
		}
	}	
    
	public void removeBook() {
		System.out.println("\n======== Remove a Book ========");
		System.out.print("Enter Book ID to remove or type 'list' to view all books (or 'cancel' to exit): ");
		String bookInput = input.nextLine();
	
		if (bookInput.equalsIgnoreCase("list")) {
			listBooks();
			System.out.print("Enter Book ID to remove: ");
			bookInput = input.nextLine();
		} else if (bookInput.equalsIgnoreCase("cancel")) {
			return;
		}
	
		try {
			int bookId = Integer.parseInt(bookInput);
			
			// First, delete all book loans referencing this book
			try {
				PreparedStatement deleteLoansStmt = conn.prepareStatement("DELETE FROM BookLoans WHERE BookID = ?");
				deleteLoansStmt.setInt(1, bookId);
				deleteLoansStmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Failed to remove book loans: " + e.getMessage());
				return;
			}
	
			// Then, delete the book
			try {
				PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE bookid = ?");
				stmt.setInt(1, bookId);
				int affectedRows = stmt.executeUpdate();
				if (affectedRows > 0) {
					System.out.println("Book removed successfully!");
				} else {
					System.out.println("No book found with the specified ID.");
				}
			} catch (SQLException e) {
				System.out.println("Error removing book: " + e.getMessage());
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a valid book ID.");
		}
	}
    
	public void editBookEntry() {
		System.out.println("\n======== Edit a Book Entry ========");
		System.out.print("Enter Book ID to edit or type 'list' to view all books (or 'cancel' to exit): ");
		String bookInput = input.nextLine();
	
		if (bookInput.equalsIgnoreCase("list")) {
			listBooks();
			System.out.print("Enter Book ID to edit: ");
			bookInput = input.nextLine();
		} else if (bookInput.equalsIgnoreCase("cancel")) {
			return;
		}
	
		try {
			int bookId = Integer.parseInt(bookInput);
			try {
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE bookid = ?");
				stmt.setInt(1, bookId);
				ResultSet rs = stmt.executeQuery();
	
				if (rs.next()) {
					System.out.println("Editing book: " + rs.getString("title"));
	
					System.out.print("New Title (leave blank to keep current): ");
					String title = input.nextLine();
					if (title.isEmpty()) {
						title = rs.getString("title");
					}
	
					int authorId = handleExistingAuthor(rs.getInt("authorid"));
					if (authorId == -1) {
						authorId = rs.getInt("authorid");
					}
	
					int genreId = handleExistingGenre(rs.getInt("genreid"));
					if (genreId == -1) {
						genreId = rs.getInt("genreid");
					}
	
					System.out.print("New Year of Publication (leave blank to keep current): ");
					String yearStr = input.nextLine();
					int year;
					if (yearStr.isEmpty()) {
						year = rs.getInt("bookyear");
					} else {
						year = Integer.parseInt(yearStr);
					}
	
					System.out.print("New Quantity (leave blank to keep current): ");
					String quantityStr = input.nextLine();
					int quantity;
					if (quantityStr.isEmpty()) {
						quantity = rs.getInt("quantity");
					} else {
						quantity = Integer.parseInt(quantityStr);
					}
	
					PreparedStatement updateStmt = conn.prepareStatement("UPDATE books SET title = ?, authorid = ?, genreid = ?, bookyear = ?, quantity = ? WHERE bookid = ?");
					updateStmt.setString(1, title);
					updateStmt.setInt(2, authorId);
					updateStmt.setInt(3, genreId);
					updateStmt.setInt(4, year);
					updateStmt.setInt(5, quantity);
					updateStmt.setInt(6, bookId);
					updateStmt.executeUpdate();
	
					System.out.println("Book updated successfully!");
				} else {
					System.out.println("No book found with the specified ID.");
				}
			} catch (SQLException e) {
				System.out.println("Error updating book: " + e.getMessage());
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a valid book ID.");
		}
	}
	
	private int handleExistingAuthor(int currentAuthorId) {
		System.out.print("Enter new Author ID (leave blank to keep current, type 'list' to see all authors, or 'cancel' to exit): ");
		String inputStr = input.nextLine();
	
		if (inputStr.equalsIgnoreCase("list")) {
			authorService.listAuthors();
			System.out.print("Enter Author ID: ");
			inputStr = input.nextLine();
		} else if (inputStr.equalsIgnoreCase("cancel") || inputStr.isEmpty()) {
			return currentAuthorId;
		}
	
		return handleAuthor(inputStr);
	}
	
	private int handleExistingGenre(int currentGenreId) {
		System.out.print("Enter new Genre ID (leave blank to keep current, type 'list' to see all genres, or 'cancel' to exit): ");
		String inputStr = input.nextLine();
	
		if (inputStr.equalsIgnoreCase("list")) {
			genreService.listGenres();
			System.out.print("Enter Genre ID: ");
			inputStr = input.nextLine();
		} else if (inputStr.equalsIgnoreCase("cancel") || inputStr.isEmpty()) {
			return currentGenreId;
		}
	
		return handleGenre(inputStr);
	}
	
	private int handleAuthor(String inputStr) {
		try {
			return Integer.parseInt(inputStr);
		} catch (NumberFormatException e) {
			try {
				PreparedStatement stmt = conn.prepareStatement("SELECT AuthorID FROM Author WHERE AuthorName = ?");
				stmt.setString(1, inputStr);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return rs.getInt("AuthorID");
				} else {
					System.out.println("Author not found. Do you want to make this new entry? (yes/no)");
					String decision = input.nextLine();
					if (decision.equalsIgnoreCase("yes")) {
						PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Author (AuthorName) VALUES (?) RETURNING AuthorID");
						insertStmt.setString(1, inputStr);
						ResultSet insertRs = insertStmt.executeQuery();
						if (insertRs.next()) {
							return insertRs.getInt("AuthorID");
						}
					}
					throw new SQLException("Author creation was cancelled or failed.");
				}
			} catch (SQLException ex) {
				System.out.println("Database error: " + ex.getMessage());
				return -1;
			}
		}
	}
	
	private int handleGenre(String inputStr) {
		try {
			return Integer.parseInt(inputStr);
		} catch (NumberFormatException e) {
			try {
				PreparedStatement stmt = conn.prepareStatement("SELECT GenreID FROM Genre WHERE GenreName = ?");
				stmt.setString(1, inputStr);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return rs.getInt("GenreID");
				} else {
					System.out.println("Genre not found. Do you want to make this new entry? (yes/no)");
					String decision = input.nextLine();
					if (decision.equalsIgnoreCase("yes")) {
						PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Genre (GenreName) VALUES (?) RETURNING GenreID");
						insertStmt.setString(1, inputStr);
						ResultSet insertRs = insertStmt.executeQuery();
						if (insertRs.next()) {
							return insertRs.getInt("GenreID");
						}
					}
					throw new SQLException("Genre creation was cancelled or failed.");
				}
			} catch (SQLException ex) {
				System.out.println("Database error: " + ex.getMessage());
				return -1;
			}
		}
	}
	
	private int handleNewAuthor() {
		System.out.print("Enter Author ID (type 'list' to see all authors, or 'cancel' to exit): ");
		String inputStr = input.nextLine();
	
		if (inputStr.equalsIgnoreCase("list")) {
			authorService.listAuthors();
			System.out.print("Enter Author ID: ");
			inputStr = input.nextLine();
		} else if (inputStr.equalsIgnoreCase("cancel")) {
			return -1;
		}
	
		return handleAuthor(inputStr);
	}
	
	private int handleNewGenre() {
		System.out.print("Enter Genre ID (type 'list' to see all genres, or 'cancel' to exit): ");
		String inputStr = input.nextLine();
	
		if (inputStr.equalsIgnoreCase("list")) {
			genreService.listGenres();
			System.out.print("Enter Genre ID: ");
			inputStr = input.nextLine();
		} else if (inputStr.equalsIgnoreCase("cancel")) {
			return -1;
		}
	
		return handleGenre(inputStr);
	}	

	public void extendLoanForMember() {
		System.out.println("\n======== Extend Book Loan for Member ========");
		System.out.print("Enter Member ID or type 'list' to view all members with active loans (or 'cancel' to exit): ");
		String memberInput = input.nextLine();
	
		if (memberInput.equalsIgnoreCase("list")) {
			listMembersWithLoans();
			System.out.print("Enter Member ID: ");
			memberInput = input.nextLine();
		} else if (memberInput.equalsIgnoreCase("cancel")) {
			return;
		}
	
		try {
			int memberId = Integer.parseInt(memberInput);
			
			// Ask if user wants to list the books the member has
			System.out.print("Do you want to list the books this member has? (yes/no): ");
			String listBooksResponse = input.nextLine();
			
			if (listBooksResponse.equalsIgnoreCase("yes")) {
				listCheckedOutBooks(memberId);
			}
	
			System.out.print("Enter Book ID to extend loan or type 'cancel' to exit: ");
			String bookInput = input.nextLine();
	
			if (bookInput.equalsIgnoreCase("cancel")) {
				return;
			}
	
			try {
				int bookId = Integer.parseInt(bookInput);
				PreparedStatement stmt = conn.prepareStatement(
					"SELECT DueDate FROM BookLoans WHERE BookID = ? AND MemberID = ? AND BookReturn = 'N'");
				stmt.setInt(1, bookId);
				stmt.setInt(2, memberId);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					LocalDate currentDueDate = rs.getDate("DueDate").toLocalDate();
					LocalDate newDueDate = currentDueDate.plusDays(14); // Extend loan by 14 days
	
					PreparedStatement updateStmt = conn.prepareStatement(
						"UPDATE BookLoans SET DueDate = ? WHERE BookID = ? AND MemberID = ? AND BookReturn = 'N'");
					updateStmt.setDate(1, java.sql.Date.valueOf(newDueDate));
					updateStmt.setInt(2, bookId);
					updateStmt.setInt(3, memberId);
					updateStmt.executeUpdate();
					System.out.println("Loan extended successfully!");
				} else {
					System.out.println("No active loan found for the given Book ID and Member ID.");
				}
			} catch (SQLException e) {
				System.out.println("Failed to extend loan: " + e.getMessage());
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter valid numeric IDs.");
		}
	}
	
	private void listMembersWithLoans() {
		System.out.println("\n======== List of Members with Active Loans ========");
		try {
			PreparedStatement stmt = conn.prepareStatement(
				"SELECT DISTINCT u.MemberID, u.Name " +
				"FROM \"User\" u " +
				"JOIN BookLoans bl ON u.MemberID = bl.MemberID " +
				"WHERE bl.BookReturn = 'N'");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				System.out.println("Member ID: " + rs.getInt("MemberID") + ", Name: " + rs.getString("Name"));
			}
		} catch (SQLException e) {
			System.out.println("Failed to list members with active loans: " + e.getMessage());
		}
	}
	
	private void listCheckedOutBooks(int memberId) {
		System.out.println("\n======== List of Checked-out Books for Member ID: " + memberId + " ========");
		try {
			PreparedStatement stmt = conn.prepareStatement(
				"SELECT b.BookID, b.Title FROM BookLoans bl JOIN Books b ON bl.BookID = b.BookID WHERE bl.MemberID = ? AND bl.BookReturn = 'N'");
			stmt.setInt(1, memberId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				System.out.println("Book ID: " + rs.getInt("BookID") + ", Title: " + rs.getString("Title"));
			}
		} catch (SQLException e) {
			System.out.println("Failed to list checked-out books: " + e.getMessage());
		}
	}
	

	public void checkoutBookForMember() {
		System.out.println("\n======== Check-out Book for Member ========");
		System.out.print("Enter Member ID or type 'list' to view all members (or 'cancel' to exit): ");
		String memberInput = input.nextLine();
	
		if (memberInput.equalsIgnoreCase("list")) {
			listMembers();
			System.out.print("Enter Member ID or type 'cancel' to exit: ");
			memberInput = input.nextLine();
		} 
	
		if (memberInput.equalsIgnoreCase("cancel")) {
			return;
		}
	
		try {
			int memberId = Integer.parseInt(memberInput);
			System.out.print("Enter Book ID to checkout or type 'list' to view all available books (or 'cancel' to exit): ");
			String bookInput = input.nextLine();
	
			if (bookInput.equalsIgnoreCase("list")) {
				listAvailableBooks();
				System.out.print("Enter Book ID or type 'cancel' to exit: ");
				bookInput = input.nextLine();
			} 
	
			if (bookInput.equalsIgnoreCase("cancel")) {
				return;
			}
	
			LocalDate loanDate = LocalDate.now();
			LocalDate dueDate = loanDate.plusDays(14); // Assuming a 14-day loan period
	
			try {
				int bookId = Integer.parseInt(bookInput);
				PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO BookLoans (BookID, MemberID, LoanDate, DueDate, BookReturn, LoanPrice) VALUES (?, ?, ?, ?, 'N', 15)");
				stmt.setInt(1, bookId);
				stmt.setInt(2, memberId);
				stmt.setDate(3, java.sql.Date.valueOf(loanDate));
				stmt.setDate(4, java.sql.Date.valueOf(dueDate));
				stmt.executeUpdate();
				System.out.println("Book checked out successfully!");
			} catch (SQLException e) {
				System.out.println("Failed to checkout book: " + e.getMessage());
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter valid numeric IDs.");
		}
	}	
	public void listAvailableBooks() {
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;

        try {
            callableStatement = conn.prepareCall("CALL get_available_books(?)");
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);

            callableStatement.execute();

            resultSet = (ResultSet) callableStatement.getObject(1);

            System.out.println("\n======== List of Available Books ========");
            while (resultSet.next()) {
                System.out.println("Book ID: " + resultSet.getInt("BookID") + ", Title: " + resultSet.getString("Title"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list available books: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (callableStatement != null) callableStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Failed to close resources: " + e.getMessage());
            }
        }
    }

	public void checkInBookForMember() {
		System.out.println("\n======== Check-in Book for Member ========");
		System.out.print("Enter Member ID or type 'list' to view all members with active loans (or 'cancel' to exit): ");
		String memberInput = input.nextLine();
	
		if (memberInput.equalsIgnoreCase("list")) {
			listMembersWithActiveLoans();
			System.out.print("Enter Member ID or type 'cancel' to exit: ");
			memberInput = input.nextLine();
		} 
	
		if (memberInput.equalsIgnoreCase("cancel")) {
			return;
		}
	
		try {
			int memberId = Integer.parseInt(memberInput);
			System.out.print("Enter Book ID to check in or type 'list' to view all checked-out books by this member (or 'cancel' to exit): ");
			String bookInput = input.nextLine();
	
			if (bookInput.equalsIgnoreCase("list")) {
				listCheckedOutBooks(memberId);
				System.out.print("Enter Book ID or type 'cancel' to exit: ");
				bookInput = input.nextLine();
			} 
	
			if (bookInput.equalsIgnoreCase("cancel")) {
				return;
			}
	
			LocalDate returnDate = LocalDate.now();
	
			try {
				int bookId = Integer.parseInt(bookInput);
				PreparedStatement stmt = conn.prepareStatement(
					"UPDATE BookLoans SET ReturnDate = ?, BookReturn = 'Y' WHERE BookID = ? AND MemberID = ? AND BookReturn = 'N'");
				stmt.setDate(1, java.sql.Date.valueOf(returnDate));
				stmt.setInt(2, bookId);
				stmt.setInt(3, memberId);
				int affectedRows = stmt.executeUpdate();
				if (affectedRows > 0) {
					System.out.println("Book checked in successfully!");
				} else {
					System.out.println("No matching loan record found or the book is already returned.");
				}
			} catch (SQLException e) {
				System.out.println("Failed to check in book: " + e.getMessage());
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter valid numeric IDs.");
		}
	}
	
	private void listMembersWithActiveLoans() {
		System.out.println("\n======== List of Members with Active Loans ========");
		try {
			PreparedStatement stmt = conn.prepareStatement(
				"SELECT DISTINCT u.MemberID, u.Name " +
				"FROM \"User\" u " +
				"JOIN BookLoans bl ON u.MemberID = bl.MemberID " +
				"WHERE bl.BookReturn = 'N'");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				System.out.println("Member ID: " + rs.getInt("MemberID") + ", Name: " + rs.getString("Name"));
			}
		} catch (SQLException e) {
			System.out.println("Failed to list members with active loans: " + e.getMessage());
		}
	}

	private void listMembers() {
		System.out.println("\n======== List of Members ========");
		try {
			PreparedStatement stmt = conn.prepareStatement(
				"SELECT u.MemberID, u.Name " +
				"FROM \"User\" u " +
				"JOIN Member m ON u.MemberID = m.MemberID"
			);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				System.out.println("Member ID: " + rs.getInt("MemberID") + ", Name: " + rs.getString("Name"));
			}
		} catch (SQLException e) {
			System.out.println("Failed to list members: " + e.getMessage());
		}
	}

    public void listBooks() {
        try {
			System.out.println("\n======== List Books ========");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("bookid") + ", Title: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing books: " + e.getMessage());
        }
    }

    public void searchBooks(String partOfTitle) {
		System.out.println("\n======== Search a Book Entry ========");
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT bookid, title FROM books WHERE lower(title) LIKE ?");
            stmt.setString(1, "%" + partOfTitle.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    System.out.println("Matching books:");
                    found = true;
                }
                System.out.println("Book ID: " + rs.getInt("bookid") + ", Title: " + rs.getString("title"));
            }
            if (!found) {
                System.out.println("No books found with the specified title part.");
            }
        } catch (SQLException e) {
            System.out.println("Error searching for books: " + e.getMessage());
        }
    }

    public void findBook() {
		System.out.println("\n======== Find a Book Entry ========");
        System.out.print("Enter title or part of title to search: ");
        String title = input.nextLine();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE lower(title) LIKE ?");
            stmt.setString(1, "%" + title.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("bookid") + ", Title: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            System.out.println("Error finding book: " + e.getMessage());
        }
    }
}
