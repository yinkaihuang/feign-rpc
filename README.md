# feign的http调用变tcp调用


## 使用
```
1.在rpc-core中执行 mvn install

2.在需要使用的springcloud项目中添加如下依赖
 <dependency>
      <groupId>cn.bucheng</groupId>
      <artifactId>rpc-core</artifactId>
      <version>0.0.1-SNAPSHOT</version>
 </dependency>
 
 3.可以添加如下配置(非必填)
 #是否启动feign的rpc调用默认为true
   feign.rpc.enable=true
 #配置rpc调用超时时间默认10秒
   feign.rpc.timeout=10
```
## 架构图
![](https://github.com/yinbucheng/mypic/blob/master/feign-rpc%E6%95%B4%E4%BD%93%E6%9E%B6%E6%9E%84%E5%9B%BE.png?raw=true)


## 流程图
![](https://github.com/yinbucheng/mypic/blob/master/feign%E6%95%B4%E4%BD%93%E8%B0%83%E7%94%A8.png?raw=true)


### 性能压测对比图
```
接口：http://192.168.54.23:7001/test/hello?word=nice
并发线程数量：10
执行次数：1000
```


#### 第一次压测对比(第一幅为http调用  第二副为tcp调用)

```
本地环境中压测：
10并发 100次
http: 
90% 332 95% 364 99% 430 吞吐量: 410.6/sec

改造：
90% 176 95% 192 99% 227 吞吐量: 695.4/sec
```


### demo地址

```
当前项目模块中的example模块
1.先启动register
2.再启动producer
3.最后启动consumer

4.测试接口：
url地址：http://127.0.0.1:7001/test/hello?word=nice
请求地址：GET

http:压测：
将consumer中的配置 feign.rpc.enable=false

改造压测:
将consumer中配置 feign.rpc.enable=true

```
