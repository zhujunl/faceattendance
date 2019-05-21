package com.miaxis.faceattendance.view.fragment;


import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.adapter.CategoryAdapter;
import com.miaxis.faceattendance.adapter.listener.EndLessOnScrollListener;
import com.miaxis.faceattendance.contract.CategoryContract;
import com.miaxis.faceattendance.manager.CategoryManager;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.entity.Category;
import com.miaxis.faceattendance.presenter.CategoryPresenter;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;
import com.miaxis.faceattendance.view.listener.OnLimitClickHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends BaseFragment implements CategoryContract.View {

    @BindView(R.id.iv_add_category)
    ImageView ivAddCategory;
    @BindView(R.id.rv_category)
    RecyclerView rvCategory;
    @BindView(R.id.srl_category)
    SwipeRefreshLayout srlCategory;
    @BindView(R.id.et_category_name)
    EditText etCategoryName;
    @BindView(R.id.et_category_prompt)
    EditText etCategoryPrompt;
    @BindView(R.id.et_category_id)
    EditText etCategoryId;

    private boolean resetFlag = false;

    private OnFragmentInteractionListener mListener;
    private CategoryContract.Presenter presenter;
    private CategoryAdapter<Category> categoryAdapter;
    private EndLessOnScrollListener endLessOnScrollListener;
    private MaterialDialog checkDialog;
    private MaterialDialog waitDialog;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_category;
    }

    @Override
    protected void initData() {
        presenter = new CategoryPresenter(this, this);
    }

    @Override
    protected void initView() {
        initDialog();
        categoryAdapter = new CategoryAdapter<>(getContext(), new ArrayList<>());
        rvCategory.setAdapter(categoryAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvCategory.setLayoutManager(linearLayoutManager);
        rvCategory.setItemAnimator(new SlideInLeftAnimator());
        endLessOnScrollListener = new EndLessOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                presenter.loadCategoryList(currentPage, ValueUtil.PAGESIZE);
            }
        };
        categoryAdapter.setOnItemClickListener((view, position) -> {
            Category whiteCard = categoryAdapter.getData(position);
            new MaterialDialog.Builder(getContext())
                    .title("确认删除\"" + whiteCard.getCategoryName() + "\"吗？")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> presenter.deleteCategory(whiteCard))
                    .negativeText("取消")
                    .show();
        });
        ivAddCategory.setOnClickListener(new OnLimitClickHelper(view -> checkDialog.show()));
        srlCategory.setOnRefreshListener(this::refreshCategory);
        refreshCategory();
    }

    @Override
    public void loadCategoryListCallback(List<Category> categoryList) {
        if (srlCategory.isRefreshing()) {
            srlCategory.setRefreshing(false);
        }
        if (categoryList != null) {
            CategoryManager.getInstance().setCategoryList(categoryList);
            if (resetFlag) {
                resetFlag = false;
                categoryAdapter.setDataList(categoryList);
                rvCategory.removeOnScrollListener(endLessOnScrollListener);
                endLessOnScrollListener.reset();
                rvCategory.addOnScrollListener(endLessOnScrollListener);
            } else {
                categoryAdapter.appendDataList(categoryList);
            }
            categoryAdapter.notifyDataSetChanged();
            if (categoryList.isEmpty()) {
                ToastManager.toast(getContext(), "没有更多了", ToastManager.INFO);
            }
        } else {
            ToastManager.toast(getContext(), "读取数据库失败", ToastManager.ERROR);
        }
    }

    @Override
    public void addCategoryCallback(boolean result) {
        waitDialog.dismiss();
        if (result) {
            etCategoryId.setText("");
            etCategoryName.setText("");
            etCategoryPrompt.setText("");
            refreshCategory();
            ToastManager.toast(getContext(), "添加成功", ToastManager.SUCCESS);
        } else {
            ToastManager.toast(getContext(), "添加失败", ToastManager.ERROR);
        }
    }

    @Override
    public void deleteCategoryCallback(Category category, boolean result) {
        if (result) {
            ToastManager.toast(getContext(), "删除\"" + category.getCategoryName() + "\"成功", ToastManager.SUCCESS);
            categoryAdapter.removeData(category);
        } else {
            ToastManager.toast(getContext(), "删除失败", ToastManager.ERROR);
        }
    }

    @Override
    public void getCategoryByNameCallback(Category category) {
        if (category == null) {
            handleCategory();
        } else {
            new MaterialDialog.Builder(getContext())
                    .title("重复类别")
                    .content("库中已包含编号为\"" + category.getId() + "\"的人员类别，是否覆盖？")
                    .positiveText("覆盖")
                    .onPositive((dialog, which) -> handleCategory())
                    .negativeText("放弃")
                    .show();
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
        presenter.doDestroy();
    }

    public void refreshCategory() {
        resetFlag = true;
        presenter.loadCategoryList(1, ValueUtil.PAGESIZE);
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
            presenter.getCategoryById(etCategoryId.getText().toString());
        }
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(etCategoryName.getText().toString())
                || TextUtils.isEmpty(etCategoryPrompt.getText().toString())
                || TextUtils.isEmpty(etCategoryId.getText().toString())) {
            ToastManager.toast(getContext(), "请先完善相关信息后保存", ToastManager.INFO);
            return false;
        }
        if (Long.valueOf(etCategoryId.getText().toString()) == 0) {
            ToastManager.toast(getContext(), "请勿设置编号为0", ToastManager.INFO);
            return false;
        }
        return true;
    }

    private void handleCategory() {
        waitDialog.getContentView().setText("正在保存");
        waitDialog.show();
        presenter.addCategory(Long.valueOf(etCategoryId.getText().toString()), etCategoryName.getText().toString(), etCategoryPrompt.getText().toString());
    }

}
