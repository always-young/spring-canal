create database study;

create table user(

    id int not null,

    username varchar(30),

    primary key(id)

);

##测试插入
insert into user values(1,'kevin');

##测试更新
update user set username = 'kevin liu' wher id = 1;

##测试删除
delete from user where id = 1;