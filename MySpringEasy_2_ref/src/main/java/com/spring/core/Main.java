package com.spring.core;

import com.spring.core.entity.User;
import com.spring.core.main.ApplicationContext;
import com.spring.core.main.ClassPathXmlApplicationContext;
import com.spring.core.pojo.*;
import com.spring.core.service.UserService;

import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {

        //读取配置文件创建对象,可以使用对应的接口对象
        ApplicationContext app = new ClassPathXmlApplicationContext("applicationContext.xml");

        //根据name的注入
//        //多例
//        User user = (User)app.getBean("user");
//        User user2 = (User)app.getBean("user");
//
//        //单例
//        User user3 = (User)app.getBean("user2");
//        User user4 = (User)app.getBean("user2");
//
//        System.out.println(user.getId() + "--" + user.getName() + "--" + user.getAge() );
//        System.out.println(user2.getId() + "--" + user2.getName() + "--" + user2.getAge() );
//        System.out.println(user3.getId() + "--" + user3.getName() + "--" + user3.getAge() );
//        System.out.println(user4.getId() + "--" + user4.getName() + "--" + user4.getAge() );
//        System.out.println("------------------");
//        System.out.println(user);
//        System.out.println(user2);
//        System.out.println(user3);
//        System.out.println(user4);
//        System.out.println("------------------");
//        System.out.println(user == user2);
//        System.out.println(user3 == user4);


        //引用
        UserService userService = (UserService) app.getBean("userService");
        userService.print();

        //循环引用
//        PojoA pojoA = (PojoA) app.getBean("pojoA");
//        PojoB pojoB = (PojoB) app.getBean("pojoB");
//        PojoC pojoC = (PojoC) app.getBean("pojoC");
//        pojoA.printA();
//        pojoB.printB();
//        pojoC.printC();
        


    }


}
