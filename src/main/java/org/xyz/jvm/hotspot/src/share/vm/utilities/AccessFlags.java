package org.xyz.jvm.hotspot.src.share.vm.utilities;

import lombok.Data;

/**
 * 类、字段、方法的访问权限
 * */
@Data
public class AccessFlags {
    private int flag;

    public AccessFlags(int flag) {
        this.flag = flag;
    }

    public boolean isStatic() {
        return (flag & BasicType.JVM_ACC_STATIC) != 0;
    }

}
