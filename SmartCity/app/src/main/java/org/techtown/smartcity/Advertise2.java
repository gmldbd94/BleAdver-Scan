package org.techtown.smartcity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.UUID;

public class Advertise2 extends AppCompatActivity {
    TextView textView;
    public static final String KEY_ADVERTISE_DATA = "data";
    public static final int REQUEST_CODE_ADVERTISE1 = 102;      // Advertise1 액티비티 요청 상수
    public static final int REQUEST_CODE_ADVERTISE3 = 103;      // Advertise3 액티비티 요청 상수

    // advertise 준비 ///////////////////////////////////////////////////////////////////////////////
    BluetoothManager bleManager;
    BluetoothAdapter bleAdapter;
    BluetoothLeAdvertiser bleAdvertiser;
    AdvertiseSettings bleAdvertiseSettings;
    AdvertiseData bleAdvertiseData;
    AdvertiseCallback bleAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);                            // advertise 성공 시
            Log.v("Tag","Success to start advertise: " + settingsInEffect.toString());
        }

        @Override
        public void onStartFailure(int errorCode) {                           // advertise 실패 시
            super.onStartFailure(errorCode);
            textView.setText("송신에 실패하였습니다.\n조난자 정보를 예시에 맞게 다시 작성해주세요.");
            textView.invalidate();
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise2);

        textView = findViewById(R.id.textView2);

        // 이전 액티비티 요청 ////////////////////////////////////////////////////////////////////////
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleAdvertiser.stopAdvertising(bleAdvertiseCallback);
                Intent intent = new Intent(getApplicationContext(), Advertise1.class);
                startActivityForResult(intent, REQUEST_CODE_ADVERTISE1);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        Intent intent = getIntent();
        startAdvertise(intent);        // advertise 시작
    }

    private void startAdvertise(Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            AdvertiseInfo advertiseInfo = bundle.getParcelable(KEY_ADVERTISE_DATA);   // 이전 액티비티에서 정보 받아오기
            if (intent != null) {
                final String str = advertiseInfo.birth + advertiseInfo.gender + advertiseInfo.num;      // 패킷에 넣을 조난자 정보
                ParcelUuid pUuid = new ParcelUuid(UUID.fromString("CDB7950D-73F1-4D4D-8E47-C090502DBD63"));   // 패킷에 넣을 id
                final String serviceData = str;

                bleManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                bleAdapter = bleManager.getAdapter();

                // 디바이스가 BLE 지원하는지 체크 /////////////////////////////////////////////////////
                if (bleAdapter.isMultipleAdvertisementSupported() == false) {
                    textView.setText("디바이스는 BLE를 지원하지 않습니다.");
                    return;
                }
                ////////////////////////////////////////////////////////////////////////////////////

                bleAdvertiser = bleAdapter.getBluetoothLeAdvertiser();
                // advertise 셋팅 ///////////////////////////////////////////////////////////////////
                bleAdvertiseSettings = new AdvertiseSettings.Builder()
                        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)   // 신호 주기 설정
                        .setConnectable(true)                                    // 블루투스 연결 필요시 True
                        .setTimeout(0)
                        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)       // 신호 세기 설정
                        .build();
                // advertise 패킷에 넣을 정보 ////////////////////////////////////////////////////////
                bleAdvertiseData = new AdvertiseData.Builder()
                        .setIncludeDeviceName(false)                                             // 디바이스 이름 필요시 True
                        .setIncludeTxPowerLevel(false)                                           // 신호 세기 보낼 시 True
                        .addServiceUuid(pUuid)                                                    // id
                        .addServiceData(pUuid, serviceData.getBytes(Charset.forName("UTF-8")))   // 패킷에 조난자 정보 넣기
                        .build();

                textView.setText("Advertise contains Service Data : " + serviceData);

                bleAdvertiser.startAdvertising(bleAdvertiseSettings, bleAdvertiseData, bleAdvertiseCallback);   // advertise 시작

                // 채팅 버튼 클릭시
                Button chatBtn = (Button) findViewById(R.id.chatBtn);
                chatBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), Advertise3.class);
                        startActivityForResult(intent, REQUEST_CODE_ADVERTISE3);
                    }
                });
            }
        }
    }
}
