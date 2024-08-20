import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ManagerChangePasswordServer {

    public static void handleRequest(Socket clientSocket, String requestData) {
        try {
            // Extract managerId and new password from request
            String[] parts = requestData.split("\\|");
            String managerId = parts[1];
            String newPassword = parts[2];

            // Change the manager's password in the database
            if (changeManagerPassword(managerId, newPassword)) {
                sendResponseToClient(clientSocket, "passwordchanged");
            } else {
                sendResponseToClient(clientSocket, "passwordchangeerror");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean changeManagerPassword(String managerId, String newPassword) {
        // JDBC connection parameters
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "UPDATE managertable SET password = ? WHERE managerid = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, managerId);

            // Execute update
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
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



