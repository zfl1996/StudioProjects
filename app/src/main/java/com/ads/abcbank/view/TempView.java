package com.ads.abcbank.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PlaylistResultBean;
import com.ads.abcbank.fragment.ImageFragment;
import com.ads.abcbank.fragment.VideoFragment;
import com.ads.abcbank.fragment.WebFragment;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2019/5/19
 */

public class TempView extends LinearLayout {
    private Context context;
    private String type;
    private ViewPager viewpager;
    private ViewPager viewpagerHot;
    private List<Fragment> fragmentList = new ArrayList<>();
    private PlaylistResultBean playlistBean;
    private ImageView image;

    public TempView(Context context) {
        this(context, null);
    }

    public TempView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (TextUtils.isEmpty(json)) {
            json = Utils.getStringFromAssets("playlist.json", context);
        }
        playlistBean = JSON.parseObject(json, PlaylistResultBean.class);
        View view = LayoutInflater.from(context).inflate(R.layout.view_temp, null);
        image = view.findViewById(R.id.image);
        viewpager = view.findViewById(R.id.viewpager);
        viewpagerHot = view.findViewById(R.id.viewpager_hot);

        int src = R.mipmap.bg_land;
        if (Utils.getContentTypeMiddle(context).equals("V")) {
            src = R.mipmap.bg_port;
        }
        image.setImageResource(src);
        addView(view);
    }

    public void setType(String type) {
        this.type = type;
        addTempViewList();
        viewpager.setAdapter(new MyPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager()));
        viewpager.setCurrentItem(0);
    }

    private void addTempViewList() {
        if (playlistBean == null || playlistBean.data == null || playlistBean.data.items == null)
            return;
        fragmentList.clear();
        for (int i = 0; i < playlistBean.data.items.size(); i++) {
            PlaylistBodyBean bodyBean = playlistBean.data.items.get(i);
            String contentTypeMiddle = Utils.getContentTypeMiddle(context);
            String contentTypeEnd = Utils.getContentTypeEnd(context);
            if (contentTypeEnd.equals("*")) {
                if (bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                        type.contains(bodyBean.contentType.substring(0, 1))) {
                    String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                    BaseTempFragment fragment = null;
                    switch (suffix) {
                        case "mp4":
                        case "mkv":
                        case "wmv":
                        case "avi":
                        case "rmvb":
                            fragment = new VideoFragment();
                            fragment.setBean(bodyBean);
                            break;
                        case "jpg":
                        case "png":
                        case "bmp":
                        case "jpeg":
                            fragment = new ImageFragment();
                            fragment.setBean(bodyBean);
                            break;
                        default:
                            fragment = new WebFragment();
                            fragment.setBean(bodyBean);
                            break;
                    }
                    fragment.setTempView(this);
                    fragmentList.add(fragment);
                }
            } else {
                if (bodyBean.contentType.endsWith(contentTypeEnd) &&
                        bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                        type.contains(bodyBean.contentType.substring(0, 1))) {
                    String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                    BaseTempFragment fragment = null;
                    switch (suffix) {
                        case "mp4":
                        case "mkv":
                        case "wmv":
                        case "avi":
                        case "rmvb":
                            fragment = new VideoFragment();
                            fragment.setBean(bodyBean);
                            break;
                        case "jpg":
                        case "png":
                        case "bmp":
                        case "jpeg":
                            fragment = new ImageFragment();
                            fragment.setBean(bodyBean);
                            break;
                        default:
                            fragment = new WebFragment();
                            fragment.setBean(bodyBean);
                            break;
                    }
                    fragment.setTempView(this);
                    fragmentList.add(fragment);
                }
            }
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    public void nextPlay() {
        int current = viewpager.getCurrentItem();
        if (current < fragmentList.size() - 1) {
            viewpager.setCurrentItem(current + 1);
        } else {
            viewpager.setCurrentItem(0);
        }
    }
}
