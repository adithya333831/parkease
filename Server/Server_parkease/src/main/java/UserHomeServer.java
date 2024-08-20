import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserHomeServer {

    public static void handleRequest(Socket clientSocket, String requestData) {
        try {
            // Extract username from request
            String[] parts = requestData.split("\\|");
            String username = parts[1];

            // Fetch user details from the database
            String userDetails = getUserDetails(username);

            // Send user details to the client
            sendResponseToClient(clientSocket, userDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getUserDetails(String username) {
        // JDBC connection parameters
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "SELECT name, phone FROM usertable WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // If user exists, return name and phone
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    return name + "|" + phone + "|" + username;
                } else {
                    // User doesn't exist
                    return "userdoesntexist";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
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





