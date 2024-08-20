
import java.io.*;
import java.net.*;
import java.sql.*;

public class ShowTicketServer {
    public static void handleRequest(Socket clientSocket, String[] parts) {
        if (parts.length != 3) {
            System.out.println("Invalid request format for FETCH_BOOKING_IDS");
            return;
        }

        String username = parts[1];
        String currentDate = parts[2];
        String responseData = fetchBookingIds(username, currentDate);

        try {
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            outputStream.writeUTF(responseData);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fetchBookingIds(String username, String currentDate) {
        String dbUrl = "jdbc:mysql://localhost:3306/parkease"; // Replace with your database URL
        String dbUsername = "root"; // Replace with your database username
        String dbPassword = "Password@1"; // Replace with your database password

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String query = "SELECT bookingid FROM bookingtable WHERE username = ? AND entrytime LIKE ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, currentDate + "%"); // Assuming entrytime is stored with date and time

            ResultSet resultSet = statement.executeQuery();
            StringBuilder bookingIds = new StringBuilder();
            while (resultSet.next()) {
                int bookingId = resultSet.getInt("bookingid");
                bookingIds.append(bookingId).append("|");
            }
            return bookingIds.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
