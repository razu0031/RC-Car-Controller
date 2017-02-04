package com.example.var.rccarcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    FloatingActionButton fabRight;
    FloatingActionButton fabLeft;
    FrameLayout toolbarMain;
    Switch bluetoothSwitch;
    ProgressBar mProgressBar;
    TextView newDevicesTitle;
    Button scanButton;
    TextView deviceConnectionStatus;

    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    String deviceName;

    //INITIALIZING FOR PAIRED DEVICES
    private SimpleAdapter dataSimpleAdapter;
    ArrayList<HashMap< String,String >> dataArrayList=new ArrayList<HashMap<String, String>>();
    HashMap<String,String> dataHashMap;

    String[] dataSources=new String[]{"DEVICE_NAME_KEY","DEVICE_PAIR_STATUS_KEY","DEVICE_MAC_ADDRESS_KEY"};
    int[] dataDestinations=new int[]{R.id.deviceNameListTextViewId,R.id.devicePairStatusListTextViewId,R.id.deviceMacAddressTextViewId};
    int resourceLayout=R.layout.row_of_device_list_view_layout;

    //INITIALIZING FOR NEW DEVICES
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    int page=0;
    int loop=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabRight = (FloatingActionButton) findViewById(R.id.fabRightId);
        toolbarMain = (FrameLayout) findViewById(R.id.toolbarMain);
        bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitch);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        newDevicesTitle = (TextView)findViewById(R.id.title_new_devices);
        deviceConnectionStatus = (TextView)findViewById(R.id.deviceConnectionStatusId);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothControlForFirstRun();
        pairedDevices = bluetoothAdapter.getBondedDevices();

        bluetoothSwitchControl();

        fabRight.setEnabled(true);

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    if (!bluetoothAdapter.isEnabled())
                    {
                        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnOn, 0);
                        addDataToShowAllDeviceListView();
                        bluetoothAdapter.cancelDiscovery();

                        Intent reStartIntent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(reStartIntent);
                        finish();
                    }

                }
                else
                {
                    if (bluetoothAdapter.isEnabled())
                    {
                        bluetoothAdapter.disable();
                    }

                }

            }

        });


        scanButton = (Button) findViewById(R.id.scanButtonId);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                addDataToShowAllDeviceListView();
                doDiscovery();
                //v.setVisibility(View.GONE);
            }
        });


        //INITIALIZE ARRAY ADAPTER FOR PAIRED DEVICES
        //dataSimpleAdapter=new SimpleAdapter(this, dataArrayList, resourceLayout, dataSources, dataDestinations);
        //pairedDeviceListView.setAdapter(dataSimpleAdapter);
        addDataToShowAllDeviceListView();

        //INITIALIZE ARRAY ADAPTER FOR NEWLY DISCOVERED DEVICES
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.row_of_newly_discovered_device_list_view_layout);

        //FIND AND SET UP THE LIST VIEW FOR NEWLY DISCOVERED DEVICES
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        //REGISTER FOR BROADCASTS WHEN A DEVICE IS DISCOVERED
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        //REGISTER FOR BROADCASTS WHEN DISCOVERY HAS FINISHED
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);


        /*myListView = (ListView)findViewById(R.id.device2ListViewId);
        // create the arrayAdapter that contains the BTDevices, and set it to the ListView
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        myListView.setAdapter(BTArrayAdapter);*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }


    //START DEVICE DISCOVER WITH THE BLUETOOTH ADAPTER
    private void doDiscovery()
    {
        //INDICATE SCANNING BAR IN THE TITLE
        mProgressBar.setVisibility(View.VISIBLE);
        newDevicesTitle.setText("New Devices");

        //TURN ON SUB-TITLE FOR NEW DEVICES
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        //IF WE'RE ALREADY DISCOVERING, STOP IT
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        //REQUEST DISCOVER FROM BLUETOOTH ADAPTER
        mNewDevicesArrayAdapter.clear();
        bluetoothAdapter.startDiscovery();
    }

    //THE ON-CLICK LISTENER FOR ALL DEVICES IN THE LIST VIEWS
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
            //CANCEL DISCOVERY BECAUSE IT'S COSTLY AND WE'RE ABOUT TO CONNECT
            bluetoothAdapter.cancelDiscovery();

            //GET THE DEVICE MAC ADDRESS, WHICH IS THE LAST 17 CHARS IN THE VIEW
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //CREATE THE RESULT INTENT AND INCLUDE THE MAC ADDRESS
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            //SET RESULT AND FINISH THIS ACTIVITY
            setResult(Activity.RESULT_OK, intent);
            //finish();

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            deviceName=device.getName();

            //device.createBond();//requires api level 19

            ///Method method = mNewDevicesArrayAdapter.getClass().getMethod("createBond", (Class[]) null);

            if (device.getBondState() == BluetoothDevice.BOND_BONDED)
            {
                unPairDevice(device);
            }

            else
            {
                showToast("Pairing...");
                pairDevice(device);
            }

            registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

        }
    };


    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    Toast.makeText(getApplicationContext(),"Paired",Toast.LENGTH_SHORT).show();
                    addDataToShowAllDeviceListView();
                    doDiscovery();
                }

                else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED)
                {
                    Toast.makeText(getApplicationContext(),"UnPaired  "+deviceName,Toast.LENGTH_SHORT).show();
                    addDataToShowAllDeviceListView();
                }

                mNewDevicesArrayAdapter.notifyDataSetChanged();
            }
        }
    };


    //THE BROADCAST RECEIVER THAT LISTENS FOR DISCOVERED DEVICES AND CHANGES THE TITLE WHEN DISCOVERY IS FINISHED
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            //WHEN DISCOVERY FINDS A DEVICE
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                //GET THE BLUETOOTH DEVICE OBJECT FROM THE INTENT
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //IF IT'S ALREADY PAIRED, SKIP IT, BECAUSE IT'S BEEN LISTED ALREADY
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\nMac Address   :  " + device.getAddress());
                    mNewDevicesArrayAdapter.notifyDataSetChanged();

                    /*dataHashMap = new HashMap<String, String>();
                    dataHashMap.put("DEVICE_NAME_KEY", device.getName());
                    dataHashMap.put("DEVICE_PAIR_STATUS_KEY", "Paired");
                    dataHashMap.put("DEVICE_MAC_ADDRESS_KEY", device.getAddress());

                    dataArrayList.add(dataHashMap);

                    dataSimpleAdapter = new SimpleAdapter(getApplicationContext(), dataArrayList, resourceLayout, dataSources, dataDestinations);
                    */
                }

            }

            //WHEN DISCOVERY IS FINISHED, CHANGE THE ACTIVITY TITLE
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                mProgressBar.setVisibility(View.INVISIBLE);

                if (mNewDevicesArrayAdapter.getCount() == 0)
                {
                    //mNewDevicesArrayAdapter.add("No devices found");
                    newDevicesTitle.setText("No device Found");
                }
            }
        }
    };


    public void fabRightClickAction(View view) {

        //fabRight.setEnabled(false);
        //fabLeft.setEnabled(true);

        // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

       /* if(bluetoothSwitch.isChecked())
        {
            bluetoothSwitch.setChecked(false);
            //Toast.makeText(this,)
        }
        else if(!bluetoothSwitch.isChecked())bluetoothSwitch.setChecked(true);
*/

    }


    public void addDataToShowAllDeviceListView()
    {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        dataArrayList.clear();

        ListView pairedDeviceListView = (ListView) findViewById(R.id.deviceListViewId);
        if(pairedDevices.size()>0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                dataHashMap=new HashMap<String, String>();
                dataHashMap.put("DEVICE_NAME_KEY",device.getName());
                dataHashMap.put("DEVICE_PAIR_STATUS_KEY","Paired");
                dataHashMap.put("DEVICE_MAC_ADDRESS_KEY",device.getAddress());

                dataArrayList.add(dataHashMap);

            }
        }

        dataSimpleAdapter=new SimpleAdapter(this, dataArrayList, resourceLayout, dataSources, dataDestinations);
        pairedDeviceListView.setAdapter(dataSimpleAdapter);

        pairedDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                bluetoothAdapter.cancelDiscovery();

                String deviceName=((TextView)view.findViewById(R.id.deviceNameListTextViewId)).getText().toString();
                String address=((TextView)view.findViewById(R.id.deviceMacAddressTextViewId)).getText().toString();

                /*BluetoothDevice device=bluetoothAdapter.getRemoteDevice(address);
                unPairDevice(device);*/

                Bundle bundle = new Bundle();
                bundle.putString("DEVICE_NAME_KEY", deviceName);
                bundle.putString("ADDRESS_KEY", address);

                Intent unPairOrConnectIntent = new Intent(MainActivity.this, ManagePairedDeviceActivity.class);
                unPairOrConnectIntent.putExtras(bundle);
                startActivity(unPairOrConnectIntent);
            }
        });

    }


    public void bluetoothControlForFirstRun()
    {
        if (!bluetoothAdapter.isEnabled())
        {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 3);

            bluetoothSwitch.setChecked(true);
            //addDataToShowAllDeviceListView();
            // bluetoothSwitchControl();
        }

        else if(bluetoothAdapter.isEnabled())
        {
            bluetoothSwitch.setChecked(true);
        }
    }

    public void bluetoothSwitchControl()
    {
        loop=0;
        final Handler myHandler=new Handler();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // while ((bluetoothSwitch.isChecked() && loop<=15) || !bluetoothAdapter.isEnabled())
                while(loop==0)
                {
                    try
                    {
                        Thread.sleep(1000);
                        myHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if(bluetoothAdapter.isEnabled())
                                {
                                    bluetoothSwitch.setChecked(true);
                                }
                                else //if(!bluetoothAdapter.isEnabled())
                                {
                                    bluetoothSwitch.setChecked(false);

                                    bluetoothAdapter.cancelDiscovery();
                                    dataArrayList.clear();
                                    dataSimpleAdapter.notifyDataSetChanged();
                                    mNewDevicesArrayAdapter.clear();
                                    newDevicesTitle.setVisibility(View.GONE);

                                }

                                //loop++;

                                if(bluetoothSwitch.isChecked() && !mProgressBar.isShown() )scanButton.setVisibility(View.VISIBLE);
                                else scanButton.setVisibility(View.INVISIBLE);
                            }
                        });

                    } catch (Exception e) {
                        System.out.println("exception in thread "+"my : "+e);
                    }
                }
            }
        }).start();

    }


    private void showToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        addDataToShowAllDeviceListView();

    }
}
