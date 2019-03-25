package com.miaxis.faceattendance.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.app.GlideApp;
import com.miaxis.faceattendance.model.entity.Person;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonAdapter<T> extends RecyclerView.Adapter<PersonAdapter.MyViewHolder> {

    private List<T> dataList;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public PersonAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_person, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PersonAdapter.MyViewHolder holder, int position) {
        Person person = (Person) dataList.get(position);
        GlideApp.with(layoutInflater.getContext()).load(person.getFacePicture()).into(holder.ivHeader);
        holder.tvName.setText(person.getName());
        holder.tvPersonSex.setText(TextUtils.isEmpty(person.getSex()) ? "" : person.getSex());
        holder.tvPersonNation.setText(TextUtils.isEmpty(person.getNation()) ? "" : person.getNation());
        holder.tvCardNumber.setText(person.getCardNumber());
        holder.tvWarehousingTime.setText("入库时间：" + person.getWarehousingTime());
        holder.tvDeletePerson.setOnClickListener(v ->
                onItemClickListener.onItemClick(holder.tvDeletePerson, holder.getLayoutPosition()));
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

        @BindView(R.id.iv_header)
        ImageView ivHeader;
        @BindView(R.id.tv_delete_person)
        TextView tvDeletePerson;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_person_sex)
        TextView tvPersonSex;
        @BindView(R.id.tv_person_nation)
        TextView tvPersonNation;
        @BindView(R.id.tv_card_number)
        TextView tvCardNumber;
        @BindView(R.id.tv_warehousing_time)
        TextView tvWarehousingTime;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
