package com.chrisprime.primestationonecontrol.utilities;

import java.util.Calendar;

/**
 * Created by akuma1 on 12/9/15.
 */
public class PublishDateSorting {

    private Calendar publishDate;
    private String title;
    private String subTitle;
    private Boolean ctaToolState;

    public Calendar getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Calendar publishDate) {
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Boolean getCTAState() {
        return ctaToolState;
    }

    public void setCTAState(Boolean ctaToolState) {
        this.ctaToolState = ctaToolState;
    }
}
