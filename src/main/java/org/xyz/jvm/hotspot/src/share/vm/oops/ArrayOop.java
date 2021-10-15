package org.xyz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

@Data
public class ArrayOop {
    // 数组元素类型
    private int type;

    // 如果是引用类型数组，数组元素对应的类名
    private String referenceName;

    // 数组大小
    private int size;

    // 数组维度
    private int dimension;

    private Object[] data;

    public Object get(int index) {
        if (index < 0 || index > size) {
            throw new ArrayIndexOutOfBoundsException("数组访问越界");
        }
        return data[index];
    }

    public void set(int index, Object object) {
        if (index < 0 || index > size) {
            throw new ArrayIndexOutOfBoundsException("数组访问越界");
        }
        data[index] = object;
    }

    public ArrayOop(int type, int size) {
        this.type = type;
        this.size = size;
        this.dimension = 1;

        data = new Object[size];
    }

    public ArrayOop(int type, String referenceName, int size) {
        this.type = type;
        this.size = size;
        this.referenceName = referenceName;
        this.dimension = 1;

        data = new Object[size];
    }

    public ArrayOop(int type, int size, int dimension) {
        this.type = type;
        this.size = size;
        this.dimension = dimension;

        data = new Object[size];
    }

    public ArrayOop(int type, String referenceName, int size, int dimension) {
        this.type = type;
        this.size = size;
        this.referenceName = referenceName;
        this.dimension = dimension;

        data = new Object[size];
    }
}
