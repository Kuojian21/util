package com.test.json;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test {

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        JSONArray slots = JSONArray.fromObject(
                "[{\"x\":0,\"y\":0,\"width\":100,\"height\":100}]");
        jsonObject.put("slots", slots);
        jsonObject.put("width", 200);
        jsonObject.put("height", 200);
        jsonObject.put("previewUrl", "http://static.yximgs.com/bs2/fes/growth_21430b04a2348f4a9d09c4eae4d55376.png");
        jsonObject.put("foregroundUrl", "http://static.yximgs.com/bs2/fes/growth_21430b04a2348f4a9d09c4eae4d55376.png");
        System.out.println(jsonObject.toString());
        String[] s = "200x200".split("x");
        String data = "[{\"x\":0,\"y\":0,\"width\":100,\"height\":100}]";
        String previewUrl = "http://static.yximgs.com/bs2/fes/growth_21430b04a2348f4a9d09c4eae4d55376.png";
        String foregroundUrl = "http://static.yximgs.com/bs2/fes/growth_21430b04a2348f4a9d09c4eae4d55376.png";
        StringBuilder builder = new StringBuilder();
        builder.append("{\"slots\":").append(data).append(",\"width\":").append(s[0])
                .append(",\"height\":").append(s[1]).append(",\"previewUrl\":\"")
                .append(previewUrl).append("\",\"foregroundUrl\":\"")
                .append(foregroundUrl).append("\"}");
        System.out.println(builder.toString());
    }
}
