# LonersHub

## 项目简介

### 功能介绍

正如其名，孤独者中心，也即孤独者的聚集地，俱乐部。

茫茫人海中，你也许很难找到几个志同道合的朋友，也许无法自然地融入群体，也许对集体活动完全不感兴趣......

这时，你发现了孤独，发现了自己已经变成了一个名副其实的孤独者，A Loner。

每一个Loner都可以在LonersHub通过日记本记录自己的生活，可以自己上传一部影片，并预约一个合适的时间，邀请其它Loner一起观赏电影，并对影片进行愉快的评论。

### 工具介绍

- 后端基于SpringBoot，Mybatis-Plus，MySQL，Redis等构建。
- 前端采用Bootstrap，Thymeleaf，Ajax等构建。

## 快速搭建

### 通过sql脚本搭建数据库

执行lonersHub.sql脚本进行快速搭建

### 建立bean对象
- Diary
- Loner
- Participant
- Video
- ......

### 创建crud接口及实现类

- mapper
  - DiaryMapper
  - LonerMapper
  - MessageMapper
  - ......
- service
  - impl
    - DiaryServiceImpl
    - LonerServiceImpl
    - MessageServiceImpl
    - ......
  - DiaryService
  - LonerService
  - MessageService
  - ......

### 通过bootstrap快速搭建前端页面

我的推荐是：

