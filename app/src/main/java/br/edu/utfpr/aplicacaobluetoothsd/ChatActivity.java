package br.edu.utfpr.aplicacaobluetoothsd;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatActivity extends ListActivity implements View.OnClickListener {

    public static final String EXTRA_BLUETOOTH_DEVICE = "bluetooth_device";

    private ArrayList<String> chatThread = new ArrayList<>();
    private BluetoothDevice bluetoothDevice;
    private Button bSend;

    private EditText etMessage;

    private BluetoothServerSocket btServerSocket;
    private BluetoothSocket btSocket;

    private boolean isServerConnected, isClientConnected, isChatRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Serializer serializer = (Serializer) getIntent().getSerializableExtra(EXTRA_BLUETOOTH_DEVICE);
        bluetoothDevice = (BluetoothDevice) serializer.getObject();

        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatThread));

        bSend = (Button) findViewById(R.id.bSend);
        bSend.setOnClickListener(this);
    }


    private void addMessage(boolean you, String message) {
        String label = you ? getResources().getString(R.string.message_label_person_you) : bluetoothDevice.getName() + ":";
        chatThread.add(0, label + " " + message);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        if (v == bSend) {
            String messageToSend = etMessage.getText().toString().trim();
            try {
                DataOutputStream out = new DataOutputStream(btSocket.getOutputStream());
                out.writeUTF(messageToSend);
            } catch (IOException e) {
                Log.e("Enviar mensagem", "Falha ao enviar", e);
            }
            addMessage(true, messageToSend);
        }
    }



    private class StartBluetoothChatTask extends AsyncTask<Void, Void, Throwable> {


        @Override
        protected Throwable doInBackground(Void... params) {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            try {
                // Conectando-se como Servidor
                btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(
                        bluetoothDevice.getName(),
                        bluetoothDevice.getUuids()[0].getUuid());

                // Conectando-se como Cliente
                btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(bluetoothDevice.getUuids()[0].getUuid());
            } catch (IOException e) {
                return e;
            }

            return null;
        }


        @Override
        protected void onPostExecute(Throwable throwable) {
            if (throwable == null) {
                // Fazendo as conexões
                new ConnectToClientTask().execute();
                new ConnectToServerTask().execute();
            } else {
                Log.e("Criar conexões bluetooth", "Falha ao tentar criar as conexões", throwable);
            }
        }
    }



    private class ConnectToClientTask extends AsyncTask<Void, Void, Throwable> {


        @Override
        protected Throwable doInBackground(Void... params) {
            try {
                isServerConnected = false;
                btSocket = btServerSocket.accept();
                isServerConnected = true;
            } catch (IOException e) {
                return e;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Throwable throwable) {
            if (throwable == null) {
                if (isServerConnected && isClientConnected)
                    new RunChatTask().execute();
            } else {
                Log.e("Conectar servidor bluetooth", "Falha ao tentar se conectar ao cliente");
            }
        }
    }



    private class ConnectToServerTask extends AsyncTask<Void, Void, Throwable> {


        @Override
        protected Throwable doInBackground(Void... params) {
            try {
                isClientConnected = false;
                btSocket.connect();
                isClientConnected = true;
            } catch (IOException e) {
                return e;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Throwable throwable) {
            if (throwable == null) {
                if (isClientConnected && isServerConnected)
                    new RunChatTask().execute();
            }
            else {
                Log.e("Conectar cliente bluetooth", "Falha ao tentar se conectar ao servidor", throwable);
            }
        }
    }



    private class RunChatTask extends AsyncTask<Void, String, Throwable> {


        @Override
        protected Throwable doInBackground(Void... params) {
            if (isChatRunning)
                return null;
            isChatRunning = true;
            while(true) {
                try {
                    InputStream is = btSocket.getInputStream();
                    DataInputStream input = new DataInputStream(is);
                    String messageToReceive = input.readUTF();
                    publishProgress(messageToReceive);
                } catch (IOException e) {
                    return e;
                }
            }
        }


        @Override
        protected void onProgressUpdate(String... values) {
            String messageReceived = values[0];
            addMessage(false, messageReceived);
        }


        @Override
        protected void onPostExecute(Throwable throwable) {
            if (throwable == null)
                return;
            Log.e("Chat bluetooth", "Problema ao tentar ler mensagem do bluetooth do outro aparelho", throwable);
        }
    }
}
