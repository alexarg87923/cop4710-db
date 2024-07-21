import java.sql.Connection;
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
        Integer userId = UserService.verifyLogin(email, password);
        if (userId != null) {
            String roleUser;
            String rolePassword;

            try (Connection conn = DatabaseUtil.connect("cop4710", "yMJ6zenikfum@3a")) {
                boolean isMember = UserService.isMember(userId);
                if (isMember) {
                    roleUser = "member_role";
                    rolePassword = "member123";
                    try (Connection roleConn = DatabaseUtil.connect(roleUser, rolePassword)) {
                        new MemberServices(input, userId, roleConn).showMemberHomeScreen();
                    }
                } else {
                    roleUser = "employee_role";
                    rolePassword = "employee123";
                    try (Connection roleConn = DatabaseUtil.connect(roleUser, rolePassword)) {
                        new EmployeeServices(input, roleConn).showEmployeeHomeScreen();
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error in connection: " + e.getMessage());
            }
        } else {
            System.out.println("Login failed. Please try again.");
        }
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
                if (UserService.saveSignUp(email, password, name, phone, address, userType, position, salary)) {
                    System.out.println("Sign up successful. Please log in.");
                } else {
                    System.out.println("Sign up failed. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for salary. Please enter a valid number.");
            }
        } else { // Member
            if (UserService.saveSignUp(email, password, name, phone, address, userType, null, 0)) {
                System.out.println("Sign up successful. Please log in.");
            } else {
                System.out.println("Sign up failed. Please try again.");
            }
        }
    }
}
