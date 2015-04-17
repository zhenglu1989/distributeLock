package com.luffy.until;


import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;

/**
 * @author zhenglu
 * @since 15/4/15
 */
public class ZkUtils {

    private static final Logger logger = Logger.getLogger(ZkUtils.class);

    public static String getZkPath(String rootPath,String key){
        if(!StringUtils.isEmpty(rootPath)){
            if(key.startsWith("/")){
                key = key.substring(1);
            }
            if(rootPath.endsWith("/")){
                return rootPath + key;
            }
            return rootPath + "/" + key;
        }
        return key;

    }


    public static void mkPaths(ZkClient client,String path){
        String[] subs = path.split("\\/");
        if(subs.length < 2){
            return;
        }
        String currentPath = "";
        for(int i = 0 ; i < subs.length;i++){
            currentPath = currentPath + "/" + subs[i];
            if(!client.exists(currentPath)){
               if(logger.isDebugEnabled()){
                   logger.debug("trying to create zk node: " +currentPath);
               }
            }
        }
    }

}
