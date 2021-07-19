package org.xyz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class InstanceKlass extends Klass {
    // 魔数       u4      0xCAFEBABE
    private byte[] magic = new byte[4];
    // 次版本号     u2
    private byte[] minionVersion = new byte[2];
    // 主版本号     u2
    private byte[] majorVersion = new byte[2];

    // 常量池
    private ConstantPool constantPool;

    // 类或接口的访问权限及属性，通过多个访问权限和属性 与操作 计算出来        u2
    private int accessFlags;
    // 常量池中的类索引  u2
    private int thisClass;
    // 常量池中的父类索引    u2
    private int superClass;

    // 实现的接口数量  u2
    private int interfacesCount;
    // 实现的接口列表
    private List<InterfaceInfo> interfaces;

    // 成员字段数量   u2
    private int fieldsCount;
    // 成员字段详情表
    private List<FiledInfo> fields;

    // 成员方法数量   u2
    private int methodsCount;
    // 成员方法详情表
    private List<MethodInfo> methods;

    // 类属性数量    u2
    private int attributesCount;
    // 类属性详情表
    private Map<String, Attribute> attributes;

    public InstanceKlass() {
        constantPool = new ConstantPool(this);
    }

    /**
     * 初始化该类实现的接口列表
     * */
    public void initInterfaceContainer() {
        interfaces = new ArrayList<>(interfacesCount);
    }

    /**
     * 初始化成员字段列表
     * */
    public void initFieldContainer() {
        fields = new ArrayList<>(fieldsCount);
    }

    /**
     * 初始化成员方法列表
     * */
    public void initMethodContainer() {
        methods = new ArrayList<>(methodsCount);
    }

    /**
     * 初始化类属性列表
     * */
    public void initAttributeContainer() {
        attributes = new HashMap<>(attributesCount);
    }
}
