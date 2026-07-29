package deque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.lang.reflect.Array;
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
}
