package com.miaxis.faceattendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.model.entity.WhiteCard;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WhitelistAdapter<T> extends RecyclerView.Adapter<WhitelistAdapter.MyViewHolder> {

    private List<T> dataList;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public WhitelistAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_whitelist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final WhitelistAdapter.MyViewHolder holder, int position) {
        WhiteCard whiteCard = (WhiteCard) dataList.get(position);
        holder.tvWhiteName.setText(whiteCard.getName());
        holder.tvWhiteCardNumber.setText(whiteCard.getCardNumber());
        holder.tvRecordOpdate.setText(whiteCard.getRegisterTime());
        holder.tvDeleteWhiteCard.setOnClickListener(v ->
                onItemClickListener.onItemClick(holder.tvDeleteWhiteCard, holder.getLayoutPosition()));
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

        @BindView(R.id.tv_white_name)
        TextView tvWhiteName;
        @BindView(R.id.tv_white_card_number)
        TextView tvWhiteCardNumber;
        @BindView(R.id.tv_record_opdate)
        TextView tvRecordOpdate;
        @BindView(R.id.tv_delete_white_card)
        TextView tvDeleteWhiteCard;
        @BindView(R.id.ll_item)
        LinearLayout llItem;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
