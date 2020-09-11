使用方式见spring-canal-sample模块
1. application.yml中配置canal的连接信息

2. 新建对应表的处理类 比如监听User表的变化
   加上`@CanalHandler`注解 代码如下
```java
@CanalHandler(tableName = "user",doClass = User.class)
public class UserService {
      @InsertOption
       public void insert(User user) {
           System.out.println(user);
       }
   
       @UpdateOption
       public void update(User user) {
           System.out.println(user);
       }
   
       @UpdateOption
       public void update2(User user) {
           System.out.println(user);
       }
   
       @DeleteOption
       public void delete(User user) {
           System.out.println(user);
       }
   
       @CanalOption(method = CanalEntry.EventType.DELETE)
       public void delete2(User user){
           System.out.println(user);
       }
}
```


@CanalHandler(tableName = "user",doClass = User.class)
@CanalHandler中配置监听的表名以及对应的实体类

在类中定义方法,使用@InsertOption, @UpdateOption, @DeleteOption
或者@CanalOption(method = CanalEntry.EventType.DELETE)指定监听的操作类型
监听方法必须是单入参并且入参只能是对应的数据库实体类
针对同一个操作，比如更新，可以配置多个监听方法。

