package com.luffy.configmanager;

/**
 * 监听配置文件的改变
 * @author zhenglu
 * @since 15/4/14
 */
public interface PropertiesChangedListener {

    /**
     * 监听配置文件改变
     * @param param1
     * @param param2
     */
    public abstract void propertiesChanged(String param1,String param2);
}
