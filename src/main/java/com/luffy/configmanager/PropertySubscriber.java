package com.luffy.configmanager;

import java.util.List;

/**
 * 配置文件的订阅者，在每一个znode上订阅一个监听器
 * @author zhenglu
 * @since 15/4/15
 */
public  interface PropertySubscriber {

    public abstract String getInitValue(String paramString);

    public abstract void subscriber(String param,PropertiesChangedListener listener);

    public abstract List<String> listKeys();
}
