package com.xiannvzuo.guijing.controller.vo;

import java.util.List;

public class GuijingIndexCategoryVO {
    private Long categoryId;
    private Byte categoryLevel;
    private String categoryName;
    private List<SecondLevelCategoryVO> secondLevelCategoryVOS;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Byte getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(Byte categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<SecondLevelCategoryVO> getSecondLevelCategoryVOS() {
        return secondLevelCategoryVOS;
    }

    public void setSecondLevelCategoryVOS(List<SecondLevelCategoryVO> secondLevelCategoryVOS) {
        this.secondLevelCategoryVOS = secondLevelCategoryVOS;
    }

    @Override
    public String toString() {
        return "GuijingIndexCategoryVO{" +
                "categoryId=" + categoryId +
                ", categoryLevel=" + categoryLevel +
                ", categoryName='" + categoryName + '\'' +
                ", secondLevelCategoryVOS=" + secondLevelCategoryVOS +
                '}';
    }
}
