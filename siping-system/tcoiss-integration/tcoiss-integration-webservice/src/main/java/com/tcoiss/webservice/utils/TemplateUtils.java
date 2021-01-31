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

    public static Map getUtilMethods() {
        return utils;
    }

    public static void addUtils(Map root) {
        root.put("ut", utils);
    }

    public static void main(String[] args)
    {
        Map template = new HashMap() {{
            put("first", "该订单信息${orderNo},共支付${money} ");
            put("keyword1", "${orderNo}");
            put("keyword2", "${orderTime}");
            put("keyword3", "${money}");
            put("keyword4", "${pay}");
            put("remark", "说明${orderNo},通过${pay}支付了${money} ");
        }};
        List wxMpTemplateData = new ArrayList<>(template.size());
        JSONObject jsonObject= JSON.parseObject("{\n" +
                " \"orderNo\": \"5555555\",\n" +
                " \"money\": \"8888888\",\n" +
                " \"orderTime\":\"2020-09-03 15:46:56\",\n" +
                " \"pay\":\"支付宝\"\n" +
                " }");
        /*template.forEach((key, value) -> {
            try {
                Template tpl = new Template(null, value, null);
                Writer out = new StringWriter(2048);
                tpl.process(jsonObject,out);
                wxMpTemplateData.add(new WxMpTemplateData(key,out.toString()));
            } catch (IOException | TemplateException e) {
                e.printStackTrace();
            }
        });*/

    }
}
