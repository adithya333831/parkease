import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManagerHomeServer {

    public static void handleRequest(Socket clientSocket, String requestData) {
        try {
            // Extract manager ID from request
            String[] parts = requestData.split("\\|");
            String managerId = parts[1];

            // Fetch manager details from the database
            String managerDetails = getManagerDetails(managerId);

            // Send manager details to the client
            sendResponseToClient(clientSocket, managerDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getManagerDetails(String managerId) {
        // JDBC connection parameters
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "SELECT name, code FROM managertable WHERE managerid = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, managerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // If manager exists, return name, manager ID, and code
                    String name = rs.getString("name");
                    String code = rs.getString("code");
                    return name + "|" + managerId + "|" + code;
                } else {
                    // Manager doesn't exist
                    return "managerdoesntexist";
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

