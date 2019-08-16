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
import com.ads.abcbank.xx.utils.helper.IOHelper;
import com.ads.abcbank.xx.utils.helper.PdfHelper;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.alibaba.fastjson.JSON;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

                        Utils.getExecutorService().submit(() -> loadPlaylist());
                        Utils.getExecutorService().submit(() -> loadPreset());
                        Utils.getExecutorService().submit(() -> showWelcome(null));

                        break;

                    case Constants.SLIDER_STATUS_CODE_UPDATE:
                        int resCode = (int)msg.obj;

                        Utils.getExecutorService().submit(() -> {
                            if (resCode == Constants.NET_MANAGER_DATA_PLAYLIST)
                                    loadPlaylist();
                            else if(resCode == Constants.NET_MANAGER_DATA_PRESET)
                                    loadPreset();
                        });

                        break;

                    case Constants.SLIDER_STATUS_CODE_DOWNSUCC:
                        if (msg.obj instanceof String[] ){
                            String[] downInfo = (String[]) msg.obj;
                            if (downInfo.length > 1) {
                                try {
                                    finishDownload(downInfo[0], downInfo[1]);
                                } catch (Exception e) {
                                    Logger.e(TAG, e.getMessage());
                                }
                            }
                        }

                        break;

                    default:
                        break;
                }
            }
        };
    }


    /*
     * 处理资源下载完成通知
     * */
    private void finishDownload(String fileUrl, String filePath) {
        if (Constants.SYS_CONFIG_IS_CHECKMD5) {
            if (!loadedMaterial.containsKey(fileUrl)
                    || !IOHelper.fileToMD5(filePath).equals(loadedMaterial.get(fileUrl).md5)
            ) {
                IOHelper.deleteFile(filePath);
                loadedMaterial.remove(fileUrl);
                return;
            }
        }

        PlaylistBodyBean bodyBean = loadedMaterial.get(fileUrl);
        bodyBean.secUsed = ResHelper.getTimeDiff(bodyBean.started);
        netTaskMoudle.notifyDownloadFinish(new String[]{ bodyBean.id, bodyBean.started, bodyBean.secUsed });

        // 更新素材集状态
        String correctionFilePath = ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id);
        String _fileKey = correctionFilePath.substring(correctionFilePath.lastIndexOf("/") + 1,
                correctionFilePath.lastIndexOf(".") );
        if (materialStatus.containsKey(_fileKey) && materialStatus.get(_fileKey) == 1)
            return;


        if (!IOHelper.copyOrMoveFile(filePath, correctionFilePath, true)) {
            Logger.e(TAG, "copyOrMoveFile failed-->" + filePath + " to " + correctionFilePath);
            return;
        }

        filePath = correctionFilePath;
        materialStatus.put(_fileKey, 1);

        // 更新已下载素材状态
        String[] ids = materialStatus.keySet().toArray(new String[0]);
        Utils.put(context, Constants.MM_STATUS_FINISHED_TASKID, ResHelper.join(ids, ","));

        // 处理pdf缓存或者通知前端显示
//                        Utils.getExecutorService().submit(() -> {
        String fileKey = filePath.substring(filePath.lastIndexOf("/") + 1,
                filePath.lastIndexOf(".") );
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        boolean needNotify = false;
        Logger.e(TAG, "MM_STATUS_FINISHED_TASKID-->" + _fileKey + "." + suffix + " ,count:" + ids.length /*ResHelper.join(ids, ",")*/);

        if (suffix.toLowerCase().equals("pdf")) {
            List<PlayItem> list = PdfHelper.cachePdfToImage( fileName, fileKey,
                    bodyBean.playDate, bodyBean.stopDate,
                    bodyBean.onClickLink, bodyBean.QRCode  );

            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PDF_CACHED, list);
            needNotify = true;
        } else if (BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_IMAGE
                || BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_VIDEO) {
            PlayItem playItem = new PlayItem(fileKey,
                    filePath,
                    BllDataExtractor.getIdentityType(suffix),
                    bodyBean.playDate, bodyBean.stopDate,
                    bodyBean.onClickLink, bodyBean.QRCode);

            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_DOWNSUCC, new ArrayList<PlayItem>(){
                { add(playItem); }
            });
            needNotify = true;
        } else if (suffix.toLowerCase().equals("txt")) {
            String wmsg = ResHelper.readFile2String(filePath);
            if (!ResHelper.isNullOrEmpty(wmsg)) {
                List<String> list = new ArrayList<>();
                list.add(wmsg);

                ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_WELCOME_LOADED, list);
            }
        }

