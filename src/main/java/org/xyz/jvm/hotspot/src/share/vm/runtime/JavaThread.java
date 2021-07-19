package org.xyz.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;

import java.util.Stack;

/**
 * Java线程
 * */
@Data
public class JavaThread extends Thread {
    // 当前线程的虚拟机栈，VFrame是栈帧
    private Stack<VFrame> stack = new Stack<>();
}
