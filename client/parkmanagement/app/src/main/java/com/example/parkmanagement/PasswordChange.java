package com.example.parkmanagement;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.DataOutputStream;
import java.net.Socket;

public class PasswordChange extends AppCompatActivity {

    EditText newPasswordField;
    Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);

        newPasswordField = findViewById(R.id.newPasswordField);
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordField.getText().toString();
                if (!newPassword.isEmpty()) {
                    // Send the new password to the server for changing
                    sendChangePasswordRequest(newPassword);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a new password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendChangePasswordRequest(String newPassword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //String serverIpAddress = "192.168.1.4";
                    //String serverIpAddress = "192.168.1.11";
                    //String serverIpAddress = "192.168.137.220";
                    String serverIpAddress = "192.168.249.159";
                    //String serverIpAddress = "192.168.6.41";


                    int serverPort = 6001; // Your server port

                    Socket socket = new Socket(serverIpAddress, serverPort);
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                    // Send request to change password
                    String username = getIntent().getStringExtra("username");
                    String requestData = "CHANGE_PASSWORD|" + username + "|" + newPassword;
                    outputStream.writeUTF(requestData);

                    // Close the socket and stream
                    outputStream.close();
                    socket.close();

                    // Show a message indicating successful password change
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PasswordChange.this, UserHome.class);
                            intent.putExtra("username", username); // Pass the username back
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}


