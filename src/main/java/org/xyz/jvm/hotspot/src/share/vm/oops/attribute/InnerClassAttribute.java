package org.xyz.jvm.hotspot.src.share.vm.oops.attribute;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;
import org.xyz.jvm.hotspot.src.share.vm.utilities.AccessFlags;

import java.util.ArrayList;
import java.util.List;

@Data
public class InnerClassAttribute extends Attribute {
    // 内部类的数量    u2
    private int numberOfClasses;
    // 内部类的列表
    private List<InnerClassAttribute.Class> classes;

    public void initClassesTable() {
        classes = new ArrayList<>(numberOfClasses);
    }

    @Data
    public static class Class {
        // 内部类在常量池中的索引(Class_info)
        private int innerClassInfoIndex;
        // 外部类在常量池中的索引(Class_info)
        private int outerClassInfoIndex;
        // 内部类名
        private int innerClassNameIndex;
        // 访问标志
        private AccessFlags innerClassAccessFlags;
    }
}
