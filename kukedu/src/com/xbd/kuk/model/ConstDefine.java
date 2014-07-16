package com.xbd.kuk.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: 马生录（mason
 * Date: 13-10-9
 * Time: 下午2:32
 */
public class ConstDefine {
    //缺省主题
    public final static String TOPIC_DEFAULT = "cn/com/open/topic/default";
    //消息处理服务主题
    public final static String TOPIC_MESSAGE_SERVER = "cn/com/open/topic/message/server";
    //公用消息主题
    public final static String TOPIC_PUBLIC_MESSAGE = "cn/com/open/topic/message/default";
    //公用通知主题
    public final static String TOPIC_PUBLIC_NOTIFICATION = "cn/com/open/topic/notification/default";
    //个人消息主题前缀
    public final static String TOPIC_USER_MESSAGE = "cn/com/open/topc/message/user/";
    //个人通知主题前缀
    public final static String TOPIC_USER_NOTIFICATION = "cn/com/open/topc/notification/user/";

    public final static int DEFAULT_QOS = 0;//0 最多一次 1 至少一次 2一次 3 保留
    public final static boolean CLEAN_START = true;
    public final static short DEFAULT_KEEP_ALIVE = 30;

    public final static String DEFAULT_SERVER_ID ="server";
    public final static String DEFAULT_WEBSERVER_ID ="webserver";

    public final static String[] ALL_DEFAULT_PUBLISH_TOPICS=new String[]{TOPIC_DEFAULT, TOPIC_PUBLIC_MESSAGE, TOPIC_PUBLIC_NOTIFICATION};

    public static String[] getUserSubscribeTopicStrings(String id){
        String[] topics=new String[5];
        topics[0]= TOPIC_DEFAULT;
        topics[1]= TOPIC_PUBLIC_MESSAGE;
        topics[2]= TOPIC_PUBLIC_NOTIFICATION;
        topics[3]= TOPIC_USER_MESSAGE +id;
        topics[4]= TOPIC_USER_NOTIFICATION +"id";
        return topics;
    }

    public static Map<String, Integer> getUserSubscribeTopicMaps(String id){
        return getUserSubscribeTopicMaps(id, DEFAULT_QOS);
    }
    public static Map<String, Integer> getUserSubscribeTopicMaps(String id,int qos){
        String[] strings = getUserSubscribeTopicStrings(id);
        Map<String, Integer> map=new LinkedHashMap<String, Integer>();
        for (String string : strings) {
            map.put(string,DEFAULT_QOS);
        }
        return map;
    }
}
