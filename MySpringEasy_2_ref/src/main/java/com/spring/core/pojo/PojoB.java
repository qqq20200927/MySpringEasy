package com.spring.core.pojo;

public class PojoB {
    private PojoC pojoC;

    public void printB(){
        System.out.println("i am PojoB");
    }

    public void setPojoC(PojoC pojoC) {
        this.pojoC = pojoC;
    }
}
