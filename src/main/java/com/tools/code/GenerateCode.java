package com.tools.code;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.netease.finance.crowdfund.core.payplatform.constants.Config;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.RuntimeServices;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import com.netease.data.automapper.AutoMapper;
import com.netease.finance.data.constant.LogConstant;

public class GenerateCode {

    private static final RuntimeInstance RUNTIME_INSTANCE = new RuntimeInstance();
    private static final String UTF_8 = "UTF-8";

    private static String MODULE_NAME = null;

    private GenerateCode() {
    
    }

    static {
        init();
    }

    public static RuntimeServices getRuntimeServices() {
        return RUNTIME_INSTANCE;
    }

    protected static void init() {
        Properties properties = new Properties();
        properties.setProperty(Velocity.INPUT_ENCODING, UTF_8);
        properties.setProperty(Velocity.OUTPUT_ENCODING, UTF_8);
        properties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        RUNTIME_INSTANCE.init(properties);
    }

    public enum DATA_SOURCE {
        LMLC("lmlcDataSource"), PAY("payDataSource"), DATA("dataDataSource");

        private DATA_SOURCE(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getDataSource() {
            return dataSource;
        }

        private String dataSource;
    }

    /**
     * @param sql
     * @return key 是fieldName, value是类型
     */
    public static Map<String, String> generateJavaFieldsMap(String sql) {
        Map<String, String> map = new LinkedHashMap<>();
        int endPos = sql.indexOf(" from");
        String field;
        for (int pos = sql.indexOf(","); pos > 0 && pos < endPos; pos = sql.indexOf(",", pos + 1)) {
            field = sql.substring(sql.lastIndexOf(" ", pos), pos);
            int tmpPos = pos;
            while (field.trim().equals("")) {
                tmpPos--;
                field = sql.substring(sql.lastIndexOf(" ", tmpPos), pos);
                continue;
            }
            field = field.trim();
            Pattern pattern = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$");
            Matcher m = pattern.matcher(field);
            if (!m.find()) {
                continue;
            }
            map.put(field.substring(0, field.lastIndexOf("_")), getType(field.substring(field.lastIndexOf("_") + 1)));
        }

        int tmpPos = endPos;
        field = sql.substring(sql.lastIndexOf(" ", tmpPos), endPos);
        while (field.trim().equals("")) {
            tmpPos--;
            field = sql.substring(sql.lastIndexOf(" ", tmpPos), endPos);
            continue;
        }
        field = field.trim();
        map.put(field.substring(0, field.lastIndexOf("_")), getType(field.substring(field.lastIndexOf("_") + 1)));
        return map;
    }

    public static List<String> getJavaColumns(String sql) {
        Map<String, String> modelMap = generateJavaFieldsMap(sql);
        List<String> javaColumns = new ArrayList<>();
        javaColumns.add("id");
        for (Entry<String, String> entry : modelMap.entrySet()) {
            javaColumns.add(entry.getKey());
        }
        return javaColumns;
    }

    private static String getType(String type) {
        type = type.toLowerCase();
        if (type.startsWith("int")) {
            return "Integer";
        } else if (type.equals("string")) {
            return "String";
        } else if (type.equals("timestamp")) {
            return "Timestamp";
        } else if (type.equals("bigdecimal")) {
            return "BigDecimal";
        }
        throw new RuntimeException("不支持此类型或者格式错误，请采用 名称_java 类型");
    }

