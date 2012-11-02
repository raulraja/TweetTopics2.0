package com.javielinux.notifications;

import java.util.ArrayList;
import java.util.List;

public class UserNotifications {
    private List<Long> idsTimeline = new ArrayList<Long>();
    private List<Long> idsMentions = new ArrayList<Long>();
    private List<Long> idsDMs = new ArrayList<Long>();

    private String name = "";
    private long id = -1;

    public void setIdsDMs(List<Long> idsDMs) {
        this.idsDMs = idsDMs;
    }

    public void setIdsMentions(List<Long> idsMentions) {
        this.idsMentions = idsMentions;
    }

    public void setIdsTimeline(List<Long> idsTimeline) {
        this.idsTimeline = idsTimeline;
    }

    public List<Long> getIdsDMs() {
        return idsDMs;
    }

    public List<Long> getIdsMentions() {
        return idsMentions;
    }

    public List<Long> getIdsTimeline() {
        return idsTimeline;
    }

    public int getCountTimeline() {
        return idsTimeline.size();
    }

    public int getCountMentions() {
        return idsMentions.size();
    }

    public int getCountDMs() {
        return idsDMs.size();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return idsTimeline.size() + idsMentions.size() + idsDMs.size();
    }

    public long getFirstId() {
        if (idsTimeline.size() > 0) {
            return idsTimeline.get(0);
        }
        if (idsMentions.size() > 0) {
            return idsMentions.get(0);
        }
        if (idsDMs.size() > 0) {
            return idsDMs.get(0);
        }
        return 0;
    }

}
