package com.miaxis.faceattendance.adapter.listener;

import com.miaxis.faceattendance.util.ValueUtil;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class GridEndLessOnScrollListener extends  RecyclerView.OnScrollListener {

    private GridLayoutManager gridLayoutManager;

    //当前页，从0开始
    private int currentPage = 1;
    //已经加载出来的Item的数量
    private int totalItemCount;

    //主要用来存储上一个totalItemCount
    private int previousTotal = 0;

    //在屏幕上可见的item数量
    private int visibleItemCount;

    //在屏幕可见的Item中的第一个
    private int firstVisibleItem;

    //是否正在上拉数据
    private boolean loading = true;

    public GridEndLessOnScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = gridLayoutManager.getItemCount();
        firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
        if(loading){
            if(totalItemCount > previousTotal){
                //说明数据已经加载结束
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem && totalItemCount >= ValueUtil.PAGESIZE) {
            currentPage ++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    /**
     * 提供一个抽闲方法，在Activity中监听到这个EndLessOnScrollListener
     * 并且实现这个方法
     * */
    public abstract void onLoadMore(int currentPage);

    public void reset() {
        currentPage = 1;
        previousTotal = 0;
    }

}
