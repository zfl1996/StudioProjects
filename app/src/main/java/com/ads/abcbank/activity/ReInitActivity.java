package com.ads.abcbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.InitResultBean;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.bean.ResultBean;
import com.ads.abcbank.presenter.MainPresenter;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.ToastUtil;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IMainView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReInitActivity extends BaseActivity implements IMainView, View.OnClickListener {
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
    private TextView back;

    private TestArrayAdapter tAdapter, sAdapter, fAdapter, cAdapter;
    private String[] terminals = {"TV", "poster", "led", "smartDev"};
    private String[] terminalsValue = {"电视机", "海报屏", "门楣屏", "互动屏"};
    private String[] screens = {"水平", "垂直"};
    private String[][] frames = {{"模板1", "模板4", "模板5", "模板6"}, {"模板2", "模板3"}};
    private String[][][] contents = {{{"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部"}, {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部"}},
            {{"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}}};

    private int[][] tempImages = {{R.mipmap.icon_temp1, R.mipmap.icon_temp4, R.mipmap.icon_temp5, R.mipmap.icon_temp6},
            {R.mipmap.icon_temp2, R.mipmap.icon_temp3}};
    private String[][] tempValues = {{"1", "4", "5", "6"}, {"2", "3"}};
    private int tPosition, sPosition, fPosition, cPosition;
    private Map<String, String> conMap = new HashMap<>();

    private MainPresenter mainPresenter;
    private RegisterBean bean;

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
        back = (TextView) findViewById(R.id.back);
    }

    private void initDatas() {
        mainPresenter = new MainPresenter(this, this);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tAdapter = new TestArrayAdapter(this, terminalsValue);
        sAdapter = new TestArrayAdapter(this, screens);
        fAdapter = new TestArrayAdapter(this, frames[0]);
        cAdapter = new TestArrayAdapter(this, contents[0][0]);
        terminalType.setAdapter(tAdapter);
        screenDirection.setAdapter(sAdapter);
        frameSetNo.setAdapter(fAdapter);
        contentType.setAdapter(cAdapter);
        terminalType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tPosition = position;
//                if (position == 2) {
//                    screenDirection.setSelection(0);
//                    frameSetNo.setSelection(0);
//                    contentType.setSelection(0);
//                    screenDirection.setEnabled(false);
//                    frameSetNo.setEnabled(false);
//                    contentType.setEnabled(false);
//                } else {
//                    screenDirection.setEnabled(true);
//                    frameSetNo.setEnabled(true);
//                    contentType.setEnabled(true);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        screenDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fAdapter = new TestArrayAdapter(ReInitActivity.this, frames[position]);
                frameSetNo.setAdapter(fAdapter);
                if (fToPosition != -1) {
                    frameSetNo.setSelection(fToPosition);
                    fToPosition = -1;
                } else {
                    frameSetNo.setSelection(0);
                }
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
                    cAdapter = new TestArrayAdapter(ReInitActivity.this, contents[sPosition][position]);
                    contentType.setAdapter(cAdapter);
                    if (cToPosition != -1) {
                        if (!cToPositioned) {
                            cToPositioned = true;
                        } else {
                            contentType.setSelection(cToPosition);
                            cToPosition = -1;
                        }
                    } else {
                        contentType.setSelection(0);
                    }
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
        String beanStr = Utils.get(this, Utils.KEY_REGISTER_BEAN, "").toString();
        if (!TextUtils.isEmpty(beanStr)) {
            RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
            cityCode.setText(bean.cityCode);
            brchCode.setText(bean.brchCode);
            clientVersion.setText(bean.clientVersion);
            int tPosition = tAdapter.getPosition(bean.data.terminalType);
            terminalType.setSelection(tPosition);

            int sToPosition = bean.data.screenDirection.equals("H") ? 0 : 1;
            screenDirection.setSelection(sToPosition);

            switch (bean.data.frameSetNo) {
                case "1":
                case "2":
                    fToPosition = 0;
                    break;
                case "4":
                case "3":
                    fToPosition = 1;
                    break;
                case "5":
                    fToPosition = 2;
                    break;
                case "6":
                    fToPosition = 3;
                    break;
            }
            switch (Utils.getContentTypeEnd(this)) {
                case "*":
                    cToPosition = 0;
                    break;
                case "C":
                    cToPosition = 1;
                    break;
                case "D":
                    cToPosition = 2;
                    break;
                case "G":
                    cToPosition = 3;
                    break;
                case "M":
                    cToPosition = 4;
                    break;
                case "F":
                    cToPosition = 5;
                    break;
            }
            appIdAddress.setText(bean.data.appIpAddress);
            server.setText(bean.data.server);
            cdn.setText(bean.data.cdn);
            storeId.setText(bean.data.storeId);

            cityCode.setSelection(cityCode.getText().toString().length());
        }
    }

    private int fToPosition = -1;
    private int cToPosition = -1;
    private boolean cToPositioned;

    public void onRegister(View view) {
        if (TextUtils.isEmpty(cityCode.getText().toString())) {
            ToastUtil.showToast(this, "数据不可为空");
            return;
        }
        if (TextUtils.isEmpty(brchCode.getText().toString())) {
            ToastUtil.showToast(this, "数据不可为空");
            return;
        }
        if (TextUtils.isEmpty(appIdAddress.getText().toString())) {
            ToastUtil.showToast(this, "数据不可为空");
            return;
        }
        if (TextUtils.isEmpty(server.getText().toString())) {
            ToastUtil.showToast(this, "数据不可为空");
            return;
        }
        if (TextUtils.isEmpty(cdn.getText().toString())) {
            ToastUtil.showToast(this, "数据不可为空");
            return;
        }
        if (TextUtils.isEmpty(storeId.getText().toString())) {
            ToastUtil.showToast(this, "数据不可为空");
            return;
        }
        bean = new RegisterBean();
        bean.appId = appId.getText().toString();
        bean.trCode = "register";
        bean.cityCode = cityCode.getText().toString();
        bean.brchCode = brchCode.getText().toString();
        bean.clientVersion = clientVersion.getText().toString();
        bean.terminalId = Utils.getMac(this).toLowerCase().replace("-", "")
                .replace(":", "");
        bean.timestamp = System.currentTimeMillis();
        bean.uniqueId = Utils.getUUID(this);
        bean.flowNum = 0;
        bean.data.terminalType = getSelectTer();
        //        bean.data.screenDirection = getSelectScr();
//        bean.data.frameSetNo = getSelectFra();
//        getSelectCon();
//        bean.data.appIpAddress = appIdAddress.getText().toString();
//        bean.data.server = server.getText().toString();
//        bean.data.cdn = cdn.getText().toString();
//        bean.data.storeId = storeId.getText().toString();

//        if (fPosition == 2) {
//            bean.data.screenDirection = "H";
//            bean.data.frameSetNo = "";
//            getSelectCon();
//            bean.data.appIpAddress = "";
//            bean.data.storeId = "";
//        } else {
        bean.data.screenDirection = getSelectScr();
        bean.data.frameSetNo = getSelectFra();
        getSelectCon();
        bean.data.appIpAddress = appIdAddress.getText().toString();
        bean.data.storeId = storeId.getText().toString();
//        }
        bean.data.server = server.getText().toString();
        bean.data.cdn = cdn.getText().toString();

        Utils.put(this, Utils.KEY_FRAME_SET_NO, bean.data.frameSetNo);
        Utils.put(this, Utils.KEY_REGISTER_BEAN, JSONObject.toJSONString(bean));
        mainPresenter.register(JSONObject.parseObject(JSONObject.toJSONString(bean)));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    @Override
    public void init(String jsonObject) {
        if (!TextUtils.isEmpty(jsonObject)) {
            InitResultBean initResultBean = JSON.parseObject(jsonObject, InitResultBean.class);
            if (initResultBean.resCode.equals("0")) {
                String timePlaylist = Utils.get(ReInitActivity.this, Utils.KEY_TIME_PLAYLIST, "20").toString();
                int time;
                try {
                    time = Integer.parseInt(timePlaylist);
                } catch (NumberFormatException e) {
                    time = 20;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.MINUTE, -1 * time);
                String startTime = simpleDateFormat.format(calendar.getTime());

                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(new Date());
                calendar2.add(Calendar.MINUTE, time);
                String endTime = simpleDateFormat.format(calendar2.getTime());

                if (startTime.compareTo(initResultBean.data.serverTime) > 0
                        || endTime.compareTo(initResultBean.data.serverTime) < 0) {
                    ToastUtil.showToastLong(this, "请调整当前系统时间");
                    HandlerUtil.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ActivityManager.getInstance().finishAllActivity();
                            System.exit(0);
                        }
                    }, 2000);
                    return;
                }

                String beanStr = Utils.get(ReInitActivity.this, Utils.KEY_REGISTER_BEAN, "").toString();
                Intent intent = new Intent();
                if (TextUtils.isEmpty(beanStr)) {
                    intent.setClass(ReInitActivity.this, MainActivity.class);
                } else {
                    RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
                    switch (bean.data.frameSetNo) {
                        case "1":
                            intent.setClass(ReInitActivity.this, Temp1Activity.class);
                            break;
                        case "2":
                            intent.setClass(ReInitActivity.this, Temp2Activity.class);
                            break;
                        case "3":
                            intent.setClass(ReInitActivity.this, Temp3Activity.class);
                            break;
                        case "4":
                            intent.setClass(ReInitActivity.this, Temp4Activity.class);
                            break;
                        case "5":
                            intent.setClass(ReInitActivity.this, Temp5Activity.class);
                            break;
                        case "6":
                            intent.setClass(ReInitActivity.this, Temp6Activity.class);
                            break;
                    }
                }
                startActivity(intent);
                finish();
            } else if (initResultBean.resCode.equals("-1")) {
                ToastUtil.showToastLong(this, initResultBean.resMessage);
                Logger.e("服务器主动拒绝");
                finish();
            } else if (initResultBean.resCode.equals("1")) {
                ToastUtil.showToastLong(this, initResultBean.resMessage);
                Logger.e("客户端版本过低");
                if (Utils.existHttpPath(initResultBean.data.downloadLink)) {
                    Utils.startUpdateDownloadTask(mActivity, "abcBankModel.apk", initResultBean.data.downloadLink);
                } else {
                    Toast.makeText(mActivity, "下载链接为空或路径非法", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void register(String jsonObject) {
        ResultBean resultBean = null;
        if (!TextUtils.isEmpty(jsonObject)) {
            resultBean = JSON.parseObject(jsonObject, ResultBean.class);
        }
        if (resultBean != null && !TextUtils.isEmpty(resultBean.resCode) && "0".equals(resultBean.resCode)) {
            ToastUtil.showToastLong(this, "注册成功");
            HandlerUtil.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bean != null && TextUtils.isEmpty(bean.data.frameSetNo)) {
                        bean.data.frameSetNo = Utils.get(ReInitActivity.this, Utils.KEY_FRAME_SET_NO, "1").toString();
                    }
                    Utils.put(ReInitActivity.this, Utils.KEY_REGISTER_BEAN, JSONObject.toJSONString(bean));
                    mainPresenter.init(JSONObject.parseObject(JSONObject.toJSONString(bean)));
                }
            }, 2000);
        } else if (resultBean != null && !TextUtils.isEmpty(resultBean.resCode) && !TextUtils.isEmpty(resultBean.resMessage)) {
            ToastUtil.showToastLong(this, resultBean.resMessage);
        }
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

    private long mLastClickTime;
    private int clickTimes;

    public void toSetView(View view) {
        if ((System.currentTimeMillis() - mLastClickTime) > 1000) {
            mLastClickTime = System.currentTimeMillis();
            clickTimes = 0;
        } else {
            if (clickTimes < 1) {
                clickTimes++;
            } else {
                clickTimes = 0;
                startActivity(new Intent(this, SettingActivity.class));
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}
