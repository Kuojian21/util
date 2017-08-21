package ${basePackage}.dao.impl;

import org.springframework.stereotype.Repository;
import ${basePackage}.dao.${model}Dao;
import ${basePackage}.model.${model};
import com.netease.finance.crowdfund.core.dao.impl.AbstractCrudDaoImpl;

@Repository("${model}Dao")
public class ${model}DaoImpl extends AbstractCrudDaoImpl<${model}> implements ${model}Dao {

}
