import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int userType = 0;
        while (true) {
            System.out.println("=====================================");
            System.out.println("     Welcome to the Ultimate Library Group!");
            System.out.println("=====================================");
            System.out.println("Are you an Employee or a Member?");
            System.out.println("1. Employee");
            System.out.println("2. Member");
            System.out.println("3. Exit Program");
            System.out.print("Select an option: ");
            try {
                userType = Integer.parseInt(input.nextLine());
                if (userType == 1 || userType == 2) {
                    break;
                } else if (userType == 3) {
                    System.out.println("Exiting Program.");
                    System.exit(0);
                } else {
                    System.out.println("Invalid selection, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        int selection = 0;
        do {
            System.out.println("\n=====================================");
            System.out.println("      Would you like to login or sign up?");
            System.out.println("=====================================");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Go Back");
            System.out.println("4. Exit Program");
            System.out.print("Select an option: ");
            try {
                selection = Integer.parseInt(input.nextLine());
                if (selection == 1 || selection == 2) {
                    loginOrSignUp(selection, userType, input);
                } else if (selection == 3) {
                    main(args); // Restart the main method to select Employee or Member again
                } else if (selection == 4) {
                    System.out.println("Exiting Program.");
                    System.exit(0);
                } else {
                    System.out.println("Invalid selection, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (selection != 4);
    }

    public static void loginOrSignUp(int selection, int userType, Scanner input) {
        if (selection == 1) {
            login(userType, input);
        } else if (selection == 2) {
            signUp(userType, input);
        }
    }

    public static void login(int userType, Scanner input) {
        System.out.print("Email: ");
        String email = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();
        Integer userId = verifyLogin(email, password);
        if (userId != null) {
            if (userType == 1) { // Employee
                new EmployeeServices(input).showEmployeeHomeScreen();
            } else { // Member
                new MemberServices(input, userId).showMemberHomeScreen();
            }
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }

    public static Integer verifyLogin(String email, String password) {
        try (Connection conn = DatabaseUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT UserID FROM \"User\" WHERE Email = ? AND Password = ?"
            );
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("You have logged in successfully.");
                return rs.getInt("UserID");
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (SQLException e) {
            System.out.println("Login failed due to system error: " + e.getMessage());
        }
        return null;
    }

    public static void signUp(int userType, Scanner input) {
        System.out.print("Email (required): ");
        String email = input.nextLine();
        System.out.print("Password (required): ");
        String password = input.nextLine();
        System.out.print("Name (required): ");
        String name = input.nextLine();
        System.out.print("Phone # (press enter to skip): ");
        String phone = input.nextLine();
        System.out.print("Address (press enter to skip): ");
        String address = input.nextLine();

        if (userType == 1) { // Employee
            System.out.print("Position (required): ");
            String position = input.nextLine();
            System.out.print("Salary (required): ");
            try {
                double salary = Double.parseDouble(input.nextLine());
                saveSignUp(email, password, name, phone, address, userType, position, salary);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for salary. Please enter a valid number.");
            }
        } else {
            saveSignUp(email, password, name, phone, address, userType, null, 0);
        }
    }

    public static void saveSignUp(String email, String password, String name, String phone, String address, int userType, String position, double salary) {
        try (Connection conn = DatabaseUtil.connect()) {
            conn.setAutoCommit(false);

            // Insert into User table without MemberID or EmployeeID
            PreparedStatement userStmt = conn.prepareStatement("INSERT INTO \"User\" (Email, Password, Name, Phone, Address) VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, email);
            userStmt.setString(2, password);
            userStmt.setString(3, name);
            userStmt.setString(4, phone);
            userStmt.setString(5, address);
            userStmt.executeUpdate();
            
            ResultSet userRs = userStmt.getGeneratedKeys();
            if (userRs.next()) {
                int userId = userRs.getInt(1);

                if (userType == 2) { // Member
                    // Insert into Member table
                    PreparedStatement memberStmt = conn.prepareStatement("INSERT INTO Member (MemberID, RegisterDate) VALUES (?, DEFAULT)");
                    memberStmt.setInt(1, userId);
                    memberStmt.executeUpdate();

                    // Update User table with MemberID
                    PreparedStatement updateUserStmt = conn.prepareStatement("UPDATE \"User\" SET MemberID = ? WHERE UserID = ?");
                    updateUserStmt.setInt(1, userId);
                    updateUserStmt.setInt(2, userId);
                    updateUserStmt.executeUpdate();
                } else if (userType == 1) { // Employee
                    // Insert into Employee table
                    PreparedStatement employeeStmt = conn.prepareStatement("INSERT INTO Employee (EmployeeID, Position, Salary) VALUES (?, ?, ?)");
                    employeeStmt.setInt(1, userId);
                    employeeStmt.setString(2, position);
                    employeeStmt.setDouble(3, salary);
                    employeeStmt.executeUpdate();

                    // Update User table with EmployeeID
                    PreparedStatement updateUserStmt = conn.prepareStatement("UPDATE \"User\" SET EmployeeID = ? WHERE UserID = ?");
                    updateUserStmt.setInt(1, userId);
                    updateUserStmt.setInt(2, userId);
                    updateUserStmt.executeUpdate();
                }

                conn.commit();
                System.out.println("You have successfully signed up.");
            } else {
                conn.rollback();
                System.out.println("Sign up failed.");
            }
        } catch (SQLException e) {
            System.out.println("Sign up failed due to system error: " + e.getMessage());
            try (Connection conn = DatabaseUtil.connect()) {
                conn.rollback();
            } catch (SQLException rollbackException) {
                System.out.println("Rollback failed: " + rollbackException.getMessage());
            }
        }
    }
}
