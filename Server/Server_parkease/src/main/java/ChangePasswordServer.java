import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ChangePasswordServer {

    public static void handleRequest(Socket clientSocket, String requestData) {
        try {
            // Extract username and new password from request
            String[] parts = requestData.split("\\|");
            String username = parts[1];
            String newPassword = parts[2];

            // Change the user's password in the database
            if (changeUserPassword(username, newPassword)) {
                sendResponseToClient(clientSocket, "passwordchanged");
            } else {
                sendResponseToClient(clientSocket, "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean changeUserPassword(String username, String newPassword) {
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "UPDATE usertable SET password = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
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


