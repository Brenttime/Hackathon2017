package com.example.admin.sendatext;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Audio.PlaylistsColumns.NAME;


public class Connection extends Thread {

    // The local server socket
    private BluetoothServerSocket mmServerSocket;
    private static UUID MY_UUID = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66");
    private InputStream mmInStream;
    private OutputStream mmOutStream;


    public Connection() {
        run();
    }

    public void run() {
        BluetoothSocket socket = null;
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

        // Listen to the server socket if we're not connected
        while (true) {
            try {
                // Create a new listening server socket
                Log.d(TAG, ".....Initializing RFCOMM SERVER....");

                // MY_UUID is the UUID you want to use for communication
                mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                //mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);  you can also try using In Secure connection...

                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket = mmServerSocket.accept();
            }
            catch (Exception e) {
                //catch your exception here
            }

            try{
                Log.d(TAG, "Closing Server Socket.....");
                mmServerSocket.close();

                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the BluetoothSocket input and output streams
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();


                mmInStream = new DataInputStream(tmpIn);
                mmOutStream = new DataOutputStream(tmpOut);

                // here you can use the Input Stream to take the string from the client whoever is connecting
                //similarly use the output stream to send the data to the client
            }
            catch (Exception e) {
                //catch your exception here
            }

        }
    }

}