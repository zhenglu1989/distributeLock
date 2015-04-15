package com.luffy.configmanager;

import org.I0Itec.zkclient.IZkDataListener;

/**
 * 监听器的适配类，当zk数据变化时，触发自定义的listener进行通知
 * @author zhenglu
 * @since 15/4/15
 */
public class DataListenerAdapter implements IZkDataListener{

    private PropertiesChangedListener propertiesChangedListener;

    public DataListenerAdapter(PropertiesChangedListener propertiesChangedListener) {
        this.propertiesChangedListener = propertiesChangedListener;
    }

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {
       propertiesChangedListener.propertiesChanged(dataPath,(String)data);
    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {

    }
}
