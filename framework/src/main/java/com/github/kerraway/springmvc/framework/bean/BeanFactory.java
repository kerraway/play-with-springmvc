package com.github.kerraway.springmvc.framework.bean;

import com.github.kerraway.springmvc.framework.web.mvc.Controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kerraway
 * @date 2019/09/18
 */
public class BeanFactory {

    private static volatile BeanFactory beanFactory;

    private final Map<Class<?>, Object> beanMap;

    public static BeanFactory newInstance() {
        if (beanFactory == null) {
            synchronized (BeanFactory.class) {
                if (beanFactory == null) {
                    beanFactory = new BeanFactory();
                }
            }
        }
        return beanFactory;
    }

    private BeanFactory() {
        this.beanMap = new ConcurrentHashMap<>(256);
    }

    /**
     * 获取 bean 实例
     *
     * @param clazz bean 类型
     * @param <T>   范型
     * @return bean 实例
     */
    public <T> T getBean(Class<T> clazz) {
        return (T) beanMap.get(clazz);
    }

    /**
     * 初始化 bean 工厂
     *
     * @param classes 一些类
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void init(List<Class<?>> classes) throws InstantiationException, IllegalAccessException {
        classes = new ArrayList<>(classes);
        while (!classes.isEmpty()) {
            boolean hasRemoved = false;
            Iterator<Class<?>> iterator = classes.iterator();
            while (iterator.hasNext()) {
                Class<?> clazz = iterator.next();
                //不用实例化或者成功实例化，移除当前类
                if (doCreateBean(clazz)) {
                    iterator.remove();
                    hasRemoved = true;
                }
            }
            //存在循环依赖，导致有些类无法实例化
            if (!hasRemoved) {
                // TODO: 2019/9/18 specific exception
                throw new RuntimeException(String.format("Circular dependencies: %s.", classes));
            }
        }
    }

    /**
     * 创建 bean 实例
     *
     * @param clazz bean 类型
     * @return 不用实例化或者成功实例化，返回 true
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private boolean doCreateBean(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        //没有加注解的类，不用实例化
        if (!clazz.isAnnotationPresent(Bean.class)
                && !clazz.isAnnotationPresent(Controller.class)) {
            return true;
        }
        Object bean = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();
                Object fieldValue = getBean(fieldType);
                //有依赖没有实例化，暂时不能实例化
                if (fieldValue == null) {
                    return false;
                }
                field.setAccessible(true);
                field.set(bean, fieldValue);
            }
        }
        beanMap.put(clazz, bean);
        return true;
    }

}
