/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package com.buxiaohui.fastclickjavaassist;

import java.util.Arrays;

import android.graphics.Bitmap;

public class ImgInfo {
    private String viewIdStr;
    private int viewId;
    private int viewWidth;
    private int viewHeight;
    private String imageUrl;
    private int imageResId;
    private String imagePath;
    private int imageWidth;
    private int imageHeight;

    private int imageSourceWidth;
    private int imageSourceHeight;

    private int imageSize;
    private String imageSizeStr;
    private int imageSourceSize;
    private String imageSourceSizeStr;

    public String getViewIdStr() {
        return viewIdStr;
    }

    public ImgInfo setViewIdStr(String viewIdStr) {
        this.viewIdStr = viewIdStr;
        return this;
    }

    public int getViewId() {
        return viewId;
    }

    public ImgInfo setViewId(int viewId) {
        this.viewId = viewId;
        return this;
    }

    public int getImageSourceSize() {
        return imageSourceSize;
    }

    public String getImageSourceSizeStr() {
        return imageSourceSizeStr;
    }

    public ImgInfo setImageSourceSizeStr(String imageSourceSizeStr) {
        this.imageSourceSizeStr = imageSourceSizeStr;
        return this;
    }

    public ImgInfo setImageSourceSize(int imageSourceSize) {
        this.imageSourceSize = imageSourceSize;
        return this;
    }

    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public ImgInfo setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public ImgInfo setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
        return this;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public ImgInfo setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ImgInfo setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public int getImageResId() {
        return imageResId;
    }

    public ImgInfo setImageResId(int imageResId) {
        this.imageResId = imageResId;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ImgInfo setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public ImgInfo setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
        return this;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public ImgInfo setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
        return this;
    }

    public int getImageSourceWidth() {
        return imageSourceWidth;
    }

    public ImgInfo setImageSourceWidth(int imageSourceWidth) {
        this.imageSourceWidth = imageSourceWidth;
        return this;
    }

    public int getImageSourceHeight() {
        return imageSourceHeight;
    }

    public ImgInfo setImageSourceHeight(int imageSourceHeight) {
        this.imageSourceHeight = imageSourceHeight;
        return this;
    }

    public int getImageSize() {
        return imageSize;
    }

    public ImgInfo setImageSize(int imageSize) {
        this.imageSize = imageSize;
        return this;
    }

    public String getImageSizeStr() {
        return imageSizeStr;
    }

    public ImgInfo setImageSizeStr(String imageSizeStr) {
        this.imageSizeStr = imageSizeStr;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImgInfo that = (ImgInfo) o;
        return viewWidth == that.viewWidth &&
                viewHeight == that.viewHeight &&
                imageResId == that.imageResId &&
                imageWidth == that.imageWidth &&
                imageHeight == that.imageHeight &&
                imageSourceWidth == that.imageSourceWidth &&
                imageSourceHeight == that.imageSourceHeight &&
                imageSize == that.imageSize &&
                imageSourceSize == that.imageSourceSize &&
                equals(imageSizeStr, that.imageSizeStr) &&
                equals(imageSourceSizeStr, that.imageSourceSizeStr) &&
                equals(imageUrl, that.imageUrl) &&
                equals(imagePath, that.imagePath) &&
                equals(bitmap, that.bitmap);
    }

    private static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    private static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"viewIdStr\":\"")
                .append(viewIdStr).append('\"');
        sb.append(",\"viewId\":")
                .append(viewId);
        sb.append(",\"viewWidth\":")
                .append(viewWidth);
        sb.append(",\"viewHeight\":")
                .append(viewHeight);
        sb.append(",\"imageUrl\":\"")
                .append(imageUrl).append('\"');
        sb.append(",\"imageResId\":")
                .append(imageResId);
        sb.append(",\"imagePath\":\"")
                .append(imagePath).append('\"');
        sb.append(",\"imageWidth\":")
                .append(imageWidth);
        sb.append(",\"imageHeight\":")
                .append(imageHeight);
        sb.append(",\"imageSourceWidth\":")
                .append(imageSourceWidth);
        sb.append(",\"imageSourceHeight\":")
                .append(imageSourceHeight);
        sb.append(",\"imageSize\":")
                .append(imageSize);
        sb.append(",\"imageSizeStr\":\"")
                .append(imageSizeStr).append('\"');
        sb.append(",\"imageSourceSize\":")
                .append(imageSourceSize);
        sb.append(",\"imageSourceSizeStr\":\"")
                .append(imageSourceSizeStr).append('\"');
        sb.append(",\"bitmap\":")
                .append(bitmap != null ? bitmap.hashCode() : "null");
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return hash(viewWidth,
                viewHeight,
                viewIdStr,
                viewId,
                imageResId,
                imageWidth,
                imageHeight,
                imageSourceWidth,
                imageSourceHeight,
                imageSize,
                imageSourceSize,
                imageUrl,
                imageSourceSizeStr,
                imageSizeStr,
                imagePath,
                bitmap);
    }

}
