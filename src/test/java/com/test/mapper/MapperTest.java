package com.test.mapper;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import com.google.common.collect.Lists;

public class MapperTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	static TypeFactory factory = TypeFactory.defaultInstance();

	/*
    static {
        MAPPER.disable(FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.enable(ALLOW_UNQUOTED_CONTROL_CHARS);
        MAPPER.enable(ALLOW_COMMENTS);
        MAPPER.registerModule(new ParameterNamesModule());
        MAPPER.registerModule(new KotlinModule());
    }
    */ 
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String obj = "{\\\"object_store_url\\\":\\\"https:\\/\\/itunes.apple.com\\/app\\/id1338605092\\\",\\\"application_id\\\":\\\"1797702170527735\\\"}";
		int sIndex = obj.toString().indexOf("\\\"application_id\\\":\\\"") + "\\\"application_id\\\":\\\"".length();
        int eIndex = obj.toString().indexOf("\\\"", sIndex);
        String appId = obj.toString().substring(sIndex, eIndex);
		System.out.println(appId);
		obj = "https://graph.facebook.com/v2.11/act_974963309352161/adsets";
		sIndex = obj.indexOf("/act_") + "/act_".length();
        eIndex = obj.indexOf("/", sIndex);
        System.out.println(obj.substring(sIndex, eIndex));
		
        System.out.println(Lists.newArrayList(1).subList(0, 1));
        
		Object readValue = MAPPER.readValue("{\\\"object_store_url\\\":\\\"https:\\/\\/itunes.apple.com\\/app\\/id1338605092\\\",\\\"application_id\\\":\\\"1797702170527735\\\"}",factory.constructMapType(Map.class,String.class,Object.class));
		System.out.println(readValue);
	}

}
