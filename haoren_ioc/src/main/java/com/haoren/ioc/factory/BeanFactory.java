package com.haoren.ioc.factory;

import com.haoren.ioc.annotation.Component;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BeanFactory {

    private static final Map<String, Object> map = new ConcurrentHashMap<>();

    static {
        InputStream resourceAsStream =
                BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> list = rootElement.selectNodes("//bean");
            for (Element element : list) {
                String id = element.attributeValue("id");
                String clz = element.attributeValue("class");
                Class<?> aClass = Class.forName(clz);
                Object o = aClass.newInstance();
                map.put(id, o);
            }
            List<Element> propertyNodes = rootElement.selectNodes("//property");
            for (Element element : propertyNodes) {
                String name = element.attributeValue("name");
                String ref = element.attributeValue("ref");
                String parentId = element.getParent().attributeValue("id");
                Object parentObject = map.get(parentId);
                Method[] methods = parentObject.getClass().getMethods();
                for (Method method : methods) {
                    if (("set" + name).equals(method.getName())) {
                        Object refObject = map.get(ref);
                        method.invoke(parentObject, refObject);
                    }
                }
                map.put(parentId, parentObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getBean(String id) {
        return map.get(id);
    }
}
