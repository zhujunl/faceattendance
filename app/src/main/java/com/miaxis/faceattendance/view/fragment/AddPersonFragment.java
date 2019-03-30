package com.miaxis.faceattendance.view.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.event.CardEvent;
import com.miaxis.faceattendance.event.FeatureEvent;
import com.miaxis.faceattendance.manager.CameraManager;
import com.miaxis.faceattendance.manager.CardManager;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.PersonModel;
import com.miaxis.faceattendance.model.entity.IDCardRecord;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.util.FileUtil;
import com.miaxis.faceattendance.view.custom.CameraSurfaceView;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPersonFragment extends BaseFragment {

    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.csv_camera)
    CameraSurfaceView csvCamera;
    @BindView(R.id.tv_take_picture)
    TextView tvTakePicture;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.spinner_sex)
    Spinner spinnerSex;
    @BindView(R.id.et_card_number)
    EditText etCardNumber;

    private MaterialDialog waitDialog;
    private MaterialDialog checkDialog;

    private Bitmap facePicture;
    private IDCardRecord idCardRecord;
    private Person person;
    private OnFragmentInteractionListener mListener;

    public static AddPersonFragment newInstance() {
        return new AddPersonFragment();
    }

    public AddPersonFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_add_person;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        CardManager.getInstance().startReadCard(FaceAttendanceApp.getInstance());
    }

    @Override
    protected void initView() {
        initDialog();
        String[] arr = getResources().getStringArray(R.array.sex);
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_start, R.id.tv_spinner, arr);
        spinnerSex.setAdapter(sexAdapter);
        tvTakePicture.setOnClickListener(v -> {
            if (TextUtils.equals(tvTakePicture.getText().toString(), "点击拍照")) {
                CameraManager.getInstance().takePicture((data, camera) -> {
                    facePicture = BitmapFactory.decodeByteArray(data, 0, data.length);
                    tvTakePicture.setText("重新拍摄");
                });
            } else if (TextUtils.equals(tvTakePicture.getText().toString(), "重新拍摄")) {
                facePicture = null;
                CameraManager.getInstance().startPreview();
                tvTakePicture.setText("点击拍照");
            }
        });
        ivSave.setOnClickListener(v -> checkDialog.show());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCardEvent(CardEvent event) {
        switch (event.getMode()) {
            case CardEvent.READ_CARD:
                this.idCardRecord = event.getIdCardRecord();
                etName.setText(event.getIdCardRecord().getName());
                if (TextUtils.equals(event.getIdCardRecord().getSex(), "男")) {
                    spinnerSex.setSelection(1);
                } else {
                    spinnerSex.setSelection(2);
                }
                etCardNumber.setText(event.getIdCardRecord().getCardNumber());
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFeatureEvent(FeatureEvent event) {
        switch (event.getMode()) {
            case FeatureEvent.IMAGE_FACE:
                if (event.getFeature() != null && event.getMxFaceInfoEx() != null) {
                    onFeatureExtract(event);
                } else {
                    waitDialog.dismiss();
                    ToastManager.toast(getContext(), event.getMessage(), ToastManager.INFO);
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        CardManager.getInstance().closeReadCard();
    }

    private void initDialog() {
        checkDialog = new MaterialDialog.Builder(getContext())
                .title("确定添加？")
                .positiveText("确认")
                .onPositive((dialog, which) -> checkPerson())
                .negativeText("取消")
                .build();
        waitDialog = new MaterialDialog.Builder(getContext())
                .progress(true, 100)
                .content("")
                .cancelable(false)
                .autoDismiss(false)
                .build();
    }

    private void checkPerson() {
        if (checkInput()) {
            if (PersonModel.getPersonByCardNumber(etCardNumber.getText().toString()) == null) {
                HandlePerson();
            } else {
                new MaterialDialog.Builder(getContext())
                        .title("重复人员")
                        .content("库中已包含证件号码为\"" + etCardNumber.getText().toString() + "\"的人员，是否覆盖？")
                        .positiveText("覆盖")
                        .onPositive((dialog, which) -> HandlePerson())
                        .negativeText("放弃")
                        .show();
            }
        } else {
            ToastManager.toast(getContext(), "请先完善相关信息后保存", ToastManager.INFO);
        }
    }

    private boolean checkInput() {
        if (facePicture == null
                || TextUtils.isEmpty(etName.getText().toString())
                || spinnerSex.getSelectedItemPosition() == 0
                || TextUtils.isEmpty(etCardNumber.getText().toString())) {
            return false;
        }
        return true;
    }

    private void buildPerson() {
        if (idCardRecord != null && TextUtils.equals(etCardNumber.getText().toString(), idCardRecord.getCardNumber())) {
            person = new Person.Builder()
                    .name(etName.getText().toString())
                    .cardNumber(etCardNumber.getText().toString())
                    .sex(spinnerSex.getSelectedItemPosition() == 1 ? "男" : "女")
                    .nation(idCardRecord.getNation())
                    .birthday(idCardRecord.getBirthday())
                    .address(idCardRecord.getAddress())
                    .validateStart(idCardRecord.getValidateStart())
                    .validateEnd(idCardRecord.getValidateEnd())
                    .cardId(idCardRecord.getCardId())
                    .issuingAuthority(idCardRecord.getIssuingAuthority())
                    .build();
        } else {
            person = new Person.Builder()
                    .name(etName.getText().toString())
                    .cardNumber(etCardNumber.getText().toString())
                    .sex(spinnerSex.getSelectedItemPosition() == 1 ? "男" : "女")
                    .build();
        }
    }

    private void HandlePerson() {
        waitDialog.getContentView().setText("正在提取特征");
        waitDialog.show();
        buildPerson();
        new Thread(() -> {
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            facePicture = Bitmap.createBitmap(facePicture, 0, 0, facePicture.getWidth(), facePicture.getHeight(), matrix, true);
            FaceManager.getInstance().getFeatureByBitmap(facePicture);
        }).start();
    }

    private void onFeatureExtract(FeatureEvent event) {
        Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            waitDialog.getContentView().setText("特征提取完成，正在裁剪图片");
            person.setFaceFeature(Base64.encodeToString(event.getFeature(), Base64.NO_WRAP));
            emitter.onNext(facePicture);
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .observeOn(Schedulers.io())
                .map(bitmap -> FaceManager.getInstance().tailoringFace(bitmap, event.getMxFaceInfoEx()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(bitmap -> waitDialog.getContentView().setText("裁剪完成，正在保存"))
                .observeOn(Schedulers.io())
                .map(bitmap -> {
                    String filePath = FileUtil.FACE_IMG_PATH + File.separator + person.getCardNumber() + "-" + System.currentTimeMillis() + ".jpg";
                    FileUtil.saveBitmap(filePath, bitmap);
                    person.setFacePicture(filePath);
                    PersonModel.savePerson(person);
                    return filePath;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                            tvTakePicture.performClick();
                            waitDialog.dismiss();
                            clear();
                            ToastManager.toast(getContext(), "添加人员成功", ToastManager.SUCCESS);
                        }, throwable -> {
                            tvTakePicture.performClick();
                            waitDialog.dismiss();
                            ToastManager.toast(getContext(), "请将人脸对齐中心区域", ToastManager.ERROR);
                        });
    }

    private void clear() {
        etCardNumber.setText("");
        etName.setText("");
        spinnerSex.setSelection(0);
        person = null;
    }

}
