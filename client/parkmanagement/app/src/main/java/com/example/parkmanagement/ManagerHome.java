package com.example.parkmanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ManagerHome extends AppCompatActivity {

    TextView nameField, usernameField, codeField;
    Button btnBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managerhome);

        // Initialize TextView fields
        nameField = findViewById(R.id.nameField);
        usernameField = findViewById(R.id.usernameField);
        codeField = findViewById(R.id.codeField);

        // Fetch manager ID from intent
        String managerId = getIntent().getStringExtra("managerId");

        // Call AsyncTask to send manager ID and fetch manager details from the server
        new SendFetchDetails().execute(managerId);

        Button btnPasswordChange = findViewById(R.id.changepass);
        btnPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerHome.this, ManagerPasswordChange.class);
                intent.putExtra("managerId", managerId);
                startActivity(intent);
            }
        });

        btnBookings = findViewById(R.id.findButton);
        btnBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bookings activity
                Intent intent = new Intent(ManagerHome.this, ManagerBookingListActivity.class);
                intent.putExtra("managerId", managerId);
                startActivity(intent);
            }
        });
    }

    private class SendFetchDetails extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            try {
                //String serverIpAddress = "192.168.1.4";
                //String serverIpAddress = "192.168.137.220";
                //String serverIpAddress = "192.168.1.11";
                String serverIpAddress = "192.168.249.159";
                //String serverIpAddress = "192.168.6.41";
                int serverPort = 6001; // Your server port

                Socket socket = new Socket(serverIpAddress, serverPort);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                // Send request to fetch manager details
                String managerId = params[0];
                String requestData = "FETCH_MANAGER_DETAILS|" + managerId;
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(requestData);

                // Receive response from the server
                String response = inputStream.readUTF();

                // Close the socket and streams
                inputStream.close();
                outputStream.close();
                socket.close();

                // Split the response into name, manager ID, and code
                return response.split("\\|");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] managerDetails) {
            super.onPostExecute(managerDetails);
            if (managerDetails != null && managerDetails.length == 3) {
                // Populate TextViews with manager details
                nameField.setText(managerDetails[0]);
                usernameField.setText(managerDetails[1]);
                codeField.setText(managerDetails[2]);
            } else {
                // Display error message
                Toast.makeText(getApplicationContext(), "Failed to fetch manager details", Toast.LENGTH_SHORT).show();
            }
        }
    }
}




