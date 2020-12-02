package com.xiannvzuo.guijing.controller.admin;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.IndexConfigTypeEnum;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.entity.IndexConfig;
import com.xiannvzuo.guijing.service.IndexConfigService;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.Result;
import org.omg.PortableInterceptor.INACTIVE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RelationSupport;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class IndexConfigController {

    @Autowired
    private IndexConfigService indexConfigService;

    private static final Logger LOG = LoggerFactory.getLogger(IndexConfigController.class);

    @GetMapping("/indexConfigs")
    public String indexConfig(HttpServletRequest request, @RequestParam("configType") int configType){
        LOG.info("进入配置项");
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if (IndexConfigTypeEnum.DEFAULT.equals(indexConfigTypeEnum)) {
            return "error/error_5xx";
        }
        request.setAttribute("path", indexConfigTypeEnum.getName());
        request.setAttribute("configType", configType);
        return "admin/mall_index_config";
    }

    /**
     * 列表
     */

    @GetMapping("/indexConfigs/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        LOG.info("进入list");
        if (StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty("page")) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        // List<IndexConfig> indexConfigList = indexConfigService.findIndexConfigList(pageQueryUtil);
        return new Result(Constants.RESULT_CODE_FAIL, "参数异常", indexConfigService.findIndexConfigList(pageQueryUtil));

    }


    /**
     * save
     */

    @RequestMapping("/indexConfigs/save")
    @ResponseBody
    public Result save(@RequestBody IndexConfig indexConfig) {
        LOG.info("准备保存中");
        if (Objects.isNull(indexConfig.getConfigType())
                || Objects.isNull(indexConfig.getConfigRank())
                || StringUtils.isEmpty(indexConfig.getConfigName())) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        String result = indexConfigService.saveIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result, null);
        }
    }


    @PostMapping("/indexConfigs/update")
    @ResponseBody
    public Result update(@RequestBody IndexConfig indexConfig) {
        LOG.info("准备更新中");
        if (StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigType())
                || Objects.isNull(indexConfig.getConfigId())
                || Objects.isNull(indexConfig.getConfigRank())) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        String result = indexConfigService.updateIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result, null);
        }
    }

    @RequestMapping("/indexConfigs/info/{id}")
    @ResponseBody
    public Result info( @PathVariable("id") Long id) {
        LOG.info("查取信息中");
        IndexConfig indexConfig =  indexConfigService.getIndexConfigByPrimaryKey(id);
        if (indexConfig == null) {
            return new Result(Constants.RESULT_CODE_FAIL, "未查询到数据", null);
        }
        return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", indexConfig);
    }


    @PostMapping("/indexConfigs/delete")
    @ResponseBody
    public Result delete(@RequestBody Long[] ids) {
        LOG.info("删除数据中");
        if (ids.length < 1) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数错误", null);
        }
        if (indexConfigService.batchDelete(ids)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, "删除失败", null);
        }
    }



}
