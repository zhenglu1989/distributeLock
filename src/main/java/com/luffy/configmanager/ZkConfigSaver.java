package com.luffy.configmanager;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * zk配置文件下载类
 * @author zhenglu
 * @since 15/4/15
 */
public class ZkConfigSaver {
    public static final Logger logger = Logger.getLogger(ZkConfigSaver.class);
    public static  String ENCODING = "utf-8";

    public static  String ZK_ROOTNODE = "/root/conf";

    public static  int ZK_TIMEOUT = 3000;

    public static  String ZK_ADDRESS = "";

    private static void loadProperties(){
        InputStream inputStream =  ZkConfigSaver.class.getResourceAsStream("/zkpublisher.properties");
        if(inputStream == null) {
            logger.info("错误:找不到配置文件 zkpublisher.properties");
        }
        Properties properties = new Properties();
        try {
            properties.load(new BufferedReader(new InputStreamReader(inputStream,"utf-8")));

        } catch (IOException e) {
            logger.error("读取配置文件zkpublisher.properties 失败 错误信息 : " + e.getMessage());
            e.printStackTrace();
        }
        ENCODING = properties.getProperty("ENCODING");
        ZK_ROOTNODE = properties.getProperty("ZK_ROOTNODE");
        ZK_TIMEOUT = Integer.valueOf(properties.getProperty("ZK_TIMEOUT"));
        ZK_ADDRESS = properties.getProperty("ZK_ADDRESS");

    }
    private static void save(ZkClient client,String rootNode,File confDir){
        List<String> configs = client.getChildren(rootNode);
        for(String config:configs){
            String content = client.readData(rootNode + "/" + config);
            File configFile = new File(confDir,config);
            try {
                FileUtils.writeStringToFile(configFile,content,"utf-8");
            } catch (IOException e) {
                logger.error("error:保存文件失败 " + e.getMessage());
            }
            logger.info("success : 文件保存到本地成功 " + configFile.getAbsolutePath());
        }

    }

//    public static void main(String[] args){
//
//        System.out.println("hello!");
//        String downloadPath = "/Users/zhenglu/Downloads";
//        loadProperties();
//        ZkClient client = new ZkClient(ZK_ADDRESS,ZK_TIMEOUT);
//        client.setZkSerializer(new BytesPushThroughSerializer());
//        File file = new File(downloadPath);
//        file.mkdirs();
//        save(client,ZK_ROOTNODE,file);
//    }
}
