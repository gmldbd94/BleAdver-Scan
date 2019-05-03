package org.techtown.smartcity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Advertise1 extends AppCompatActivity {
    EditText editBirth;
    EditText editNum;
    RadioButton male;
    RadioButton female;

    public static final int REQUEST_CODE_ADVERTISE1 = 101;      // MainActivity 액티비티 요청 상수
    public static final int REQUEST_CODE_ADVERTISE2 = 102;      // Advertise2 액티비티 요청 상수
    public static final String KEY_ADVERTISE_DATA = "data";     // Advertise2 액티비티로 정보 보내기 위한 키

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise1);

        // 이전 액티비티 요청 ////////////////////////////////////////////////////////////////////////
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADVERTISE1);
            }

        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        // 조난자 정보 다음 액티비티로 넘겨주기 ////////////////////////////////////////////////////////
        editBirth = findViewById(R.id.editBirth);
        editNum = findViewById(R.id.editNum);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        Button advertiseBtn = (Button) findViewById(R.id.advertiseBtn);
        advertiseBtn.setOnClickListener(new View.OnClickListener() {     // 조난신호 송신 버튼 클릭시
            @Override
            public void onClick(View v) {
                String birth = editBirth.getText().toString();
                String num = editNum.getText().toString();
                String gender;
                if(male.isChecked())                                     // 남자를 체크 했을 경우
                {
                    gender = "1";
                } else {                                                 // 여자를 체크 했을 경우
                    gender = "2";
                }

                Intent intent = new Intent(getApplicationContext(), Advertise2.class);
                AdvertiseInfo advertiseinfo = new AdvertiseInfo(birth, num, gender);
                intent.putExtra(KEY_ADVERTISE_DATA, advertiseinfo);
                startActivityForResult(intent, REQUEST_CODE_ADVERTISE2);
            }
        });
    }
}
