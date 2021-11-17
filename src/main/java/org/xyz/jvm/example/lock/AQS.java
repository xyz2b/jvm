package org.xyz.jvm.example.lock;

import sun.misc.Unsafe;;import java.lang.reflect.Field;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;

/**
 * Created By ziya
 * 2021/11/12
 */
public class AQS extends AbstractOwnableSynchronizer {
    // 节点，封装了线程
    static final class Node {
        static final Node SHARED = new Node();
        static final Node EXCLUSIVE = null;

        static final int CANCELLED =  1;
        static final int SIGNAL    = -1;
        static final int CONDITION = -2;
        static final int PROPAGATE = -3;

        volatile Node prev;
        volatile Node next;

        /**
         * -1
         * 0
         * 1
         * */
        volatile int waitStatus;

        // 该节点所封装的线程
        volatile Thread thread;

        Node nextWaiter;
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {}
        Node(Thread thread, Node node) {
            this.nextWaiter = node;
            this.thread = thread;
        }
        Node(Thread thread, int waitStatus) {
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

    // 头节点
    private transient volatile Node head;
    // 尾节点
    private transient volatile Node tail;
    // 0没有被上锁，1被上锁
    private volatile int state;

    public Node getHead() {
        return head;
    }

    public void setHead(Node node) {
        this.head = node;
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node node) {
        this.tail = node;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    // CAS设置state
    public boolean compareAndSetState(int expect, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    // CAS设置Head
    protected final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    // CAS设置Tail
    protected final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    // ----------------------------------
    private static final Unsafe unsafe;
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);

            // 获取该类中state字段的偏移量
            stateOffset = unsafe.objectFieldOffset
                    (AQS.class.getDeclaredField("state"));
            // 获取该类中head字段的偏移量
            headOffset = unsafe.objectFieldOffset
                    (AQS.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset
                    (AQS.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset
                    (Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset
                    (Node.class.getDeclaredField("next"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

}
