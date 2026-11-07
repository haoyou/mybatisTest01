package com.example.mybatistest01;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class MybatisTest01ApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testStream1(){
        //单列
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
        list.forEach(System.out::println);
    }

    @Test
    void testStream2(){
        //双列
        Map<String,Integer> map = new HashMap<>();
        map.put("a",1);
        map.put("b",2);
        map.put("c",3);
        map.put("d",4);
        map.keySet().stream().forEach(s -> System.out.println(s));
        map.entrySet().stream().forEach(e -> System.out.println(e.getKey()+":"+e.getValue()));
    }

    @Test
    void testStream3(){
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        list.add("ab");
        list.add("a");
        list.add("abc");
        list.stream().filter(s -> s.startsWith("a") && s.length() == 3).forEach(System.out::println);
        long count = list.stream().count();
        System.out.println(count);
    }

}
