
import java.io.*;
import java.net.*;
import java.sql.*;

public class MainServer {
    public static void main(String[] args) {
        final int SERVER_PORT = 6001;

        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Main Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle client request in a separate thread
                new Thread(() -> {
                    try {
                        DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                        String requestData = inputStream.readUTF();

                        String[] parts = requestData.split("\\|");
                        String requestType = parts[0];

                        switch (requestType) {
                            case "LOGIN":
                                LoginServer.handleRequest(clientSocket, requestData);
                                break;
                            case "MANAGER_LOGIN":
                                ManagerLoginServer.handleRequest(clientSocket, requestData);
                                break;
                            case "REGISTER":
                                RegisterServer.handleRequest(clientSocket, requestData);
                                break;
                            case "FETCH_DETAILS":
                                UserHomeServer.handleRequest(clientSocket, requestData);
                                break;
                            case "FETCH_MANAGER_DETAILS":
                                ManagerHomeServer.handleRequest(clientSocket, requestData);
                                break;
                            case "FETCH_PARKING_AREAS":
                                ParkFindServer.handleRequest(clientSocket, parts);
                                break;
                            case "FETCH_LOCATION":
                                FetchLocationServer.handleRequest(clientSocket, parts[1]);
                                break;
                            case "CHANGE_PASSWORD":
                                ChangePasswordServer.handleRequest(clientSocket, requestData);
                                break;
                            case "CHANGE_MANAGER_PASSWORD":
                                ManagerChangePasswordServer.handleRequest(clientSocket, requestData);
                                break;
                            case "BOOK_PARKING":
                                BookingServer.handleRequest(parts, clientSocket);
                                break;
                            case "FETCH_BOOKING_IDS":
                                ShowTicketServer.handleRequest(clientSocket, parts);
                                break;
                            case "FETCH_MANAGER_BOOKINGS":
                                FetchManagerBookingsServer.handleRequest(clientSocket, parts);
                                break;
                            case "UPDATE_BOOKING":
                                BookingUpdateServer.handleRequest(clientSocket, parts);
                                break;
                            default:
                                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                                outputStream.writeUTF("Invalid request type");
                                outputStream.close();
                        }

                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
















