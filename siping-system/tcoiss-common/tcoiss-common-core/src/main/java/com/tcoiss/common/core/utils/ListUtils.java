package com.tcoiss.common.core.utils;

import java.util.*;

public class ListUtils {

    public static void listMapSortByKey(List<Map<String,Object>> mapList,String key){
        Collections.sort(mapList, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer name1 =Integer.valueOf(o1.get(key).toString());//name1是从你list里面拿出来的一个
                Integer name2= Integer.valueOf(o2.get(key).toString()); //name1是从你list里面拿出来的第二个name
                return name1.compareTo(name2);
            }
        });
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("name", "p");
        map1.put("cj", "1234712432");
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("name", "h");
        map2.put("cj", "1");
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("name", "f");
        map3.put("cj", "33434");
        list.add(map1);
        list.add(map3);
        list.add(map2);
        //排序前
        for (Map<String, Object> map : list) {
            System.out.println(map.get("cj"));
        }
        listMapSortByKey(list,"cj");
        //排序后
        System.out.println("-------------------");
        for (Map<String, Object> map : list) {
            System.out.println(map.get("cj"));
        }
    }
}
