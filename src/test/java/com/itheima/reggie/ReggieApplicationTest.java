package com.itheima.reggie;


import com.itheima.reggie.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReggieApplicationTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void sendSms(){
        String host = "https://cdcxdxjk.market.alicloudapi.com";
        String path = "/chuangxin/dxjk";
        String method = "POST";
        String appcode = "e7b2f21963504d42ab70382bb8237fbd";//开通服务后 买家中心-查看AppCode
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("content", "【创信】你的验证码是：123456，3分钟内有效！");
        querys.put("mobile", "15171515026");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendSms1(){
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "e7b2f21963504d42ab70382bb8237fbd";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:8888");
        bodys.put("phone_number", "15171515026");
        bodys.put("template_id", "TPL_0000");


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void testRedis(){
//        // 1 获取连接
//        Jedis jedis = new Jedis("localhost", 6379);
//
//        // 2 执行具体的操作
//        jedis.set("username", "xioaming");
//
//        String value = jedis.get("usrname");
//        System.out.println(value);
//
////        jedis.del("username");
//
//        Set<String> keys = jedis.keys("*");
//        for (String key : keys) {
//            System.out.println(key);
//        }
//
//        // 3 关闭操作
//        jedis.close();
//    }

    @Test
    public void testString(){
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        valueOperations.set("city123", "beijing");

        Object value = (String)redisTemplate.opsForValue().get("city123");



    }

    /**
     * 操作hash类型的数据
     */
    @Test
    public void testHash(){
        HashOperations hashOperations = redisTemplate.opsForHash();

        // 存值
        hashOperations.put("002", "name", "xiaoming");
        hashOperations.put("002", "age", "12");
        hashOperations.put("002", "address", "bj");


        // 取值
        String age = (String)hashOperations.get("002", "age");

        // 获取hash结构中的所有字段
        Set keys = hashOperations.keys("002");
        for (Object key : keys) {
            System.out.println(key);
        }

        // 获得hash结构长的所有值
        List values = hashOperations.values("002");
        for (Object value : values) {
            System.out.println(value);
        }

    }


    /**
     * 操作list类型的数据
     */
    @Test
    public void testList(){
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush("mylist", "z");
        listOperations.leftPushAll("mylist", "b", "c", "d");

        // 取值
        List<String> mylist = listOperations.range("mylist", 0, -1);
        for (String o : mylist) {
            System.out.println(o);
        }

        // 获得列表的长度
        Long size = listOperations.size("mylist");
        int lSize = size.intValue();

        // 出队列
        for(int i = 0; i < lSize; i++){
            // 出队列
            String element = (String)listOperations.rightPop("mylist");
            System.out.println(element);
        }


    }

    /**
     * 操作set类型的数据
     */
    @Test
    public void testSet(){
        SetOperations setOperations = redisTemplate.opsForSet();


        // 存值
        setOperations.add("myset", "a", "b", "c", "a");

        // 取值
        Set myset = setOperations.members("myset");
        for (Object o : myset) {
            System.out.println(o);
        }

        // 删除
        setOperations.remove("myset", "a", "b");


    }

    /**
     * 操作zset类型的数据
     */
    @Test
    public void testZSet(){
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        // 存值
        zSetOperations.add("myZset", "a", 10);
        zSetOperations.add("myZset", "b", 13);
        zSetOperations.add("myZset", "c", 12);

        // 取值
        Set<String> myZset = zSetOperations.range("myZset", 0, -1);
        for (String s : myZset) {
            System.out.println(s);
        }

        // 修改分数
        zSetOperations.incrementScore("myZset","b",  20);

        // 删除
        zSetOperations.remove("myZset", "a", "b");
    }

    /**
     * 通用操作，针对不同的数据类型都可以操作
     */
    @Test
    public void testCommon(){
        // 获取resdis中所有的key
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }

        // 判断某个key是否存在
        Boolean itcast = redisTemplate.hasKey("itcast");
        System.out.println(itcast);

        // 删除指定key
        redisTemplate.delete("myZset");

        // 获取指定key对应的value的数据类型
        DataType dataType = redisTemplate.type("myset");
        System.out.println(dataType.name());

    }


}
