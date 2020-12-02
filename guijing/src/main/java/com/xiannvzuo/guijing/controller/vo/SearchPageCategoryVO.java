package com.xiannvzuo.guijing.controller.vo;

import com.xiannvzuo.guijing.entity.GoodsCategory;

import java.util.List;
import java.util.Locale;

public class SearchPageCategoryVO {
    private String firstLevelCategoryName;
    private List<GoodsCategory> secondLevelCategoryList;
    private String secondLevelCategoryName;
    private List<GoodsCategory> thirdLevelCategoryList;
    private String currentCategoryName;

    @Override
    public String toString() {
        return "SearchPageCategoryVO{" +
                "firstLevelCategoryName='" + firstLevelCategoryName + '\'' +
                ", secondLevelCategoryList=" + secondLevelCategoryList +
                ", secondLevelCategoryName='" + secondLevelCategoryName + '\'' +
                ", thirdLevelCategoryList=" + thirdLevelCategoryList +
                ", currentCategoryName='" + currentCategoryName + '\'' +
                '}';
    }

    public String getFirstLevelCategoryName() {
        return firstLevelCategoryName;
    }

    public void setFirstLevelCategoryName(String firstLevelCategoryName) {
        this.firstLevelCategoryName = firstLevelCategoryName;
    }

    public List<GoodsCategory> getSecondLevelCategoryList() {
        return secondLevelCategoryList;
    }

    public void setSecondLevelCategoryList(List<GoodsCategory> secondLevelCategoryList) {
        this.secondLevelCategoryList = secondLevelCategoryList;
    }

    public String getSecondLevelCategoryName() {
        return secondLevelCategoryName;
    }

    public void setSecondLevelCategoryName(String secondLevelCategoryName) {
        this.secondLevelCategoryName = secondLevelCategoryName;
    }

    public List<GoodsCategory> getThirdLevelCategoryList() {
        return thirdLevelCategoryList;
    }

    public void setThirdLevelCategoryList(List<GoodsCategory> thirdLevelCategoryList) {
        this.thirdLevelCategoryList = thirdLevelCategoryList;
    }

    public String getCurrentCategoryName() {
        return currentCategoryName;
    }

    public void setCurrentCategoryName(String currentCategoryName) {
        this.currentCategoryName = currentCategoryName;
    }
}
