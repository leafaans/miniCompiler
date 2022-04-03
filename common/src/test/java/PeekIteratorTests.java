import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PeekIteratorTests {

    @Test
    public void testNext() {
        var source = "abcdefg";
        var it = new PeekIterator<Character>(source.chars().mapToObj(c -> (char) c));

        assertEquals('a', it.next());
        assertEquals('b', it.next());
        it.next();
        it.next();
        assertEquals('e', it.next());
        assertEquals('f', it.peek());
        assertEquals('f', it.peek());
        assertEquals('f', it.next());
        assertEquals('g', it.next());
    }

    @Test
    public void testPutBack() {
        var source = "abcdefg";
        var it = new PeekIterator<Character>(source.chars().mapToObj(c -> (char) c));
        assertEquals('a', it.next());
        assertEquals('b', it.next());
        assertEquals('c', it.next());
        it.putBack();
        it.putBack();
        assertEquals('b', it.next());
    }

    @Test
    public void testPeek() {
        var source = "abcdefg";
        var it = new PeekIterator<Character>(source.chars().mapToObj(c -> (char) c));
        assertEquals('a', it.next());
        // peek 向前看一眼，即取b放入队列，再将b转移到放回栈中
        assertEquals('b', it.peek());
        // peek 只能多看一眼，重复peek直接取放回栈中的值
        assertEquals('b', it.peek());
        // 将a放入放回栈
        it.putBack();
        // 再把a弹出，即放回原字符流
        assertEquals('a', it.next());

    }
}
