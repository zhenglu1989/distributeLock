package com.luffy.configmanager;

import com.luffy.until.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态配置文件工具类
 * @author zhenglu
 * @since 15/4/14
 */
public class DynamicPropertiesHelper {

    private static Logger LOG = Logger.getLogger(DynamicPropertiesHelper.class);

    private ConcurrentHashMap<String,String> properties = new ConcurrentHashMap<String,String>();

    private ConcurrentHashMap<String,List<PropertiesChangedListener>> properitesList = new ConcurrentHashMap<String, List<PropertiesChangedListener>>();

    public DynamicPropertiesHelper(String initValue) {
        Properties pro = prase(initValue);
        for(Map.Entry<Object,Object> p :pro.entrySet()){
            properties.put((String)p.getKey(),(String)p.getValue());
        }


    }

    /**
     * 解析配置文件，并加载到内存中
     * @param value
     * @return
     */
    private Properties prase(String value){
      Properties properties = new Properties();
      if(!StringUtils.isEmpty(value)){
          try {
              properties.load(new StringReader(value));
          } catch (IOException e) {

              LOG.error("load properties failure ::" + e);
          }
      }
      return properties;
    }

    private void setValue(String key,String newValue){
        String oldValue = properties.get(key);
        properties.put(key,newValue);
        if(!newValue.equals(oldValue)){
            // 如果值不相同，通知监听器
             noticePropertychanged(key,oldValue,newValue);
        }

    }
    private void noticePropertychanged(String key,String oldValue,String newValue){
        List<PropertiesChangedListener> propertieslisteners = properitesList.get(key);
        if(propertieslisteners == null || propertieslisteners.size() == 0){
            return;
        }
        for(PropertiesChangedListener changedlistener:propertieslisteners){
            changedlistener.propertiesChanged(oldValue,newValue);
        }

    }

    /**
     * 刷新
     * @param value
     */
    public synchronized void refresh(String value){
       LOG.info("properties refresh start");
       Properties pros = prase(value);
        for(Map.Entry<Object,Object> pro:pros.entrySet()){
            setValue((String)pro.getKey(),(String)pro.getValue());
        }
        LOG.info("properties refresh success");

    }

    /**
     * 是否包含该key
     * @param key
     * @return
     */
    public boolean containsProperty(String key){
        return properties.contains(key);
    }

    public String getProperty(String key){
        return (String)properties.get(key);
    }

    public String getProperty(String key,String defaultValue){
        if(!containsProperty(key) || properties.get(key) == null){
            return defaultValue;
        }
        return (String)properties.get(key);
    }

    public Boolean getBooleanProperty(String key,Boolean defaultValue){
        if(!containsProperty(key) || properties.get(key) == null){
            return defaultValue;
        }
        return Boolean.valueOf(properties.get(key));
    }

    public Integer getIntegerProperty(String key,Integer defaultValue){
        if(!containsProperty(key) || properties.get(key) == null){
            return defaultValue;
        }
        return Integer.valueOf(properties.get(key));
    }

    public Double getDoubleProperty(String key,Double defaultValue){
        if(!containsProperty(key) || properties.get(key) == null){
            return defaultValue;
        }
        return Double.valueOf(properties.get(key));
    }
    public Float getFloatProperty(String key,Float defaultValue){
        if(!containsProperty(key) || properties.get(key) == null){
            return defaultValue;
        }
        return Float.valueOf(properties.get(key));
    }

    public Enumeration<String> getPropertyKey(){
        return properties.keys();
    }
    // 解惑
    public void registerListener(String key,PropertiesChangedListener listener){
        List<PropertiesChangedListener> listeners = new ArrayList<PropertiesChangedListener>();
        List<PropertiesChangedListener> old =  properitesList.putIfAbsent(key,listeners);
        if(old != null){
            listeners = old;
        }
        listeners.add(listener);
    }


}

