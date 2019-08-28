package cn.bucheng.consumer.controller;


import cn.bucheng.consumer.service.ITest;
import cn.bucheng.consumer.utils.LogUtils;
import cn.bucheng.consumer.utils.ServerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author buchengyin
 * @Date 2019/3/30 15:26
 **/
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private ITest test;

    @RequestMapping("/hello")
    public String hello(String word) {
        String test = this.test.hello(word);
        return test;
    }

    @RequestMapping("/save")
    public String save() {
        log.info("consumer save method invoke ");
        return test.testSave();
    }


    @RequestMapping("/testJson")
    public Object testJson(@RequestBody Map<String, String> param) {
        Object o = test.testJson(param);
        log.info("get object:{}",o);
        return o;
    }

    @GetMapping("/testGet")
    public String testGet(String name) {
        System.out.println(name);
        return test.testGet(name);
    }

    @RequestMapping("/listAll")
    public List<String> listAll() {
        log.info("consumer listAll method invoke");
        return test.listAll();
    }


    @RequestMapping("/uploadFile")
    public Object uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ServerResult.fail("请选择文件");
        }
        String name = file.getOriginalFilename();
       log.info("filename:{}, filesize:{}" ,file.getName(),file.getSize());
        return ServerResult.success("upload ok");
    }
}
