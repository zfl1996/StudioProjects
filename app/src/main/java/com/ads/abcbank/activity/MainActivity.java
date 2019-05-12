package com.ads.abcbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ads.abcbank.R;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toTemp1(View view) {
        startActivity(new Intent(this, Temp1Activity.class));
    }
    public void toTemp2(View view) {
        startActivity(new Intent(this, Temp2Activity.class));
    }
    public void toTemp3(View view) {
        startActivity(new Intent(this, Temp3Activity.class));
    }
    public void toTemp4(View view) {
        startActivity(new Intent(this, Temp4Activity.class));
    }
    public void toTemp5(View view) {
        startActivity(new Intent(this, Temp5Activity.class));
    }
    public void toTemp6(View view) {
        startActivity(new Intent(this, Temp6Activity.class));
    }
}
