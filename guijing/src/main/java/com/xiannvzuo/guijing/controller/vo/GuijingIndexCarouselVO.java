package com.xiannvzuo.guijing.controller.vo;

import java.io.Serializable;

/*
注意不要忘了序列化
 */
public class GuijingIndexCarouselVO implements Serializable {
    private String carouselUrl;

    private String redirectUrl;

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

}
