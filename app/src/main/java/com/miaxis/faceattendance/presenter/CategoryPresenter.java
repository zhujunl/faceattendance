package com.miaxis.faceattendance.presenter;

import com.miaxis.faceattendance.contract.CategoryContract;
import com.miaxis.faceattendance.model.CategoryModel;
import com.miaxis.faceattendance.model.entity.Category;
import com.miaxis.faceattendance.util.ValueUtil;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CategoryPresenter extends BasePresenter<FragmentEvent> implements CategoryContract.Presenter {

    private CategoryContract.View view;

    public CategoryPresenter(LifecycleProvider<FragmentEvent> provider, CategoryContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void loadCategoryList(int pageNum, int pageSize) {
        Observable.create((ObservableOnSubscribe<List<Category>>) emitter ->
                emitter.onNext(CategoryModel.loadCategoryList(pageNum, pageSize)))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(whiteCardList -> view.loadCategoryListCallback(whiteCardList),
                        throwable -> view.loadCategoryListCallback(null));
    }

    @Override
    public void addCategory(long id, String name, String prompt) {
        Observable.create((ObservableOnSubscribe<Category>) emitter -> {
            Category category = new Category.Builder()
                    .id(id)
                    .categoryName(name)
                    .categoryPrompt(prompt)
                    .registerTime(ValueUtil.simpleDateFormat.format(new Date()))
                    .build();
            emitter.onNext(category);
        })
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(CategoryModel::saveCategory)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(whiteCard -> view.addCategoryCallback(Boolean.TRUE),
                        throwable -> view.addCategoryCallback(Boolean.FALSE));
    }

    @Override
    public void deleteCategory(Category category) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            CategoryModel.deleteCategory(category);
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> view.deleteCategoryCallback(category, aBoolean),
                        throwable -> view.deleteCategoryCallback(null, Boolean.FALSE));
    }

    @Override
    public void getCategoryById(String id) {
        Observable.create((ObservableOnSubscribe<Category>) emitter ->
                emitter.onNext(CategoryModel.getCategoryById(id)))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(whiteCard -> view.getCategoryByNameCallback(whiteCard),
                        throwable -> view.getCategoryByNameCallback(null));
    }

    @Override
    public void doDestroy() {
        this.view = null;
    }
}
