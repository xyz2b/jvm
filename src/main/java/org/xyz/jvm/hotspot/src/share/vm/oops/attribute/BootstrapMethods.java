package org.xyz.jvm.hotspot.src.share.vm.oops.attribute;

import lombok.Data;
import org.xyz.jvm.hotspot.src.share.vm.oops.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * BootstrapMethods 的属性
 * */
@Data
public class BootstrapMethods extends Attribute {
    // BootstrapMethod的数量    u2
    private int numberOfBootstrapMethods;
    // BootstrapMethodTable
    private List<BootstrapMethods.BootstrapMethod> bootstrapMethods;

    public void initBootstrapMethodTable() {
        bootstrapMethods = new ArrayList<>(numberOfBootstrapMethods);
    }

    @Data
    public static class BootstrapMethod {
        // BootstrapMethod在常量池中对应的MethodHandle_info的索引
        private int bootstrapMethodRef;
        // BootstrapMethod的参数个数
        private int numOfBootstrapArguments;
        // 参数对应在常量池中索引的列表
        private List<Integer> bootstrapArguments;

        public void initBootstrapArguments() {
            bootstrapArguments = new ArrayList<>(numOfBootstrapArguments);
        }
    }
}
