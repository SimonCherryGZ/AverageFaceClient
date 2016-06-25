package com.simoncherry.averagefaceclient2.base;

/**
 * Created by Simon on 2016/6/22.
 */
public class LayerBean {
    private static int layer = 0;
    private static String directory;

    public static int getLayer() {
        return layer;
    }

    public static void setLayer(int layer) {
        LayerBean.layer = layer;
    }

    public static String getDirectory() {
        return directory;
    }

    public static void setDirectory(String directory) {
        LayerBean.directory = directory;
    }
}
