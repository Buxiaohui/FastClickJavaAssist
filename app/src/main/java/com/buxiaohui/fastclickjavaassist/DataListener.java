/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package com.buxiaohui.fastclickjavaassist;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.util.ContentLengthInputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

public class DataListener {
    private static final String TAG = "DataListener";
    private static HashMap<String, ImgInfo> imgInfoMap = new HashMap<>();

    public void onDownsamplerDecodeStream(Bitmap result,
                                          int sourceWidth,
                                          int sourceHeight,
                                          String sourceMimeType,
                                          BitmapFactory.Options options,
                                          Bitmap downsampled,
                                          int requestedWidth,
                                          int requestedHeight,
                                          long startTime) {
        Log.e(TAG, "onDownsamplerDecodeStream->result:" + result + "\n"
                + ",sourceWidth:" + sourceWidth + "\n"
                + ",sourceHeight:" + sourceHeight + "\n"
                + ",sourceMimeType:" + sourceMimeType + "\n"
                + ",options:" + options + "\n"
                + ",downsampled:" + downsampled + "\n"
                + ",requestedWidth:" + requestedWidth + "\n"
                + ",requestedHeight:" + requestedHeight + "\n"
                + ",startTime:" + startTime);

    }

    public void onResourceReady(Object resource, Object model, Target target, DataSource dataSource,
                                boolean isFirstResource) {
        int width = 0;
        int heigh = 0;
        int viewId = 0;
        StringBuilder viewInfo = new StringBuilder();
        if (target instanceof ViewTarget) {
            View view = ((ViewTarget) target).getView();
            width = view.getWidth();
            heigh = view.getHeight();
            viewId = view.getId();
            if (viewId <= 0) {
                viewInfo.append("no id");// TODO 优化，展示view层级信息
            } else {
                String viewStr = view.getResources().getResourceName(view.getId());
                viewInfo.append(viewStr);
            }
        }
        Log.e(TAG, "onResourceReady,resource:" + resource + "\n"
                + ",isFirstResource:" + isFirstResource + "\n"
                + ",model:" + model + "\n"
                + ", view(宽*高）:" + width + "*" + heigh + "\n"
                + ",dataSource:" + dataSource);
        switch (dataSource) {
            case LOCAL:
                break;
            case REMOTE:
                saveInfoOnRemoteReady(resource, model, width, heigh, viewInfo.toString());
                break;
            case MEMORY_CACHE:
                break;
            case DATA_DISK_CACHE:
                break;
            case RESOURCE_DISK_CACHE:
                break;
            default:
                break;
        }
    }

    private void saveInfoOnRemoteReady(Object resource, Object model, int width, int heigh, String viewInfo) {
        ImgInfo info = null;
        if (resource instanceof Bitmap) {
            info = transform(model.toString(), (Bitmap) resource, "Glide", width, heigh, viewInfo);
        } else if (resource instanceof BitmapDrawable) {
            info = transform(model.toString(), (BitmapDrawable) resource, "Glide", width, heigh, viewInfo);
        }

        if (info != null) {
            printRetMap();
            if (!imgInfoMap.containsKey(model)) {
                imgInfoMap.put((String) model, info);
            } else {
                imgInfoMap.get(model)
                        .setImageWidth(info.getImageWidth())
                        .setImageHeight(info.getImageWidth())
                        .setViewHeight(info.getViewHeight())
                        .setViewWidth(info.getViewWidth())
                        .setViewIdStr(info.getViewIdStr())
                        .setBitmap(info.getBitmap())
                        .setImageSizeStr(formatSizeString(info.getImageSize()))
                        .setImageSize(info.getImageSize());
            }
        }
        printRetMap();
    }

    private void printRetMap() {
        Iterator<ImgInfo> imgInfoIterator = imgInfoMap.values().iterator();
        while (imgInfoIterator.hasNext()) {
            ImgInfo info1 = imgInfoIterator.next();
            Log.e(TAG, "imageinfo:" + info1);
        }
    }

    public ImgInfo transform(String imageUrl, BitmapDrawable bitmapDrawable, String framework, int targetWidth,
                             int targetHeight, String viewInfo) {
        Bitmap sourceBitmap = drawable2Bitmap(bitmapDrawable);
        return transform(imageUrl, sourceBitmap, framework, targetWidth, targetHeight, viewInfo);
    }

    public ImgInfo transform(String imageUrl, Bitmap sourceBitmap, String framework, int targetWidth,
                             int targetHeight, String viewInfo) {
        if (null == sourceBitmap) {
            return null;
        }
        ImgInfo info = new ImgInfo()
                .setImageUrl(imageUrl)
                .setViewWidth(targetWidth)
                .setViewHeight(targetHeight)
                .setViewIdStr(viewInfo)
                .setBitmap(sourceBitmap)
                .setImageWidth(sourceBitmap.getWidth())
                .setImageHeight(sourceBitmap.getHeight())
                .setImageSize(sourceBitmap.getByteCount())
                .setImageSizeStr(formatSizeString(sourceBitmap.getByteCount()));
        return info;
    }

