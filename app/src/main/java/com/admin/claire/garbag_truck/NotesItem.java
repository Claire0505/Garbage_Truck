package com.admin.claire.garbag_truck;

import java.util.Date;
import java.util.Locale;

/**
 * Created by claire on 2017/5/16.
 */

public class NotesItem implements java.io.Serializable {

    private long id;
    private long datetime;
    private Colors color;
    private String title;
    private String content;
    private String fileName;
    private long lastModify;
    private boolean selected;
    // 提醒日期時間
    private long alarmDatetime;

    public NotesItem(){
        title = "";
        content = "";
        color = Colors.BLUE;
    }

    public NotesItem(long id, long datetime, Colors color, String title,
                     String content, String fileName, long lastModify) {

        this.id = id;
        this.datetime = datetime;
        this.color = color;
        this.title = title;
        this.content = content;
        this.fileName = fileName;
        this.lastModify = lastModify;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDatetime() {
        return datetime;
    }

    // 裝置區域的日期時間
    public String getLocaleDateTime() {
        return String.format(Locale.getDefault(), "%tF  %<tR", new Date(datetime));
    }

    // 裝置區域的日期
    public String getLocaleDate(){
        return String.format(Locale.getDefault(), "%tF", new Date(datetime));
    }

    // 裝置區域的時間
    public String getLocaleTime(){
        return String.format(Locale.getDefault(), "%tR", new Date(datetime));
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public Colors getColor() {
        return color;
    }

    public void setColor(Colors color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getAlarmDatetime() {
        return alarmDatetime;
    }

    public void setAlarmDatetime(long alarmDatetime) {
        this.alarmDatetime = alarmDatetime;
    }
}
