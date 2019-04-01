package com.miaxis.faceattendance.view.fragment;


import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.app.GlideApp;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.PersonModel;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.Record;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_id_photo)
    ImageView ivIdPhoto;
    @BindView(R.id.iv_camera_photo)
    ImageView ivCameraPhoto;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_cardNo)
    TextView tvCardNo;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_has_up)
    TextView tvHasUp;

    private Record record;

    public static RecordDialogFragment newInstance(@NonNull Record record) {
        RecordDialogFragment recordDialogFragment = new RecordDialogFragment();
        recordDialogFragment.setRecord(record);
        return recordDialogFragment;
    }

    public RecordDialogFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_record_dialog;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        tvName.setText(record.getName());
        tvCardNo.setText(record.getCardNumber());
        tvSex.setText(record.getSex());
        tvHasUp.setText(record.getUpload() == Boolean.TRUE ? "已上传" : "未上传");
        tvLocation.setText(record.getLocation());
        tvResult.setText("人脸通过 " + record.getScore());
        GlideApp.with(getContext()).load(record.getFacePicture()).into(ivCameraPhoto);
        Observable.create((ObservableOnSubscribe<Person>) emitter -> {
            Person person = PersonModel.getPersonByCardNumber(record.getCardNumber());
            emitter.onNext(person);
        })
                .subscribeOn(Schedulers.io())
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(person -> GlideApp.with(getContext()).load(person.getFacePicture()).into(ivIdPhoto),
                        throwable -> ToastManager.toast(getContext(), "未找到该人员，该人员可能已经被删除", ToastManager.INFO));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.77), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
