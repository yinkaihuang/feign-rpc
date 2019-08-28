package cn.bucheng.rpc.producer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName LogUtils
 * @Author buchengyin
 * @Date 2019/4/1 12:40
 **/
public class LogUtils {

   private static Logger getLogger(Object bean){
       return LoggerFactory.getLogger(bean.getClass());
   }

   public static void info(Object bean,String message){
       getLogger(bean).info(message);
   }

   public static void infof(Object bean,String message,Object ...params){
       getLogger(bean).info(message,params);
   }
}
