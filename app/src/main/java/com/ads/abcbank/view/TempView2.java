package com.ads.abcbank.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PlaylistResultBean;
import com.ads.abcbank.fragment.ImageFragment;
import com.ads.abcbank.fragment.PdfFragment;
import com.ads.abcbank.fragment.TxtFragment;
import com.ads.abcbank.fragment.VideoFragment;
import com.ads.abcbank.fragment.WebFragment;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @date 2019/5/19
 */

public class TempView2 extends LinearLayout {
    private Context context;
    private String type;
    private ViewPager viewpager;
    private ViewPager viewpagerHot;
    private List<BaseTempFragment> fragmentList = new ArrayList<>();
    private PlaylistResultBean playlistBean;
    private ImageView image;

    public ImageView getImage() {
        return image;
    }

    private boolean showStaticData;

    public void setShowStaticData(boolean showStaticData) {
        this.showStaticData = showStaticData;
    }

    public TempView2(Context context) {
        this(context, null);
    }

    public TempView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempView2(Context context, AttributeSet attrs, int defStyle) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.view_temp2, null);
        image = view.findViewById(R.id.image);
        viewpager = view.findViewById(R.id.viewpager_temp);
        viewpagerHot = view.findViewById(R.id.viewpager_hot);

        int src = R.mipmap.h_zsyhxc;
        if (Utils.getContentTypeMiddle(context).equals("V")) {
            src = R.mipmap.v_sxdhb;
        }
        setImageSrc(src);
        addView(view);
    }

    public void setImageSrc(int src) {
        image.setImageResource(src);
    }

    public void setType(String type) {
        this.type = type;
        if (showStaticData) {
            addImages();
        } else {
            addTempViewList();
        }
//        viewpager.setAdapter(new MyPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager()));
        viewpager.setAdapter(new WillPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager(), fragmentList));
        viewpager.setCurrentItem(0);
    }

    private void addImages() {
        if (playlistBean == null || playlistBean.data == null || playlistBean.data.items == null)
            return;
        fragmentList.clear();
        for (int i = 0; i < playlistBean.data.items.size(); i++) {
            PlaylistBodyBean bodyBean = playlistBean.data.items.get(i);
            BaseTempFragment fragment = null;
            fragment = new ImageFragment();
            fragment.setBean(bodyBean);
            fragment.setTempView2(this);
            fragmentList.add(fragment);
        }
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
                            break;
                        case "jpg":
                        case "png":
                        case "bmp":
                        case "jpeg":
                            fragment = new ImageFragment();
                            break;
                        case "pdf":
                            fragment = new PdfFragment();
                            break;
                        case "txt":
                            fragment = new TxtFragment();
                            break;
                        default:
                            fragment = new WebFragment();
                            break;
                    }
                    fragment.setBean(bodyBean);
                    fragment.setTempView2(this);
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
                            break;
                        case "jpg":
                        case "png":
                        case "bmp":
                        case "jpeg":
                            fragment = new ImageFragment();
                            break;
                        case "pdf":
                            fragment = new PdfFragment();
                            break;
                        case "txt":
                            fragment = new TxtFragment();
                            break;
                        default:
                            fragment = new WebFragment();
                            break;
                    }
                    fragment.setBean(bodyBean);
                    fragment.setTempView2(this);
                    fragmentList.add(fragment);
                }
            }
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

    public class WillPagerAdapter extends FragmentPagerAdapter {
        // SparseArray是Hashmap的改良品，其核心是折半查找函数（binarySearch）
        SparseArray<WeakReference<Fragment>> registeredFragments = new SparseArray<WeakReference<Fragment>>();
        private List<BaseTempFragment> mList;


        public WillPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public WillPagerAdapter(FragmentManager fm, List<BaseTempFragment> list) {
            this(fm);
            // TODO Auto-generated constructor stub
            mList = list;
        }

        /*
         * 生成新的 Fragment 对象。 .instantiateItem() 在大多数情况下，都将调用 getItem() 来生成新的对象
         */
        @Override
        public Fragment getItem(int position) {
            // TODO Auto-generated method stub
            BaseTempFragment fragment = BaseTempFragment.newInstance(mList.get(position));
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            // 得到缓存的fragment
            BaseTempFragment fragment = (BaseTempFragment) super.instantiateItem(container,
                    position);
            WeakReference<Fragment> weak = new WeakReference<Fragment>(fragment);
            registeredFragments.put(position, weak);
            return fragment;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            registeredFragments.remove(position);

            super.destroyItem(container, position, object);

        }

        /**
         * 要求getItemPosition、FragmentStatePagerAdapter
         */
        public void remove(int position) {
            mList.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public BaseTempFragment getRegisteredFragment(int position) {
            return (BaseTempFragment) registeredFragments.get(position).get();
        }
    }
}
