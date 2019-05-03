package org.techtown.smartcity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity {
    // BLE
    BluetoothManager bleManager;
    BluetoothAdapter bleAdapter;

    public static final int REQUEST_CODE_ADVERTISE1 = 101;   // Advertise1 액티비티 요청 상수
    public static final int REQUEST_CODE_SCAN1 = 201;          // Scan1 액티비티 요청 상수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 블루투스 활성화 하기 //////////////////////////////////////////////////////////////////////
        bleManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bleManager.getAdapter();
        if (bleAdapter != null && !bleAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        // 위치권한 얻기 /////////////////////////////////////////////////////////////////////////////
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("사용자 위치 권한 필요");
            builder.setMessage("구조신호 탐색을 위해서 사용자의 위치 권한이 필요합니다.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            });
            builder.show();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        // 구조요청 버튼 클릭시 //////////////////////////////////////////////////////////////////////
        Button advertiseBtn = (Button) findViewById(R.id.advertiseBtn);
        advertiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Advertise1.class);
                startActivityForResult(intent, REQUEST_CODE_ADVERTISE1);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        // 구조신호탐색 버튼 클릭시 ///////////////////////////////////////////////////////////////////
        Button scanBtn = (Button) findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Scan1.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN1);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
