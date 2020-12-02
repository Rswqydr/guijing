package com.xiannvzuo.guijing.entity;

import org.apache.ibatis.type.Alias;
import org.springframework.stereotype.Component;


@Alias("AdminUser")
public class AdminUser {
    private Integer id;
    private String adminName;
    private String adminPassword;
    private String niceName;
    private Byte locked;

    public AdminUser() {
    }

    public AdminUser(Integer id, String adminName, String adminPassword, String niceName, Byte locked) {
        this.id = id;
        this.adminName = adminName;
        this.adminPassword = adminPassword;
        this.niceName = niceName;
        this.locked = locked;
    }

    /**
     * 为了更好地同前端页面交互，一定要将toString的格式设置正确
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AdminUser");
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", adminUserId=").append(id);
        sb.append(", loginUserName=").append(adminName);
        sb.append(", loginPassword=").append(adminPassword);
        sb.append(", nickName=").append(niceName);
        sb.append(", locked=").append(locked);
        sb.append("]");
        return sb.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getNiceName() {
        return niceName;
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }

    public Byte getLocked() {
        return locked;
    }

    public void setLocked(Byte locked) {
        this.locked = locked;
    }
}
