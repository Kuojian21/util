
package com.test.json;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Maps;

import net.sf.json.JSONObject;

public class JsonTest {

	public static void main(String[] args) {
		Map<String, Object> map = Maps.newHashMap();
		Map<String, Object> subMap = Maps.newHashMap();
		subMap.put("application_id", "223232");
		map.put("subMap", JSON.toJSON(subMap));

		System.out.println(JSONObject.fromObject(map).toString());
	}

}
