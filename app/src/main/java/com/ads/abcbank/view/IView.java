package com.ads.abcbank.view;

import com.alibaba.fastjson.JSONObject;

/**
 * @date 2019/5/4
 */

public interface IView {
    void updateMainDate(JSONObject jsonObject);

    void updateBottomDate(JSONObject jsonObject);

    void updatePresetDate(JSONObject jsonObject);
}
