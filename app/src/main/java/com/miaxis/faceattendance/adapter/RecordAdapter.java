package com.miaxis.faceattendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.util.ValueUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordAdapter<T> extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> {

    private List<T> dataList;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public RecordAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_record, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecordAdapter.MyViewHolder holder, int position) {
        Record record = (Record) dataList.get(position);
        holder.tvRecordName.setText(record.getName());
        holder.tvRecordCardNumber.setText(record.getCardNumber());
        holder.tvRecordResult.setText("通过");
        holder.tvRecordOpdate.setText(record.getVerifyTime());
        holder.llItem.setOnClickListener(v -> onItemClickListener.onItemClick(holder.llItem, holder.getLayoutPosition()));
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_record_name)
        TextView tvRecordName;
        @BindView(R.id.tv_record_card_number)
        TextView tvRecordCardNumber;
        @BindView(R.id.tv_record_result)
        TextView tvRecordResult;
        @BindView(R.id.tv_record_opdate)
        TextView tvRecordOpdate;
        @BindView(R.id.ll_item)
        LinearLayout llItem;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
