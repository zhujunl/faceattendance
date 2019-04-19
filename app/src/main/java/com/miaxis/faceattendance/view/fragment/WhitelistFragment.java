package com.miaxis.faceattendance.view.fragment;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.adapter.RecordAdapter;
import com.miaxis.faceattendance.adapter.WhitelistAdapter;
import com.miaxis.faceattendance.adapter.listener.EndLessOnScrollListener;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.contract.WhitelistContract;
import com.miaxis.faceattendance.event.CardEvent;
import com.miaxis.faceattendance.manager.CardManager;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.WhiteCardModel;
import com.miaxis.faceattendance.model.entity.WhiteCard;
import com.miaxis.faceattendance.presenter.WhitelistPresenter;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;
import com.miaxis.faceattendance.view.listener.OnLimitClickHelper;
import com.miaxis.faceattendance.view.listener.OnLimitClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class WhitelistFragment extends BaseFragment implements WhitelistContract.View {

    @BindView(R.id.iv_add_whitelist)
    ImageView ivAddWhitelist;
    @BindView(R.id.srl_whitelist)
    SwipeRefreshLayout srlWhitelist;
    @BindView(R.id.rv_whitelist)
    RecyclerView rvWhitelist;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_card_number)
    EditText etCardNumber;

    private boolean resetFlag = false;

    private OnFragmentInteractionListener mListener;
    private WhitelistContract.Presenter presenter;
    private WhitelistAdapter<WhiteCard> whitelistAdapter;
    private EndLessOnScrollListener endLessOnScrollListener;
    private MaterialDialog checkDialog;
    private MaterialDialog waitDialog;

    public static WhitelistFragment newInstance() {
        return new WhitelistFragment();
    }

    public WhitelistFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_whitelist;
    }

    @Override
    protected void initData() {
        presenter = new WhitelistPresenter(this, this);
        EventBus.getDefault().register(this);
        CardManager.getInstance().startReadCard(FaceAttendanceApp.getInstance());
    }

    @Override
    protected void initView() {
        initDialog();
        whitelistAdapter = new WhitelistAdapter<>(getContext(), new ArrayList<>());
        rvWhitelist.setAdapter(whitelistAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvWhitelist.setLayoutManager(linearLayoutManager);
        rvWhitelist.setItemAnimator(new SlideInLeftAnimator());
        endLessOnScrollListener = new EndLessOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                presenter.loadWhitelist(currentPage, ValueUtil.PAGESIZE);
            }
        };
        whitelistAdapter.setOnItemClickListener((view, position) -> {
            WhiteCard whiteCard = whitelistAdapter.getData(position);
            new MaterialDialog.Builder(getContext())
                    .title("确认删除\"" + whiteCard.getCardNumber() + "\"吗？")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> presenter.deleteWhiteCard(whiteCard))
                    .negativeText("取消")
                    .show();
        });
        ivAddWhitelist.setOnClickListener(new OnLimitClickHelper(view -> checkDialog.show()));
        srlWhitelist.setOnRefreshListener(this::refreshPerson);
        refreshPerson();
    }

    @Override
    public void loadWhitelistCallback(List<WhiteCard> whiteCardList) {
        if (srlWhitelist.isRefreshing()) {
            srlWhitelist.setRefreshing(false);
        }
        if (whiteCardList != null) {
            if (resetFlag) {
                resetFlag = false;
                whitelistAdapter.setDataList(whiteCardList);
                rvWhitelist.removeOnScrollListener(endLessOnScrollListener);
                endLessOnScrollListener.reset();
                rvWhitelist.addOnScrollListener(endLessOnScrollListener);
            } else {
                whitelistAdapter.appendDataList(whiteCardList);
            }
            whitelistAdapter.notifyDataSetChanged();
            if (whiteCardList.isEmpty()) {
                ToastManager.toast(getContext(), "没有更多了", ToastManager.INFO);
            }
        } else {
            ToastManager.toast(getContext(), "读取数据库失败", ToastManager.ERROR);
        }
    }

    @Override
    public void addWhiteCardCallback(boolean result) {
        waitDialog.dismiss();
        if (result) {
            etName.setText("");
            etCardNumber.setText("");
            refreshPerson();
            ToastManager.toast(getContext(), "添加成功", ToastManager.SUCCESS);
        } else {
            ToastManager.toast(getContext(), "添加失败", ToastManager.ERROR);
        }
    }

    @Override
    public void deleteWhiteCardCallback(WhiteCard whiteCard, boolean result) {
        if (result) {
            ToastManager.toast(getContext(), "删除\"" + whiteCard.getName() + "\"成功", ToastManager.SUCCESS);
            whitelistAdapter.removeData(whiteCard);
        } else {
            ToastManager.toast(getContext(), "删除失败", ToastManager.ERROR);
        }
    }

    @Override
    public void getWhiteCardByCardNumberCallback(WhiteCard whiteCard) {
        if (whiteCard == null) {
            handleWhiteCard();
        } else {
            new MaterialDialog.Builder(getContext())
                    .title("重复号码")
                    .content("库中已包含证件号码为\"" + etCardNumber.getText().toString().replaceAll("\\p{P}", "") + "\"的白名单成员，是否覆盖？")
                    .positiveText("覆盖")
                    .onPositive((dialog, which) -> handleWhiteCard())
                    .negativeText("放弃")
                    .show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCardEvent(CardEvent event) {
        switch (event.getMode()) {
            case CardEvent.READ_CARD:
                etName.setText(event.getIdCardRecord().getName());
                etCardNumber.setText(event.getIdCardRecord().getCardNumber());
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
    public void onStop() {
        super.onStop();
        CardManager.getInstance().closeReadCard();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.doDestroy();
    }

    public void refreshPerson() {
        resetFlag = true;
        presenter.loadWhitelist(1, ValueUtil.PAGESIZE);
    }

    private void initDialog() {
        checkDialog = new MaterialDialog.Builder(getContext())
                .title("确定添加？")
                .positiveText("确认")
                .onPositive((dialog, which) -> checkPersonWhiteCard())
                .negativeText("取消")
                .build();
        waitDialog = new MaterialDialog.Builder(getContext())
                .progress(true, 100)
                .content("")
                .cancelable(false)
                .autoDismiss(false)
                .build();
    }

    private void checkPersonWhiteCard() {
        if (checkInput()) {
            presenter.getWhiteCardByCardNumber(etCardNumber.getText().toString());
        } else {
            ToastManager.toast(getContext(), "请先完善相关信息后保存", ToastManager.INFO);
        }
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(etName.getText().toString())
                || TextUtils.isEmpty(etCardNumber.getText().toString().replaceAll("\\p{P}", ""))) {
            return false;
        }
        return true;
    }

    private void handleWhiteCard() {
        waitDialog.getContentView().setText("正在保存");
        waitDialog.show();
        presenter.addWhiteCard(etName.getText().toString(), etCardNumber.getText().toString());
    }

}
