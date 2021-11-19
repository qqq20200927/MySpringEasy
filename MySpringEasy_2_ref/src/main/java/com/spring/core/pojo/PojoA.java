package com.spring.core.pojo;

public class PojoA {
    private PojoB pojoB;

    public void printA(){
        System.out.println("i am A");
    }

    public void setPojoB(PojoB pojoB) {
        this.pojoB = pojoB;
    }
}
