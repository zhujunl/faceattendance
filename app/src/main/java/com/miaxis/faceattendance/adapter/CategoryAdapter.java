package com.miaxis.faceattendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.model.entity.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAdapter<T> extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<T> dataList;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public CategoryAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.MyViewHolder holder, int position) {
        Category category = (Category) dataList.get(position);
        holder.tvCategoryId.setText(String.valueOf(category.getId()));
        holder.tvCategoryName.setText(category.getCategoryName());
        holder.tvCategoryPrompt.setText(category.getCategoryPrompt());
        holder.tvDeleteCategory.setOnClickListener(v ->
                onItemClickListener.onItemClick(holder.tvDeleteCategory, holder.getLayoutPosition()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public void appendDataList(List<T> dataList) {
        this.dataList.addAll(dataList);
    }

    public T getData(int position) {
        return dataList.get(position);
    }

    public void removeData(T t) {
        int position = dataList.indexOf(t);
        dataList.remove(t);
        notifyItemRemoved(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_category_id)
        TextView tvCategoryId;
        @BindView(R.id.tv_category_name)
        TextView tvCategoryName;
        @BindView(R.id.tv_category_prompt)
        TextView tvCategoryPrompt;
        @BindView(R.id.tv_delete_category)
        TextView tvDeleteCategory;
        @BindView(R.id.ll_item)
        LinearLayout llItem;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
