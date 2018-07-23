package com.test.json;

import java.util.Map;

import com.google.common.collect.Maps;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();
		JSONArray slots = JSONArray.fromObject("[{\"x\":0,\"y\":0,\"width\":100,\"height\":100}]");
		jsonObject.put("slots", slots);
		jsonObject.put("width", 200);
		jsonObject.put("height", 200);
		System.out.println(jsonObject.toString());
		String[] s = "200x200".split("x");
		String data = "[{\"x\":0,\"y\":0,\"width\":100,\"height\":100}]";
		StringBuilder builder = new StringBuilder();
		System.out.println(builder.toString());

		Map<String, String> creative = Maps.newHashMap();
		creative.put("creative_id", "{result=" + s[0] + ":$.id}");
		
		
		
		System.out.println(JSONObject.fromObject(creative).toString());
	}
}
