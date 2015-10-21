package br.edu.utfpr.aplicacaobluetoothsd;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private BluetoothDevicesListAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAdapter = new BluetoothDevicesListAdapter(this);
        getListView().setAdapter(listAdapter);
        getListView().setOnItemClickListener(this);

        // Solicitando começar a procurar dispositivos ao redor
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            // Se o bluetooth já estiver ativado, já solicita o escaneamento de dispositivos
            listAdapter.startScanningDevices();
        } else {
            // Se não estiver ligado, solicita ao usuário para ativar antes de começar o escaneamento
            Intent intentRequestEnableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentRequestEnableBluetooth, REQUEST_ENABLE_BLUETOOTH);
        }
        Log.d("bb", "App Created");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Se o usuário aceitou ativar o bluetooth sem erro
            listAdapter.startScanningDevices();
        }
        // Ou o usuário recusou ativar o bluetooth ou algum erro ocorreu
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        listAdapter.unregisterFoundDeviceListener();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("bb", "item clicado");
        Intent i = new Intent(this, SensorActivity.class);
        BluetoothDevice chosenDevice = listAdapter.getItem(position);
        Log.d("bb", "dispositivo: " + chosenDevice.toString());
        i.putExtra(SensorActivity.EXTRA_BLUETOOTH_DEVICE, chosenDevice.getAddress());
        startActivity(i);
    }
}
