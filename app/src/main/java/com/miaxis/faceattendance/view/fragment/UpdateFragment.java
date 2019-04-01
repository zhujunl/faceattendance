package com.miaxis.faceattendance.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.util.FileUtil;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateFragment extends BaseFragment {

    @BindView(R.id.clpg_download)
    ContentLoadingProgressBar clpgDownload;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.ll_download_failed)
    LinearLayout llDownloadFailed;

    private OnFragmentInteractionListener mListener;
    private URL url;
    private Disposable countDownDisposable;

    public static UpdateFragment newInstance(URL url) {
        UpdateFragment updateFragment = new UpdateFragment();
        updateFragment.setUrl(url);
        return updateFragment;
    }

    public UpdateFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_update;
    }

    @Override
    protected void initData() {
        downloadFile(url.toString(), FileUtil.MAIN_PATH + File.separator + url.getFile());
    }

    @Override
    protected void initView() {
        btnBack.setOnClickListener(v -> mListener.enterAnotherFragment(UpdateFragment.class, VerifyFragment.class, null));
        btnRetry.setOnClickListener(v -> {
            llDownloadFailed.setVisibility(View.INVISIBLE);
            downloadFile(url.toString(), FileUtil.MAIN_PATH + File.separator + url.toString().substring(url.toString().lastIndexOf('/') + 1));
        });
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
        FileDownloader.getImpl().pauseAll();
        if (countDownDisposable != null) {
            countDownDisposable.dispose();
        }
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    private void downloadFile(String url, String path) {
        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        int percent = (int) ((double) soFarBytes / (double) totalBytes * 100);
                        clpgDownload.setProgress(percent);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        onDownloadCompleted(task.getPath());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        onDownloadFinish("下载已取消");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        onDownloadFinish("下载时出现错误，已停止下载");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

    private void onDownloadCompleted(String path) {
        ToastManager.toast(getContext(), "下载已完成，正在准备安装", ToastManager.SUCCESS);
        File file = new File(path);
        if (file.exists()) {
            installApk(file);
        } else {
            ToastManager.toast(getContext(), "未找到下载的文件，请尝试手动安装", ToastManager.SUCCESS);
        }
    }

    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void onDownloadFinish(String message) {
        ToastManager.toast(getContext(), message, ToastManager.INFO);
        llDownloadFailed.setVisibility(View.VISIBLE);
        connectCountDown(15);
    }

    private void connectCountDown(int count) {
        if (countDownDisposable != null) {
            countDownDisposable.dispose();
        }
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .take(count + 1)
                .map(aLong -> count - aLong)
                .observeOn(AndroidSchedulers.mainThread())//ui线程中进行控件更新
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        countDownDisposable = d;
                    }

                    @Override
                    public void onNext(Long num) {
                        String text = btnBack.getText().toString();
                        int split = text.indexOf("（");
                        String oriText = split != -1 ? text.substring(0, split) : text;
                        btnBack.setText(oriText + "（ " + num + "S ）");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("asd", "倒计时出错");
                    }

                    @Override
                    public void onComplete() {
                        btnBack.performClick();
                    }
                });
    }

}
