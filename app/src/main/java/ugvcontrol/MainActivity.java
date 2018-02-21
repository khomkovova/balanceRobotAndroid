package ugvcontrol;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vasyl.androidhttpclient.R;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import ugvcontrol.arduino.ArduinoConnector;
import ugvcontrol.arduino.DefaultCommandWriter;
import ugvcontrol.client.CommandClient;
import ugvcontrol.client.CommandServerListener;
import ugvcontrol.client.CommandWebSocketClient;
import ugvcontrol.utils.Command;
import ugvcontrol.utils.UiLog;

public class MainActivity extends Activity {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final String DEFAULT_SERVER_ENDPOINT = "http://35.197.205.200:8080";


    Button buttonShowDevices;
    Button buttonStart;
    Button buttonStop;
    Button buttonSetSleepTime;
    Button buttonGetCommand;
    Button buttonSendCommand;

    EditText editTextServerEndpoint;
    EditText editTextDevicesList;
    EditText editTextSleepTime;
    EditText editTextSpeedCommand;
    EditText editTextDirCommand;

    TextView textViewCommand;

    UsbManager usbManager;
    UsbDevice device = null;

    static CommandServerListener commandServerListener;
    static ArduinoConnector arduinoConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        buttonShowDevices = (Button) findViewById(R.id.buttonShowDevices);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonSetSleepTime = (Button) findViewById(R.id.buttonSetSleepTime);
        buttonGetCommand = (Button) findViewById(R.id.buttonGetCommand);
        buttonSendCommand = (Button) findViewById(R.id.buttonSendCommand);

        editTextServerEndpoint = (EditText) findViewById(R.id.editTextsServerEndpoint);
        editTextDevicesList = (EditText) findViewById(R.id.editTextDevicesList);
        editTextSleepTime = (EditText) findViewById(R.id.editTextSleepTime);
        editTextSpeedCommand = (EditText) findViewById(R.id.editTextSpeedCommand);
        editTextDirCommand = (EditText) findViewById(R.id.editTextDirCommand);
        editTextServerEndpoint.setText(DEFAULT_SERVER_ENDPOINT);
        editTextSleepTime.setText("1000");

        textViewCommand = (TextView) findViewById(R.id.textViewCommand);

        UiLog.init(this, editTextDevicesList);

        usbManager  = (UsbManager) getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        final PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                editTextDevicesList.append("[onReceive]\n");
                device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                showDeviceInfo(device);

//                UsbDeviceConnection connection = usbManager.openDevice(device);
//                UsbSerialDriver serialDriver = UsbSerialProber.getDefaultProber().probeDevice(device);

//                UsbSerialPort port = serialDriver.getPorts().get(0);
//                try {
//                    port.open(connection);
//                    byte[] data = new byte[1];
//                    data[0] = 0x1f;
//                    port.write(data, 1000);
//                } catch (IOException e) {
//                    editTextDevicesList.append(e.getMessage() + "\n");
//                    e.printStackTrace();
//                }

                editTextDevicesList.append("[/onReceive]\n");
            }
        }, filter);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            usbManager.requestPermission(device, permissionIntent);
        }

        buttonShowDevices.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

                editTextDevicesList.append("Devices count: " + deviceList.size() + "\n");

                for (UsbDevice device : deviceList.values()) {
                    showDeviceInfo(device);
                }
            }
        });

        buttonStart.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                URI uri = null;
                try {
                     uri = new URI("ws://35.197.205.200:8080");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                WebSocketClient webSocketClient = new CommandWebSocketClient(uri);
                webSocketClient.connect();

//                commandServerListener = new CommandServerListener(new DefaultCommandHandler());
                arduinoConnector = new ArduinoConnector(new DefaultCommandWriter(usbManager, device));
//
//                new Thread(commandServerListener).start();
                new Thread(arduinoConnector).start();
            }
        });

        buttonStop.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                commandServerListener.stop();
                arduinoConnector.stop();
            }
        });

        buttonSetSleepTime.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int sleepTime = Integer.parseInt(editTextSleepTime.getText().toString());
                if (sleepTime > 0) {
                    commandServerListener.setSleepTime(sleepTime);
                }
            }
        });

        buttonGetCommand.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String serverEndpoint = editTextServerEndpoint.getText().toString();
                System.out.println("Server endpoint: "  + serverEndpoint);
                Log.d("[server endpoint]", serverEndpoint);
                CommandClient commandClient = new CommandClient(serverEndpoint);
                String response = commandClient.getRawResponse();
                Log.d("[client response]", response);
                Log.e("[client response]", response);
                Log.i("[client response]", response);
                textViewCommand.setText(
                        "response: " + response);
            }
        });

        buttonSendCommand.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                byte commandSpeed = Byte.parseByte(editTextSpeedCommand.getText().toString());
                byte commandDir = Byte.parseByte(editTextDirCommand.getText().toString());
                final Command[] commands = {
                        new Command(1, 15), new Command(3, 15),
                        new Command(1, 0),  new Command(4, 15),
                        new Command(2, 15),  new Command(3, 15),
                        new Command(1, 15),  new Command(4, 15),
                        new Command(1, 0),  new Command(3, 15),
                        new Command(2, 15),  new Command(4, 15),
                        new Command(2, 0),  new Command(3, 15),

                };


                Handler handler = new Handler();

                for ( int i = 0; i < commands.length; i += 2) {
                    final byte[] data = {commands[i].getByte(),commands[i+1].getByte()};
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    UsbDeviceConnection connection = usbManager.openDevice(device);
                                    UsbSerialPort port = UsbSerialProber
                                            .getDefaultProber()
                                            .probeDevice(device)
                                            .getPorts()
                                            .get(0);

                                    editTextDevicesList.append("commands: [i] "+ data[0] +  "\n");
                                    editTextDevicesList.append("commands: [i+1] "+ data[1] +  "\n");
                                    port.open(connection);
                                    port.write(data, 1000);
                                    port.close();
                                    connection.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, ((i / 2) + 1) * 1000);
                }

            }
        });
    }

    private void showDeviceInfo(UsbDevice device) {
        System.out.println("Device id: " + device.getDeviceId());
        System.out.println("Device name: " + device.getDeviceName());
        System.out.println("Serial number: " + device.getSerialNumber());

        editTextDevicesList.append("Device id: " + device.getDeviceId() + "\n");
        editTextDevicesList.append("Device name: " + device.getDeviceName() + "\n");
        editTextDevicesList.append("Product name: " + device.getProductName() + "\n");
        editTextDevicesList.append("Manufactured name: " + device.getManufacturerName() + "\n");
        editTextDevicesList.append("Serial number: " + device.getSerialNumber() + "\n\n");
    }
}
