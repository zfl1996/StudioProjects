package com.ads.abcbank.activity;

import android.os.Bundle;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.view.TempView;

public class Temp2Activity extends BaseActivity implements TempView {
    private TempPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp2);

    }
}
