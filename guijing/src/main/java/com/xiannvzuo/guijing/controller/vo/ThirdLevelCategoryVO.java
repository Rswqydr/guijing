package com.xiannvzuo.guijing.controller.vo;

public class ThirdLevelCategoryVO {
    private Long categoryId;
    private Byte categoryLevel;
    private String categoryName;

    @Override
    public String toString() {
        return "ThirdLevelCategoryVO{" +
                "categoryId=" + categoryId +
                ", categoryLevel=" + categoryLevel +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }

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
}
