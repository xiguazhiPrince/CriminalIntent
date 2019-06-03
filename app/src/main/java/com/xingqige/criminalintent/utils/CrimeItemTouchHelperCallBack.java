package com.xingqige.criminalintent.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.xingqige.criminalintent.CrimeListFragment;

public class CrimeItemTouchHelperCallBack extends ItemTouchHelper.Callback {

    private CrimeListFragment.CrimeAdapter mAdapter;

    public CrimeItemTouchHelperCallBack(CrimeListFragment.CrimeAdapter adapter){
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN ;        //允许上下的拖动
        int dragFlags = 0;
        int swipeFlags = ItemTouchHelper.LEFT;   //只允许从右向左侧滑
        return makeMovementFlags(dragFlags,swipeFlags);
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(),viewHolder1.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        mAdapter.onItemDissmiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
