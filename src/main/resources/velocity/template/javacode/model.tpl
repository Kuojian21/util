package ${basePackage}.model;

#foreach($key in $modelImport)
private $key;
#end 

/**
 * GenerateCode 自动生成此类
 */
public class ${model} {


#foreach($field in $modelFields.entrySet())
    private $field.value $field.key;
#end 


#foreach($field in $modelFields.entrySet())
    public $field.value get${field.key.substring(0,1).toUpperCase()}${field.key.substring(1)}() {
        return $field.key;
    }

    public void set${field.key.substring(0,1).toUpperCase()}${field.key.substring(1)}($field.value $field.key) {
        this.$field.key = $field.key;
    }
#end

}
