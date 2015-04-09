package com.luffy.distribute;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhenglu
 * @since 15/4/8
 */
public class DistributedLock {

    private static final Logger logger = Logger.getLogger(DistributedLock.class);

    private static final int SESSION_TIMEOUT = 5000;

    private String host = "localhost:4180,localhost:4181,localhost:4182";

    private String groupNode = "grouplock";

    private String subNode = "sub";

    private ZooKeeper zooKeeper;

    private volatile String currentPath;

    private volatile String waitPath;

    private CountDownLatch latch  = new CountDownLatch(1);

    public void connectZookeeper() throws Exception{

            zooKeeper = new ZooKeeper(host,SESSION_TIMEOUT,new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    try {
                        System.out.println("the watch coming....");
                        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){

                          latch.countDown();
                        }

                         //如果发生了waitpath的删除事件
                        if(watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)){
//                            exe();
                            //确认thispath 是否真的是列表中最小的节点
                            List<String> childNode = zooKeeper.getChildren("/" + groupNode,false);
                            String thisNode = currentPath.substring(("/" + groupNode + "/").length());
                            Collections.sort(childNode);
                            int index = childNode.indexOf(thisNode);
                            if(index == 0){
                                exe();
                            }else {
                                //异常原因
                                waitPath = "/" + groupNode + "/" + childNode.get(index -1);
                                if(zooKeeper.exists(waitPath,true) == null ){
                                    exe();
                                }

                            }

                        }


                    } catch (Exception e) {
                        logger.error("when watch some thing has one error::" + e);
                    }
                }

            });

        latch.await();
      // 子节点的类型设置为EPHEMERAL_SEQUENTIAL, 表明这是一个临时节点, 且在子节点的名称后面加上一串数字后缀
       currentPath = zooKeeper.create("/" + groupNode + "/" + subNode,null, Ids.OPEN_ACL_UNSAFE,
               CreateMode.EPHEMERAL_SEQUENTIAL);

        List<String> childNode = zooKeeper.getChildren("/" + groupNode,true);
        if(childNode.size() == 1){
            exe();
        }

    }

    /**
     * 可以考虑做成抽象，定义为钩子，子类自己去实现需要共享资源逻辑的访问
     * @throws Exception
     */

    public void exe() throws Exception{
        try {

            System.out.println("do what you want to do.......");
            System.out.println("gain lock ::" + currentPath);

        }catch (Exception e){
            logger.error("exe has error "+ e);

        }finally {
           System.out.println("done must delte node");
           zooKeeper.delete(this.currentPath,-1);

        }
    }

    public static void main(String[] args){
//        System.out.println("hellosdlfjdsfljsdlfjdsfsd".substring(("/" + "grouplock" + "/").length()));

       for(int i = 0;i < 10 ;i++){
           new Thread(){
               @Override
               public void run() {
                   try {
                       DistributedLock lock = new DistributedLock();
                       lock.connectZookeeper();

                   }catch (Exception e){
                       logger.error("跑main函数出错啦。。");
                   }
               }
           }.start();
       }
    }


}
