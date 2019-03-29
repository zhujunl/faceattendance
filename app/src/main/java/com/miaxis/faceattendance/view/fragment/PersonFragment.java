package com.miaxis.faceattendance.view.fragment;


import android.content.Context;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.adapter.PersonAdapter;
import com.miaxis.faceattendance.adapter.listener.GridEndLessOnScrollListener;
import com.miaxis.faceattendance.contract.PersonContract;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.presenter.PersonPresenter;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends BaseFragment implements PersonContract.View {

    @BindView(R.id.iv_add_person)
    ImageView ivAddPerson;
    @BindView(R.id.rv_person)
    RecyclerView rvPerson;

    private MaterialDialog waitDialog;

    private PersonContract.Presenter presenter;
    private OnFragmentInteractionListener mListener;
    private PersonAdapter<Person> personAdapter;
    private GridEndLessOnScrollListener gridEndLessOnScrollListener;

    private boolean resetFlag = false;

    public static PersonFragment newInstance() {
        return new PersonFragment();
    }

    public PersonFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_person;
    }

    @Override
    protected void initData() {
        presenter = new PersonPresenter(this, this);
    }

    @Override
    protected void initView() {
        initDialog();
        ivAddPerson.setOnClickListener(v -> mListener.enterAnotherFragment(PersonFragment.class, AddPersonFragment.class, null));
        personAdapter = new PersonAdapter<>(getContext(), new ArrayList<>());
        rvPerson.setAdapter(personAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvPerson.setLayoutManager(gridLayoutManager);
        rvPerson.setItemAnimator(new SlideInLeftAnimator());
        personAdapter.setOnItemClickListener((view, position) -> {
            Person person = personAdapter.getData(position);
            new MaterialDialog.Builder(getContext())
                    .title("确认删除")
                    .content("你确定要从库中删除\"" + person.getName() + "\"吗？")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> presenter.deletePerson(person))
                    .negativeText("取消")
                    .show();
        });
        gridEndLessOnScrollListener = new GridEndLessOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                presenter.loadPerson(currentPage, ValueUtil.PAGESIZE);
            }
        };
        rvPerson.addOnScrollListener(gridEndLessOnScrollListener);
        presenter.loadPerson(1, ValueUtil.PAGESIZE);
    }

    @Override
    public void loadPersonCallback(List<Person> personList) {
        if (personList != null) {
            if (resetFlag) {
                resetFlag = false;
                personAdapter.setDataList(personList);
                rvPerson.removeOnScrollListener(gridEndLessOnScrollListener);
                gridEndLessOnScrollListener.reset();
                rvPerson.addOnScrollListener(gridEndLessOnScrollListener);
            } else {
                personAdapter.appendDataList(personList);
            }
            personAdapter.notifyDataSetChanged();
            if (personList.isEmpty()) {
                ToastManager.toast(getContext(), "没有更多了", ToastManager.INFO);
            }
        } else {
            ToastManager.toast(getContext(), "读取数据库失败", ToastManager.ERROR);
        }
    }

    @Override
    public void deletePersonCallback(Person person, boolean result) {
        if (result) {
            ToastManager.toast(getContext(), "删除\"" + person.getName() + "\"成功", ToastManager.SUCCESS);
            personAdapter.removeData(person);
        } else {
            ToastManager.toast(getContext(), "删除失败", ToastManager.ERROR);
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

    public void showWaitDialogWithMessage(String message) {
        waitDialog.setContent(message);
        if (!waitDialog.isShowing()) {
            waitDialog.show();
        }
    }

    public void dismissWaitDialog() {
        waitDialog.dismiss();
    }

    public void refreshPerson() {
        resetFlag = true;
        presenter.loadPerson(1, ValueUtil.PAGESIZE);
    }

    private void initDialog() {
        waitDialog = new MaterialDialog.Builder(getContext())
                .progress(true, 100)
                .content("")
                .cancelable(false)
                .autoDismiss(false)
                .build();
    }

}
