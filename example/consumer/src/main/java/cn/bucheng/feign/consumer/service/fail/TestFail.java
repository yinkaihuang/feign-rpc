package cn.bucheng.feign.consumer.service.fail;

import org.springframework.stereotype.Component;
import cn.bucheng.feign.consumer.service.ITest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author buchengyin
 * @Date 2019/3/30 15:25
 **/
@Component
public class TestFail implements ITest {
    @Override
    public String hello(String word) {
        return "fail callback";
    }


    @Override
    public List<String> listAll() {
        return Collections.emptyList();
    }

    @Override
    public String testJson(Map<String, String> param) {
        return "fail";
    }

    @Override
    public String testGet(String name) {
        return "fail";
    }

    @Override
    public String testSave() {
        return "fail";
    }

    @Override
    public String testFormData(String name, String gender) {
        return "fail";
    }
}
