package cn.bucheng.feign.consumer.handle;


import cn.bucheng.feign.consumer.annotation.IgnoreAdvice;
import cn.bucheng.feign.consumer.model.ServerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author ：yinchong
 * @create ：2019/7/25 17:25
 * @description：
 * @modified By：
 * @version:
 */
@ControllerAdvice
@Slf4j
public class ResponseAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        if(methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreAdvice.class)){
            return false;
        }
        if(methodParameter.getMethodAnnotation(IgnoreAdvice.class)!=null){
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
       if(o==null){
           return ServerModel.success(null);
       }else if(o instanceof ServerModel){
           return o;
       }else{
           return ServerModel.success(o);
       }
    }
}