    public static Bitmap drawable2Bitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void onHttpDataFetchReady(int len, URL url, Map<String, String> headers) {
        Log.e(TAG, "onHttpDataFetchReady->len:" + len + "\n"
                + ",url:" + url + "\n"
                + ",headers:" + headers);
        saveInfoOnHttpFetchSuccess(len, url.toString());
    }

    private void saveInfoOnHttpFetchSuccess(int len, String url) {
        ImgInfo info = new ImgInfo()
                .setImageUrl(url)
                .setImageSourceSizeStr(formatSizeString(len))
                .setImageSourceSize(len);
        printRetMap();
        if (!imgInfoMap.containsKey(url)) {
            imgInfoMap.put(url, info);
        } else {
            ImgInfo info1 = imgInfoMap.get(url);
            info1.setImageWidth(info.getImageWidth())
                    .setImageSourceSizeStr(formatSizeString(len))
                    .setImageSourceSize(len);
        }
    }

    public void onDecodeFromRetrievedData(long startFetchTime,
                                          Object currentData,
                                          Key currentSourceKey,
                                          DataFetcher<?> currentFetcher,
                                          DataSource currentDataSource,
                                          int viewWidth,
                                          int viewHeight) {
        Log.e(TAG, "onDecodeFromRetrievedData->startFetchTime:" + startFetchTime + "\n"
                + ",currentData:" + currentData + "\n"
                + ",currentSourceKey:" + currentSourceKey + "\n"
                + ",currentFetcher:" + currentFetcher + "\n"
                + ",currentDataSource:" + currentDataSource + "\n"
                + ",viewWidth:" + viewWidth + "\n"
                + ",viewHeight:" + viewHeight);
        Map<String, String> infoMap = new HashMap<>();
        if (currentData != null && currentData instanceof InputStream) {
            Bitmap bitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            resetOptions(options);
            options.inJustDecodeBounds = true;
            try {

                TransformationUtils.getBitmapDrawableLock().lock();
                if (currentData instanceof AssetManager.AssetInputStream) {
                    infoMap.put("size", formatSizeString(((AssetManager.AssetInputStream) currentData).available()));
                    Log.e(TAG, "1-markSupported:" + ((AssetManager.AssetInputStream) currentData).markSupported());
                    bitmap = BitmapFactory.decodeStream(((InputStream) currentData));
                } else if (currentData instanceof ContentLengthInputStream) {
                    infoMap.put("size", formatSizeString(((ContentLengthInputStream) currentData).available()));
                    bitmap = BitmapFactory.decodeStream(((InputStream) currentData));
                    Log.e(TAG, "2-markSupported:" + ((ContentLengthInputStream) currentData).markSupported());
                }
                TransformationUtils.getBitmapDrawableLock().lock();
            } catch (Exception e) {
                Log.e(TAG, "onDecodeFromRetrievedData->decodeStream-e:" + e);
            }
            Log.e(TAG, "onDecodeFromRetrievedData->bitmap:" + bitmap);
            infoMap.put("ori_bitmap_size", formatSizeString(bitmap != null ? bitmap.getByteCount() : 0));
            infoMap.put("width", bitmap != null ? bitmap.getWidth() + "" : "--");
            infoMap.put("height", bitmap != null ? bitmap.getHeight() + "" : "--");
        }
        String size = infoMap.get("size");
        String dataWidth = infoMap != null ? infoMap.get("width") : "--";
        String dataHeight = infoMap != null ? infoMap.get("height") : "--";
        Log.e(TAG, "onDecodeFromRetrievedData->" + "\n"
                + ",文件大小:" + size + "\n"
                + ",data宽高:" + dataWidth + "," + dataHeight + "\n"
                + ",view宽高:" + viewWidth + "," + viewHeight + "\n"
                + ",文件来源:" + currentDataSource + "\n"
                + ",文件地址:" + currentSourceKey);

    }

    private static void resetOptions(BitmapFactory.Options decodeBitmapOptions) {
        decodeBitmapOptions.inTempStorage = null;
        decodeBitmapOptions.inDither = false;
        decodeBitmapOptions.inScaled = false;
        decodeBitmapOptions.inSampleSize = 1;
        decodeBitmapOptions.inPreferredConfig = null;
        decodeBitmapOptions.inJustDecodeBounds = false;
        decodeBitmapOptions.inDensity = 0;
        decodeBitmapOptions.inTargetDensity = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decodeBitmapOptions.inPreferredColorSpace = null;
            decodeBitmapOptions.outColorSpace = null;
            decodeBitmapOptions.outConfig = null;
        }
        decodeBitmapOptions.outWidth = 0;
        decodeBitmapOptions.outHeight = 0;
        decodeBitmapOptions.outMimeType = null;
        decodeBitmapOptions.inBitmap = null;
        decodeBitmapOptions.inMutable = true;
    }

    private static String formatSizeString(int fileS) {
        String size;
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileS < 1024) {
            size = df.format((double) fileS) + "BT";
        } else if (fileS < 1048576) {
            size = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            size = df.format((double) fileS / (1024 * 1024)) + "MB";
        } else {
            size = df.format((double) fileS / (1024 * 1024 * 1024)) + "GB";
        }
        return size;
    }
}
