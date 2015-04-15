package com.luffy.configmanager;



import com.luffy.until.StringUtils;
import com.luffy.until.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 订阅者的具体实现类
 * @author zhenglu
 * @since 15/4/15
 */
public class ZkPropertySubscriberImpl implements PropertySubscriber{

   private static final Logger logger  = Logger.getLogger(ZkPropertySubscriberImpl.class);

   private ZkClient zkClient;

   private String rootNode;


    @Override
    public String getInitValue(String subPath) {
        String path  = ZkUtils.getZkPath(this.rootNode,subPath);
        return (String)zkClient.readData(path);
    }

    @Override
    public void subscriber(String subPath, PropertiesChangedListener listener) {
        String path = ZkUtils.getZkPath(this.rootNode,subPath);
        if(!this.zkClient.exists(path)){
            logger.error("配置 "+ path +" 不存在,必须先定义配置才能监听配置的变化，请检查配置的key是否正确");
        }
        this.zkClient.subscribeDataChanges(path,new DataListenerAdapter(listener));
    }
    private String getKey(String path){
        String key = path;
        if(!StringUtils.isEmpty(this.rootNode)){
            key = path.replaceFirst(this.rootNode,"");
            if(key.startsWith("/")){
                key = key.substring(1);
            }
        }
        return key;
    }

    /**
     * 通知监听器
     * @param path
     * @param value
     * @param propertiesChangedListener
     */
    private void noticeConfigChanged(String path,String value,PropertiesChangedListener propertiesChangedListener){
        propertiesChangedListener.propertiesChanged(getKey(path),value);
    }

    @Override
    public List<String> listKeys() {
       return this.zkClient.getChildren(rootNode);
    }
}
