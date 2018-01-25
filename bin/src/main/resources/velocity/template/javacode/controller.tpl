package ${basePackage}.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.netease.finance.crowdfund.core.constant.LogConstant;
import com.netease.finance.crowdfund.ms.model.${model};
import com.netease.finance.crowdfund.ms.service.${model}Service;


@Controller
@RequestMapping("${baseURLPath}")
public class ${model}Controller {

    @Resource
    private ${model}Service ${model.substring(0,1).toLowerCase()}${model.substring(1)}Service;

	public static final String module = "${module}";

    @RequestMapping("/insert")
    public String insert(HttpServletRequest request, HttpServletResponse response, Model model,${model} entity) {
        String ftl = "${urlPath}/insert.jsp";
        String submodule = "新增";
        try {
        	LogConstant.runLog.info(module,submodule + ":开始");
 			boolean rtn = this.${model.substring(0,1).toLowerCase()}${model.substring(1)}Service.insertEntity(entity);
 			if(rtn){
 				LogConstant.runLog.info(module,submodule + ":成功", "entity="+JSON.toJSONString(entity));
 			}else{
 				LogConstant.runLog.info(module,submodule + ":失败", "entity="+JSON.toJSONString(entity));
 			}
 			LogConstant.runLog.info(module,submodule + ":结束");
        } catch (Exception e) {
            LogConstant.runLog.error(module,submodule + ":异常", e);
        }
        return ftl;
    }

    @RequestMapping("/update")
    public String update(HttpServletRequest request, HttpServletResponse response, Model model,${model} entity) {
        String ftl = "${urlPath}/update.jsp";
        String submodule = "修改";
        try {
        	LogConstant.runLog.info(module,submodule + ":开始");
 			boolean rtn = this.${model.substring(0,1).toLowerCase()}${model.substring(1)}Service.updateEntity(entity);
 			if(rtn){
 				LogConstant.runLog.info(module,submodule + ":成功", "entity="+JSON.toJSONString(entity));
 			}else{
 				LogConstant.runLog.info(module,submodule + ":失败", "entity="+JSON.toJSONString(entity));
 			}
 			LogConstant.runLog.info(module,submodule + ":结束");
        } catch (Exception e) {
            LogConstant.runLog.error(module,submodule + ":异常", e);
        }
        return ftl;
    }
    
    @RequestMapping("/delete")
    public String delete(HttpServletRequest request, HttpServletResponse response, Model model,${model} entity) {
        String ftl = "${urlPath}/delete.jsp";
        String submodule = "删除";
        try {
        	LogConstant.runLog.info(module,submodule + ":开始");
 			boolean rtn = this.${model.substring(0,1).toLowerCase()}${model.substring(1)}Service.deleteEntity(entity);
 			if(rtn){
 				LogConstant.runLog.info(module,submodule + ":成功", "entity="+JSON.toJSONString(entity));
 			}else{
 				LogConstant.runLog.info(module,submodule + ":失败", "entity="+JSON.toJSONString(entity));
 			}
 			LogConstant.runLog.info(module,submodule + ":结束");
        } catch (Exception e) {
            LogConstant.runLog.error(module,submodule + ":异常", e);
        }
        return ftl;
    }
    
    @RequestMapping("/list")
    public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
        String ftl = "${urlPath}/list.jsp";
        String submodule = "查询";
        try {
        	LogConstant.runLog.info(module,submodule + ":开始");
 			boolean rtn = this.${model.substring(0,1).toLowerCase()}${model.substring(1)}Service.insertEntity(entity);
 			if(rtn){
 				LogConstant.runLog.info(module,submodule + ":成功", "entity="+JSON.toJSONString(entity));
 			}else{
 				LogConstant.runLog.info(module,submodule + ":失败", "entity="+JSON.toJSONString(entity));
 			}
 			LogConstant.runLog.info(module,submodule + ":结束");
        } catch (Exception e) {
            LogConstant.runLog.error(module,submodule + ":异常", e);
        }
        return ftl;
    }

}
