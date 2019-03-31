package com.miaxis.faceattendance.view.fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.adapter.RecordAdapter;
import com.miaxis.faceattendance.adapter.listener.EndLessOnScrollListener;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.contract.RecordContract;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.presenter.RecordPresenter;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindArray;
import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends BaseFragment implements RecordContract.View {

    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.srl_record)
    SwipeRefreshLayout srlRecord;
    @BindView(R.id.rv_record)
    RecyclerView rvRecord;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.spinner_sex)
    Spinner spinnerSex;
    @BindView(R.id.et_card_number)
    EditText etCardNumber;
    @BindView(R.id.spinner_upload)
    Spinner spinnerUpload;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindArray(R.array.sex)
    String[] sexArray;
    @BindArray(R.array.upload)
    String[] uploadArray;

    private String nameCriteria = "";
    private String sexCriteria = "";
    private String cardNumberCriteria = "";
    private Boolean uploadCriteria = null;
    private String startDateCriteria = "";
    private String endDateCriteria = "";
    private boolean resetFlag = false;

    private DatePickerDialog startDateDialog;
    private DatePickerDialog endDateDialog;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    private OnFragmentInteractionListener mListener;
    private RecordContract.Presenter presenter;
    private RecordAdapter<Record> recordAdapter;
    private EndLessOnScrollListener endLessOnScrollListener;

    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_record;
    }

    @Override
    protected void initData() {
        presenter = new RecordPresenter(this, this);
    }

    @Override
    protected void initView() {
        initSearchView();
        recordAdapter = new RecordAdapter<>(getContext(), new ArrayList<>());
        rvRecord.setAdapter(recordAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvRecord.setLayoutManager(linearLayoutManager);
        rvRecord.setItemAnimator(new SlideInLeftAnimator());
        endLessOnScrollListener = new EndLessOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                presenter.loadRecord(currentPage, ValueUtil.PAGESIZE, nameCriteria, sexCriteria, cardNumberCriteria, uploadCriteria, startDateCriteria, endDateCriteria);
            }
        };
        rvRecord.addOnScrollListener(endLessOnScrollListener);
        recordAdapter.setOnItemClickListener((view, position) -> {
        });
        srlRecord.setOnRefreshListener(() -> srlRecord.setRefreshing(false));
        presenter.loadRecord(0, ValueUtil.PAGESIZE, nameCriteria, sexCriteria, cardNumberCriteria, uploadCriteria, startDateCriteria, endDateCriteria);
    }

    @Override
    public void loadRecordCallback(List<Record> recordList) {
        if (recordList != null) {
            if (resetFlag) {
                resetFlag = false;
                recordAdapter.setDataList(recordList);
                rvRecord.removeOnScrollListener(endLessOnScrollListener);
                endLessOnScrollListener.reset();
                rvRecord.addOnScrollListener(endLessOnScrollListener);
                ToastManager.toast(getContext(), "查询结束", ToastManager.SUCCESS);
            } else {
                recordAdapter.appendDataList(recordList);
            }
            recordAdapter.notifyDataSetChanged();
            if (recordList.isEmpty()) {
                ToastManager.toast(getContext(), "没有更多了", ToastManager.INFO);
            }
        } else {
            ToastManager.toast(getContext(), "读取数据库失败", ToastManager.ERROR);
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

    private void initSearchView() {
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_center, R.id.tv_spinner, sexArray);
        spinnerSex.setAdapter(sexAdapter);
        spinnerSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sexCriteria = sexArray[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        ArrayAdapter<String> uploadAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_center, R.id.tv_spinner, uploadArray);
        spinnerUpload.setAdapter(uploadAdapter);
        spinnerUpload.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                uploadCriteria = position == 0 ? null : (position == 1 ? Boolean.TRUE : Boolean.FALSE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.YEAR, 2019);
        minDate.set(Calendar.MONTH, 0);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        Calendar today = Calendar.getInstance();
        startDateDialog = new DatePickerDialog(getActivity(), R.style.MyDatePickerDialogTheme, (view, year, month, dayOfMonth) -> {
            String mDate = year + "-" + (month + 1 < 10 ? "0" : "") + (month + 1) + "-" + (dayOfMonth < 10 ? "0" : "") + dayOfMonth;
            startDateCriteria = mDate + " 00:00:00";
            tvStartTime.setText(startDateCriteria);
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        startDatePicker = startDateDialog.getDatePicker();
        startDatePicker.setMinDate(minDate.getTimeInMillis());
        startDatePicker.setMaxDate(today.getTimeInMillis());
        tvStartTime.setOnClickListener(v -> startDateDialog.show());
        endDateDialog = new DatePickerDialog(getActivity(), R.style.MyDatePickerDialogTheme, (view, year, month, dayOfMonth) -> {
            String mDate = year + "-" + (month + 1 < 10 ? "0" : "") + (month + 1) + "-" + (dayOfMonth < 10 ? "0" : "") + dayOfMonth;
            endDateCriteria = mDate + " 23:59:59";
            tvEndTime.setText(endDateCriteria);
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        endDatePicker = endDateDialog.getDatePicker();
        endDatePicker.setMinDate(minDate.getTimeInMillis());
        endDatePicker.setMaxDate(today.getTimeInMillis());
        tvEndTime.setOnClickListener(v -> endDateDialog.show());
        ivSearch.setOnClickListener(v -> {
            hideInputManager();
            resetFlag = true;
           presenter.loadRecord(0,
                   ValueUtil.PAGESIZE,
                   nameCriteria = etName.getText().toString(),
                   sexCriteria,
                   cardNumberCriteria = etCardNumber.getText().toString(),
                   uploadCriteria,
                   startDateCriteria,
                   endDateCriteria);
        });
    }

    private void hideInputManager() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus().getWindowToken()!=null){
            manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
