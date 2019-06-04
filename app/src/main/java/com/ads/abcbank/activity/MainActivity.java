package com.ads.abcbank.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private ImageView ivTemp;
    private TextView appId;
    private EditText cityCode;
    private EditText brchCode;
    private EditText clientVersion;
    private Spinner terminalType;
    private Spinner screenDirection;
    private Spinner frameSetNo;
    private Spinner contentType;
    private EditText appIdAddress;
    private EditText server;
    private EditText cdn;
    private EditText storeId;
    private TextView tvSubmit;

    private TestArrayAdapter tAdapter, sAdapter, fAdapter, cAdapter;
    private String[] terminals = {"TV", "poster", "led", "smartDev"};
    private String[] screens = {"水平", "垂直"};
    private String[][] frames = {{"模板1", "模板4", "模板5", "模板6"}, {"模板2", "模板3"}};
    private String[][][] contents = {{{"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部"}, {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部"}},
            {{"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}}};

    private int[][] tempImages = {{R.mipmap.icon_temp1, R.mipmap.icon_temp4, R.mipmap.icon_temp5, R.mipmap.icon_temp6},
            {R.mipmap.icon_temp2, R.mipmap.icon_temp3}};
    private String[][] tempValues = {{"1", "4", "5", "6"}, {"2", "3"}};
    private int tPosition, sPosition, fPosition, cPosition;
    private Map<String, String> conMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conMap.put("全部", "*");
        conMap.put("信用卡", "C");
        conMap.put("大额存单", "D");
        conMap.put("贵金属", "G");
        conMap.put("理财", "M");
        conMap.put("基金", "F");
        initViews();
        initDatas();
    }

    private void initViews() {
        ivTemp = (ImageView) findViewById(R.id.iv_temp);
        appId = (TextView) findViewById(R.id.appId);
        cityCode = (EditText) findViewById(R.id.cityCode);
        brchCode = (EditText) findViewById(R.id.brchCode);
        clientVersion = (EditText) findViewById(R.id.clientVersion);
        terminalType = (Spinner) findViewById(R.id.terminalType);
        screenDirection = (Spinner) findViewById(R.id.screenDirection);
        frameSetNo = (Spinner) findViewById(R.id.frameSetNo);
        contentType = (Spinner) findViewById(R.id.contentType);
        appIdAddress = (EditText) findViewById(R.id.appIdAddress);
        server = (EditText) findViewById(R.id.server);
        cdn = (EditText) findViewById(R.id.cdn);
        storeId = (EditText) findViewById(R.id.storeId);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
    }

    private void initDatas() {
        tAdapter = new TestArrayAdapter(this, terminals);
        sAdapter = new TestArrayAdapter(this, screens);
        fAdapter = new TestArrayAdapter(this, frames[0]);
        ;
        cAdapter = new TestArrayAdapter(this, contents[0][0]);
        ;
//        fAdapter.addAll(frames[0]);
//        cAdapter.addAll(contents[0][0]);
        terminalType.setAdapter(tAdapter);
        screenDirection.setAdapter(sAdapter);
        frameSetNo.setAdapter(fAdapter);
        contentType.setAdapter(cAdapter);
        terminalType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        screenDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fAdapter = new TestArrayAdapter(MainActivity.this, frames[position]);
                frameSetNo.setAdapter(fAdapter);
                frameSetNo.setSelection(0);
                sPosition = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        frameSetNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    cAdapter = new TestArrayAdapter(MainActivity.this, contents[sPosition][position]);
                    contentType.setAdapter(cAdapter);
                    contentType.setSelection(0);
                    fPosition = position;
                    ivTemp.setImageResource(tempImages[sPosition][fPosition]);
                } catch (Exception e) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        contentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onRegister(View view) {
        RegisterBean bean = new RegisterBean();
        bean.appId = appId.getText().toString();
        bean.trCode = "register";
        bean.cityCode = cityCode.getText().toString();
        bean.brchCode = brchCode.getText().toString();
        bean.clientVersion = Utils.getVersionName(this);
        bean.terminalId = Utils.getMac(this).toLowerCase().replace("-", "")
                .replace(":", "");
        bean.timestamp = System.currentTimeMillis();
        bean.uniqueId = Utils.getUUID(this);
        bean.flowNum = 0;
        bean.data.terminalType = getSelectTer();
        bean.data.screenDirection = getSelectScr();
        bean.data.frameSetNo = getSelectFra();
        getSelectCon();
        bean.data.appIpAddress = appIdAddress.getText().toString();
        bean.data.server = server.getText().toString();
        bean.data.cdn = cdn.getText().toString();
        bean.data.storeId = storeId.getText().toString();
        Intent intent = new Intent();
        switch (getSelectFra()) {
            case "1":
                intent.setClass(this, Temp1Activity.class);
                break;
            case "2":
                intent.setClass(this, Temp2Activity.class);
                break;
            case "3":
                intent.setClass(this, Temp3Activity.class);
                break;
            case "4":
                intent.setClass(this, Temp4Activity.class);
                break;
            case "5":
                intent.setClass(this, Temp5Activity.class);
                break;
            case "6":
                intent.setClass(this, Temp6Activity.class);
                break;
        }
        startActivity(intent);
//        finish();
    }

    private String getSelectTer() {
        return terminals[tPosition];
    }

    private String getSelectScr() {
        return sPosition == 0 ? "H" : "V";
    }

    private String getSelectFra() {
        return tempValues[sPosition][fPosition];
    }

    private void getSelectCon() {
        String start = "";
        String end = "";
        String selectFra = getSelectFra();
        String selectCon = contents[sPosition][fPosition][cPosition];
        end = conMap.get(selectCon);
        switch (selectFra) {
            case "1":
                start = "M,H,P,N,E,L,R";
                break;
            case "2":
                start = "M,H,P,N,E,L,R";
                break;
            case "3":
                start = "M,H,P,N,E,L,R";
                break;
            case "4":
                start = "H,L";
                break;
            case "5":
                start = "M,H,P,N,E,L,R";
                break;
            case "6":
                start = "T";
                break;
        }
        Utils.setContentTypeStart(this, start);
        Utils.setContentTypeMiddle(this, getSelectScr());
        Utils.setContentTypeEnd(this, end);
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

    class TestArrayAdapter extends ArrayAdapter<String> {
        private Context mContext;
        private String[] mStringArray;

        public TestArrayAdapter(Context context, String[] stringArray) {
            super(context, android.R.layout.simple_spinner_item, stringArray);
            mContext = context;
            mStringArray = stringArray;
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.item_spinner_dropdown, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            if (mStringArray != null && position < mStringArray.length) {
                try {
                    tv.setText(mStringArray[position]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return convertView;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.item_spinner, parent, false);
            }

            //此处text1是Spinner默认的用来显示文字的TextView
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            if (mStringArray != null && position < mStringArray.length) {
                try {
                    tv.setText(mStringArray[position]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return convertView;
        }

        public void clearData() {
            mStringArray = null;
        }

        public void addData(String[] items) {
            mStringArray = items;
        }
    }
}
