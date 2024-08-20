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

public class UserLoginActivity extends AppCompatActivity {
    EditText editTextUsername,editTextPassword,UsernameLogin;
    Button btnLoginManager, btnRegisterUser,btnLoginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);

        btnLoginManager = findViewById(R.id.buttonLoginManager);
        btnLoginManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLoginActivity.this, ManagerLoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegisterUser = findViewById(R.id.buttonRegisterUser);
        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
                startActivity(intent);
            }
        });

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        btnLoginUser = findViewById(R.id.buttonLoginUser);
        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from EditText fields
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // Check if any field is empty
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call AsyncTask to send registration data to the server
                new SendLoginCredential().execute(username, password);
            }
        });
    }

    private class SendLoginCredential extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                //String serverIpAddress = "192.168.6.99"; // Change this to your server's IP address
                //String serverIpAddress = "192.168.1.4";
                //String serverIpAddress = "192.168.1.11";
                //String serverIpAddress ="192.168.137.220";
                String serverIpAddress = "192.168.249.159";
                //String serverIpAddress = "192.168.6.41";


                int serverPort = 6001; // Your server port
                //int serverPort = 80;
                Socket socket = new Socket(serverIpAddress, serverPort);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                // Send request to check user existence
                String username = params[0];
                String password = params[1];
                String requestData = "LOGIN|" + username + "|" + password ;
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
            if (result.equals("userexists")) {
                // User exists, move to MainActivity

                UsernameLogin= findViewById(R.id.editTextUsername);
                String username = UsernameLogin.getText().toString();
                Intent intent = new Intent(UserLoginActivity.this,UserHome.class);
                startActivity(intent);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
                 // Close current activity
            } else {
                // User doesn't exist, display toast message
                Toast.makeText(getApplicationContext(), "Please register user", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
/*import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class UserLoginActivity extends AppCompatActivity {
    private static final String TAG = "UserLoginActivity";
    EditText editTextUsername, editTextPassword, UsernameLogin;
    Button btnLoginManager, btnRegisterUser, btnLoginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);

        btnLoginManager = findViewById(R.id.buttonLoginManager);
        btnLoginManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLoginActivity.this, ManagerLoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegisterUser = findViewById(R.id.buttonRegisterUser);
        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
                startActivity(intent);
            }
        });

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        btnLoginUser = findViewById(R.id.buttonLoginUser);
        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                new SendLoginCredential().execute(username, password);
            }
        });
    }

    private class SendLoginCredential extends AsyncTask<String, Void, String> {
        private static final String TAG = "SendLoginCredential";

        @Override
        protected String doInBackground(String... params) {
            try {
                String serverIpAddress = "192.168.249.159";
                int serverPort = 6001;
                Log.d(TAG, "Attempting to connect to " + serverIpAddress + ":" + serverPort);
                Socket socket = new Socket(serverIpAddress, serverPort);
                Log.d(TAG, "Connected to server");

                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                String username = params[0];
                String password = params[1];
                String requestData = "LOGIN|" + username + "|" + password;
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(requestData);
                Log.d(TAG, "Sent data: " + requestData);

                String response = inputStream.readUTF();
                Log.d(TAG, "Received response: " + response);

                inputStream.close();
                outputStream.close();
                socket.close();

                return response;
            } catch (Exception e) {
                Log.e(TAG, "Error connecting to server", e);
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute result: " + result);
            if (result.equals("userexists")) {
                UsernameLogin = findViewById(R.id.editTextUsername);
                String username = UsernameLogin.getText().toString();
                Intent intent = new Intent(UserLoginActivity.this, UserHome.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish(); // Close current activity
            } else {
                Toast.makeText(getApplicationContext(), "Please register user", Toast.LENGTH_SHORT).show();
            }
        }
    }
}*/





