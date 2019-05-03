package org.techtown.smartcity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Scan3 extends AppCompatActivity {
    Button listen,send,listDevices;
    ListView listView;
    TextView msg_box,status,distance;
    EditText writeMsg;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    static final int STATE_LISTENUNG = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan3);

        findViewByIdes();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        implementListeners();
    }

    private void implementListeners() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = String.valueOf(writeMsg.getText());
                sendReceive.write(string.getBytes());
                msg_box.append("Me : " + string + "\n");
                writeMsg.setText("");
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case STATE_LISTENUNG:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("연결시도중");
                    break;
                case STATE_CONNECTED:
                    status.setText("연결됨");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("연결실패");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg =  new String(readBuff,0,msg.arg1);
                    tempMsg ="You : " +tempMsg+"\n";
                    msg_box.append(tempMsg);
                    break;
            }

            return true;
        }
    });


    private void findViewByIdes() {
        //listen = (Button) findViewById(R.id.listen);
        send = (Button) findViewById(R.id.sendBtn);
        //listView = (ListView) findViewById(R.id.listview);
        msg_box = (TextView) findViewById(R.id.chatTV);
        msg_box.setMovementMethod(new ScrollingMovementMethod());
        msg_box.clearComposingText();
        //status = (TextView) findViewById(R.id.status);
        writeMsg = (EditText) findViewById(R.id.editMsg);
        //listDevices = (Button) findViewById(R.id.listDevices);
    }

    private class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;
        }
        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
