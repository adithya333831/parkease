import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FetchManagerBookingsServer {
    public static void handleRequest(Socket clientSocket, String[] parts) {
        if (parts.length != 2) {
            System.out.println("Invalid request format for FETCH_MANAGER_BOOKINGS");
            return;
        }

        String managerUsername = parts[1];
        String responseData = fetchManagerBookings(managerUsername);

        try {
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            outputStream.writeUTF(responseData);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fetchManagerBookings(String managerUsername) {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDateString = currentDate.format(formatter);

        String dbUrl = "jdbc:mysql://localhost:3306/parkease";
        String dbUsername = "root";
        String dbPassword = "Password@1";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String query = "SELECT * FROM bookingtable WHERE managerusername = ? AND entrytime LIKE ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, managerUsername);
            statement.setString(2, currentDateString + "%");

            ResultSet resultSet = statement.executeQuery();
            StringBuilder response = new StringBuilder();

            while (resultSet.next()) {
                int bookingId = resultSet.getInt("bookingid");
                String code = resultSet.getString("code");
                String category = resultSet.getString("category");
                String vehicleNumber = resultSet.getString("vehicleno");
                String entryTime = resultSet.getString("entrytime");
                int extraAmount = resultSet.getInt("extraamount");
                String exitTime = resultSet.getString("exittime");
                String paymentStatus = resultSet.getString("payment_status");
                String status = resultSet.getString("status");

                response.append(bookingId).append("|")
                        .append(code).append("|")
                        .append(category).append("|")
                        .append(vehicleNumber).append("|")
                        .append(entryTime).append("|")
                        .append(extraAmount).append("|")
                        .append(exitTime).append("|")
                        .append(paymentStatus).append("|")
                        .append(status).append("\n");
            }

            statement.close();
            return response.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}




