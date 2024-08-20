import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ParkFindServer {

    public static void handleRequest(Socket clientSocket, String[] requestData) {
        try {
            if (requestData.length < 3) {
                System.out.println("Insufficient parameters received from client.");
                return;
            }

            String place = requestData[1];
            String vehicleType = requestData[2]; // Added vehicle type parameter

            List<String> parkingAreas = fetchParkingAreas(place, vehicleType); // Fetch based on vehicle type
            sendResponseToClient(clientSocket, parkingAreas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> fetchParkingAreas(String place, String vehicleType) {
        List<String> parkingAreas = new ArrayList<>();
        // JDBC connection parameters
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String pass = "Password@1";
        String sql = "SELECT name, code, car, bike FROM parking_area WHERE place = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, place);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Check availability based on vehicle type
                    if ("car".equalsIgnoreCase(vehicleType) && rs.getInt("car") > 0) {
                        parkingAreas.add(rs.getString("name") + " - " + rs.getString("code"));
                    } else if ("bike".equalsIgnoreCase(vehicleType) && rs.getInt("bike") > 0) {
                        parkingAreas.add(rs.getString("name") + " - " + rs.getString("code"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parkingAreas;
    }

    private static void sendResponseToClient(Socket clientSocket, List<String> parkingAreas) {
        try {
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            for (String area : parkingAreas) {
                outputStream.writeUTF(area);
            }
            outputStream.writeUTF("END");
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





