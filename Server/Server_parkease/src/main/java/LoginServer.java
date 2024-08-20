import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginServer {

    public static void handleRequest(Socket clientSocket, String requestData) {
        try {
            // Extract username and password from request
            String[] parts = requestData.split("\\|");
            String operation = parts[0];
            String username = parts[1];
            String password = parts[2];

            // Check if user exists in the database
            if (isUserExists(username,password)) {
                // User exists
                sendResponseToClient(clientSocket, "userexists");
            } else {
                // User doesn't exist
                sendResponseToClient(clientSocket, "userdoesntexist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isUserExists(String username,String password) {
        // JDBC connection parameters
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "SELECT * FROM usertable WHERE username = ? AND password = ?"
;

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                // If there is any result, user exists
                return rs.next();
            }
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


