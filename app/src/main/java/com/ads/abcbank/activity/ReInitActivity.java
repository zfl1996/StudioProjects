package com.ads.abcbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.ads.abcbank.view.KeyboardWindow;
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
    private TextView tvTip;

    private TestArrayAdapter tAdapter, sAdapter, fAdapter, cAdapter;
    //    private String[] terminals = {"TV", "poster", "led", "smartDev"};
//    private String[] terminalsValue = {"电视机", "海报屏", "门楣屏", "互动屏"};
    private String[] terminals = {"TV", "poster", "smartDev"};
    private String[] terminalsValue = {"电视机", "海报屏", "互动屏"};
    private String[] screens = {"横版", "竖版"};
    //    private String[][] frames = {{"模板1", "模板4", "模板5", "模板6"}, {"模板2", "模板3"}};
//    private String[][][] contents = {{{"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部"}, {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部"}},
//            {{"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}}};
//
//    private int[][] tempImages = {{R.mipmap.icon_temp1, R.mipmap.icon_temp4, R.mipmap.icon_temp5, R.mipmap.icon_temp6},
//            {R.mipmap.icon_temp2, R.mipmap.icon_temp3}};
//    private String[][] tempValues = {{"1", "4", "5", "6"}, {"2", "3"}};
    private String[][] frames = {{"横版通用", "横版全屏"}, {"竖版通用", "竖版全屏", "竖版公告公示", "竖版营销"}};
    private String[][][] contents = {{{"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}},
            {{"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"},
                    {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}, {"全部", "信用卡", "大额存单", "贵金属", "理财", "基金"}}};

    private int[][] tempImages = {{R.mipmap.icon_temp1, R.mipmap.icon_temp5},
            {R.mipmap.icon_temp2, R.mipmap.icon_temp3, R.mipmap.icon_temp3, R.mipmap.icon_temp2}};
    private String[][] tips = {{"用于展示产品营销、价格行情、金融信息、监管宣传、文字滚动、公示公告、人员资质以及执照许可等信息。",
            "用于展示人员资质、价格行情、公告公示、监管宣传等信息。"},
            {"用于展示产品营销、价格行情、监管宣传、文字滚动、公示公告、人员资质以及执照许可等信息。",
                    "用于全屏展示产品营销、价格行情、监管宣传、公示公告、人员资质以及执照许可等信息。",
                    "该模板不仅展示产品营销、价格行情、金融信息、风险提示、公示公告、人员资质以及执照许可等信息，还可为用户提供周边商圈、线上特惠、二维码墙等模块。用户可点击相关图标跳转至对应页面。",
                    "用于展示自营产品、代销产品、营销活动、周边商圈、线上特惠、二维码墙以及文字滚动等信息。"}};
    private String[][] tempValues = {{"1", "5"}, {"2", "3", "7", "8"}};
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

    private KeyboardWindow keyboardWindow;

    private void initViews() {
        ivTemp = findViewById(R.id.iv_temp);
        appId = findViewById(R.id.appId);
        cityCode = findViewById(R.id.cityCode);
        brchCode = findViewById(R.id.brchCode);
        clientVersion = findViewById(R.id.clientVersion);
        terminalType = findViewById(R.id.terminalType);
        screenDirection = findViewById(R.id.screenDirection);
        frameSetNo = findViewById(R.id.frameSetNo);
        contentType = findViewById(R.id.contentType);
        appIdAddress = findViewById(R.id.appIdAddress);
        server = findViewById(R.id.server);
        cdn = findViewById(R.id.cdn);
        storeId = findViewById(R.id.storeId);
        tvSubmit = findViewById(R.id.tv_submit);
        back = findViewById(R.id.back);
        tvTip = findViewById(R.id.tv_tip);

        addListener(cityCode, true);
        addListener(brchCode, true);
        addListener(appIdAddress, true);
        addListener(server, false);
        addListener(cdn, true);
        addListener(storeId, true);
    }

    @SuppressWarnings("ALL")
    private void addListener(EditText editText, boolean isNum) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (keyboardWindow != null) {
                        keyboardWindow.dismiss();
                    }
                    keyboardWindow = new KeyboardWindow(ReInitActivity.this, editText, isNum);
                    keyboardWindow.show();
                    editText.requestFocus();
                }

                int inType = editText.getInputType();
                editText.setInputType(InputType.TYPE_NULL);
                editText.onTouchEvent(event);
                editText.setInputType(inType);
                CharSequence text = editText.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
                return false;
            }
        });
        editText.setMovementMethod(ScrollingMovementMethod.getInstance());
        editText.setSelection(editText.getText().length(), editText.getText().length());
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
        contentType.setEnabled(false);
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
                    contentType.setEnabled(false);
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
                    tvTip.setText(tips[sPosition][fPosition]);
                } catch (Exception e) {
                    Logger.e(e.toString());
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
            clientVersion.setText(Utils.getVersionName(this));
            int tPosition = tAdapter.getPosition(bean.data.terminalType);
            terminalType.setSelection(tPosition);

            int sToPosition = "H".equals(bean.data.screenDirection) ? 0 : 1;
            screenDirection.setSelection(sToPosition);

            switch (bean.data.frameSetNo) {
                case "1":
                case "2":
                    fToPosition = 0;
                    break;
                case "5":
                case "3":
                    fToPosition = 1;
                    break;
                case "7":
                    fToPosition = 2;
                    break;
                case "8":
                    fToPosition = 3;
                    break;
//                case "6":
//                    fToPosition = 3;
//                    break;
                default:
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
                default:
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

        Utils.showProgressDialog(this);
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
        String end;
        String selectFra = getSelectFra();
        String selectCon = contents[sPosition][fPosition][cPosition];
        end = conMap.get(selectCon);
        switch (selectFra) {
            case "1":
                start = Utils.TYPES_TEMP1;
                break;
            case "2":
                start = Utils.TYPES_TEMP2;
                break;
            case "3":
                start = Utils.TYPES_TEMP3;
                break;
            case "4":
                start = Utils.TYPES_TEMP4;
                break;
            case "5":
                start = Utils.TYPES_TEMP5;
                break;
            case "6":
                start = Utils.TYPES_TEMP6;
                break;
            case "7":
                start = Utils.TYPES_TEMP7;
                break;
            case "8":
                start = Utils.TYPES_TEMP8;
                break;
            default:
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
                        case "7":
                            intent.setClass(ReInitActivity.this, Temp7Activity.class);
                            break;
                        case "8":
                            intent.setClass(ReInitActivity.this, Temp8Activity.class);
                            break;
                        default:
                            break;
                    }
                }
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void init(String jsonObject) {
        if (!TextUtils.isEmpty(jsonObject)) {
            InitResultBean initResultBean;
            try {
                initResultBean = JSON.parseObject(jsonObject, InitResultBean.class);
            } catch (Exception e) {
                ToastUtil.showToastLong(this, "初始化返回结果异常：" + jsonObject);
                Logger.e("初始化返回结果异常：" + jsonObject);
                return;
            }
            if ("0".equals(initResultBean.resCode)) {
                ToastUtil.showToastLong(this, "初始化成功");


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
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
                    ToastUtil.showToastLong(ReInitActivity.this, "请调整当前系统时间");
                    Utils.getExecutorService().submit(new Runnable() {
                        @Override
                        public void run() {
                            HandlerUtil.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ActivityManager.getInstance().finishAllActivity();
                                    System.exit(0);
                                }
                            }, 3000);
                        }
                    });
                    return;
                }

                HandlerUtil.postDelayed(new Runnable() {
                    @Override
                    public void run() {

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
                                case "7":
                                    intent.setClass(ReInitActivity.this, Temp7Activity.class);
                                    break;
                                case "8":
                                    intent.setClass(ReInitActivity.this, Temp8Activity.class);
                                    break;
                                default:
                                    break;
                            }
                        }
                        Utils.put(ReInitActivity.this, Utils.KEY_PLAY_LIST, "");
                        Utils.put(ReInitActivity.this, Utils.KEY_PLAY_LIST_DOWNLOAD, "");
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            } else if ("-1".equals(initResultBean.resCode)) {
                ToastUtil.showToastLong(this, initResultBean.resMessage);
                Logger.e("服务器主动拒绝");
                finish();
            } else if ("1".equals(initResultBean.resCode)) {
                ToastUtil.showToastLong(this, initResultBean.resMessage);
                Logger.e("客户端版本过低");
                if (Utils.existHttpPath(initResultBean.data.downloadLink)) {
                    Utils.showProgressDialog(this, "正在下载最新版本");
                    Utils.startUpdateDownloadTask(mActivity, "abcBankModel.apk", initResultBean.data.downloadLink);
                } else {
                    ToastUtil.showToastLong(mActivity, "下载链接为空或路径非法");
                    finish();
                }
            }
        } else {
            ToastUtil.showToastLong(this, "初始化失败");
            HandlerUtil.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    ActivityManager.getInstance().finishAllActivity();
//                    System.exit(0);
                }
            }, 2000);

            if (Utils.IS_TEST) {
                String beanStr = Utils.get(ReInitActivity.this, Utils.KEY_REGISTER_BEAN, "").toString();
                Intent intent = new Intent();
                if (TextUtils.isEmpty(beanStr)) {
                    intent.setClass(ReInitActivity.this, ReInitActivity.class);
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
                        case "7":
                            intent.setClass(ReInitActivity.this, Temp7Activity.class);
                            break;
                        case "8":
                            intent.setClass(ReInitActivity.this, Temp8Activity.class);
                            break;
                        default:
                            break;
                    }
                }
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void register(String jsonObject) {
        ResultBean resultBean;
        if (!TextUtils.isEmpty(jsonObject)) {
            try {
                resultBean = JSON.parseObject(jsonObject, ResultBean.class);
            } catch (Exception e) {
                ToastUtil.showToastLong(this, "注册返回结果异常：" + jsonObject);
                Logger.e("注册返回结果异常：" + jsonObject);
                return;
            }
        } else {
            ToastUtil.showToastLong(this, "注册失败");
            if (Utils.IS_TEST) {
                if (bean != null && TextUtils.isEmpty(bean.data.frameSetNo)) {
                    bean.data.frameSetNo = Utils.get(ReInitActivity.this, Utils.KEY_FRAME_SET_NO, "1").toString();
                }
                Utils.put(ReInitActivity.this, Utils.KEY_REGISTER_BEAN, JSONObject.toJSONString(bean));

                mainPresenter.init(JSONObject.parseObject(JSONObject.toJSONString(bean)));
            }
            return;
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
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.item_spinner_dropdown, parent, false);
            }

            TextView tv = convertView.findViewById(android.R.id.text1);
            if (mStringArray != null && position < mStringArray.length) {
                try {
                    tv.setText(mStringArray[position]);
                } catch (Exception e) {
                    Logger.e(e.toString());
                }
            }
            return convertView;

        }

        @Override
        public @NonNull
        View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.item_spinner, parent, false);
            }

            //此处text1是Spinner默认的用来显示文字的TextView
            TextView tv = convertView.findViewById(android.R.id.text1);
            if (mStringArray != null && position < mStringArray.length) {
                try {
                    tv.setText(mStringArray[position]);
                } catch (Exception e) {
                    Logger.e(e.toString());
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
