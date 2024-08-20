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

public class UserRegisterActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword, editTextName, editTextPhone;
    Button buttonRegisterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userregister);

        Button btnGoBackUserLogin = findViewById(R.id.buttonGoBackUserLogin);
        btnGoBackUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRegisterActivity.this, UserLoginActivity.class);
                startActivity(intent);
            }
        });

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonRegisterUser = findViewById(R.id.buttonRegisterUser);

        buttonRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from EditText fields
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String name = editTextName.getText().toString();
                String phone = editTextPhone.getText().toString();

                // Check if any field is empty
                if (username.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call AsyncTask to send registration data to the server
                new SendRegistrationDataTask().execute(username, password, name, phone);
            }
        });
    }

    private class SendRegistrationDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // Connect to the server and send registration data
            try {
                //String serverIpAddress = "192.168.6.99"; // Change this to your server's IP address
                //String serverIpAddress = "192.168.1.4";
                //String serverIpAddress = "192.168.1.11";
                //String serverIpAddress = "192.168.137.220";
                String serverIpAddress = "192.168.249.159";
                //String serverIpAddress = "192.168.6.41";


                int serverPort = 6001; // Your server port

                Socket socket = new Socket(serverIpAddress, serverPort);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                // Send registration data to the server
                String username = params[0];
                String password = params[1];
                String name = params[2];
                String phone = params[3];
                String requestData = "REGISTER|" + username + "|" + password + "|" + name + "|" + phone;
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
            if (result.equals("duplicate")) {
                Toast.makeText(getApplicationContext(), "Try different username", Toast.LENGTH_SHORT).show();
            } else if (result.equals("added")) {
                Toast.makeText(getApplicationContext(), "User added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserRegisterActivity.this, UserLoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }
}














