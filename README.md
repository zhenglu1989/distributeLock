#分布式锁
##原理
利用zookeeper，创建一个sequence类型的子节点，采用zookeeper的全局有序的特性来实现
