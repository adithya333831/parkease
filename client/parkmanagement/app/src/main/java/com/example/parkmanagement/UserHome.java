package com.example.parkmanagement;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserHome extends AppCompatActivity {

    TextView nameField, usernameField, phoneNumberField;
    Spinner vehicleTypeSpinner;
    Button btnFind, btnShowTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userhome);

        Button btnpasswordchange = findViewById(R.id.changepass);
        btnpasswordchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserHome.this, PasswordChange.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
            }
        });

        // Initialize TextView fields
        nameField = findViewById(R.id.nameField);
        usernameField = findViewById(R.id.usernameField);
        phoneNumberField = findViewById(R.id.phoneNumberField);
        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);

        // Fetch username from intent
        String username = getIntent().getStringExtra("username");

        // Call AsyncTask to send username and fetch user details from the server
        new SendFetchDetails().execute(username);

        btnFind = findViewById(R.id.findButton);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected vehicle type
                String selectedVehicleType = vehicleTypeSpinner.getSelectedItem().toString();

                // Start ParkFind activity and pass the selected vehicle type
                Intent parkFindIntent = new Intent(UserHome.this, ParkFind.class);
                parkFindIntent.putExtra("vehicleType", selectedVehicleType);
                parkFindIntent.putExtra("username", username); // Pass username to ParkFind
                startActivity(parkFindIntent);
            }

        });

        btnShowTicket = findViewById(R.id.showticket);
        btnShowTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showTicketIntent = new Intent(UserHome.this, ShowTicketActivity.class);
                showTicketIntent.putExtra("username", username);

                // Get the current date in the required format
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                showTicketIntent.putExtra("currentDate", currentDate);

                startActivity(showTicketIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch username from intent
        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            // Call AsyncTask to send username and fetch user details from the server
            new SendFetchDetails().execute(username);
        } else {
            Toast.makeText(this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
        }
    }

    private class SendFetchDetails extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            try {
                //String serverIpAddress = "192.168.137.220";
                //String serverIpAddress = "192.168.1.4";
                //String serverIpAddress = "192.168.1.11";
                String serverIpAddress = "192.168.249.159";
                //String serverIpAddress = "192.168.6.41";


                int serverPort = 6001; // Your server port

                Socket socket = new Socket(serverIpAddress, serverPort);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                // Send request to fetch user details
                String username = params[0];
                String requestData = "FETCH_DETAILS|" + username;
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(requestData);

                // Receive response from the server
                String response = inputStream.readUTF();

                // Close the socket and streams
                inputStream.close();
                outputStream.close();
                socket.close();

                // Split the response into name, phone, and username
                return response.split("\\|");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] userDetails) {
            super.onPostExecute(userDetails);
            if (userDetails != null && userDetails.length == 3) {
                // Populate TextViews with user details
                nameField.setText(userDetails[0]);
                phoneNumberField.setText(userDetails[1]);
                usernameField.setText(userDetails[2]);
            } else {
                // Display error message
                Toast.makeText(getApplicationContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
            }
        }
    }
}






