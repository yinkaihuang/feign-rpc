package cn.bucheng.feign.consumer.service;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import cn.bucheng.feign.consumer.service.fail.TestFail;

import java.util.List;
import java.util.Map;

/**
 * @author buchengyin
 * @Date 2019/3/30 15:23
 **/
@FeignClient(value = "server-provider",fallback = TestFail.class)
public interface ITest {

    @GetMapping("/test/hello")
     String hello(@RequestParam("word") String word);



    @GetMapping("/test/listAll")
    List<String> listAll();

    @PostMapping("/test/testJson")
    String testJson(@RequestBody Map<String, String> param);

    @GetMapping("/test/testGet")
    String testGet(@RequestParam("name") String name);

    @RequestMapping("/save")
    String testSave();

    @PostMapping("/test/testFormData")
    String testFormData(@RequestParam("name") String name, @RequestParam("gender") String gender);
}
