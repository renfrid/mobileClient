package org.sacids.android.models;

/**
 * Created by Renfrid-Sacids on 2/22/2016.
 */

public class NavDrawerItem {
    private boolean showNotify;
    private String title,icon;


    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title,String icon) {
        this.showNotify = showNotify;
        this.title = title;
        this.icon=icon;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon(){return icon;}

    public void setIcon(String icon){this.icon=icon;}
}