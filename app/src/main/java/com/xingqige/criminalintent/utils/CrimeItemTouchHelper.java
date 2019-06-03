package com.xingqige.criminalintent.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.xingqige.criminalintent.CrimeListFragment;

public interface CrimeItemTouchHelper {
    //数据交换
    void onItemMove(int fromPosition,int toPosition);
    //数据删除
    void onItemDissmiss(int position);

}