//        loadedMaterial.remove(fileUrl);
        if (needNotify)
            resendPlaylistOkMessage();
//                        });
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

                Logger.e(TAG, "loadPlaylist-->" + jsonFinish + " ,count:" + ids.length);
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
            List<String> welcomeItems = new ArrayList<>();
            String contentTypeMiddle = Utils.getContentTypeMiddle(context);
            String contentTypeEnd = Utils.getContentTypeEnd(context);
            long taskFlag = System.currentTimeMillis();

            for (PlaylistBodyBean bodyBean:playlistBodyBeanLists) {
                try{
                    // 过滤非下载时段和已下载项
                    if (!BllDataExtractor.isInDownloadTime(bodyBean)
                            || !BllDataExtractor.isInFilter(filters, bodyBean, contentTypeMiddle, contentTypeEnd)
                    )
                        continue;

                    String suffix = bodyBean.downloadLink.substring(bodyBean.downloadLink.lastIndexOf(".") + 1).toLowerCase();
                    String savePath = ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id);
                    boolean needDownload = false;

                    if (loadedMaterial.containsKey(bodyBean.downloadLink)) {
                        if (!isMaterialMarked(bodyBean.id) || ResHelper.isExistsFile(savePath))
                            continue;
                        else
                            needDownload = true;
                    }

                    // 初次加载
                    if (!isMaterialMarked(bodyBean.id)) {
                        needDownload = true;
                    }

                    bodyBean.started = ResHelper.getCurTime();
                    loadedMaterial.put(bodyBean.downloadLink, bodyBean);

                    // 构建待下载数据
                    if (needDownload) {
                        materialStatus.remove(bodyBean.id);

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
                    } else
                        Logger.e(TAG, "not in:" + savePath + ".." + suffix);

                } catch (Exception e) {
                    Logger.e(TAG, e.getMessage());
                }
            }

            if (waitForDownloadUrls.size() > 0 && waitForDownloadUrls.size() == waitForDownloadSavePath.size()) {
                Logger.e(TAG, "downloadModule.start-->" + ", count" + waitForDownloadUrls.size()
                        + ", items:" + ResHelper.join( waitForDownloadUrls.toArray(new String[0]), "#"));

                downloadModule.start(waitForDownloadUrls, ResHelper.getTempRootDir() + taskFlag, waitForDownloadSavePath);
            }

            if (allPlayItems.size() > 0)
                ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PLAYLIST_LOADED, allPlayItems);

            if (!isActionExecuted(Constants.MM_STATUS_KEY_PLAYLIST_LOADED)) {
                managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_LOADED, 1);
                managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_DOWNLOADED, allPlayItems.size() > 0 ? 1 : 0);
                ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PROGRESS,
                        allPlayItems.size() > 0 ? Constants.SLIDER_PROGRESS_CODE_PLAYLIST_OK : Constants.SLIDER_PROGRESS_CODE_PLAYLIST_EMPTY);
            }

            if (welcomeItems.size() > 0)
                showWelcome(welcomeItems);
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
        } else
            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_WELCOME_LOADED, welcomeItems);

    }

    private void resendPlaylistOkMessage() {
        if (!isActionExecuted(Constants.MM_STATUS_KEY_PLAYLIST_DOWNLOADED)){
            managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_DOWNLOADED, 1);
            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PLAYLIST_OK);
        }
    }

}
