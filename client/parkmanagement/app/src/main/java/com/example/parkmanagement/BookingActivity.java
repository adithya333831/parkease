
package com.example.parkmanagement;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class BookingActivity extends AppCompatActivity {

    //private static final String SERVER_IP = "192.168.1.4";
    private static final String SERVER_IP = "192.168.249.159";
    //private static final String SERVER_IP = "192.168.6.41";
    private static final int SERVER_PORT = 6001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking);
        final String username = getIntent().getStringExtra("username");
        final String vehicleType = getIntent().getStringExtra("vehicleType");
        final String code = getIntent().getStringExtra("code");

        final EditText vehicleNumberField = findViewById(R.id.vehicleNumberField);
        final TextView codeField = findViewById(R.id.codeField); // Reference to code TextView
        final TextView categoryField = findViewById(R.id.categoryField); // Reference to category TextView

        // Set code and category values in the layout
        codeField.setText(code);
        categoryField.setText(vehicleType);

        Button bookButton = findViewById(R.id.bookButton);

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vehicleNumber = vehicleNumberField.getText().toString().trim();

                if (!vehicleNumber.isEmpty()) {
                    // Prepare parameters for booking request
                    String[] params = {"BOOK_PARKING", vehicleType, code, vehicleNumber, username};
                    sendBookingRequest(params, username); // Pass the username to the method
                } else {
                    Toast.makeText(BookingActivity.this, "Please enter vehicle number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendBookingRequest(final String[] params, final String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                    StringBuilder requestData = new StringBuilder();
                    for (String param : params) {
                        requestData.append(param).append("|");
                    }
                    outputStream.writeUTF(requestData.toString());

                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    final String response = inputStream.readUTF(); // Read response from server

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.equals("BOOKING_SUCCESS")) {
                                Toast.makeText(BookingActivity.this, "Booking successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(BookingActivity.this, UserHome.class);
                                intent.putExtra("username", username); // Pass the username back
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(BookingActivity.this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    outputStream.close();
                    inputStream.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BookingActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}