    private static Map<String, String> generateYamlColumns(List<String> javaColumns) {
        Map<String, String> columnMap = new LinkedHashMap<>();
        for (String column : javaColumns) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < column.length(); i++) {
                char c = column.charAt(i);
                if (Character.isLowerCase(c) || Character.isDigit(c)) {
                    sb.append(column.charAt(i));
                } else {
                    sb.append("_");
                    sb.append(Character.toLowerCase(c));
                }
            }
            columnMap.put(sb.toString(), column);
        }
        return columnMap;
    }

    public static void generateYaml(String tableName, List<String> javaColumns, String outPath, String fullModelName)
            throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        options.setIndent(4);
        Yaml yaml = new Yaml(options);
        Map<String, Object> tableMap = new LinkedHashMap<>();
        Map<String, Object> innerMap = new LinkedHashMap<>();
        tableMap.put(tableName, innerMap);
        Map<String, Object> insertMap = new LinkedHashMap<>();
        insertMap.put("create_time", "sysdate()");
        insertMap.put("update_time", "sysdate()");
        Map<String, Object> updateMap = new LinkedHashMap<>();
        updateMap.put("update_time", "sysdate()");
        innerMap.put("model", fullModelName);
        innerMap.put("columns", generateYamlColumns(javaColumns));
        innerMap.put("insert", insertMap);
        innerMap.put("update", updateMap);
        FileWriter fw = new FileWriter(new File(outPath));
        yaml.dump(tableMap, fw);
        fw.close();
        System.out.println("生成：" + outPath);
    }

    /**
     *
     * @param packageName
     * @param simpleModelName
     * @param fields   以类型和field名字组成的map
     * @param outPath
     */
    public static void generateJavaBeanFile(String packageName, String simpleModelName, Map<String, String> fields,
                                            String outPath) {
        String tpl = "/vm/model.vm";
        VelocityContext vc = new VelocityContext();
        try {
            vc.put("fieldMap", fields);
            vc.put("packageName", packageName);
            vc.put("simpleModelName", simpleModelName);
            vc.put("module", MODULE_NAME);
            FileWriter fw = new FileWriter(new File(outPath));
            Template template = RUNTIME_INSTANCE.getTemplate(tpl, UTF_8);
            template.merge(vc, fw);
            fw.flush();
            fw.close();
        } catch (Exception ex) {
//            LogConstant.runLog.error("", ex);
        }
        System.out.println("生成：" + outPath);
    }

    public static void generateJavaDaoFile(String packageName, String fullModelName, String simpleModelName, String dataSource,
                                           String outPath) {
        String tpl = "/vm/dao.vm";
        VelocityContext vc = new VelocityContext();
        try {
            ;
            vc.put("packageName", packageName);
            vc.put("fullModelName", fullModelName);
            vc.put("simpleModelName", simpleModelName);
            vc.put("dataSource", dataSource);
            vc.put("module", MODULE_NAME);
            FileWriter fw = new FileWriter(new File(outPath));
            Template template = RUNTIME_INSTANCE.getTemplate(tpl, UTF_8);
            template.merge(vc, fw);
            fw.flush();
            fw.close();
        } catch (Exception ex) {
//            LogConstant.runLog.error("", ex);
        }
        System.out.println("生成：" + outPath);
    }

    public static void generateJavaDaoImplFile(String packageName, String fullModelName, String simpleModelName, String fullDaoName,
                                               String dataSource, String outPath) {
        String tpl = "/vm/daoImpl.vm";
        VelocityContext vc = new VelocityContext();
        try {
            vc.put("packageName", packageName);
            vc.put("fullModelName", fullModelName);
            vc.put("simpleModelName", simpleModelName);
            vc.put("fullDaoName", fullDaoName);
            vc.put("dataSource", dataSource);
            vc.put("module", MODULE_NAME);
            FileWriter fw = new FileWriter(new File(outPath));
            Template template = RUNTIME_INSTANCE.getTemplate(tpl, UTF_8);
            template.merge(vc, fw);
            fw.flush();
            fw.close();
        } catch (Exception ex) {
//            LogConstant.runLog.error("", ex);
        }
        System.out.println("生成：" + outPath);
    }

    public static void generateJavaServiceFile(String packageName, String fullModelName, String simpleModelName, String dataSource,
                                               String outPath) {
        String tpl = "/vm/service.vm";
        VelocityContext vc = new VelocityContext();
        try {
            vc.put("packageName", packageName);
            vc.put("fullModelName", fullModelName);
            vc.put("simpleModelName", simpleModelName);
            vc.put("dataSource", dataSource);
            vc.put("module", MODULE_NAME);
            FileWriter fw = new FileWriter(new File(outPath));
            Template template = RUNTIME_INSTANCE.getTemplate(tpl, UTF_8);
            template.merge(vc, fw);
            fw.flush();
            fw.close();
        } catch (Exception ex) {
//            LogConstant.runLog.error("", ex);
        }
        System.out.println("生成：" + outPath);
    }

    public static void generateJavaServiceImplFile(String packageName, String fullModelName, String simpleModelName,
                                                   String fullDaoName, String fullServiceName, String dataSource, String outPath) {
        String tpl = "/vm/serviceImpl.vm";
        VelocityContext vc = new VelocityContext();
        try {
            vc.put("packageName", packageName);
            vc.put("fullModelName", fullModelName);
            vc.put("simpleModelName", simpleModelName);
            vc.put("fullDaoName", fullDaoName);
            vc.put("fullServiceName", fullServiceName);
            vc.put("dataSource", dataSource);
            vc.put("module", MODULE_NAME);
            FileWriter fw = new FileWriter(new File(outPath));
            Template template = RUNTIME_INSTANCE.getTemplate(tpl, UTF_8);
            template.merge(vc, fw);
            fw.flush();
            fw.close();
        } catch (Exception ex) {
//            LogConstant.runLog.error("", ex);
        }
        System.out.println("生成：" + outPath);
    }

    public static void generateJavaCronFile(String packageName, String simpleModelName, String dataSource, String outPath) {
        String tpl = "/vm/cron.vm";
        VelocityContext vc = new VelocityContext();
        try {
            vc.put("packageName", packageName);
            vc.put("simpleModelName", simpleModelName);
            vc.put("dataSource", dataSource);
            vc.put("module", MODULE_NAME);
            FileWriter fw = new FileWriter(new File(outPath));
            Template template = RUNTIME_INSTANCE.getTemplate(tpl, UTF_8);
            template.merge(vc, fw);
            fw.flush();
            fw.close();
        } catch (Exception ex) {
//            LogConstant.runLog.error("", ex);
        }
        System.out.println("生成：" + outPath);
    }

    public static void generateJavaControllerFile(String packageName, String simpleModelName, String outPath) {
        String tpl = "/vm/controller.vm";
        VelocityContext vc = new VelocityContext();
        try {
            vc.put("packageName", packageName);
            vc.put("simpleModelName", simpleModelName);
            vc.put("module", MODULE_NAME);
            FileWriter fw = new FileWriter(new File(outPath));
            Template template = RUNTIME_INSTANCE.getTemplate(tpl, UTF_8);
            template.merge(vc, fw);
            fw.flush();
            fw.close();
        } catch (Exception ex) {
//            LogConstant.runLog.error("", ex);
        }
        System.out.println("生成：" + outPath);
    }

    public static void generateDaoXmlFile(String relativeYamlPath, String daoImplXmlPath) throws Exception {
        URL url = AutoMapper.class.getResource(relativeYamlPath);
        AutoMapper autoMapper = new AutoMapper(url.getPath());
        String result = autoMapper.makeXmlMapperToString();
        FileWriter fw = new FileWriter(new File(daoImplXmlPath));
        fw.write(result);
        fw.flush();
        fw.close();
        System.out.println("生成：" + daoImplXmlPath);
    }

    public static void generateReportRelatedFile(String moduleName, String sql, String newTableName, String newModelName, String dataSource)
            throws Exception {

        System.err.println("用法： 程序自动生成model dao daoimpl service serviceimpl cron, 执行一遍，刷新工程或者clean,再执行一遍");
        System.err.println(
                "1:手动拷贝sql到main 方法， 或者至少拷贝到含“ from ”处， 并且修改返回字段，名称_java类型，例如： select userCount_Integer, createTime_Timestamp from ,这个是有效输入， id是保留字段， 建议有如前字段createTime");
        System.err.println("2:执行main方法，并修改generateReportRelatedFile参数，表名，对应生成的model名，从哪个数据源来的数据");
        System.err.println("3:刷新工程，如遇到失败，刷新或者clean工程，再次执行main");
        System.err.println("4:！！！！手动添加生成的daoImpl.xml至sqlMapConfig.xml");
        System.err.println("5:参看生成的daoImpl, cron, controller, 做相应修改");
        System.err.println("6:参看生成的daoImpl.xml, 创建新表");

        MODULE_NAME = moduleName;

        String baseDotPackage = GenerateCode.class.getName().substring(0, GenerateCode.class.getName().indexOf("util"));
        String baseSlashPackage = baseDotPackage.replaceAll("\\.", "\\\\");
        String module = moduleName;
        if (moduleName == null) {
            module = "";
            MODULE_NAME = "";
        } else if (moduleName != null && !moduleName.equals("")) {
            MODULE_NAME = moduleName + ".";
            module = "\\" + moduleName.replaceAll("\\.", "\\\\");
        }
        String basePath = Paths.get(".").toAbsolutePath().normalize().toString() + "\\src\\main\\java\\";
        String baseModelPath = basePath + baseSlashPackage + "model" + module;
        String baseDaoPath = basePath + baseSlashPackage + "dao" + module;
        String baseDaoImplPath = basePath + baseSlashPackage + "dao\\impl" + module;
        String baseServicePath = basePath + baseSlashPackage + "service" + module;
        String baseServiceImplPath = basePath + baseSlashPackage + "service\\impl" + module;
        String baseCronPath = basePath + baseSlashPackage + "cron" + module;
        String baseControllerPath = basePath + baseSlashPackage + "controller" + module;
        String yamlPath = Paths.get(".").toAbsolutePath().normalize().toString() + "\\src\\main\\resources\\yaml\\" + newModelName
                + ".yaml";
        String daoImplXmlPath = Paths.get(".").toAbsolutePath().normalize().toString() + "\\src\\main\\resources\\mybatis\\"
                + newModelName + "DaoImpl.xml";
        File f = new File(baseModelPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(baseDaoPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(baseDaoImplPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(baseServicePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(baseServiceImplPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(baseCronPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(baseControllerPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        if (moduleName != null && !moduleName.equals("")) {
            module = moduleName + ".";
        }
        String fullModelName = baseDotPackage + "model." + module + newModelName;
        if (moduleName != null && !moduleName.equals("")) {
            module = "." + moduleName;
        }
        generateYaml(newTableName, getJavaColumns(sql), yamlPath, fullModelName);
        generateJavaBeanFile(baseDotPackage + "model" + module, newModelName, generateJavaFieldsMap(sql),
                baseModelPath + "\\" + newModelName + ".java");

        generateJavaDaoFile(baseDotPackage + "dao" + module, fullModelName, newModelName, dataSource,
                baseDaoPath + "\\" + newModelName + "Dao.java");

        generateJavaDaoImplFile(baseDotPackage + "dao.impl" + module, fullModelName, newModelName,
                baseDotPackage + "dao" + module + "." + newModelName + "Dao", dataSource,
                baseDaoImplPath + "\\" + newModelName + "DaoImpl.java");

        generateJavaServiceFile(baseDotPackage + "service" + module, fullModelName, newModelName, dataSource,
                baseServicePath + "\\" + newModelName + "Service.java");
        generateJavaServiceImplFile(baseDotPackage + "service.impl" + module, fullModelName, newModelName,
                baseDotPackage + "dao" + module + "." + newModelName + "Dao",
                baseDotPackage + "service" + module + "." + newModelName + "Service", dataSource,
                baseServiceImplPath + "\\" + newModelName + "ServiceImpl.java");
        generateDaoXmlFile("/yaml/" + newModelName + ".yaml", daoImplXmlPath);
        generateJavaCronFile(baseDotPackage + "cron" + module, newModelName, dataSource,
                baseCronPath + "\\" + newModelName + "Cron.java");
        generateJavaControllerFile(baseDotPackage + "controller" + module, newModelName,
                baseControllerPath + "\\" + newModelName + "Controller.java");
    }

    public static void main(String[] args) throws Exception {
//        String sql = "select distinct u.sex gender_String,count(distinct b.account_id) userCount_integer, trunc((sysdate-1),'dd') dataDate_timestamp, 1 type_integer, sysdate createTime_timestamp from ";
//        sql = "select 1 netEaseTime_string, 2 hongkangTime_string, 3 accountId_string,4 orderId_string, 5 partnerOrderId_string, 6 orderAmount_bigdecimal from";
//        generateReportRelatedFile(sql, "tb_charge_withdraw_device", "HongkangReport", DATA_SOURCE.LMLC.getDataSource());

//        String sql = "select days_Integer, money_bigdecimal from";
//        generateReportRelatedFile(sql, "tb_collect_mobile_report_conversion_rate", "MobileConversionRateReport", DATA_SOURCE.DATA.getDataSource());
//        String jdbcName = "jdbc_jr_data";
        String tableName = "tb_finance_user_tag_history_order";
        String sql = getSql("jdbc_jr_data", tableName);


//        String sql = "select name_String, nameSet_String, validStatus_integer, type_integer, os_String, param0Name_String, param1Name_string, param2Name_string, param3Name_string, param4Name_string, param5Name_string, param6Name_string, param7Name_String, param8Name_string, param9Name_string from ";
//        String sql = "select newPayedDate_TimeStamp, payedUserCount_integer, payedAmount_BigDecimal, comparedDate_Timestamp, comparedDateUserCount_integer, comparedDateAmount_BigDecimal, comparedDate3DaysAgoUserCount_integer, comparedDate3DaysAgoAmount_BigDecimal, comparedDate7DaysAgoUserCount_integer, comparedDate7DaysAgoAmount_BigDecimal from";
        generateReportRelatedFile("report", sql, "tb_finance_user_tag_history_order", "HistoryUserTagOrder", DATA_SOURCE.DATA.dataSource);
//        generateReportRelatedFile("report", sql, "test", "NewPayedReport", DATA_SOURCE.LMLC.getDataSource());
    }

    public static void test() throws Exception {
        String sql = "select a1 aBC_int  , b1 bDc_timestamp, c1 c_string   from ";
        generateJavaFieldsMap(sql);
        generateYaml("table_test", getJavaColumns(sql), "D:/test.yaml", "com.test.model");
        generateJavaBeanFile("com.test", "MyModel", generateJavaFieldsMap(sql), "D:/testModel.java");
        // generateJavaDaoFile("com.test.dao", "com.test.model.MyModel",
        // "MyModel", "D:/testDao.java");
        // generateJavaDaoImplFile("com.test.dao.impl",
        // "com.test.model.MyModel", "MyModel", "com.test.dao.MyModelDao",
        // "D:/testDaoImpl.java");
        // generateJavaServiceFile("com.test.service", "com.test.model.MyModel",
        // "MyModel", "D:/testService.java");
        // generateJavaServiceImplFile("com.test.service.impl",
        // "com.test.model.MyModel", "MyModel", "com.test.dao.MyModelDao",
        // "com.test.service.MyModelService", "D:/testServiceImpl.java");
    }

    /**
     * 传入jdbc.properties对应数据库配置的前缀，如lmlc_data的前缀为jdbc_jr_data
     * 以及表名，返回拼装成的sql
     *
     * @param jdbcName  jdbc.properties对应数据库配置的前缀
     * @param tableName 表名
     * @return
     */
    private static String getSql(String jdbcName, String tableName) throws Exception {
        //jdbcName：jdbc_jr_data=data,jdbc_jr=payment,lmlc_jdbc_jr=lmlc，详情见jdbc.properties文件
        String url = Config.getConfig("jdbc.properties", jdbcName + ".url");
        String username = Config.getConfig("jdbc.properties", jdbcName + ".username");
        String password = Config.getConfig("jdbc.properties", jdbcName + ".password");
        String driverClassName = Config.getConfig("jdbc.properties", jdbcName + ".driverClassName");

        Map<String, String> map = Maps.newLinkedHashMap();
        //加载MySql的驱动类
        Class.forName(driverClassName);
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement st = connection.createStatement();
            String sql = "SELECT * FROM " + tableName.toUpperCase();
            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String columnName = rsmd.getColumnName(i).toLowerCase();
                String typeName = rsmd.getColumnTypeName(i).toUpperCase();
                map.put(columnName, typeName);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select ");
        Set<String> keys = map.keySet();
        Map<String, String> time = Maps.newLinkedHashMap();
        for (String key : keys) {
            if (key.equals("id")) continue;
            if (key.equals("create_time")) {
                time.put(key, map.get(key));
                continue;
            }
            if (key.equals("update_time")) {
                time.put(key, map.get(key));
                continue;
            }

            String[] keyNameSplit = key.split("_");
            String keyName = "";
            for (int index = 0; index < keyNameSplit.length; index++) {
                String temp = keyNameSplit[index];
                if (index != 0) {
                    temp = upperCap(temp);
                }
                keyName += temp;
            }
            stringBuilder.append(keyName).append("_").append(convertSqltypeToJavatype(map.get(key))).append(", ");
        }

        //时间拼接在最后
        Set<String> timeKeys = time.keySet();
        for (String key : timeKeys) {
            String[] keyNameSplit = key.split("_");
            String keyName = "";
            for (int index = 0; index < keyNameSplit.length; index++) {
                String temp = keyNameSplit[index];
                if (index != 0) {
                    temp = upperCap(temp);
                }
                keyName += temp;
            }
            stringBuilder.append(keyName).append("_").append(convertSqltypeToJavatype(map.get(key))).append(", ");
        }

        String sql = stringBuilder.append("from").toString();
        int index = sql.lastIndexOf(',');
        sql = sql.substring(0, index) + sql.substring(index + 1, sql.length());
        System.out.println(sql);
        return sql;
    }

    private static String convertSqltypeToJavatype(String str) throws Exception {

        switch (str) {
            case "VARCHAR":
                return "String";
            case "VARCHAR2":
                return "String";
            case "TINYINT":
                return "Integer";
            case "DATETIME":
                return "Timestamp";
            case "DATE":
                return "Timestamp";
            case "TIMESTAMP":
                return "Timestamp";
            case "INT":
                return "Integer";
            case "DECIMAL":
                return "BigDecimal";

            //待添加

            default:
                throw new Exception("数据库类型" + str + "没有找到对应的java类型！");

        }
    }

    private static String upperCap(String str) {

        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

}
