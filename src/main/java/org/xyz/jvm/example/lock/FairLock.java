package org.xyz.jvm.example.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * Created By ziya
 * 2021/11/12
 */
public class FairLock extends AQS implements Lock {
    /**
     *  加入队列（存在并发）
     */
    private void addQueue() {
        Node node = new Node(Thread.currentThread(), Node.EXCLUSIVE);

        if (getTail() ==  null) {   // 队列中还没有数据
            if (compareAndSetTail(null, node)) {
                setHead(node);

                node.next = node.prev = node;

                System.out.println(Thread.currentThread().getName() + ": 第一个，加入队列成功");

                // 阻塞等待唤醒
                LockSupport.park();
            }
        } else {    // 队列中有数据，尾插
            Node tail = getTail();

            node.prev = tail;

            if (compareAndSetTail(tail, node)) {
                tail.next = node;

                System.out.println(Thread.currentThread().getName() + ": 不是第一个，加入队列成功");

                // 阻塞等待唤醒
                LockSupport.park();
            }
        }

        // 唤醒后抢锁
        lock();
    }


    @Override
    public void lock() {
        if (getState() == 0) { // 锁没被占用
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());

                System.out.println(Thread.currentThread().getName() + ": 抢占锁成功");

                return;
            }
        }

        System.out.println(Thread.currentThread().getName() + ": 抢占锁失败，加入队列");

        addQueue();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        // 清除锁标志
        setState(0);

        // 取出队列中第一个元素
        Node head = getHead();

        if (head.next != null) {
            setHead(head.next);

            // 唤醒线程
            LockSupport.unpark(head.thread);
        } else {
            // 唤醒线程
            LockSupport.unpark(head.thread);
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
