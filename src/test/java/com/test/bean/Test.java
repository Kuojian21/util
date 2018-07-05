package com.test.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Test {

    public static void main(String[] args) {
        JSONObject json = (JSONObject) JSON.parse(
                "{}");
        json.put("orgId",1);
        json.put("type",1);
        System.out.println(json.getClass());
        System.out.println(json.toJSONString());
    }

}