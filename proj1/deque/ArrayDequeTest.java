package deque;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;

import static org.junit.Assert.*;




public class ArrayDequeTest {
    @Test
    public void addIsEmptySizeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<String> lld1 = new ArrayDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();

    }





    @Test
    public void randomizedTest(){
        ArrayDeque<Integer> test = new ArrayDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                test.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int testSize = test.size();
                System.out.println("size: " + testSize);

            } else if (operationNumber == 2 && test.size()>0){
                int index = StdRandom.uniform(0,test.size());
                int Val = test.get(index);
                System.out.println("val: " + Val);

            } else if (operationNumber == 3 && !test.isEmpty()) {
                // size
                int removed = test.removeLast();
                System.out.println("removed: " + removed);
            }
        }
    }

    public class IntegerComparator implements Comparator<Integer> {

        // 第二步：必须重写 compare 方法，参数就是两个准备打架的元素
        @Override
        public int compare(Integer a, Integer b) {

            // 第三步：写出你的评判标准
            // 规则铁律：
            // 如果你觉得 a 赢了 (a > b)，返回任何正数
            // 如果你觉得 b 赢了 (a < b)，返回任何负数
            // 如果打平了 (a == b)，返回 0

            return a - b;
        }
    }

    @Test
    public void maxArrayDequeTest(){
        // 1. 实例化你刚写的裁判
        Comparator<Integer> myJudge = new IntegerComparator();


        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(myJudge);


        deque.addLast(15);
        deque.addLast(100);
        deque.addLast(42);

        Integer biggest = deque.max();
        assertEquals(100,(int) biggest);
    }
}
