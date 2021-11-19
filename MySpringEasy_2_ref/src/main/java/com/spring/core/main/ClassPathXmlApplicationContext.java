package com.spring.core.main;

import com.spring.core.config.EntityBean;
import com.spring.core.config.Property;
import org.apache.commons.beanutils.BeanUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassPathXmlApplicationContext implements ApplicationContext {

    private String configFileName;
    //解析xml的map  , <id , entitybean>
    private Map<String,EntityBean> beansInfo ;

    //反射创建的map , <id , entity>
    //一级缓存
    private Map<String,Object> results = new HashMap<>();

    //二级缓存
    private Map<String,Object> earlyResults = new HashMap<>();

    //标签类对应的Map
    private Map<String , Property> propertyMap = new HashMap<>();

    //半成品的引用对象
    private Object refObj = new Object();

    //交由ioc管理的对象
    private Object result =null;

    public ClassPathXmlApplicationContext() {
        this.configFileName = "applicationContext.xml";
    }

    public ClassPathXmlApplicationContext(String configFileName) {
        this.configFileName = configFileName;
        try {
            //解析xml得到的Map
            beansInfo = springXmlParser();

            //遍历该map
            for (Map.Entry<String, EntityBean> stringEntityBeanEntry : beansInfo.entrySet()) {
                String key = stringEntityBeanEntry.getKey();
                EntityBean value = stringEntityBeanEntry.getValue();

                Object bean = results.get(key);

                //如果是单例模式,则在本环节(加载配置文件)就创建对象
                if ("singleton".equals(value.getScope()) && bean == null){
                    results = getIOC(beansInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Map<String, EntityBean> springXmlParser() throws Exception{
        //创建解读器
        XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
        //读取xml配置文件
        InputStream in = ClassPathXmlApplicationContext.class.getClassLoader().getResourceAsStream(this.configFileName);
        pullParser.setInput(in,"utf-8");
        //基于事件机制编写xml解析，并且组装目标对象
        int everyType = pullParser.getEventType();
        Map<String ,EntityBean> beans = null;
        EntityBean bean = null;
        while (everyType != XmlPullParser.END_DOCUMENT){
            switch (everyType){
                case XmlPullParser.START_DOCUMENT:
                    beans = new HashMap<String, EntityBean>();
                    break;
                case XmlPullParser.START_TAG:
                    if ("bean".equals(pullParser.getName())){
                        bean = new EntityBean();
                        bean.setId(pullParser.getAttributeValue(null,"id"));
                        bean.setClassname(pullParser.getAttributeValue(null,"class"));
                        bean.setScope(pullParser.getAttributeValue(null , "scope"));
                    }
                    if ("property".equals(pullParser.getName())){
                        String attrName = pullParser.getAttributeValue(null,"name");
                        String attrVal = pullParser.getAttributeValue(null,"value");
                        String attrRef = pullParser.getAttributeValue(null, "ref");
                        Property property = new Property();
                        property.setName(attrName);
                        property.setValue(attrVal);
                        property.setRef(attrRef);

                        bean.getProperties().put(attrName,property);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("bean".equals(pullParser.getName())){
                        beans.put(bean.getId(),bean);
                    }
                    break;
            }
            everyType = pullParser.next();
        }
        return beans;
    }

    //根据xml解析出来的map,反射创建entity对象
    public Map<String,Object> getIOC(Map<String,EntityBean> beansInfo) throws Exception{
        //创建一个容器
        Map<String,Object> results = new HashMap<String, Object>();

        for (Map.Entry<String,EntityBean> beanInfo : beansInfo.entrySet()){
            //beanInfo是entrybean(k-v)
            //bean是entrybean对象
            String resultId = beanInfo.getKey();
            Object obj = creatBean(beanInfo.getValue());

            results.put(resultId,obj);
        }
        return results;
    }

    //创建bean
    private  Object creatBean(EntityBean entityBean) throws InvocationTargetException, IllegalAccessException {

        //获取entrybean的全限定名
        String classname = entityBean.getClassname();
        //拿到properties标签的内容
        Map<String, Property> properties = entityBean.getProperties();
        //反射-----输入字符串，返回对象
        Class clazz = null;
        Object obj = null;
        try {
            clazz = Class.forName(classname);
            obj = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //earlyResults.put(entityBean.getId() , obj);

        //遍历每行proerties标签
        for (Map.Entry<String,Property> property : properties.entrySet()){
            //name-value
            String propName = property.getKey();
            Property propValue = property.getValue();

            //引用byType
            if (propValue.getRef() != null){
                //先获取bean对象,看ioc容器中是否有对象,没有则需要递归创建对象
                Object existBean = results.get(propValue.getRef());

                if (existBean == null){
                    //如果现在pojoA ref pojoB , 那么我们先拿到 pojoA的name
                    //这样使得pojoB引用pojoA时,容器已经存在了pojoA,这样就能避免循环引用
                    String ref = entityBean.getId();

                    //如果map中没有,那么便添加一个半成品进入map,此时的obj没有具体的属性,仅仅通过反射创建了对象
                    if (!results.containsKey(ref)){
                        results.put(ref , obj);
                    }

                    existBean = creatBean(beansInfo.get(propValue.getRef()));

                    //创建对象后,只有是单例模式才会放入results容器中
                    if ("singleton".equals(beansInfo.get(propValue.getRef()).getScope())){
                        results.put(propValue.getRef() , existBean);
                    }
                }

                try {
                    // setMethod.invoke(obj, existBean);
                    //通过BeanUtils为obj设置属性
                    BeanUtils.setProperty(obj, propValue.getName(), existBean);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("bean的属性" + propValue.getName()
                            + "没有对应的set方法");
                }

            }
            //byName
            if(propValue.getName() != null) {
                //拼接字符串set
                StringBuilder builder = new StringBuilder("set");

                //取的id,name的第一个字符并大写,之后把剩下的部分补上
                builder.append(propName.substring(0,1).toUpperCase());
                builder.append(propName.substring(1));
                /**
                 * 经历了上面三方代码,我们就拼出了一个字符串
                 * id --> setId   name->setName
                 * 这里正好对应entity实体类中的set方法
                 */

                String setterMethodName = builder.toString();

                //返回field对象
                //返回Method对象
                /**
                 * 参数  1.  方法名 , 上面拼接出的方法
                 *       2.  field是什么类型(八大基本类型)
                 */
                Field field = null;
                Method setMethod = null;
                try {
                    field = clazz.getDeclaredField(propName);
                    setMethod = clazz.getDeclaredMethod(setterMethodName,field.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //根据实体类的字段属性找对应
                if ("int".equals(field.getType().getName())){
                    setMethod.invoke(obj,Integer.parseInt(propValue.getValue()));
                }else if ("java.lang.String".equals(field.getType().getName())){
                    setMethod.invoke(obj,propValue.getValue());
                }
            }

        }
        return obj;
    }


    @Override
    public Object getBean(String beanId) {

        try {
            //解析xml文件得到对应的配置bean
            EntityBean entityBean = beansInfo.get(beanId);

            //如果scope为prototype,证明是多例模式,则需要重新创建一个对象并返回
            if ("prototype".equals(entityBean.getScope())){
                results = getIOC(beansInfo);
                Object result1 = results.get(beanId);
                return result1;
            }

            result = results.get(beanId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
