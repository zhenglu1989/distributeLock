package com.luffy.configmanager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态配置文件辅助类工厂类，在创建动态配置文件辅助类时，会订阅zk数据改变的事件
 * @author zhenglu
 * @since 15/4/15
 */
public class DynamicPropertityHelperFactory {

    private PropertySubscriber propertySubscriber;

    private ConcurrentHashMap<String,DynamicPropertiesHelper> helper = new ConcurrentHashMap<String, DynamicPropertiesHelper>();

    public DynamicPropertityHelperFactory(PropertySubscriber propertySubscriber) {
        this.propertySubscriber = propertySubscriber;
    }
    private  DynamicPropertiesHelper createHelper(String key){
        List<String> keys = this.propertySubscriber.listKeys();
        if(keys == null || keys.size() == 0){
            return null;
        }
        if(!keys.contains(key)){
            return  null;
        }
        String initValue = this.propertySubscriber.getInitValue(key);
        final DynamicPropertiesHelper dynamicPropertiesHelper = new DynamicPropertiesHelper(initValue);
        DynamicPropertiesHelper old = this.helper.putIfAbsent(key,dynamicPropertiesHelper);
        if(old != null){
            return old;
        }
        //订阅zk数据改变
        this.propertySubscriber.subscriber(key,new PropertiesChangedListener() {
            @Override
            public void propertiesChanged(String key, String value) {
                dynamicPropertiesHelper.refresh(value);
            }
        });

        return dynamicPropertiesHelper;


    }
    public DynamicPropertiesHelper getHelper(String key){
        DynamicPropertiesHelper dynamicPropertiesHelper =  this.helper.get(key);
        if(dynamicPropertiesHelper != null){
            return dynamicPropertiesHelper;
        }
       return createHelper(key);
    }
}
