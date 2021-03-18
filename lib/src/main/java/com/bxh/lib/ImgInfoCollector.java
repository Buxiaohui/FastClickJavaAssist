/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package com.bxh.lib;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImgInfoCollector {
    public static Map<String, String> getImageDataFromFile(String imgFilePath) {
        Map<String, String> imageMap = new HashMap<String, String>();
        File picture = new File(imgFilePath);
        BufferedImage sourceImg = null;
        try {
            sourceImg = ImageIO.read(new FileInputStream(picture));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String size = "";
        if (picture.exists() && picture.isFile()) {
            long fileS = picture.length();
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
        } else if (picture.exists() && picture.isDirectory()) {
            size = "";
        } else {
            size = "0BT";
        }
        int width = sourceImg.getWidth();
        int height = sourceImg.getHeight();
        imageMap.put("size", size);
        imageMap.put("path", imgFilePath);
        imageMap.put("height", String.valueOf(height));
        imageMap.put("width", String.valueOf(width));
        return imageMap;
    }

    public static Map<String, String> getImageDataFromInputStream(InputStream inputStream) throws IOException {
        Map<String, String> imageMap = new HashMap<String, String>();
        BufferedImage sourceImg = null;
        try {
            sourceImg = ImageIO.read(inputStream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String size = "";
            long fileS = inputStream.available();
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
        int width = sourceImg.getWidth();
        int height = sourceImg.getHeight();
        imageMap.put("size", size);
        imageMap.put("height", String.valueOf(height));
        imageMap.put("width", String.valueOf(width));
        return imageMap;
    }
}
