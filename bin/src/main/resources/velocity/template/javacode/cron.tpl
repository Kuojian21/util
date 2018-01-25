package ${packageName};

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.netease.finance.data.constant.LogConstant;
import com.netease.finance.data.model.${module}${simpleModelName};
import com.netease.finance.data.service.${module}${simpleModelName}Service;

/**
 * GenerateCode 自动生成此类
 */
@Component("${simpleModelName.substring(0,1).toLowerCase()}${simpleModelName.substring(1)}Cron")
public class ${simpleModelName}Cron {

    @Resource
    private ${simpleModelName}Service ${simpleModelName.substring(0,1).toLowerCase()}${simpleModelName.substring(1)}Service;

    public void fetchDataCron() {
        LogConstant.runLog.info("---------开始执行fetchDataCron--------");
        try {
            for (${simpleModelName} report : ${simpleModelName.substring(0,1).toLowerCase()}${simpleModelName.substring(1)}Service.selectFrom${dataSource.substring(0,1).toUpperCase()}${dataSource.substring(1)}()) {
                if (!${simpleModelName.substring(0,1).toLowerCase()}${simpleModelName.substring(1)}Service.insertEntity(report)) {
                    LogConstant.runLog.info("插入表失败");
                }
            }
        } catch (Exception e) {
            LogConstant.runLog.error("失败", e);
        } finally {
            LogConstant.runLog.info("---------结束执行fetchDataCron--------");
        }
    }
}
