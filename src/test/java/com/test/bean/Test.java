package com.test.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Test {

    public static void main(String[] args) {
        JSONObject json = (JSONObject) JSON.parse(
                "{\"22267\":{\"orgId\":7,\"template\":\"All-5\",\"videos\":{\"0\":{\"id\":0,\"videoId\":\"6754425316\",\"src\":\"http://adsconsole.corp.kuaishou.com/api/download/photo/video?photoId=6754425316\",\"ready\":true,\"preSelected\":[0,6000],\"selected\":[0,6000],\"duration\":11934,\"expectedDuration\":6000}}}}");
        json.put("orgId",1);
        json.put("type",1);
        System.out.println(json.getClass());
        System.out.println(json.toJSONString());
    }

}