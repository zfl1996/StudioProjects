package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.BllDataExtractor;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.helper.PdfHelper;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.alibaba.fastjson.JSON;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialManager extends MaterialManagerBase {
    static String TAG = MaterialManagerBase.class.getSimpleName();;

    public MaterialManager(Context context, MaterialStatusListener materialStatusListener) {
        this.context = context;
        this.itemStatusListener = new WeakReference<>(materialStatusListener);
    }

    @Override
    protected Handler buildMaterialHandler() {
        return new Handler(materialThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (null == msg)
                    return;

                switch (msg.what) {

                    case Constants.SLIDER_STATUS_CODE_INIT:
                        Logger.e(TAG, "tid:(SLIDER_STATUS_CODE_INIT)" + Thread.currentThread().getId());
                        Utils.getExecutorService().submit(() -> loadPlaylist());
                        Utils.getExecutorService().submit(() -> loadPreset());
                        Utils.getExecutorService().submit(() -> showWelcome(null));

                        break;

                    case Constants.SLIDER_STATUS_CODE_UPDATE:
                        int resCode = (int)msg.obj;
                        Logger.e(TAG, "SLIDER_STATUS_CODE_UPDATE --> resCode:" + resCode + "(tid:" + Thread.currentThread().getId() + ")");
                        Utils.getExecutorService().submit(() -> {
                            if (resCode == Constants.NET_MANAGER_DATA_PLAYLIST) {
                                if (managerStatus.get(Constants.MM_STATUS_KEY_PLAYLIST_LOADED) != 1)
                                    loadPlaylist();
                            } else if(resCode == Constants.NET_MANAGER_DATA_PRESET) {
                                if (managerStatus.get(Constants.MM_STATUS_KEY_PRESET_LOADED) != 1)
                                    loadPreset();
                            }
                        });

                        break;

                    case Constants.SLIDER_STATUS_CODE_DOWNSUCC:
                        String taskStr = "SLIDER_STATUS_CODE_DOWNSUCC-->";
                        if (msg.obj instanceof String[] ){
                            String[] downInfo = (String[]) msg.obj;
                            if (downInfo.length > 1) {
                                taskStr += downInfo[1];
                                finishDownload(downInfo[0], downInfo[1]);
                            }
                        }
                        Logger.e(TAG, taskStr);

                        break;

                    default:
                        break;
                }
            }
        };
    }

    /*
    * 加载汇率数据
    * */
    private void loadPreset() {
        if (!isActionExecuted(Constants.MM_STATUS_KEY_PRESET_INIT)) {
            managerStatus.put(Constants.MM_STATUS_KEY_PRESET_INIT, 1);
            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PRESET_PRE);
        }

        String json = Utils.get(context, Utils.KEY_PRESET, "").toString();

        if (!ResHelper.isNullOrEmpty(json)) {

            PresetBean bean = JSON.parseObject(json, PresetBean.class);
            if (null == bean || !"0".equals(bean.resCode))
                return;

            List<PlayItem> presetItems = new ArrayList<>();
            List<String> presetTitles = new ArrayList<>();

            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_SAVE, bean.data.saveRate) );
            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_LOAN, bean.data.loanRate) );
            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_BUY, bean.data.buyInAndOutForeignExchange) );

            presetTitles.add(bean.data.saveRate.title.substring(0, 4) + "\n" + bean.data.saveRate.title.substring(4));
            presetTitles.add(bean.data.loanRate.title.substring(0, 4) + "\n" + bean.data.loanRate.title.substring(4));
            presetTitles.add(bean.data.buyInAndOutForeignExchange.title.substring(0, 4) + "\n" + bean.data.buyInAndOutForeignExchange.title.substring(4));

            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_RATE_LOADED, new Object[]{presetItems, presetTitles});

            if (!isActionExecuted(Constants.MM_STATUS_KEY_PRESET_LOADED)) {
                managerStatus.put(Constants.MM_STATUS_KEY_PRESET_LOADED, 1);
                ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PRESET_OK);
            }
        }
    }

    /*
    * 处理播放列表信息
    * */
    private void loadPlaylist() {
        if (!isActionExecuted(Constants.MM_STATUS_KEY_PLAYLIST_INIT)) {
            managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_INIT, 1);
            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PLAYLIST_PRE);

            // 同步已下载完成数据项
            String jsonFinish = Utils.get(context, Constants.MM_STATUS_FINISHED_TASKID, "").toString();
            if (!TextUtils.isEmpty(jsonFinish)) {
                String[] ids = jsonFinish.split(",");
                for (String id : ids) {
                    if (!ResHelper.isNullOrEmpty(id))
                        materialStatus.put(id, 1);
                }
            }
        }

        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (ResHelper.isNullOrEmpty(json))
            return;

        try {
            List<PlaylistBodyBean> playlistBodyBeanLists = JSON.parseArray(json, PlaylistBodyBean.class);
            List<PlayItem> allPlayItems = new ArrayList<>();
            List<String> waitForDownloadUrls = new ArrayList<>();
            List<String> waitForDownloadSavePath = new ArrayList<>();
            Map<String, Integer> waitForFiles = new HashMap<>();
            List<String> welcomeItems = new ArrayList<>();
            String contentTypeMiddle = Utils.getContentTypeMiddle(context);
            String contentTypeEnd = Utils.getContentTypeEnd(context);
            long taskFlag = System.currentTimeMillis();

            for (PlaylistBodyBean bodyBean:playlistBodyBeanLists) {
                // 过滤非下载时段和已下载项
                if (!BllDataExtractor.isInDownloadTime(bodyBean)
                        || !BllDataExtractor.isInFilter(filters, bodyBean, contentTypeMiddle, contentTypeEnd)
//                        || materialStatus.containsKey(bodyBean.id)
                        || waitForFiles.containsKey(bodyBean.downloadLink) )
                    continue;

                waitForFiles.put(bodyBean.downloadLink, 0);
//                materialStatus.put(bodyBean.id, 0);

                String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                String savePath = ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id);

                // 构建待下载数据
                if (!materialStatus.containsKey(bodyBean.id)
                        || materialStatus.get(bodyBean.id) != 1 || !ResHelper.isExistsFile(savePath)) {
                    String[] pathSegments = ResHelper.getSavePathDataByUrl(bodyBean.downloadLink);
                    if (pathSegments.length <= 0)
                        continue;

                    if (!ResHelper.isNullOrEmpty(bodyBean.isUrg) && bodyBean.isUrg.equals("1")) {
                        waitForDownloadUrls.add(0, bodyBean.downloadLink);
                        waitForDownloadSavePath.add(0, pathSegments[1] + bodyBean.id + "." + pathSegments[0]);
                    } else {
                        waitForDownloadUrls.add(bodyBean.downloadLink);
                        waitForDownloadSavePath.add(pathSegments[1] + bodyBean.id + "." + pathSegments[0]);
                    }

                    waitForDownloadMaterial.put(bodyBean.downloadLink, bodyBean);
                    continue;
                }


                if (suffix.equals("pdf")) {
                    allPlayItems.addAll(PdfHelper.getCachedPdfImage(bodyBean.id + ".pdf",
                            bodyBean.playDate, bodyBean.stopDate,
                            bodyBean.onClickLink, bodyBean.QRCode));
                } else if(BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_IMAGE
                    || BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_VIDEO) {
                    allPlayItems.add(new PlayItem(bodyBean.id,
                            ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id),
                            BllDataExtractor.getIdentityType(bodyBean),
                            bodyBean.playDate, bodyBean.stopDate,
                            bodyBean.onClickLink, bodyBean.QRCode));
                } else if (suffix.equals("txt")) {
                    String wmsg = ResHelper.readFile2String(ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id));
                    if (!ResHelper.isNullOrEmpty(wmsg))
                        welcomeItems.add(wmsg);
                }
            }

            if (waitForDownloadUrls.size() > 0 && waitForDownloadUrls.size() == waitForDownloadSavePath.size()) {
                Logger.e(TAG, "tid:(loadPlaylist)" + Thread.currentThread().getId());
                downloadModule.start(waitForDownloadUrls, ResHelper.getTempRootDir() + taskFlag, waitForDownloadSavePath);
            }

            if (allPlayItems.size() > 0)
                ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PLAYLIST_LOADED, allPlayItems);

            if (welcomeItems.size() > 0)
                showWelcome(welcomeItems);


            if (!isActionExecuted(Constants.MM_STATUS_KEY_PLAYLIST_LOADED)) {
                managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_LOADED, 1);
//            if (!isIntegrationPresetData())
                ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PLAYLIST_OK);
            }
            Logger.e(TAG, "loadPlaylist-->" + ResHelper.join((String[]) waitForDownloadSavePath.toArray(), "@@\r\n"));

        } catch (Exception e) {
            Logger.e("解析播放列表出错" + json);
        }

    }

    private void showWelcome(List<String> welcomeItems) {
        if (null == welcomeItems || welcomeItems.size() <= 0) {
            welcomeItems = buildWelcomeWords();

            managerStatus.put(Constants.MM_STATUS_KEY_WELCOME_LOADED, 0);
            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_WELCOME_DEFAULT, welcomeItems);
        } else {
//            managerStatus.put(Constants.MM_STATUS_KEY_WELCOME_LOADED, 1);
            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_WELCOME_LOADED, welcomeItems);
            if (!isActionExecuted(Constants.MM_STATUS_KEY_WELCOME_LOADED) && welcomeItems.size() > 0)
                managerStatus.put(Constants.MM_STATUS_KEY_WELCOME_LOADED, 1);
        }

    }

}
