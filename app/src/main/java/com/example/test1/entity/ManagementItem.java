package com.example.test1.entity;

public class ManagementItem {
    private String title;
    private Class<?> activityClass;

    public ManagementItem(String title, Class<?> activityClass) {
        this.title = title;
        this.activityClass = activityClass;
    }

    public String getTitle() {
        return title;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }
}
