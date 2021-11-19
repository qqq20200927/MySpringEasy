package com.spring.core.service;

import com.spring.core.dao.UserDao;

public class UserService {
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void print(){
        System.out.println("经过UserDao的print方法" + userDao.print());
    }
}
