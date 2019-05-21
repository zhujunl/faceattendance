package com.miaxis.faceattendance.manager;

import com.miaxis.faceattendance.model.CategoryModel;
import com.miaxis.faceattendance.model.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryManager {

    private CategoryManager() {
    }

    public static CategoryManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CategoryManager instance = new CategoryManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    private List<Category> categoryList;

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void checkCategory() {
        try {
            categoryList = DaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();
        } catch (Exception e) {
            e.printStackTrace();
            categoryList = new ArrayList<>();
        }
    }

    public String getCategoryNameById(long id) {
        for (Category category : categoryList) {
            if (category.getId() == id) {
                return category.getCategoryName();
            }
        }
        return "";
    }

    public Category getCategoryById(long id) {
        for (Category category : categoryList) {
            if (category.getId() == id) {
                return category;
            }
        }
        return null;
    }

}
