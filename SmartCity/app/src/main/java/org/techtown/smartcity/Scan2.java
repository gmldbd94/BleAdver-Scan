package org.techtown.smartcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Scan2 extends AppCompatActivity {
    TextView textView;
    public static final int REQUEST_CODE_SCAN3 = 203;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan2);

        // 이전 화면 요청 ////////////////////////////////////////////////////////////////////////////
        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Scan1.class);
                startActivity(intent);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        textView = findViewById(R.id.textView);
        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            ScanInfo scanInfo = bundle.getParcelable("scan");
            if(intent != null){
                String birth = scanInfo.data.substring(16, 22);
                String num = scanInfo.data.substring(22, 23);
                String gender;
                if(scanInfo.data.substring(23, 24).equals("1")) {
                    gender = "남자";
                }else{
                    gender = "여자";
                }
                String rssi = scanInfo.data.substring(6, 9);
                textView.append("생년월일 : " + birth
                                + "\n조난인원 : " + num
                                + "\n성별 : " + gender
                                + "\nRssi : " + rssi);
            }
        }

        Button chatBtn = (Button) findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Scan3.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN3);
            }
        });
    }
}
