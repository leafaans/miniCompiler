import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

public class PeekIterator<T> implements Iterator<T> {

    private Iterator<T> it;
    // 终止符
    private T endToken = null;

    // 缓冲区大小
    private final static int CACHE_SIZE = 10;
    // 缓冲区队列 （先进先出）
    private LinkedList<T> queueCache = new LinkedList<>();
    // 回放的栈 （先进后出）
    private LinkedList<T> stackPutBacks = new LinkedList<>();


    public PeekIterator(Iterator<T> it, T endToken) {
        this.it = it;
        this.endToken = endToken;
    }

    public PeekIterator(Stream<T> stream) {
        it = stream.iterator();
    }

    public PeekIterator(Stream<T> stream, T endToken) {
        this.it = stream.iterator();
        this.endToken = endToken;
    }


    @Override
    public boolean hasNext() {
        // 有下一个字符的条件：不是终止符不为空，放回的栈不空，字符流有下一个字符
        return endToken != null || this.stackPutBacks.size() > 0 || this.it.hasNext();
    }

    @Override
    public T next() {
        T val = null;
        if (this.stackPutBacks.size() > 0) {
            val = this.stackPutBacks.pop();
        }else {
            // 字符流没有下一个字符，则终止符endToken=null
            if (!this.it.hasNext()) {
                T tmp = endToken;
                endToken = null;
                return tmp;
            }
            val = it.next();
        }
        while (queueCache.size() > CACHE_SIZE - 1) {
            // 直接扔掉了
            queueCache.poll();
        }
        queueCache.add(val);
        return val;
    }

    /**
     * 向前看一个字符
     *
     * @return
     */
    public T peek() {
        if (this.stackPutBacks.size() > 0) {
            // 只看不扔
            return this.stackPutBacks.getFirst();
        }
        if (!this.it.hasNext()) {
            return endToken;
        }
        // 再取一个放入缓存队列，同时转到放回栈中
        T val = next();
        this.putBack();
        return val;
    }

    /**
     * 放回操作
     * 缓存队列 ： A -> B -> C -> D
     * 放回栈 ： D -> C -> B -> A（栈顶）
     */
    public void putBack() {
        if (this.queueCache.size() > 0) {
            // 将缓存区队列最后一位放入放回栈的栈顶
            this.stackPutBacks.push(this.queueCache.pollLast());
        }
    }
}
