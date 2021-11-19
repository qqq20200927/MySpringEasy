package com.spring.core.config;

import java.util.HashMap;
import java.util.Map;

//xml配置文件解析对应的实体类
public class EntityBean {
    private String id;
    private String classname;
    //是否为单例模式
    private String scope = "singleton";

    //proerties标签中 <name , property>标签类
    private Map<String, Property> properties;

    @Override
    public String toString() {
        return "EntityBean{" +
                "id='" + id + '\'' +
                ", classname='" + classname + '\'' +
                ", scope='" + scope + '\'' +
                ", properties=" + properties +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public EntityBean() {
        properties = new HashMap<String, Property>();
    }

    public EntityBean(String id, String classname, String scope, Map<String, Property> properties) {
        this.id = id;
        this.classname = classname;
        this.scope = scope;
        this.properties = properties;
    }
}
