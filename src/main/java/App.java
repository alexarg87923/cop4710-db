import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        DatabaseUtil.initializeDatabase();
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to the Ultimate Library Group!");
        System.out.println("Are you an Employee or a Member?");
        System.out.println("1. Employee");
        System.out.println("2. Member");
        int userType = Integer.parseInt(input.nextLine());

        int selection;
        do {
            System.out.println("Would you like to login or sign up?");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit Program");
            selection = Integer.parseInt(input.nextLine());

            if (selection == 1 || selection == 2) {
                loginOrSignUp(selection, userType, input);
            } else if (selection == 3) {
                System.out.println("Exiting Program.");
                System.exit(0);
            } else {
                System.out.println("Invalid selection, please try again");
            }
        } while (selection != 3);
    }

    public static void loginOrSignUp(int selection, int userType, Scanner input) {
        if (selection == 1) {
            login(userType, input);
        } else if (selection == 2) {
            signUp(input);
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
	

    public static void signUp(Scanner input) {
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
        saveSignUp(email, password, name, phone, address);
    }

    public static void saveSignUp(String email, String password, String name, String phone, String address) {
        try (Connection conn = DatabaseUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO \"User\" (email, password, name, phone, address) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, email);
            stmt.setString(2, password);
			stmt.setString(3, name);
			stmt.setString(4, phone);
			stmt.setString(5, address);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("You have successfully signed up.");
            } else {
                System.out.println("Sign up failed.");
            }
        } catch (SQLException e) {
            System.out.println("Sign up failed due to system error: " + e.getMessage());
        }
    }
}
