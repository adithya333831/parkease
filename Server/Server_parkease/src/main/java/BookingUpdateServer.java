import java.io.*;
import java.net.*;
import java.sql.*;

public class BookingUpdateServer {
    public static void handleRequest(Socket clientSocket, String[] parts) {
        if (parts.length != 5) {
            System.out.println("Invalid request format for UPDATE_BOOKING");
            return;
        }

        int bookingId = Integer.parseInt(parts[1]);
        String exitTime = parts[2];
        boolean baseAmountPaid = Boolean.parseBoolean(parts[3]);
        boolean extraAmountPaid = Boolean.parseBoolean(parts[4]);

        boolean updateSuccess = updateBooking(bookingId, exitTime, baseAmountPaid, extraAmountPaid);

        try {
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            if (updateSuccess) {
                outputStream.writeUTF("UPDATE_SUCCESS");
            } else {
                outputStream.writeUTF("UPDATE_FAILED");
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean updateBooking(int bookingId, String exitTime, boolean baseAmountPaid, boolean extraAmountPaid) {
        String dbUrl = "jdbc:mysql://localhost:3306/parkease";
        String dbUsername = "root";
        String dbPassword = "Password@1";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String query = "UPDATE bookingtable SET exittime = ?, payment_status = ?, status = ? WHERE bookingid = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, exitTime);
            statement.setString(2, baseAmountPaid ? "paid" : "unpaid");
            statement.setString(3, extraAmountPaid ? "paid" : "unpaid");
            statement.setInt(4, bookingId);

            int rowsUpdated = statement.executeUpdate();
            statement.close();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}



