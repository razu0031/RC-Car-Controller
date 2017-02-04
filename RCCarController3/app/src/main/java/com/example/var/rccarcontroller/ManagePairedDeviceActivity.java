package com.example.var.rccarcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class ManagePairedDeviceActivity extends AppCompatActivity {

    TextView deviceNameTextView;
    Button connectButton;
    Button unPairButton;

    String mDeviceName;
    String mDeviceAddress;

    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_paired_device_activity);

        deviceNameTextView=(TextView)findViewById(R.id.deviceNameUnPairOrConnectTextViewId);
        connectButton=(Button)findViewById(R.id.connectButtonId);
        unPairButton=(Button)findViewById(R.id.unPairButtonId);

        Bundle bundle=getIntent().getExtras();
        mDeviceName=bundle.getString("DEVICE_NAME_KEY");
        mDeviceAddress=bundle.getString("ADDRESS_KEY");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceNameTextView.setText(mDeviceName);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        unPairButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
                Snackbar.make(v, "UnPairing...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                unPairDevice(device);
                registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("DEVICE_NAME_KEY", mDeviceName);
                bundle.putString("ADDRESS_KEY", mDeviceAddress);

                Intent unPairOrConnectIntent = new Intent(ManagePairedDeviceActivity.this, ConnectAndDataTransferActivity.class);
                unPairOrConnectIntent.putExtras(bundle);
                startActivity(unPairOrConnectIntent);

                finish();
            }
        });

    }


    private void unPairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state 		= intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING)
                {
                    //Toast.makeText(getApplicationContext(),"Paired",Toast.LENGTH_SHORT).show();
                    showToast("Paired");
                }

                else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED)
                {
                    //Toast.makeText(getApplicationContext(),"UnPaired  "+mDeviceName,Toast.LENGTH_SHORT).show();
                    showToast("UnPaired  "+mDeviceName);
                    finish();
                }

            }
        }
    };

    private void showToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
