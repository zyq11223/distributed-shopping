                                              

## &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  CentOS6.5下的Redis集群安装

## 一，Redis集群安装  <br>

&nbsp;1.准备一台Linux虚拟机(此处是CentOS6.5)开设6个端口， 此处开设6000, 6001,6002,6003,6004,6005 共6个端口。<br>
&nbsp;2 准备一个远程连接工具如winscp用于向Linux虚拟机上传文件，同时准备一个securityCRT软件用来远程连接Linux虚拟机以便于远程配置与调试<br>
&nbsp;3 开始安装，redis集群需要版本号在3.0以上,此处使用的是redis-3.0.4 。如下图所示。
                       ![输入图片说明](https://images.gitee.com/uploads/images/2018/1211/143420_5104c1b9_1648495.png "1.png") <br>
 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   步骤 1： <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;       使用命令tar -xzvf redis-3.0.4.tar.gz将redis压缩包解压并在解压缩完成后执行命令mv redis-3.0.4 /usr/local/redis 将redis压缩后的文件夹移动到目录 /usr/local/redis中 。 <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   步骤 2：               
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;       进入目录/usr/local/redis，开始编译，安装。由于执行make需要安装gcc,因为redis是通过C语言开发的，需要支持C语言的环境，故需先执行如下命令：<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; yum install gcc-c++ <br>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; yum install zlib-devel <br>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; yum install openssl-devel <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 执行完后，依次执行make、  make install <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   步骤 3： <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   在redis安装目录/usr/local/redis下创建目录cluster，以下分别创建目录6000、6001等6个文件
夹分别代表各个redis启动端口；然后将安装目录下的redis.conf配置文件分别考到6000、6001等端口对应的目录，以复制到文件夹6000为例，将redis.conf复制到其他5个文件夹仿该指令进行。指令如下：  cp /usr/local/redis/redis.conf /usr/local/redis/cluster/6000/，如下二图所示。<br>
![输入图片说明](https://images.gitee.com/uploads/images/2018/1211/145333_82c9146c_1648495.png "2.png") <br>
![输入图片说明](https://images.gitee.com/uploads/images/2018/1211/145346_5cc3a6a9_1648495.png "3.png") <br>
    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   步骤 4： <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   6000、6001等目录下的redis.conf配置文件需要修改，进入目录6000，执行命令 vim redis.conf 。同时需要修改以下参数：<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  port  **6000**  <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  daemonize yes     //开启后端启动redis模式  <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  bind 虚拟机IP地址  <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  cluster-config-file nodes- **6000** .conf  <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  cluster-node-timeout 5000  <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  dbfilename dump **6000** .rdb  <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 完成并保存。 其他5个端口以此为例，将加粗处修改为端口号即可；<br><br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   步骤 5： <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   添加ruby环境。若在搭建redis集群时但是提示需要ruby2.2.+ 。下载uby-2.2.7.tar.gz，并解压，同时在/usr/local下创建目录ruby，进入到/usr/local/ruby下，开始依次执行 ./configure --prefix=/usr/local/ruby，   make && make install <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 删除旧的快捷方式重新创建快捷键，执行命令   ln -s /usr/local/ruby/bin/ruby /usr/bin/ruby  <br>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   步骤 6： <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   安装rubygems, 并执行命令  yum install rubygems  <br><br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   步骤 7： <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 安装ruby和redis的接口程序 下载 redis-3.0.4.gem，拷贝redis-3.0.4.gem至/usr/local下， 执行命令： gem install/usr/local/redis-3.0.4.gem <br>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   步骤 8： <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 分别启动6个实例。启动实例进入src下或者/usr/local/bin下执行以下指令，以执行端口号6000对应的redis服务器为例，redis-server /usr/local/redis/cluster/6000/redis.conf     执行其他5个端口号对应的redis服务器仿此执行<br>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   步骤 9： <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  启动集群redis-trib.rb指令在在安装目录src下执行命令  vim /etc/sysconfig/iptables， 开放6000、6001等6个端口或执行chkconfig iptables off 启动自动关闭防火墙，重启后有效。<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  进入目录/usr/local/redis执行如下命令 <br>
  ./src/redis-trib.rb create --replicas 1 192.168.1.115:6000 192.168.1.115:6001 192.168.1.115:6002 192.168.1.115:6003 192.168.1.115:6004 192.168.1.115:6005  <br>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   集群启动如下二图所示<br>
![输入图片说明](https://images.gitee.com/uploads/images/2018/1211/151444_11f37d3e_1648495.png "4.png")  <br>
![输入图片说明](https://images.gitee.com/uploads/images/2018/1211/151505_1bdc4261_1648495.png "5.png")  <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   至此，Redis集群搭建成功!   <br>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  需要注意的是，Sringboot连接redis集群时会出现错误  <font size="3" color="red">No reachable node in cluster </font>  <br>


&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  原因在于对于redis3.0的集群，用jedis的JedisCluster.close()方法造成的集群连接关闭的情况。 jedisCluster内部使用了池化技术，每次使用完毕都会自动释放Jedis因此不需要关闭。如果调用close方法后再调用jedisCluster的api进行操作时就会出现这样的错误。   <br>

## 二，Redis集群相关操作

1. 添加结点   <br>
    添加新的节点的基本过程实际上是添加一个空的节点然后移动一些数据给它，存在两种情况，1)添加一个主结点 2)添加一个从结点。  <br>
    1)添加主结点   <br>
     以本例说明，现在在原有基础上添加一个6006端口的Redis结点。首先在复制一份6006端口文件目录，并且修改redis.conf中如下信息：
     
     
    ![输入图片说明](https://github.com/yhf56davis/distributed-shopping/blob/master/docs/img/RedisClusterImg/1.png)  <br>
