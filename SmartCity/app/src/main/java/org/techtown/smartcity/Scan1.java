package org.techtown.smartcity;

import android.app.AlertDialog;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Scan1 extends AppCompatActivity {
    // ble scan 준비 ////////////////////////////////////////////////////////////////////////////////
    BluetoothManager bleManager;
    BluetoothAdapter bleAdapter;
    BluetoothLeScanner bleScanner;
    ScanSettings.Builder bleScanSettings;   // 신호 주기 변경 시 필요
    List<ScanFilter> scanFilters;           // 필터링(Uuid, Mac주소 등)
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // 리스트뷰 /////////////////////////////////////////////////////////////////////////////////////
    ArrayList<HashMap<String,String>> listDevice;
    HashMap<String,String> inputData;
    ListView listView;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int REQUEST_CODE_SCAN2 = 202;          // Scan2 액티비티 요청 상수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan1);

        // 이전 화면 요청 ////////////////////////////////////////////////////////////////////////////
        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScaning();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        bleManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bleManager.getAdapter();
        bleScanner = bleAdapter.getBluetoothLeScanner();

        listView = (ListView)findViewById(R.id.listDevice);
        listDevice = new ArrayList<HashMap<String, String>>();

        startScaning();

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Scan2.class);
                String scanData = listDevice.get(position).toString();
                ScanInfo scanInfo = new ScanInfo(scanData);
                intent.putExtra("scan", scanInfo);
                startActivityForResult(intent, REQUEST_CODE_SCAN2);
            }
        });*/
    }

    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            String serviceData = new String(result.getScanRecord().getBytes(), Charset.forName("UTF-8"));
            String str = serviceData.substring(24);

            inputData = new HashMap<String, String>();
            inputData.put("data", str);
            inputData.put("rssi", Integer.toString(result.getRssi()));
            listDevice.add(inputData);

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    getApplicationContext(),
                    listDevice,
                    android.R.layout.simple_list_item_2,
                    new String[]{"data", "rssi"},
                    new int[]{android.R.id.text1,android.R.id.text2});
            listView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BluetoothDevice device = result.getDevice();
                    device.createBond();
                    stopScaning();
                    Intent intent = new Intent(getApplicationContext(), Scan2.class);
                    String scanData = listDevice.get(position).toString();
                    ScanInfo scanInfo = new ScanInfo(scanData);
                    intent.putExtra("scan", scanInfo);
                    startActivityForResult(intent, REQUEST_CODE_SCAN2);
                }
            });
        }
    };

    public void startScaning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                bleScanSettings = new ScanSettings.Builder();
                bleScanSettings.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
                // 얘는 스캔 주기를 2초로 줄여주는 Setting입니다.
                // 공식문서에는 위 설정을 사용할 때는 다른 설정을 하지 말고
                // 위 설정만 단독으로 사용하라고 되어 있네요 ^^
                // 위 설정이 없으면 테스트해 본 결과 약 10초 주기로 스캔을 합니다.
                ScanSettings scanSettings = bleScanSettings.build();

                scanFilters = new Vector<>();
                ScanFilter.Builder scanFilter = new ScanFilter.Builder();
                scanFilter.setServiceUuid(ParcelUuid.fromString("CDB7950D-73F1-4D4D-8E47-C090502DBD63"));
                ScanFilter scan = scanFilter.build();
                scanFilters.add(scan);
                bleScanner.startScan(scanFilters, scanSettings,bleScanCallback);
            }
        });
    }

    public void stopScaning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                bleScanner.stopScan(bleScanCallback);
            }
        });
    }
}
