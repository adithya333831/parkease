import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterServer {

    public static void handleRequest(Socket clientSocket, String requestData) {
        try {
            // Extract registration data from request
            String[] userData = requestData.split("\\|");
            String operation = userData[0];
            String username = userData[1];
            String password = userData[2];
            String name = userData[3];
            String phone = userData[4];

            // Check if the user already exists in the database
            if (isUserExists(username)) {
                // User already exists
                sendResponseToClient(clientSocket, "duplicate");
            } else {
                // User doesn't exist, proceed with registration
                if (insertUserIntoDatabase(username, password, name, phone)) {
                    // Registration successful
                    sendResponseToClient(clientSocket, "added");
                } else {
                    // Registration failed
                    sendResponseToClient(clientSocket, "Error occurred");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponseToClient(clientSocket, "Error occurred");
        }
    }

    private static boolean isUserExists(String username) {
        // JDBC connection parameters
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "SELECT * FROM usertable WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                // If there is any result, user exists
                return rs.next();
            }
        } catch (SQLException e) {
            // Catch SQLException for database-related errors
            e.printStackTrace();
            return false;
        }
    }

    private static boolean insertUserIntoDatabase(String username, String password, String name, String phone) {
        // JDBC connection parameters
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "INSERT INTO usertable (username, password, name, phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set parameters
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, phone);

            // Execute the insert statement
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void sendResponseToClient(Socket clientSocket, String message) {
        try {
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            outputStream.writeUTF(message);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



