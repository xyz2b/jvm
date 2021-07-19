package org.xyz.jvm.hotspot.src.share.vm.oops;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterfaceInfo {
    // 常量池中类型为 CONSTANT_Class_info 结构表项的有效索引值       u2
    private int constantPoolIndex;

    // 结构名称，class 文件中并没有该项
    private String interfaceName;
}
