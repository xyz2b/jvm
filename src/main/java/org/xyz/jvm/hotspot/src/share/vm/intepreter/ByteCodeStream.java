package org.xyz.jvm.hotspot.src.share.vm.intepreter;

import org.xyz.jvm.hotspot.src.share.vm.oops.attribute.CodeAttribute;
import org.xyz.jvm.hotspot.src.share.vm.oops.MethodInfo;

public class ByteCodeStream extends BaseBytecodeStream {
    public ByteCodeStream(MethodInfo belongMethod, CodeAttribute belongCode) {
        this.belongMethod = belongMethod;
        this.belongCode = belongCode;
        this.length = belongCode.getCodeLength();
        this.index = 0;
        this.codes = new byte[length];
    }
}
