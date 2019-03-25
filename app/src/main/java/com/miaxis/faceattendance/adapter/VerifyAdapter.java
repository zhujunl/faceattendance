package com.miaxis.faceattendance.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.app.GlideApp;
import com.miaxis.faceattendance.model.entity.VerifyPerson;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifyAdapter<T> extends RecyclerView.Adapter<VerifyAdapter.MyViewHolder> {

    private List<T> dataList;
    private LayoutInflater layoutInflater;

    public VerifyAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_verify, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final VerifyAdapter.MyViewHolder holder, int position) {
        VerifyPerson verifyPerson = (VerifyPerson) dataList.get(position);
        holder.tvVerifyName.setText(verifyPerson.getName());
        holder.tvVerifyTime.setText(verifyPerson.getTime());
        GlideApp.with(layoutInflater.getContext()).load(verifyPerson.getFacePicturePath()).into(holder.ivVerifyHeader);
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

    public boolean containsName(String cardNumber) {
        for (T t : dataList) {
            if (TextUtils.equals(((VerifyPerson) t).getCardNumber(), cardNumber)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void insertData(int position, T t) {
        dataList.add(position, t);
        notifyItemInserted(0);
        if (dataList.size() > 5) {
            dataList.remove(dataList.size() - 1);
            notifyItemRemoved(dataList.size());
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_verify_header)
        ImageView ivVerifyHeader;
        @BindView(R.id.tv_verify_name)
        TextView tvVerifyName;
        @BindView(R.id.tv_verify_time)
        TextView tvVerifyTime;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
