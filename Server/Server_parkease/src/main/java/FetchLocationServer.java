import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FetchLocationServer {

    public static void handleRequest(Socket clientSocket, String code) {
        try {
            String location = getLocationFromCode(code);
            if (location != null) {
                sendResponseToClient(clientSocket, "LOCATION|" + location);
            } else {
                sendResponseToClient(clientSocket, "ERROR|Location not found for code: " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getLocationFromCode(String code) {
        String location = null;
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "SELECT location FROM parking_area WHERE code = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    location = rs.getString("location");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
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




