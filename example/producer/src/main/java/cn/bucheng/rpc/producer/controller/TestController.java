package cn.bucheng.rpc.producer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author buchengyin
 * @Date 2019/3/30 15:39
 **/
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {


    @GetMapping("/hello")
    public String hello(String word) {
        log.info("get word from client ,content:{}", word);
//        throw new RuntimeException("there happen error");
        return "server success";
    }


    @RequestMapping("/testJson")
    public String testJson(@RequestBody Map<String, String> param) {
        log.info("get param from client,content:{}", param);
        return "success";
    }

    @RequestMapping("/testGet")
    public String testGet(@RequestParam("name") String name) {
        log.info("get name from client,content:{}", name);
        return "success";
    }


    @RequestMapping("/testFormData")
    public String testFormData(String name, String gender) {
        log.info("get name and gender from client, name:{},gender:{}", name, gender);
        return "success";
    }

    @RequestMapping("/listAll")
    public List<String> listAll() {
        log.info("invoke listAll method");
        List<String> data = new LinkedList<>();
        data.add("hello ");
        data.add("word");
        return data;
    }


    @RequestMapping("save")
    public String save() {
        log.info("invoke save method");
        return "save success";
    }
}
