import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    public static Integer verifyLogin(String email, String password) {
        try (Connection conn = DatabaseUtil.connect("cop4710", "yMJ6zenikfum@3a")) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT UserID, MemberID, EmployeeID FROM \"User\" WHERE Email = ? AND Password = ?"
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

    public static boolean isMember(int userId) {
        try (Connection conn = DatabaseUtil.connect("cop4710", "yMJ6zenikfum@3a")) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT MemberID FROM \"User\" WHERE UserID = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MemberID") != 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking member status: " + e.getMessage());
        }
        return false;
    }

    public static boolean saveSignUp(String email, String password, String name, String phone, String address, int userType, String position, double salary) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.connect("cop4710", "yMJ6zenikfum@3a");
            conn.setAutoCommit(false);

            PreparedStatement userStmt = conn.prepareStatement(
                "INSERT INTO \"User\" (Email, Password, Name, Phone, Address) VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS
            );
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
                    PreparedStatement memberStmt = conn.prepareStatement(
                        "INSERT INTO Member (MemberID, RegisterDate) VALUES (?, DEFAULT)"
                    );
                    memberStmt.setInt(1, userId);
                    memberStmt.executeUpdate();

                    PreparedStatement updateUserStmt = conn.prepareStatement(
                        "UPDATE \"User\" SET MemberID = ? WHERE UserID = ?"
                    );
                    updateUserStmt.setInt(1, userId);
                    updateUserStmt.setInt(2, userId);
                    updateUserStmt.executeUpdate();
                } else if (userType == 1) { // Employee
                    PreparedStatement employeeStmt = conn.prepareStatement(
                        "INSERT INTO Employee (EmployeeID, Position, Salary) VALUES (?, ?, ?)"
                    );
                    employeeStmt.setInt(1, userId);
                    employeeStmt.setString(2, position);
                    employeeStmt.setDouble(3, salary);
                    employeeStmt.executeUpdate();

                    PreparedStatement updateUserStmt = conn.prepareStatement(
                        "UPDATE \"User\" SET EmployeeID = ? WHERE UserID = ?"
                    );
                    updateUserStmt.setInt(1, userId);
                    updateUserStmt.setInt(2, userId);
                    updateUserStmt.executeUpdate();
                }

                conn.commit();
                System.out.println("You have successfully signed up.");
                return true;
            } else {
                conn.rollback();
                System.out.println("Sign up failed.");
            }
        } catch (SQLException e) {
            System.out.println("Sign up failed due to system error: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    System.out.println("Rollback failed: " + rollbackException.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close connection: " + e.getMessage());
                }
            }
        }
        return false;
    }
}
