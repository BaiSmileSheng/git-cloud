package com.cloud.order.util;

import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @auther cs
 * @date 2020/8/28 19:02
 * @description
 */
public class DynamicEnumOrderUtils {
    private static ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

    private static void setFailsafeFieldValue(Field field, Object target,
                                              Object value) throws NoSuchFieldException, IllegalAccessException {
        //令这个字段(field)变成可访问
        field.setAccessible(true);
        //接下来通过反射机制将字段的修饰符由‘最终的’改为’可访问的‘
        //getDeclaredField(name) 返回一个 Field 对象，该对象反映此 Class 对象所表示的类或接口的指定已声明字段。
        // name 参数是一个 String，它指定所需字段的简称。
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        //PUBLIC: 1、PRIVATE: 2、PROTECTED: 4、STATIC: 8、FINAL: 16、SYNCHRONIZED: 32、
        //VOLATILE: 64、TRANSIENT: 128、NATIVE: 256、INTERFACE: 512、ABSTRACT: 1024、STRICT: 2048
        int modifiers = modifiersField.getInt(field);
        //清空修饰符int中的最后一位
        //&:位与运算符，只有两个操作数都是true，结果才是true; ~：位非运算符：如果位为0，结果是1，如果位为1，结果是0.
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(field, modifiers);
        FieldAccessor fa = reflectionFactory.newFieldAccessor(field, false);
        fa.set(target, value);
    }

    private static void blankField(Class<?> enumClass,
                                   String fieldName) throws NoSuchFieldException, IllegalAccessException {
        for (Field field : Class.class.getDeclaredFields()) {
            if (field.getName().contains(fieldName)) {
                AccessibleObject.setAccessible(new Field[] { field }, true);
                setFailsafeFieldValue(field, enumClass, null);
                break;
            }
        }
    }

    /**
     * 清除枚举类缓存
     * @param enumClass                 枚举类
     * @throws NoSuchFieldException     找不到特定方法时引发的异常
     * @throws IllegalAccessException   无访问权限时引发的异常
     */
    private static void cleanEnumCache(Class<?> enumClass) throws NoSuchFieldException, IllegalAccessException {
        blankField(enumClass, "enumConstantDirectory"); // Sun (Oracle?!?) JDK 1.5/6
        blankField(enumClass, "enumConstants"); // IBM JDK
    }

    /**
     * 获取构造函数访问器
     * @param enumClass                 枚举类
     * @param additionalParameterTypes  附加参数类型
     * @throws NoSuchMethodException    找不到特定方法时引发的异常
     */
    private static ConstructorAccessor getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes)
            throws NoSuchMethodException {
        Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;
        System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
        return reflectionFactory.newConstructorAccessor(enumClass.getDeclaredConstructor(parameterTypes));
    }

    /**
     * 创建新的枚举类
     * @param enumClass         要新增的枚举的类
     * @param value             要新增的枚举名称(EnumName)
     * @param ordinal           原数组的个数
     * @param additionalTypes   要新增的枚举类型(例：int.class,String.class,int.class)
     * @param additionalValues  要新增的枚举值(例：444,"DDD",1)
     */
    private static Object makeEnum(Class<?> enumClass, String value, int ordinal, Class<?>[] additionalTypes,
                                   Object[] additionalValues) throws Exception {
        Object[] parms = new Object[additionalValues.length + 2];
        parms[0] = value;   //要新增的枚举名称(EnumName)
        parms[1] = ordinal; //原数组的个数
        //将原数组additionalValues从下标为0到长度为additionalValues.length的数据复制一份放到目标数组从下标为2的位置，
        //从而实现在目标数组的某下标之后拼接原数组某段数据
        System.arraycopy(additionalValues, 0, parms, 2, additionalValues.length);
        return enumClass.cast(getConstructorAccessor(enumClass, additionalTypes).newInstance(parms));
    }

    /**
     * 将枚举实例添加到作为参数给定的枚举类
     *
     * @param <T>               枚举的类型（隐式）
     * @param enumType          要修改的枚举的类
     */
    private static <T extends Enum<?>> Field valuesFieldDeal(Class<T> enumType) {
        //1.健全性检查
        if (!Enum.class.isAssignableFrom(enumType)) {
            throw new RuntimeException("class " + enumType + " is not an instance of Enum");
        }
        //2.查找枚举类中的"$VALUES"holder并获取以前的枚举实例
        Field valuesField = null;
        Field[] fields = enumType.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().contains("$VALUES")) {
                valuesField = field;
                break;
            }
        }
        //令这个字段(field)变成可访问
        AccessibleObject.setAccessible(new Field[] { valuesField }, true);
        return valuesField;
    }

    /**
     * 将枚举实例添加到作为参数给定的枚举类
     *
     * @param <T>               枚举的类型（隐式）
     * @param enumType          要修改的枚举的类
     * @param enumName          要添加到类中的新枚举实例的名称。
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> void removeEnum(Class<T> enumType, String enumName) {
        Field valuesField = valuesFieldDeal(enumType);
        try {
            //3.复制
            assert valuesField != null;
            T[] previousValues = (T[]) valuesField.get(enumType);
            List<T> values = new ArrayList<T>(Arrays.asList(previousValues));
            List<T> valueList = remove(values, enumName);
            //5.设置新值字段
            setFailsafeFieldValue(valuesField, null, valueList.toArray((T[]) Array.newInstance(enumType, 0)));
            //6.清除枚举类缓存
            cleanEnumCache(enumType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将枚举实例添加到作为参数给定的枚举类
     *
     * @param <T>               枚举的类型（隐式）
     * @param enumType          要修改的枚举的类
     * @param enumName          要添加到类中的新枚举实例的名称。
     * @param additionalTypes   附加类型
     * @param additionalValues  附加值。
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> void addEnum(Class<T> enumType, String enumName,
                                                   Class<?>[] additionalTypes, Object[] additionalValues) {
        Field valuesField = valuesFieldDeal(enumType);
        try {
            //3.复制
            assert valuesField != null;
            T[] previousValues = (T[]) valuesField.get(enumType);
            List<T> values = new ArrayList<T>(Arrays.asList(previousValues));
            removeIfContains(values, enumName);
            //创建新的枚举类
            T newValue = (T) makeEnum(enumType, enumName, values.size(), additionalTypes, additionalValues);
            //4.添加新值
            values.add(newValue);
            //5.设置新值字段
            setFailsafeFieldValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumType, 0)));
            //6.清除枚举类缓存
            cleanEnumCache(enumType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    //判断是否已经存在
    private static <T extends Enum<?>> boolean removeIfContains(List<T> values, String enumName) {
        boolean flag = values.stream().anyMatch(value -> enumName.equals(value.name()));
        if (flag) {
            values.removeIf(value -> {
                return enumName.equals(value.name());
            });
        }
        return flag;
    }

    /**
     * 判断是否已经存在，如果存在，则删除(实际是将不匹配的数据放到新的list中)
     * @param values    要筛选的数组
     * @param enumName  要筛选的目标枚举名称
     * @param <T>       枚举的类型（隐式）
     * @return          筛选后的list
     */
    private static <T extends Enum<?>> List<T> remove(List<T> values, String enumName) {
        List<T> list = new ArrayList<>();
        values.forEach(value -> {if (!value.name().equals(enumName)) {list.add(value);}});
        return list;
    }
}
