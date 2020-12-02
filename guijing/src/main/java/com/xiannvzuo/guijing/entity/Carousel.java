package com.xiannvzuo.guijing.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.internal.org.objectweb.asm.tree.InnerClassNode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Carousel {
    private Integer carouselId;
    // 对应图片的url
    private String carouselUrl;
    // 对应图片跳转的url
    private String redirectUrl;
    // 对应图片等级
    private Integer carouselRank;
    // 逻辑删除
    private Byte isDelete;

    // 比较@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer createId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer updateId;

    public Carousel() {
    }

    public Carousel(Integer carouselId, String carouselUrl, String redirectUrl, Integer carouselRank, Byte isDelete, Date createTime, Integer createId, Date updateTime, Integer updateId) {
        this.carouselId = carouselId;
        this.carouselUrl = carouselUrl;
        this.redirectUrl = redirectUrl;
        this.carouselRank = carouselRank;
        this.isDelete = isDelete;
        this.createTime = createTime;
        this.createId = createId;
        this.updateTime = updateTime;
        this.updateId = updateId;
    }

    public Integer getCarouselId() {
        return carouselId;
    }

    public void setCarouselId(Integer carouselId) {
        this.carouselId = carouselId;
    }

    public String getCarouselUrl() {
        return carouselUrl;
    }

    public void setCarouselUrl(String carouselUrl) {
        this.carouselUrl = carouselUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Integer getCarouselRank() {
        return carouselRank;
    }

    public void setCarouselRank(Integer carouselRank) {
        this.carouselRank = carouselRank;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateId() {
        return createId;
    }

    public void setCreateId(Integer createId) {
        this.createId = createId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carousel");
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", carouselId=").append(carouselId);
        sb.append(", carouselUrl=").append(carouselUrl);
        sb.append(", redirectUrl=").append(redirectUrl);
        sb.append(", carouselRank=").append(carouselRank);
        sb.append(", isDeleted=").append(isDelete);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUser=").append(createId);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", updateUser=").append(updateId);
        sb.append("]");
        return sb.toString();
    }
}
