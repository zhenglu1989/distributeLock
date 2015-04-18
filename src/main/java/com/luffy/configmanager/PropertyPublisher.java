package com.luffy.configmanager;

import com.luffy.until.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * zk资源文件发布者
 * @author zhenglu
 * @since 15/4/15
 */
public class PropertyPublisher {
    private static final Logger logger = Logger.getLogger(PropertyPublisher.class);

    public static  String CONF_DIR = "conf";

    public static  String ENCODING = "utf-8";

    public static  String ZK_CONF_ROOTNODE ="/root/conf";

    public static  int SESSION_TIMEOUT = 30000;

    public static String ZK_ADDRESS = "";

    private static void loadProperties(){
        InputStream inputStream =  PropertyPublisher.class.getClassLoader().getResourceAsStream("/zkpublisher.properties");
        if(inputStream == null){
            logger.error("zkpublisher.properties can't find 404..");
            throw new RuntimeException("找不到zkpublisher.properties资源文件");
        }
        Properties properties = new Properties();
        try {
            properties.load(new BufferedReader(new InputStreamReader(inputStream, "utf-8")));

        }catch (Exception e){
            e.printStackTrace();
        }
        ZK_ADDRESS = properties.getProperty("ZK_ADDRESS");
        SESSION_TIMEOUT = Integer.parseInt(properties.getProperty("SESSION_TIMEOUT"));
        ENCODING = properties.getProperty("ENCODING");
        ZK_CONF_ROOTNODE = properties.getProperty("ZK_CONF_ROOTNODE");
        CONF_DIR = properties.getProperty("CONF_DIR");

    }
   private static void publishConfigs(ZkClient client,String rootNode,File confDir){
       File[] confs = confDir.listFiles();
       int success = 0;
       int failed = 0;
       for(File conf:confs){
           if(!conf.isFile()){
               continue;
           }
           String name = conf.getName();
           String path = ZkUtils.getZkPath(rootNode,name);
           ZkUtils.mkPaths(client,path);
           String content;
           try{
               content = FileUtils.readFileToString(conf,"utf-8");

           }catch (Exception e){
               System.out.println("错误:读取文件内容遇到异常:" + e.getMessage());
               failed++;
               continue;
           }
           if(!client.exists(path)){
               try{
                   client.createPersistent(path);
                   client.writeData(path,content);

               }catch (Exception e){
                   System.out.println("错误:尝试发布配置失败 :: " + e.getMessage());
                   failed++;
                   continue;
               }
           }else {
               client.writeData(path,content);
               System.out.println("tips: 已经成功将配置文件内容更新到zk配置 ：path ：" + path);
           }
           success++;
       }

     System.out.println("提示：完成配置发布成功，成功:" + success + "失败 ：" + failed);
   }

}
