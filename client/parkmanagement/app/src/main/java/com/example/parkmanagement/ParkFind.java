/*package com.example.parkmanagement;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class ParkFind extends AppCompatActivity {

    EditText editTextPlace;
    Button btnSearch;
    ListView listViewParkingAreas;
    ArrayAdapter<String> adapter;
    List<String> parkingAreas;
    String selectedVehicleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkfind);

        selectedVehicleType = getIntent().getStringExtra("vehicleType");

        editTextPlace = findViewById(R.id.editTextPlace);
        btnSearch = findViewById(R.id.btnSearch);
        listViewParkingAreas = findViewById(R.id.listViewParkingAreas);
        parkingAreas = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, parkingAreas) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_item_parking_area, null);
                }

                TextView textViewName = view.findViewById(R.id.textViewName);
                TextView textViewCode = view.findViewById(R.id.textViewCode);
                Button buttonBook = view.findViewById(R.id.buttonBook);
                Button buttonNavigate = view.findViewById(R.id.buttonNavigate);

                String item = parkingAreas.get(position);
                String[] parts = item.split(" - ");
                String name = parts[0];
                String code = parts[1];

                textViewName.setText(name);
                textViewCode.setText(code);

                buttonBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ParkFind.this, BookingActivity.class);
                        startActivity(intent);
                    }
                });

                buttonNavigate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLocationAndNavigate(code);
                    }
                });

                return view;
            }
        };
        listViewParkingAreas.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = editTextPlace.getText().toString();
                if (!place.isEmpty()) {
                    new FetchParkingAreasTask().execute(place);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a place", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocationAndNavigate(String code) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                return getLocationFromCode(params[0]);
            }

            @Override
            protected void onPostExecute(String location) {
                super.onPostExecute(location);
                if (location != null) {
                    startNavigation(location);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch location", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(code);
    }

    private void startNavigation(String location) {
        String uri = "google.navigation:q=" + location + "&mode=d";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private String getLocationFromCode(String code) {
        try {
            String serverIpAddress = "192.168.1.4";
            int serverPort = 6001;

            Socket socket = new Socket(serverIpAddress, serverPort);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            String requestData = "FETCH_LOCATION|" + code;
            outputStream.writeUTF(requestData);

            String response = inputStream.readUTF();

            inputStream.close();
            outputStream.close();
            socket.close();

            String[] parts = response.split("\\|");
            if (parts.length == 2 && parts[0].equals("LOCATION")) {
                return parts[1];
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class FetchParkingAreasTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            try {
                String serverIpAddress = "192.168.1.4";
                int serverPort = 6001;

                Socket socket = new Socket(serverIpAddress, serverPort);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                String place = params[0];
                String requestData = "FETCH_PARKING_AREAS|" + place + "|" + selectedVehicleType;
                outputStream.writeUTF(requestData);

                List<String> parkingAreas = new ArrayList<>();
                String response;
                while ((response = inputStream.readUTF()) != null) {
                    if (response.equals("END")) break;
                    parkingAreas.add(response);
                }

                inputStream.close();
                outputStream.close();
                socket.close();

                return parkingAreas;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            if (result != null) {
                parkingAreas.clear();
                parkingAreas.addAll(result);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to fetch parking areas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}*/

package com.example.parkmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class ParkFind extends AppCompatActivity {

    EditText editTextPlace;
    Button btnSearch;
    ListView listViewParkingAreas;
    ArrayAdapter<String> adapter;
    List<String> parkingAreas;
    String selectedVehicleType,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkfind);

        selectedVehicleType = getIntent().getStringExtra("vehicleType");
        username = getIntent().getStringExtra("username");

        editTextPlace = findViewById(R.id.editTextPlace);
        btnSearch = findViewById(R.id.btnSearch);
        listViewParkingAreas = findViewById(R.id.listViewParkingAreas);
        parkingAreas = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, parkingAreas) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_item_parking_area, null);
                }

                TextView textViewName = view.findViewById(R.id.textViewName);
                TextView textViewCode = view.findViewById(R.id.textViewCode);
                Button buttonBook = view.findViewById(R.id.buttonBook);
                Button buttonNavigate = view.findViewById(R.id.buttonNavigate);

                String item = parkingAreas.get(position);
                String[] parts = item.split(" - ");
                String name = parts[0];
                String code = parts[1];

                textViewName.setText(name);
                textViewCode.setText(code);

                buttonBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ParkFind.this, BookingActivity.class);
                        intent.putExtra("vehicleType", selectedVehicleType);
                        intent.putExtra("code", code);
                        intent.putExtra("username",username);
                        startActivity(intent);
                    }
                });

                buttonNavigate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLocationAndNavigate(code);
                    }
                });

                return view;
            }
        };
        listViewParkingAreas.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = editTextPlace.getText().toString();
                if (!place.isEmpty()) {
                    new FetchParkingAreasTask().execute(place);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a place", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocationAndNavigate(String code) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                return getLocationFromCode(params[0]);
            }

            @Override
            protected void onPostExecute(String location) {
                super.onPostExecute(location);
                if (location != null) {
                    startNavigation(location);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch location", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(code);
    }

    private void startNavigation(String location) {
        String uri = "google.navigation:q=" + location + "&mode=d";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private String getLocationFromCode(String code) {
        try {
            //String serverIpAddress = "192.168.1.4";
            //String serverIpAddress = "192.168.1.11";
            String serverIpAddress = "192.168.249.159";
            //String serverIpAddress = "192.168.137.220";
            //String serverIpAddress = "192.168.6.41";


            int serverPort = 6001;

            Socket socket = new Socket(serverIpAddress, serverPort);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            String requestData = "FETCH_LOCATION|" + code;
            outputStream.writeUTF(requestData);

            String response = inputStream.readUTF();

            inputStream.close();
            outputStream.close();
            socket.close();

            String[] parts = response.split("\\|");
            if (parts.length == 2 && parts[0].equals("LOCATION")) {
                return parts[1];
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class FetchParkingAreasTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            try {
                //String serverIpAddress = "192.168.1.4";
                //String serverIpAddress = "192.168.1.11";
                //String serverIpAddress = "192.168.137.220";
                String serverIpAddress = "192.168.249.159";
                //String serverIpAddress = "192.168.6.41";
                int serverPort = 6001;

                Socket socket = new Socket(serverIpAddress, serverPort);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                String place = params[0];
                String requestData = "FETCH_PARKING_AREAS|" + place + "|" + selectedVehicleType;
                outputStream.writeUTF(requestData);

                List<String> parkingAreas = new ArrayList<>();
                String response;
                while ((response = inputStream.readUTF()) != null) {
                    if (response.equals("END")) break;
                    parkingAreas.add(response);
                }

                inputStream.close();
                outputStream.close();
                socket.close();

                return parkingAreas;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            if (result != null) {
                parkingAreas.clear();
                parkingAreas.addAll(result);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to fetch parking areas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}











