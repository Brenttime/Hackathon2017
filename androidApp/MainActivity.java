package com.example.admin.sendatext;


import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import static android.provider.MediaStore.Audio.PlaylistsColumns.NAME;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            init();
        }
        catch(Exception error)
        {
            //error handled somewhat XD
        }
    }

    // The local server socket
    private BluetoothServerSocket mmServerSocket;

    // Output Stream & Input Stream
    private OutputStream outputStream;
    private InputStream inStream;

    //Connect Via Thread
    //private Connection connect = new Connection();

    private void init() throws IOException {

        Connection connect = new Connection();


        /*
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {

                Set<BluetoothDevice> pairedDevices = blueAdapter.getBondedDevices();

                if(pairedDevices.size() > 0) {
                    Object[] devices = (Object []) pairedDevices.toArray();

                    //init was required -- odd (clean up later)
                    BluetoothDevice device = (BluetoothDevice) devices[4];


                    ParcelUuid[] uuids = device.getUuids();
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());


                    //Create List Object From GUI
                    ListView deviceList = (ListView) findViewById(R.id.devices);

                    ArrayList<String> deviceNames = new ArrayList<String>();

                    for(BluetoothDevice bt : pairedDevices) {
                        deviceNames.add(bt.getName());
                    }

                    // This is the array adapter, it takes the context of the activity as a
                    // first parameter, the type of list view as a second parameter and your
                    // array as a third parameter.
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_list_item_1,
                            deviceNames );

                    deviceList.setAdapter(arrayAdapter);



                    socket.connect();
                    outputStream = socket.getOutputStream();
                    inStream = socket.getInputStream();

                }
                TextView debugText = (TextView) findViewById(R.id.textView3);


                /*
                while(true)
                {
                    write(" hello ");
                    run();
                    debugText.setText(inStream.toString());
                }

                //Log.e("error", "No appropriate paired devices.");
                //debugText.setText("No appropriate paired devices.");
            }

            else {
                //Log.e("error", "Bluetooth is disabled.");
                //debugText.setText("Bluetooth is disabled.");
            }

        }
        */

        //connect.run();
    }

    public void write(String s) throws IOException
    {
        outputStream.write(s.getBytes());
    }

    public void run()
    {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;

        while (true) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


