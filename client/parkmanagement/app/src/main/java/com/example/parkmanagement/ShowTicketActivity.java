package com.example.parkmanagement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ShowTicketActivity extends AppCompatActivity {

    ListView listViewTickets;
    List<String> bookingIds;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showticket);

        listViewTickets = findViewById(R.id.listViewTickets);
        bookingIds = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookingIds) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.ticketlist, null);
                }

                TextView textViewName = view.findViewById(R.id.textViewName);
                String item = bookingIds.get(position);
                textViewName.setText(item);

                return view;
            }
        };
        listViewTickets.setAdapter(adapter);

        // Retrieve username and current date from intent
        String username = getIntent().getStringExtra("username");
        String currentDate = getIntent().getStringExtra("currentDate");

        // Call AsyncTask to fetch booking IDs for the current date
        new FetchBookingIdsTask().execute(username, currentDate);
    }

    private class FetchBookingIdsTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                //String serverIpAddress = "192.168.137.220";
                //String serverIpAddress = "192.168.1.4";
                //String serverIpAddress = "192.168.1.11";
                String serverIpAddress = "192.168.249.159";
                //String serverIpAddress = "192.168.6.41";


                int serverPort = 6001; // Your server port

                Socket socket = new Socket(serverIpAddress, serverPort);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                // Send request to fetch booking IDs for username and current date
                String username = params[0];
                String currentDate = params[1];
                String requestData = "FETCH_BOOKING_IDS|" + username + "|" + currentDate;
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(requestData);

                // Receive response from the server
                String response = inputStream.readUTF();

                // Close the socket and streams
                inputStream.close();
                outputStream.close();
                socket.close();

                // Split the response into booking IDs
                String[] bookingIdsArray = response.split("\\|");
                List<String> bookingIdsList = new ArrayList<>();
                for (String id : bookingIdsArray) {
                    bookingIdsList.add(id);
                }
                return bookingIdsList;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> bookingIds) {
            super.onPostExecute(bookingIds);
            if (bookingIds != null && !bookingIds.isEmpty()) {
                // Update the ListView with the fetched booking IDs
                ShowTicketActivity.this.bookingIds.clear();
                ShowTicketActivity.this.bookingIds.addAll(bookingIds);
                adapter.notifyDataSetChanged();
            } else {
                // Display no tickets message
                Toast.makeText(getApplicationContext(), "No tickets found for today", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



