package com.example.parkmanagement;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ManagerLoginActivity extends AppCompatActivity {

    EditText editTextManagerId, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managerlogin);

        editTextManagerId = findViewById(R.id.editTextManagerid);
        editTextPassword = findViewById(R.id.editTextPassword);

        Button btnLoginManager = findViewById(R.id.buttonLoginUser);
        btnLoginManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from EditText fields
                String managerId = editTextManagerId.getText().toString();
                String password = editTextPassword.getText().toString();

                // Check if any field is empty
                if (managerId.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call AsyncTask to send login credentials to the server
                new SendLoginCredential().execute(managerId, password);
            }
        });
    }

    private class SendLoginCredential extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                //String serverIpAddress = "192.168.1.4";
                //String serverIpAddress = "192.168.1.11";
                String serverIpAddress = "192.168.249.159";
                //String serverIpAddress = "192.168.137.220";// Change this to your server's IP address
                //String serverIpAddress = "192.168.6.41";


                int serverPort = 6001; // Your server port
                Socket socket = new Socket(serverIpAddress, serverPort);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                // Send request to check manager existence
                String managerId = params[0];
                String password = params[1];
                String requestData = "MANAGER_LOGIN|" + managerId + "|" + password;
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(requestData);

                // Receive response from the server
                String response = inputStream.readUTF();

                // Close the socket and streams
                inputStream.close();
                outputStream.close();
                socket.close();

                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("managerexists")) {
                // Manager exists, move to ManagerHome
                Intent intent = new Intent(ManagerLoginActivity.this, ManagerHome.class);
                intent.putExtra("managerId", editTextManagerId.getText().toString());
                startActivity(intent);
                finish(); // Close current activity
            } else {
                // Manager doesn't exist, display toast message
                Toast.makeText(getApplicationContext(), "Invalid Manager ID or Password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



