package com.miaxis.faceattendance.view.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.constant.Constants;
import com.miaxis.faceattendance.event.CardEvent;
import com.miaxis.faceattendance.event.FeatureEvent;
import com.miaxis.faceattendance.manager.CameraManager;
import com.miaxis.faceattendance.manager.CardManager;
import com.miaxis.faceattendance.manager.CategoryManager;
import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.manager.GpioManager;
import com.miaxis.faceattendance.manager.RecordManager;
import com.miaxis.faceattendance.manager.TTSManager;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.PersonModel;
import com.miaxis.faceattendance.model.entity.Category;
import com.miaxis.faceattendance.model.entity.IDCardRecord;
import com.miaxis.faceattendance.model.entity.MyException;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.RGBImage;
import com.miaxis.faceattendance.util.FileUtil;
import com.miaxis.faceattendance.view.custom.CameraSurfaceView;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;
import com.miaxis.faceattendance.view.listener.OnLimitClickHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_card_number)
    TextView tvCardNumber;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.spinner_category)
    Spinner spinnerCategory;
    @BindView(R.id.ll_category)
    LinearLayout llCategory;

    private MaterialDialog waitDialog;
    private MaterialDialog checkDialog;

    private Bitmap facePicture;
    private IDCardRecord idCardRecord;
    private Person person;
    private OnFragmentInteractionListener mListener;
    private List<Category> categoryList;

    public static AddPersonFragment newInstance() {
        return new AddPersonFragment();
    }

    public AddPersonFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return Constants.VERSION?R.layout.fragment_add_person:R.layout.fragment_add_person_860s;
    }

    @Override
    protected void initData() {
        FaceManager.getInstance().startLoop();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        initDialog();
        categoryList = CategoryManager.getInstance().getCategoryList();
        if (!categoryList.isEmpty()) {
            List<String> categoryNameList = Stream.of(categoryList).map(Category::getCategoryName).collect(Collectors.toList());
            categoryNameList.add(0, "请选择类别");
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_start, R.id.tv_spinner, categoryNameList);
            spinnerCategory.setAdapter(categoryAdapter);
        } else {
            llCategory.setVisibility(View.INVISIBLE);
        }
        tvTakePicture.setOnClickListener(new OnLimitClickHelper(v -> {
            if (TextUtils.equals(tvTakePicture.getText().toString(), "点  击  拍  照")) {
                tvTakePicture.setText("重  新  拍  摄");
                CameraManager.getInstance().takePicture((data, camera) -> {
                    CameraManager.getInstance().stopPreview();
                    facePicture = BitmapFactory.decodeByteArray(data, 0, data.length);
                });
            } else if (TextUtils.equals(tvTakePicture.getText().toString(), "重  新  拍  摄")) {
                tvTakePicture.setText("点  击  拍  照");
                facePicture = null;
                CameraManager.getInstance().startPreview();
            }
        }));
        ivSave.setOnClickListener(v -> checkDialog.show());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCardEvent(CardEvent event) {
        switch (event.getMode()) {
            case CardEvent.READ_CARD:
                GpioManager.getInstance().openLed();
                this.idCardRecord = event.getIdCardRecord();
                tvName.setText(event.getIdCardRecord().getName());
                tvSex.setText(event.getIdCardRecord().getSex());
                tvCardNumber.setText(event.getIdCardRecord().getCardNumber());
                FaceManager.getInstance().getFeatureByBitmap(idCardRecord.getCardBitmap(), true, "card");
                break;
            case CardEvent.OVERDUE:
                TTSManager.getInstance().playVoiceMessageFlush("已过期");
            case CardEvent.NO_CARD:
                GpioManager.getInstance().closeLed();
                if (facePicture != null) {
                    tvTakePicture.performClick();
                    facePicture = null;
                }
                if (idCardRecord != null) {
                    clear();
                    idCardRecord = null;
                }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFeatureEvent(FeatureEvent event) {
        switch (event.getMode()) {
            case FeatureEvent.IMAGE_FACE:
                if (TextUtils.equals(event.getMark(), "card")) {
                    if (event.getFeature() != null) {
                        tvTakePicture.setVisibility(View.VISIBLE);
                        idCardRecord.setCardFeature(event.getFeature());
                    } else {
                        ToastManager.toast(getContext(), "二代证提取头像特征失败", ToastManager.ERROR);
                    }
                    return;
                }
                if (TextUtils.equals(event.getMark(), "picture")
                        && event.getFeature() != null
                        && event.getMxFaceInfoEx() != null
                        && idCardRecord != null
                        && idCardRecord.getCardFeature() != null) {
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
    public void onResume() {
        super.onResume();
        CardManager.getInstance().setNeedReadCard(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        FaceManager.getInstance().stopLoop();
        CardManager.getInstance().setNeedReadCard(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
            if (PersonModel.getPersonCount() > 500) {
                ToastManager.toast(getContext(), "考勤人员库数量已达500上限", ToastManager.INFO);
                return;
            }
            if (PersonModel.getPersonByCardNumber(idCardRecord.getCardNumber()) == null) {
                handlePerson();
            } else {
                new MaterialDialog.Builder(getContext())
                        .title("重复人员")
                        .content("库中已包含证件号码为\"" + idCardRecord.getCardNumber() + "\"的人员，是否覆盖？")
                        .positiveText("覆盖")
                        .onPositive((dialog, which) -> handlePerson())
                        .negativeText("放弃")
                        .show();
            }
        } else {
            ToastManager.toast(getContext(), "请先完善相关信息后保存", ToastManager.INFO);
        }
    }

    private boolean checkInput() {
        if (facePicture == null
                || idCardRecord == null
                || (!categoryList.isEmpty() && spinnerCategory.getSelectedItemPosition() == 0)) {
            return false;
        }
        return true;
    }

    private void handlePerson() {
        waitDialog.getContentView().setText("正在提取特征");
        waitDialog.show();
        person = new Person.Builder()
                .cardType(idCardRecord.getCardType())
                .cardId(idCardRecord.getCardId())
                .name(idCardRecord.getName())
                .birthday(idCardRecord.getBirthday())
                .address(idCardRecord.getAddress())
                .cardNumber(idCardRecord.getCardNumber())
                .sex(idCardRecord.getSex())
                .nation(idCardRecord.getNation())
                .validateStart(idCardRecord.getValidateStart())
                .validateEnd(idCardRecord.getValidateEnd())
                .issuingAuthority(idCardRecord.getIssuingAuthority())
                .passNumber(idCardRecord.getPassNumber())
                .issueCount(idCardRecord.getIssueCount())
                .chineseName(idCardRecord.getChineseName())
                .version(idCardRecord.getVersion())
                .categoryId(categoryList.isEmpty() ? 0L : categoryList.get(spinnerCategory.getSelectedItemPosition() - 1).getId())
                .build();
        new Thread(() -> {
            Matrix matrix = new Matrix();
            if(Constants.VERSION)matrix.postRotate(180);
            Bitmap picture = Bitmap.createBitmap(facePicture, 0, 0, facePicture.getWidth(), facePicture.getHeight(), matrix, true);
            FaceManager.getInstance().getFeatureByBitmap(picture, true, "picture");
        }).start();
    }

    private void onFeatureExtract(FeatureEvent event) {
        Observable.create((ObservableOnSubscribe<FeatureEvent>) emitter -> {
            waitDialog.getContentView().setText("特征提取完成，正在人证核验");
            person.setFaceFeature(Base64.encodeToString(event.getFeature(), Base64.NO_WRAP));
            emitter.onNext(event);
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .doOnNext(featureEvent -> {
                    float score = FaceManager.getInstance().matchFeature(idCardRecord.getCardFeature(), featureEvent.getFeature());
                    if (score < ConfigManager.getInstance().getConfig().getVerifyScore()) {
                        throw new MyException("人证核验失败");
                    }
                    person.setScore(String.valueOf(score));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(featureEvent -> waitDialog.getContentView().setText("人证核验通过，正在保存身份证照片"))
                .observeOn(Schedulers.io())
                .doOnNext(featureEvent -> {
                    String cardPicturePath = FileUtil.CARD_IMG_PATH + File.separator + idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
                    FileUtil.saveBitmap(cardPicturePath, idCardRecord.getCardBitmap());
                    person.setCardPicture(cardPicturePath);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(featureEvent -> waitDialog.getContentView().setText("保存身份证照片成功，正在裁剪人员照片"))
                .observeOn(Schedulers.io())
                .map(featureEvent -> {
                    RGBImage rgbImage = event.getRgbImage();
                    byte[] fileImage = FaceManager.getInstance().imageEncode(rgbImage.getRgbImage(), rgbImage.getWidth(), rgbImage.getHeight());
                    if (fileImage != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(fileImage, 0, fileImage.length);
                        return bitmap;
                    } else {
                        throw new MyException("图像数据转码失败");
                    }
                })
                .map(bitmap -> FaceManager.getInstance().tailoringFace(bitmap, event.getMxFaceInfoEx()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(bitmap -> waitDialog.getContentView().setText("裁剪完成，正在保存"))
                .observeOn(Schedulers.io())
                .map(bitmap -> {
                    String filePath = FileUtil.FACE_IMG_PATH + File.separator + person.getCardNumber() + "-" + System.currentTimeMillis() + ".jpg";
                    FileUtil.saveBitmap(filePath, bitmap);
                    return filePath;
                })
                .doOnNext(s -> {
                    if (new File(s).exists()) {
                        person.setFacePicture(s);
                        PersonModel.savePerson(person);
                        RecordManager.getInstance().uploadPerson(person);
                    } else {
                        throw new MyException("头像落地文件未找到");
                    }
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
                    String errorMessage = "保存过程中出错，请对准摄像头画面中心后重新添加";
                    if (throwable instanceof MyException) {
                        errorMessage = throwable.getMessage();
                    }
                    ToastManager.toast(getContext(), errorMessage, ToastManager.ERROR);
                });
    }

    private void clear() {
        tvName.setText("");
        tvCardNumber.setText("");
        tvSex.setText("");
        spinnerCategory.setSelection(0);
        person = null;
        tvTakePicture.setVisibility(View.INVISIBLE);
    }

}
