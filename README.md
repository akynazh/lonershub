# LonersHub

## 项目简介

### 功能介绍

一个简洁的视频交流社区，用户可以自己上传一部影片，并预约一个合适的时间，邀请其它 Loner 一起观赏电影，并对影片进行愉快的评论。

同时支持通过日记本记录生活。

### 工具介绍

- 后端基于 SpringBoot，Mybatis-Plus，MySQL，Redis 等构建。
- 前端采用 Bootstrap，Thymeleaf，Ajax 等构建。

## 部署流程

**部署环境为 Centos7.9。**

### 配置Java环境

```bash
tar -zxvf jdk-8u311-linux-x64.tar.gz
mkdir /usr/local/java
mv ./jdk1.8.0_311 /usr/local/java


vim /etc/bashrc
#JAVA
export JAVA_HOME=/usr/local/java/jdk1.8.0_311
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar


source /etc/bashrc
```

### 配置 MySQL 环境

```shell
wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
rpm -Uvh mysql80-community-release-el7-3.noarch.rpm
# 这时查看 /etc/yum.repos.d 可以看到如下：
# mysql-community.repo
# mysql-community-source.repo

# 将 mysql80 换为 mysql57
yum-config-manager --disable mysql80-community
yum-config-manager --enable mysql57-community

# 查看是否切换成功
yum repolist enabled | grep mysql

# 安装 mysql57
yum install mysql-community-server

# 开启 mysql 服务
systemctl start mysqld

# 获取初始 mysql 为 root 生成的默认密码并登录 mysql
grep 'temporary password' /var/log/mysqld.log
mysql -uroot -p
Enter password: # 输入默认密码

# 修改root账户登录密码
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'xxxxxx';
Query OK, 0 rows affected (0.00 sec)

# 设置允许远程访问mysql
mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'xxxxxx' WITH GRANT OPTION;
Query OK, 0 rows affected, 1 warning (0.00 sec)
mysql> FLUSH PRIVILEGES;
Query OK, 0 rows affected (0.00 sec)

# 设置开机自启动mysql
systemctl enable mysqld
systemctl daemon-reload # 重新加载某个服务的配置文件

# 设置mysql默认编码为utf-8
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
# 编辑 /etc/my.cnf 文件
vim /etc/my.cnf

[mysqld]下新增：
character_set_server=utf8
init-connect='SET NAMES utf8'

# 重启 mysql 服务
systemctl restart mysqld.service

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

# 初始化数据库
运行 lonersHub.sql
```

### 配置 Redis 环境

```bash
yum install redis
systemctl start redis

# 设置开机自启动redis
systemctl enable redis
```

### 运行 JAR 包项目

首先生成并上传 Jar 包：

```sh
# 生成 jar 包
mvn clean package
# 上传 jar 包到 /app 下
mkdir /app
chmod 777 /app
```

接着新建并编辑 application 配置文件，由于主机上的 mysql 密码与本地 mysql 密码不同，所以在 JAR 包同一个文件目录下新建 application 配置文件并指定新的密码，运行 JAR 项目时该 application 配置文件优先级是更高的。这里我还指定了用于 linux 下文件读取上传的目录路径。

```yaml
spring:
    datasource:
        password: xxxxxx
    resources:
        static-locations:
            - classpath:static/
            - file:/app/static/
```

最后运行 JAR 包：

```shell
# ls
# application.yaml  LonersHub-0.0.1-SNAPSHOT.jar static

nohup java -jar LonersHub-0.0.1-SNAPSHOT.jar &
[1] 27108
```

### 更新 JAR 包项目

首先删除原来的 jar 包并上传新的 jar 包，然后 KILL 原本进程并运行 jar 包：

```bash
ps -ef | grep java
# root     13415     1  1 00:02 ?        00:00:18 java -jar LonersHub-0.0.1-SNAPSHOT.jar
# root     16053 15504  0 00:21 pts/1    00:00:00 grep --color=auto java

kill -9 12415
nohup java -jar LonersHub-0.0.1-SNAPSHOT.jar &
```

脚本实现：

```bash
kill -9 `ps -ef | grep LonersHub | grep -v color | awk '{print $2}'`
nohup java -jar LonersHub-0.0.1-SNAPSHOT.jar &
```
