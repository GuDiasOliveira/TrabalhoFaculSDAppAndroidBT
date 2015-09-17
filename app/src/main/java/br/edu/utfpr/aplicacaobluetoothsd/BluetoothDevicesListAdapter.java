package br.edu.utfpr.aplicacaobluetoothsd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GustavoDias on 17/09/2015.
 */
public class BluetoothDevicesListAdapter extends BaseAdapter {

    private Context context;

    private List<BluetoothDevice> bluetoothDevices;

    // Evento que escuta cada descoberta de um novo dispositivo bluetooth
    private BroadcastReceiver foundDeviceEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(foundDevice);
                notifyDataSetChanged();
            }
        }
    };


    public BluetoothDevicesListAdapter(Context context) {
        this.context = context;
        bluetoothDevices = new ArrayList<>();

        // Registrando o evento que escuta cada vez que um dispositivo bluetooth for descoberto. Aí adiciona ele na lista
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(foundDeviceEvent, intentFilter);
    }


    public boolean startScanningDevices() {
        // Iniciando o escaneamento de dispositivos bluetooth ao redor
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled())
            return false;
        return adapter.startDiscovery();
    }


    public void unregisterFoundDeviceListener() {
        // Para de escutar o evento de quando encontra um dispositivo. Aí a lista não será mais atualizada
        context.unregisterReceiver(foundDeviceEvent);
    }


    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }


    @Override
    public BluetoothDevice getItem(int position) {
        return bluetoothDevices.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView tvDeviceName = (TextView) layInflater.inflate(android.R.layout.simple_list_item_1, null);

        // Cada item da lista deve mostrar o nome do dispositivo
        BluetoothDevice device = getItem(position);
        tvDeviceName.setText(device.getName());

        return tvDeviceName;
    }
}
