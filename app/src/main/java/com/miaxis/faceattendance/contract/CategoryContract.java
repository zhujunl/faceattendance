package com.miaxis.faceattendance.contract;

import com.miaxis.faceattendance.model.entity.Category;

import java.util.List;

public interface CategoryContract {
    interface View extends BaseContract.View {
        void loadCategoryListCallback(List<Category> categoryList);
        void addCategoryCallback(boolean result);
        void deleteCategoryCallback(Category category, boolean result);
        void getCategoryByNameCallback(Category category);
    }

    interface Presenter extends BaseContract.Presenter {
        void loadCategoryList(int pageNum, int pageSize);
        void addCategory(long id, String name, String prompt);
        void deleteCategory(Category category);
        void getCategoryById(String id);
    }
}
