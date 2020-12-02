package com.xiannvzuo.guijing.controller.common;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Controller
public class KaptchaController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @GetMapping("/common/kaptcha")
    public void getDefaultKaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 创建一个字节数组存放验证码输出流
        byte[] captchaOutStream = null;
        // 创建一个ByteArrayOutputStream对象
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 获取生成的验证码字符串
        try {
            String verifyCode = defaultKaptcha.createText();
            // 将该字符串写入session
            request.getSession().setAttribute("verifyCode", verifyCode);
            // 获取该字符的图片验证码
            BufferedImage image = defaultKaptcha.createImage(verifyCode);
            // 将该图片以指定格式写入字节数组流
            ImageIO.write(image, "jpg", byteArrayOutputStream);

        } catch (IllegalArgumentException e) {
            response.sendError(response.SC_NOT_FOUND);
            return ;
        }
        // 将该字节数组流转换为字节数组
        captchaOutStream = byteArrayOutputStream.toByteArray();
        //设置响应头
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        // 将字节数组写入响应头的输出流
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(captchaOutStream);
        // 刷新关闭流
        outputStream.flush();
        outputStream.close();
    }

}
