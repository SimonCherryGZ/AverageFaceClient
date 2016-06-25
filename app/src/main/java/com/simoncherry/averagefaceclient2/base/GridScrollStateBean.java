package com.simoncherry.averagefaceclient2.base;

/**
 * Created by Simon on 2016/6/25.
 */
public class GridScrollStateBean {
    private static int index = 0;
    private static int top = 0;

    public static int getIndex() {
        return index;
    }

    public static void setIndex(int index) {
        GridScrollStateBean.index = index;
    }

    public static int getTop() {
        return top;
    }

    public static void setTop(int top) {
        GridScrollStateBean.top = top;
    }
}
