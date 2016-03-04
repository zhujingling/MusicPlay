package com.zjl.util;

import com.zjl.entity.SortEntity;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/3/4.
 */
public class PinYinComparator implements Comparator<SortEntity> {
    @Override
    public int compare(SortEntity lhs, SortEntity rhs) {
        if (lhs.getSortLetters().equals("@") || rhs.getSortLetters().equals("#")) {
            return -1;
        } else if (lhs.getSortLetters().equals("#")
                || rhs.getSortLetters().equals("@")) {
            return 1;
        } else {
            return lhs.getSortLetters().compareTo(rhs.getSortLetters());
        }
    }
}
