package com.example.parkmanagement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManagerBookingListActivity extends AppCompatActivity {

    ListView listViewBookings;
    List<Booking> bookingList;
    BookingAdapter adapter;
    String managerUsername;
    Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_booking_list);

        listViewBookings = findViewById(R.id.listViewBookings);
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList);
        listViewBookings.setAdapter(adapter);
        managerUsername = getIntent().getStringExtra("managerId");
        refresh = findViewById(R.id.refresh);



        // Replace with your server IP address and port
        // String serverIpAddress = "192.168.1.4";
        //String serverIpAddress = "192.168.1.11";
        String serverIpAddress = "192.168.249.159";
        //String serverIpAddress = "192.168.6.41";
        int serverPort = 6001;

        // Replace with manager's username
        //String managerUsername = "1000";


        // Fetch manager bookings from the server
        new FetchManagerBookingsTask().execute(serverIpAddress, String.valueOf(serverPort), managerUsername);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchManagerBookingsTask().execute(serverIpAddress, String.valueOf(serverPort), managerUsername);
            }
        });
    }

    private class FetchManagerBookingsTask extends AsyncTask<String, Void, List<Booking>> {

        @Override
        protected List<Booking> doInBackground(String... params) {
            String serverIp = params[0];
            int serverPort = Integer.parseInt(params[1]);
            String managerUsername = params[2];

            List<Booking> bookings = fetchManagerBookings(serverIp, serverPort, managerUsername);
            return bookings;
        }

        @Override
        protected void onPostExecute(List<Booking> bookings) {
            if (bookings != null && !bookings.isEmpty()) {
                // Update UI with manager bookings
                bookingList.clear();
                bookingList.addAll(bookings);
                adapter.notifyDataSetChanged();
            } else {
                // Display error or no bookings message
                Toast.makeText(ManagerBookingListActivity.this, "No bookings found for manager", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<Booking> fetchManagerBookings(String serverIp, int serverPort, String managerUsername) {
        List<Booking> bookings = new ArrayList<>();
        Socket socket = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {
            socket = new Socket(serverIp, serverPort);
            outputStream = new DataOutputStream(socket.getOutputStream());

            // Send request to fetch manager bookings
            String requestData = "FETCH_MANAGER_BOOKINGS|" + managerUsername;
            outputStream.writeUTF(requestData);

            // Receive response from the server
            inputStream = new DataInputStream(socket.getInputStream());
            String response = inputStream.readUTF();

            // Process response
            String[] bookingsArray = response.split("\n");
            for (String bookingData : bookingsArray) {
                String[] parts = bookingData.split("\\|");
                if (parts.length == 9) {
                    int bookingId = Integer.parseInt(parts[0]);
                    String code = parts[1];
                    String category = parts[2];
                    String vehicleNumber = parts[3];
                    String entryTime = parts[4];
                    int extraAmount = Integer.parseInt(parts[5]);
                    String exitTime = parts[6];
                    String paymentStatus = parts[7];
                    String status = parts[8];

                    Booking booking = new Booking(bookingId, code, category, vehicleNumber, entryTime, extraAmount, exitTime, paymentStatus, status);
                    bookings.add(booking);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (socket != null) socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bookings;
    }

    private class BookingAdapter extends ArrayAdapter<Booking> {

        public BookingAdapter(List<Booking> bookings) {
            super(ManagerBookingListActivity.this, 0, bookings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Booking booking = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_booking, parent, false);
            }

            // Lookup view for data population
            TextView tvBookingId = convertView.findViewById(R.id.tvBookingId);
            TextView tvVehicleNumber = convertView.findViewById(R.id.tvVehicleNumber);
            TextView tvEntryTime = convertView.findViewById(R.id.tvEntryTime);
            TextView tvExitTime = convertView.findViewById(R.id.tvExitTime);
            TextView tvPaymentStatus = convertView.findViewById(R.id.tvPaymentStatus);
            EditText editTextExitTime = convertView.findViewById(R.id.editTextExitTime);
            CheckBox checkBoxBaseAmount = convertView.findViewById(R.id.checkBoxBaseAmount);
            CheckBox checkBoxExtraAmount = convertView.findViewById(R.id.checkBoxExtraAmount);
            TextView tvExtraAmount = convertView.findViewById(R.id.tvExtraAmount); // Added

            // Populate the data into the template view using the data object
            if (booking != null) {
                tvBookingId.setText("Booking ID: " + booking.getBookingId());
                tvVehicleNumber.setText("Vehicle Number: " + booking.getVehicleNumber());
                tvEntryTime.setText("Entry Time: " + booking.getEntryTime());
                tvExitTime.setText("Exit Time: " + booking.getExitTime());
                tvPaymentStatus.setText("Payment Status: " + booking.getPaymentStatus());
                tvExtraAmount.setText("Extra Amount: " + booking.getExtraAmount()); // Display extra amount

                // Set OnClickListener for update button
                Button buttonUpdate = convertView.findViewById(R.id.buttonUpdate);
                buttonUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String exitTime = editTextExitTime.getText().toString().trim();
                        boolean baseAmountPaid = checkBoxBaseAmount.isChecked();
                        boolean extraAmountPaid = checkBoxExtraAmount.isChecked();

                        // Validate and update booking
                        if (!exitTime.isEmpty()) {
                            new UpdateBookingTask().execute(booking.getBookingId(), exitTime, baseAmountPaid, extraAmountPaid);
                        } else {
                            Toast.makeText(ManagerBookingListActivity.this, "Please enter exit time", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            // Return the completed view to render on screen
            return convertView;
        }
    }

    private class UpdateBookingTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            int bookingId = (int) params[0];
            String exitTime = (String) params[1];
            boolean baseAmountPaid = (boolean) params[2];
            boolean extraAmountPaid = (boolean) params[3];

            // Replace with your server IP address and port
            // String serverIpAddress = "192.168.1.4";
            //String serverIpAddress = "192.168.1.11";
            String serverIpAddress = "192.168.249.159";
            //String serverIpAddress = "192.168.6.41";
            int serverPort = 6001;

            // Send update request to the server
            return sendUpdateRequest(serverIpAddress, serverPort, bookingId, exitTime, baseAmountPaid, extraAmountPaid);
        }

        @Override
        protected void onPostExecute(Boolean updateSuccess) {
            if (updateSuccess) {
                Toast.makeText(ManagerBookingListActivity.this, "Booking updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ManagerBookingListActivity.this, "Failed to update booking", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean sendUpdateRequest(String serverIp, int serverPort, int bookingId, String exitTime, boolean baseAmountPaid, boolean extraAmountPaid) {
        Socket socket = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {
            socket = new Socket(serverIp, serverPort);
            outputStream = new DataOutputStream(socket.getOutputStream());

            // Send update request to the server
            String requestData = "UPDATE_BOOKING|" + bookingId + "|" + exitTime + "|" + baseAmountPaid + "|" + extraAmountPaid;
            outputStream.writeUTF(requestData);

            // Receive response from the server
            inputStream = new DataInputStream(socket.getInputStream());
            String response = inputStream.readUTF();

            // Process response
            if ("UPDATE_SUCCESS".equals(response)) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (socket != null) socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}










