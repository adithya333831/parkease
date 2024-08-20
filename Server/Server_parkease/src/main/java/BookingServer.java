
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookingServer {

    public static void handleRequest(String[] params, Socket clientSocket) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String vehicleType = params[1];
            String code = params[2];
            String vehicleNumber = params[3];
            String username = params[4];

            // Get current date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();
            String entryTime = dateFormat.format(currentDate);

            // Insert into bookingtable
            connection = getConnection();
            String insertQuery = "INSERT INTO bookingtable (code, category, entrytime, vehicleno,username) VALUES (?, ?, ?, ?,?)";
            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, vehicleType);
            preparedStatement.setString(3, entryTime);
            preparedStatement.setString(4, vehicleNumber);
            preparedStatement.setString(5, username);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Successful booking
                // Update parking_area table
                boolean updated = updateParkingArea(connection, vehicleType, code);
                if (updated) {
                    sendResponseToClient(clientSocket, "BOOKING_SUCCESS");
                } else {
                    sendResponseToClient(clientSocket, "BOOKING_FAILED");
                }
            } else {
                // Booking failed
                sendResponseToClient(clientSocket, "BOOKING_FAILED");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                sendResponseToClient(clientSocket, "BOOKING_FAILED");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/parkease";
        String user = "root";
        String password = "Password@1";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private static boolean updateParkingArea(Connection connection, String vehicleType, String code) {
        PreparedStatement updateStatement = null;
        boolean updated = false;

        try {
            String updateQuery = "";
            if (vehicleType.equalsIgnoreCase("Car")) {
                updateQuery = "UPDATE parking_area SET car = car - 1 WHERE code = ?";
            } else if (vehicleType.equalsIgnoreCase("Bike")) {
                updateQuery = "UPDATE parking_area SET bike = bike - 1 WHERE code = ?";
            }

            updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, code);
            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                updated = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (updateStatement != null) {
                    updateStatement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return updated;
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




