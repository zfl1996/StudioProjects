package com.ads.abcbank.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.AutoPollAdapter;
import com.ads.abcbank.view.AutoPollRecyclerView;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.BaseTempFragment;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.MarqueeVerticalTextView;
import com.ads.abcbank.view.MarqueeVerticalTextViewClickListener;
import com.ads.abcbank.view.TempView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JzvdStd;

public class Temp2Activity extends BaseActivity implements IView {
    private TempView tvTemp;
    private AutoPollRecyclerView mRecyclerView;
    private AutoPollAdapter autoPollAdapter;
    private List<String> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp2);
        setiView(this);

        initViews();
        startServices(Utils.TYPES_TEMP2);
    }
    private void initViews() {
        mRecyclerView = findViewById(R.id.rv_recycleView);
        tvTemp = findViewById(R.id.tv_temp);
        tvTemp.setType(Utils.TYPES_TEMP2);
//        tvTemp.getImage().setVisibility(View.GONE);
        mRecyclerView.getBackground().setAlpha(179);
        String listStr = Utils.get(Temp2Activity.this, Utils.KEY_PLAY_LIST_TXT, "").toString();
        if (!TextUtils.isEmpty(listStr)) {
            list = JSONArray.parseArray(listStr, String.class);
        }
        if (list.size() == 0) {
            for (int i = 0; i < 6; i++) {
                list.add("中国农业银行欢迎您！");
            }
        }
        Utils.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                autoPollAdapter = new AutoPollAdapter(Temp2Activity.this, list);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(Temp2Activity.this, LinearLayoutManager.HORIZONTAL, false));
                mRecyclerView.setAdapter(autoPollAdapter);
                mRecyclerView.start();
            }
        });

        BaseTempFragment.tempView = tvTemp;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (tvTemp != null) {
            BaseTempFragment.tempView = tvTemp;
            tvTemp.setNeedUpdate(true);
        }
        HandlerUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViews();
                startServices(Utils.TYPES_TEMP2);
            }
        }, 100);
        if (mRecyclerView != null) {
            mRecyclerView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            JzvdStd.goOnPlayOnPause();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRecyclerView != null) {
            mRecyclerView.stop();
        }
    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {
        if (tvTemp != null) {
            tvTemp.setNeedUpdate(true);
        }
    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {

    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {
        if (tvTemp != null) {
            tvTemp.updatePreset();
        }
    }

    public void updateTxtBeans(List<PlaylistBodyBean> beans) {
        Utils.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                List<String> listString = new ArrayList<>();
                for (int i = 0; i < beans.size(); i++) {
                    PlaylistBodyBean bean = beans.get(i);
                    if (Utils.isInPlayTime(bean)) {
                        try {
                            File file = new File(DownloadService.downloadFilePath, bean.name);
                            if (file.exists()) {
                                listString.add(Utils.getTxtString(Temp2Activity.this, bean.name));
                            }
                        } catch (Exception e) {
                            Logger.e("TXT ERROR", e.toString());
                        }
                    }
                }
                if (!isSameTxt(listString)) {
                    updateTextList(listString);
                }
            }
        });
    }

    private boolean isSameTxt(List<String> listString) {
        if (listString.size() == 0) {
            return true;
        }
        return JSON.toJSONString(listString).equals(JSON.toJSONString(list));
    }

    private void updateTextList(List<String> listString) {
        HandlerUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRecyclerView != null) {
                    mRecyclerView.stop();
                }
                list.clear();
                list.addAll(listString);
                long length = getListTxtLength(list);
                if (length == 0) {
                    for (int i = 0; i < 6; i++) {
                        list.add("中国农业银行欢迎您！");
                    }
                } else if (length < 60) {
                    int s = (int) (60 / length) + 1;
                    for (int i = 0; i < s; i++) {
                        list.addAll(listString);
                    }
                }
                Utils.put(Temp2Activity.this, Utils.KEY_PLAY_LIST_TXT, JSONArray.toJSONString(list));
                if (autoPollAdapter != null) {
                    autoPollAdapter.notifyDataSetChanged();
                }
                if (mRecyclerView != null) {
                    mRecyclerView.start();
                }
            }
        }, 100);
    }

    private long getListTxtLength(List<String> listString) {
        if (listString == null) {
            return 0;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < listString.size(); i++) {
            stringBuffer.append(listString.get(i));
        }
        return stringBuffer.length();
    }

}
