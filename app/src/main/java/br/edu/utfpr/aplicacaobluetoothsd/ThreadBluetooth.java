package br.edu.utfpr.aplicacaobluetoothsd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ThreadBluetooth extends Thread {
    private SensorActivity activity;
    private BluetoothDevice device;
    private boolean cont = true;

    public ThreadBluetooth(String address, SensorActivity activity) {
        this.activity = activity;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        device =  adapter.getRemoteDevice(address);
     }

    @Override
    public void run() {
        while (cont) {
            try {
                Log.i("bb", "antes");
                BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"));
                socket.connect();
                Log.i("bb", "durante");
                OutputStream msg = socket.getOutputStream();
                String stringLevel = "" + activity.getLevel();
                Log.i("bb", "level: " + stringLevel);
                msg.write(stringLevel.getBytes());
                ThreadBluetooth.sleep(1000);
                socket.close();
                Log.i("bb", "depois");

            } catch (IOException e) {
                Log.i("bb", "dispositivo nao encontrado");
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void finish() {
        cont = false;
    }
}
