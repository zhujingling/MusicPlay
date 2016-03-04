package com.zjl.entity;

/**
 * Created by Administrator on 2016/3/4.
 */
public class SortEntity {
    private String name;
    private String sortLetters;
    private int sortKey;

    public SortEntity(String name, String sortLetters, int sortKey) {
        this.name = name;
        this.sortLetters = sortLetters;
        this.sortKey = sortKey;
    }

    public SortEntity() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public int getSortKey() {
        return sortKey;
    }

    public void setSortKey(int sortKey) {
        this.sortKey = sortKey;
    }
}
