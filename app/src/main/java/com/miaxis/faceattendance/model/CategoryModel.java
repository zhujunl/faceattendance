package com.miaxis.faceattendance.model;

import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.model.entity.Category;
import com.miaxis.faceattendance.model.local.greenDao.gen.CategoryDao;

import java.util.List;

public class CategoryModel {

    public static void saveCategory(Category Category) {
        DaoManager.getInstance().getDaoSession().getCategoryDao().insertOrReplace(Category);
    }

    public static void saveCategoryList(List<Category> CategoryList) {
        DaoManager.getInstance().getDaoSession().getCategoryDao().insertOrReplaceInTx(CategoryList);
    }

    public static void deleteCategory(Category Category) {
        DaoManager.getInstance().getDaoSession().getCategoryDao().delete(Category);
    }

    public static long getCategoryCount() {
        return DaoManager.getInstance().getDaoSession().getCategoryDao().count();
    }

    public static List<Category> loadAllCategoryList() {
        return DaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();
    }

    public static List<Category> loadCategoryList(int pageNum, int pageSize) {
        return DaoManager.getInstance().getDaoSession().getCategoryDao().queryBuilder()
                .orderDesc(CategoryDao.Properties.Id)
                .offset((pageNum - 1) * pageSize)
                .limit(pageSize)
                .list();
    }

    public static Category getCategoryById(String id) {
        return DaoManager.getInstance().getDaoSession().getCategoryDao().queryBuilder()
                .where(CategoryDao.Properties.Id.eq(id))
                .unique();
    }

    public synchronized static void deleteCategoryList(List<String> categoryIdList) {
        for (String id : categoryIdList) {
            Category Category = getCategoryById(id);
            if (Category != null) {
                deleteCategory(Category);
            }
        }
    }

    public static void clearCategory() {
        DaoManager.getInstance().getDaoSession().getCategoryDao().deleteAll();
    }
    
}