[BootStrap组件](https://v3.bootcss.com/components)

[BootStrap构建工具](https://www.runoob.com/try/bootstrap/layoutit/)

### 创建controller进行简单测试

- controller
    - CommunityController
    - DiaryController
    - LonerController
    - ......
    
### 一步步完善所有功能

......

## 上传至服务器

**部署环境为centos7.9**

**操作步骤如下：**

- [ ] 配置Java环境
- [ ] 配置mysql环境
- [ ] 生成，上传并运行JAR包项目

### 配置Java环境

#### 获取JDK

- 下载jdk压缩包： jdk-8u311-linux-x64.tar.gz
- 将jdk压缩包通过winsp上传至主机
- 解压缩jdk压缩包

  ```bash
  [root@VM-0-11-centos ~]# ls
  bin  jdk-8u311-linux-x64.tar.gz  passwd  var
  [root@VM-0-11-centos ~]# tar -zxvf jdk-8u311-linux-x64.tar.gz
  ...
  [root@VM-0-11-centos ~]# ls
  bin  jdk1.8.0_311  jdk-8u311-linux-x64.tar.gz  passwd  var
  ```
- 移动jdk到合适目录

  ```bash
  [root@VM-0-11-centos ~]# mkdir /usr/local/java
  [root@VM-0-11-centos ~]# mv ./jdk1.8.0_311 /usr/local/java
  ```
#### 配置环境变量

##### 编辑/etc/bashrc文件

也可以选择编辑/etc/profile。

```shell
[root@VM-0-11-centos ~]# vim /etc/bashrc # 进行全局配置

进行添加如下：

#JAVA
export JAVA_HOME=/usr/local/java/jdk1.8.0_311
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```

- 使配置文件生效：

```shell
source /etc/bashrc
```

### 配置mysql环境

#### yum方式安装mysql57

这里实验环境为centos7.9版本，且采用yum方式安装mysql57。

```shell
# 获取mysql80-community-release-el7-3.noarch.rpm软件包
[root@VM-0-11-centos ~]# wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
......
2022-01-02 12:07:24 (133 KB/s) - ‘mysql80-community-release-el7-3.noarch.rpm’ saved [26024/26024]

# 安装mysql80-community-release-el7-3.noarch.rpm软件包

[root@VM-0-11-centos ~]# rpm -Uvh mysql80-community-release-el7-3.noarch.rpm
......
Updating / installing...
1:mysql80-community-release-el7-3  ################################# [100%]

# 这时查看/etc/yum.repos.d可以看到如下：
[root@VM-0-11-centos ~]# ls  /etc/yum.repos.d | grep mysql
mysql-community.repo
mysql-community-source.repo

# 将mysql80换为mysql57
[root@VM-0-11-centos ~]# yum-config-manager --disable mysql80-community
[root@VM-0-11-centos ~]#  yum-config-manager --enable mysql57-community

# 查看是否切换成功：
[root@VM-0-11-centos ~]# yum repolist enabled | grep mysql
mysql-connectors-community/x86_64    MySQL Connectors Community              221
mysql-tools-community/x86_64         MySQL Tools Community                   135
mysql57-community/x86_64             MySQL 5.7 Community Server              544

# 安装mysql57
[root@VM-0-11-centos ~]# yum install mysql-community-server
......
Installed:
mysql-community-libs.x86_64 0:5.7.36-1.el7               mysql-community-libs-compat.x86_64 0:5.7.36-1.el7
mysql-community-server.x86_64 0:5.7.36-1.el7
......

```

#### 运行mysql

```bash
# 开启mysql服务
[root@VM-0-11-centos ~]# service mysqld start
Redirecting to /bin/systemctl start mysqld.service

# 查看mysql服务状态
[root@VM-0-11-centos ~]# systemctl status mysqld.service
● mysqld.service - MySQL Server
Loaded: loaded (/usr/lib/systemd/system/mysqld.service; enabled; vendor preset: disabled)
Active: active (running) since Sun 2022-01-02 12:21:47 CST; 11s ago
......
Jan 02 12:21:41 VM-0-11-centos systemd[1]: Starting MySQL Server...
Jan 02 12:21:47 VM-0-11-centos systemd[1]: Started MySQL Server.
```

#### 配置mysql

##### 设置root密码

```bash
# 获取初始mysql为root生成的默认密码并登录mysql
[root@VM-0-11-centos ~]# grep 'temporary password' /var/log/mysqld.log
2022-01-02T04:21:43.962084Z 1 [Note] A temporary password is generated for root@localhost: mkjhMuJBe3,%
# 则默认密码为mkjhMuJBe3,%

[root@VM-0-11-centos ~]# mysql -uroot -p
Enter password: # 输入默认密码
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 3
Server version: 5.7.36
...
mysql>

# 修改root账户登录密码
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY '658766@Jzh';
Query OK, 0 rows affected (0.00 sec)
```

##### 设置允许远程访问mysql

```bash
mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '658766@Jzh' WITH GRANT OPTION;
Query OK, 0 rows affected, 1 warning (0.00 sec)
mysql> FLUSH PRIVILEGES;
Query OK, 0 rows affected (0.00 sec)
```

##### 设置开机自启动mysql

```bash
[root@VM-0-11-centos ~]# systemctl enable mysqld
[root@VM-0-11-centos ~]# systemctl daemon-reload

```
##### 设置mysql默认编码为utf-8

```bash
# 查看原来编码
mysql> SHOW VARIABLES LIKE 'character%';
+--------------------------+----------------------------+
| Variable_name            | Value                      |
+--------------------------+----------------------------+
| character_set_client     | utf8                       |
| character_set_connection | utf8                       |
| character_set_database   | latin1                     |
| character_set_filesystem | binary                     |
| character_set_results    | utf8                       |
| character_set_server     | latin1                     |
| character_set_system     | utf8                       |
| character_sets_dir       | /usr/share/mysql/charsets/ |
+--------------------------+----------------------------+

# 编辑/etc/my.cnf文件
[root@VM-0-11-centos ~]# vim /etc/my.cnf

[mysqld]下新增：
character_set_server=utf8
init-connect='SET NAMES utf8'

# 重启mysql服务
[root@VM-0-11-centos ~]# systemctl restart mysqld.service

# 再查看编码

mysql> SHOW VARIABLES LIKE 'character%';
+--------------------------+----------------------------+
| Variable_name            | Value                      |
+--------------------------+----------------------------+
| character_set_client     | utf8                       |
| character_set_connection | utf8                       |
| character_set_database   | utf8                       |
| character_set_filesystem | binary                     |
| character_set_results    | utf8                       |
| character_set_server     | utf8                       |
| character_set_system     | utf8                       |
| character_sets_dir       | /usr/share/mysql/charsets/ |
+--------------------------+----------------------------+
8 rows in set (0.01 sec)
```

##### 建立数据库并新建表。

通过sql脚本快速建成。

### 生成，上传并运行JAR包项目

#### 上传JAR包项目

```java
mvn clean
mvn package
```

即可把项目打包为JAR包，再通过WINSP上传到服务器。

先新建一个app文件夹，放置项目所有内容：

```bash
mkdir /app
chmod 777 /app
```

#### 新建并编辑application配置文件

由于主机上的mysql密码与本地mysql密码不同，所以在JAR包同一个文件目录下新建application配置文件并指定新的密码，允许JAR项目时该application配置文件优先级是更高的。这里我还指定了用于linux下文件读取上传的目录路径。

```yaml
spring:
    datasource:
        password: ......
    resources:
        static-locations:
            - classpath:static/
            - file:/app/static/
```

```bash
[root@VM-0-11-centos app]# chmod -R 777 *
[root@VM-0-11-centos app]# ls
application.yaml  LonersHub-0.0.1-SNAPSHOT.jar static
```

#### 运行项目

##### 确保需要的端口开启

```bash
netstat -ntlp
```
##### 指定安全组规则

值得注意的一点是，在某云上购买的主机可能带有安全组，其优先级更高，所以想要开放8080端口，还得现在安全组那里先添加入站规则以放通8080端口。同样的，3306端口也一样要添加，否则同样是连接不上滴。

##### 部署运行JAR包

```java
[root@VM-0-11-centos app]# nohup java -jar LonersHub-0.0.1-SNAPSHOT.jar &
[1] 27108
```

#### 更新项目

首先删除原来的jar包并上传新的jar包。

然后杀死原本进程并运行jar包：

```bash
[root@VM-0-11-centos app]# ps -ef | grep java
root     13415     1  1 00:02 ?        00:00:18 java -jar LonersHub-0.0.1-SNAPSHOT.jar
root     16053 15504  0 00:21 pts/1    00:00:00 grep --color=auto java

[root@VM-0-11-centos app]# kill -9 12415

[root@VM-0-11-centos app]# nohup java -jar LonersHub-0.0.1-SNAPSHOT.jar &
```

脚本实现：

```bash
# 编写脚本
[root@VM-0-11-centos app]# touch restart.sh
[root@VM-0-11-centos app]# vim restart.sh

kill -9 `ps -ef | grep LonersHub | grep -v color | awk '{print $2}'`

nohup java -jar LonersHub-0.0.1-SNAPSHOT.jar &

# 运行脚本
[root@VM-0-11-centos app]# sh restart.sh
```






