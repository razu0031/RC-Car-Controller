package com.example.var.rccarcontroller;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ConnectAndDataTransferActivity extends AppCompatActivity {

    TextView deviceNameTextView;
    //Button connectButton;
    Button forwardButton;
    Button forwardRightButton;
    Button forwardLeftButton;
    Button sharpRightButton;
    Button backwardRightButton;
    Button backwardLeftButton;
    Button backwardButton;
    Button sharpLeftButton;
    Button uTurnButton;
    Button disconnectButton;


    private ProgressDialog progressDialog;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothSocket mBluetoothSocket = null;
    private boolean isBtConnected = false;

    String mDeviceAddress = null;
    String mDeviceName;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_and_data_transfer_activity);

        deviceNameTextView=(TextView)findViewById(R.id.deviceNameUnPairOrConnectTextViewId);
        //connectButton=(Button)findViewById(R.id.connectButtonId);
        forwardButton=(Button)findViewById(R.id.forwardButtonId);
        forwardRightButton=(Button)findViewById(R.id.forwardRightButtonId);
        forwardLeftButton=(Button)findViewById(R.id.forwardLeftButtonId);
        sharpRightButton=(Button)findViewById(R.id.sharpRightButtonId);
        sharpLeftButton=(Button)findViewById(R.id.sharpLeftButtonId);
        backwardButton=(Button)findViewById(R.id.backwardButtonId);
        backwardRightButton=(Button)findViewById(R.id.backwardRightButtonId);
        backwardLeftButton=(Button)findViewById(R.id.backwardLeftButtonId);
        uTurnButton=(Button)findViewById(R.id.uTurnButtonId);
        disconnectButton=(Button)findViewById(R.id.disconnectButtonId);

        Bundle bundle=getIntent().getExtras();
        mDeviceName=bundle.getString("DEVICE_NAME_KEY");
        mDeviceAddress=bundle.getString("ADDRESS_KEY");

        deviceNameTextView.setText(mDeviceName);

        new ConnectBluetooth().execute();

        /*connectButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                turnOnLed();
            }
        });*/

        forwardButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                moveForward();
            }
        });

        forwardRightButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                moveForwardRight();
            }
        });

        forwardLeftButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                moveForwardLeft();
            }
        });

        sharpRightButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                moveSharpRight();
            }
        });

        sharpLeftButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                moveSharpLeft();
            }
        });

        backwardButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                moveBackward();
            }
        });

        backwardRightButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                moveBackwardRight();
            }
        });

        backwardLeftButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                moveBackwardLeft();
            }
        });

        uTurnButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                moveUTurn();
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mDisconnectDevice();
            }
        });

    }

    private void mDisconnectDevice()
    {
        if (mBluetoothSocket!=null) //If the mBluetoothSocket is busy
        {
            try
            {
                mBluetoothSocket.close(); //close connection
            }
            catch (IOException e)
            { showToast("Error");}
        }
        finish();
    }


    private void moveForward()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("w".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Forward Error");
            }
        }
    }


    private void moveForwardRight()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("e".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("moveForwardRight Error");
            }
        }
    }


    private void moveForwardLeft()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("q".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("moveForwardLeft Error");
            }
        }
    }


    private void moveSharpRight()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("d".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("moveSharpRight Error");
            }
        }
    }


    private void moveSharpLeft()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("a".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("moveSharpLeft Error");
            }
        }
    }


    private void moveBackward()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("x".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("moveBackward Error");
            }
        }
    }


    private void moveBackwardRight()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("c".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("moveBackwardRight Error");
            }
        }
    }


    private void moveBackwardLeft()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("z".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("moveBackwardLeft Error");
            }
        }
    }

    private void moveUTurn()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("s".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("moveUTurn");
            }
        }
    }

    private void Disconnect()
    {
        if (mBluetoothSocket!=null) //If the mBluetoothSocket is busy
        {
            try
            {
                mBluetoothSocket.close(); //close connection
            }
            catch (IOException e)
            { showToast("Error");}
        }
        finish(); //return to the first layout

    }

    private void turnOffLed()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("TF".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }

    private void turnOnLed()
    {
        if (mBluetoothSocket!=null)
        {
            try
            {
                mBluetoothSocket.getOutputStream().write("w".toString().getBytes());
            }
            catch (IOException e)
            {
                showToast("Error");
            }
        }
    }

    private void showToast(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }




    private class ConnectBluetooth extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(ConnectAndDataTransferActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (mBluetoothSocket == null || !isBtConnected)
                {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice mDeviceHere = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);//connects to the device's address and checks if it's available
                    mBluetoothSocket = mDeviceHere.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBluetoothSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                showToast("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                showToast("Connected.");
                isBtConnected = true;
            }
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mDisconnectDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDisconnectDevice();
    }
}
