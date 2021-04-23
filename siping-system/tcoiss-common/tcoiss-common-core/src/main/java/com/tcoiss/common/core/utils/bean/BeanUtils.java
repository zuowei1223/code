package com.tcoiss.common.core.utils.bean;

import com.tcoiss.common.core.annotation.CopyField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean 工具类
 * 
 * @author tcoiss
 */
public class BeanUtils extends org.springframework.beans.BeanUtils
{

    private static Logger logger = LoggerFactory.getLogger(BeanUtils.class);
    /** Bean方法名中属性名开始的下标 */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /** * 匹配getter方法的正则表达式 */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /** * 匹配setter方法的正则表达式 */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean属性复制工具方法。
     * 
     * @param dest 目标对象
     * @param src 源对象
     */
    public static void copyBeanProp(Object dest, Object src)
    {
        try
        {
            copyProperties(src, dest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象的setter方法。
     * 
     * @param obj 对象
     * @return 对象的setter方法列表
     */
    public static List<Method> getSetterMethods(Object obj)
    {
        // setter方法列表
        List<Method> setterMethods = new ArrayList<Method>();

        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();

        // 查找setter方法

        for (Method method : methods)
        {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1))
            {
                setterMethods.add(method);
            }
        }
        // 返回setter方法列表
        return setterMethods;
    }

    /**
     * 获取对象的getter方法。
     * 
     * @param obj 对象
     * @return 对象的getter方法列表
     */

    public static List<Method> getGetterMethods(Object obj)
    {
        // getter方法列表
        List<Method> getterMethods = new ArrayList<Method>();
        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();
        // 查找getter方法
        for (Method method : methods)
        {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0))
            {
                getterMethods.add(method);
            }
        }
        // 返回getter方法列表
        return getterMethods;
    }

    /**
     * 检查Bean方法名中的属性名是否相等。<br>
     * 如getName()和setName()属性名一样，getName()和setAge()属性名不一样。
     * 
     * @param m1 方法名1
     * @param m2 方法名2
     * @return 属性名一样返回true，否则返回false
     */

    public static boolean isMethodPropEquals(String m1, String m2)
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }


    /**
     * <h3>拷贝一个对象的属性至另一个对象</h3>
     * <p>
     *     支持两个对象之间不同属性名称进行拷贝，使用注解{@link CopyField}
     * </p>
     * @param originBean  源对象 使用注解{@link CopyField#targetName()}
     * @param targetBean  目标对象 使用注解{@link CopyField#originName()}
     */
    public static void copyBean(Object originBean, Object targetBean) {
        Map<String, Object> originFieldKeyWithValueMap = new HashMap<>(16);
        PropertyDescriptor propertyDescriptor = null;
        //生成源bean的属性及其值的字典
        generateOriginFieldWithValue(propertyDescriptor, originBean, originFieldKeyWithValueMap, originBean.getClass());
        //设置目标bean的属性值
        settingTargetFieldWithValue(propertyDescriptor, targetBean, originFieldKeyWithValueMap, targetBean.getClass());

    }

    /**
     * 生成需要被拷贝的属性字典 属性-属性值<br/>
     * 递归取父类属性值
     * @param propertyDescriptor  属性描述器，可以获取bean中的属性及方法
     * @param originBean 待拷贝的bean
     * @param originFieldKeyWithValueMap 存放待拷贝的属性和属性值
     * @param beanClass 待拷贝的class[可能是超类的class]
     */
    private static void generateOriginFieldWithValue(PropertyDescriptor propertyDescriptor, Object originBean, Map<String, Object> originFieldKeyWithValueMap, Class<?> beanClass) {
        /**如果不存在超类，那么跳出循环*/
        if (beanClass.getSuperclass() == null) {
            return;
        }
        Field[] originFieldList = beanClass.getDeclaredFields();
        for (Field field : originFieldList) {
            try {
                /*获取属性上的注解。如果不存在，使用属性名，如果存在使用注解名*/
                CopyField annotation = field.getAnnotation(CopyField.class);
                String targetName = "";
                if (annotation != null) {
                    targetName = annotation.targetName();
                } else {
                    targetName = field.getName();
                }
                //初始化
                propertyDescriptor = new PropertyDescriptor(field.getName(), beanClass);
                //获取当前属性的get方法
                Method method = propertyDescriptor.getReadMethod();
                //设置值
                Object value = method.invoke(originBean);
                //设置值
                originFieldKeyWithValueMap.put(targetName, value);
            } catch (IntrospectionException e) {
                logger.warn("【源对象】异常:" + field.getName() + "不存在对应的get方法，无法参与拷贝！");
            } catch (IllegalAccessException e) {
                logger.warn("【源对象】异常:" + field.getName() + "的get方法执行失败！");
            } catch (InvocationTargetException e) {
                logger.warn("【源对象】异常:" + field.getName() + "的get方法执行失败！");
            }
        }
        //生成超类 属性-value
        generateOriginFieldWithValue(propertyDescriptor, originBean, originFieldKeyWithValueMap, beanClass.getSuperclass());
    }

    /**
     *
     * @param propertyDescriptor 属性描述器，获取当前传入属性的（getter/setter）方法
     * @param targetBean 目标容器bean
     * @param originFieldKeyWithValueMap 待拷贝的属性和属性值
     * @param beanClass 待设置的class[可能是超类的class]
     */
    private static void settingTargetFieldWithValue(PropertyDescriptor propertyDescriptor, Object targetBean, Map<String, Object> originFieldKeyWithValueMap, Class<?> beanClass) {
        /**如果不存在超类，那么跳出循环*/
        if (beanClass.getSuperclass() == null) {
            return;
        }
        Field[] targetFieldList = beanClass.getDeclaredFields();
        for (Field field : targetFieldList) {
            try {
                /*获取属性上的注解。如果不存在，使用属性名，如果存在使用注解名*/
                CopyField annotation = field.getAnnotation(CopyField.class);
                String originName = "";
                if (annotation != null) {
                    originName = annotation.originName();
                } else {
                    originName = field.getName();
                }
                //初始化当前属性的描述器
                propertyDescriptor = new PropertyDescriptor(field.getName(), beanClass);
                //获取当前属性的set方法
                Method method = propertyDescriptor.getWriteMethod();
                method.invoke(targetBean, originFieldKeyWithValueMap.get(originName));
            } catch (IntrospectionException e) {
                logger.warn("【目标对象】异常:" + field.getName() + "不存在对应的set方法，无法参与拷贝！");
            } catch (IllegalAccessException e) {
                logger.warn("【目标对象】异常:" + field.getName() + "的set方法执行失败！");
            } catch (InvocationTargetException e) {
                logger.warn("【目标对象】异常:" + field.getName() + "的set方法执行失败！");
            }
        }
        //设置超类属性
        settingTargetFieldWithValue(propertyDescriptor, targetBean, originFieldKeyWithValueMap, beanClass.getSuperclass());
    }

}
