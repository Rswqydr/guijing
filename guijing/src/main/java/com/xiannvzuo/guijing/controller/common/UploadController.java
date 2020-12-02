package com.xiannvzuo.guijing.controller.common;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.util.MallUtils;
import com.xiannvzuo.guijing.util.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Controller
@RequestMapping("/admin")
public class UploadController {

    @PostMapping("/upload/file")
    @ResponseBody
    public Result upload(HttpServletRequest request, @RequestParam("file")MultipartFile file) {
        // 获取文件名
        String filename = file.getOriginalFilename();
        // 获取文件后缀名
        String suffixName = filename.substring(filename.lastIndexOf("."));
        // 生成通用文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random random = new Random();
        StringBuffer tempName = new StringBuffer();
        tempName.append(sdf.format(new Date())).append(random.nextInt(100)).append(suffixName);
        String newFile = tempName.toString();
        // 文件目录
        File fileDirectory = new File(Constants.FILE_UPLOAD_DIC);
        // 创建文件
        File destFile = new File(Constants.FILE_UPLOAD_DIC + newFile);
        // 保存蹄片
        try {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdir()){
                    throw new IOException("");
                }
            }
            file.transferTo(destFile);
            String url = MallUtils.getHost(new URI(request.getRequestURI() + "")) +"/upload/" + newFile;
            return new Result(Constants.RESULT_CODE_SUCCESS, "上传成功", url);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(Constants.RESULT_CODE_FAIL, "文件上传失败",null);
        }
        // 返回结果
    }
}
