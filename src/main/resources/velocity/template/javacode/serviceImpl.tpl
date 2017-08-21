package ${basePackage}.service.impl;


import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import ${basePackage}.dao.${model}Dao;
import ${basePackage}.model.${model};
import ${basePackage}.service.${model}Service;
import com.netease.finance.crowdfund.core.dao.IEntityCrudDao;
import com.netease.finance.crowdfund.core.service.impl.AbstractServiceImpl;


@Service("${model}Service")
public class ${model}ServiceImpl extends AbstractServiceImpl<${model}> implements ${model}Service {

    @Resource
    private ${model}Dao ${model.substring(0,1).toLowerCase()}${model.substring(1)}Dao;

    @Override
    public IEntityCrudDao<${model}> getDao() {
        return ${model.substring(0,1).toLowerCase()}${model.substring(1)}Dao;
    }

}