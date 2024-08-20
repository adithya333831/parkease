import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManagerLoginServer {

    public static void handleRequest(Socket clientSocket, String requestData) {
        try {
            // Extract manager ID and password from request
            String[] parts = requestData.split("\\|");
            String operation = parts[0];
            String managerId = parts[1];
            String password = parts[2];

            // Check if manager exists in the database
            if (isManagerExists(managerId, password)) {
                // Manager exists
                sendResponseToClient(clientSocket, "managerexists");
            } else {
                // Manager doesn't exist
                sendResponseToClient(clientSocket, "managerdoesntexist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isManagerExists(String managerId, String password) {
        // JDBC connection parameters
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "SELECT * FROM managertable WHERE managerid = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, managerId);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                // If there is any result, manager exists
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


