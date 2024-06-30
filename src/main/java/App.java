import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
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
        System.out.print("Username: ");
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();
        if (verifyLogin(username, password)) {
            if (userType == 1) { // Employee
                new EmployeeServices(input).showEmployeeHomeScreen();
            } else { // Member
                new MemberServices(input).showMemberHomeScreen();
            }
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }

    public static boolean verifyLogin(String username, String password) {
        try (Connection conn = DatabaseUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("You have logged in successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Login failed due to system error: " + e.getMessage());
        }
        return false;
    }

    public static void signUp(Scanner input) {
        System.out.print("Username: ");
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();
        saveSignUp(username, password);
    }

    public static void saveSignUp(String username, String password) {
        try (Connection conn = DatabaseUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
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
