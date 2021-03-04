package com.tcoiss.webservice.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class TemplateUtils {
    private static Configuration conf;
    private static int tempSeq = 1;
    private static Map utils = new HashMap();

    static {
        conf = new Configuration();
        conf.setEncoding(Locale.CHINA,"UTF-8");
        conf.setNumberFormat("0");

    }

    public static Template createTemplate(String content) throws Exception {
        return new Template("ID:" + (tempSeq++), new StringReader(content), conf);
    }

    /*public static void main(String[] args) throws Exception {

        String content  = "{\n" +
                " \"orderNo\": ${orderNo},\n" +
                " \"money\": ${money},\n" +
                " \"orderTime\":${orderTime},\n" +
                " \"pay\":\"支付宝\"\n" +
                " }";
        //String content = "&orderNo=${orderNo}&money=${money}&orderTime=${orderTime}";
        Template template = TemplateUtils.createTemplate(content);
        StringWriter out = new StringWriter();
        Map<String,Object> root = new HashMap<>();
        root.put("orderNo","1");
        root.put("money","250");
        root.put("orderTime","2");
        template.process(root,out);
        System.out.println(out.toString());

    }*/
}
