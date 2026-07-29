package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> defaultComparator;

    /**
     * constructor
     * @param c takes in a comparator
     */
    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.defaultComparator = c;
    }

    /**
     * finds the max using default comparator
     * @return
     */
    public T max() {
        if (this.isEmpty()) {
            return null;
        }
        T currMax = get(0);
        for (T item : this) {
            if (defaultComparator.compare(currMax, item) < 0) {
                currMax = item;
            }
        }
        return currMax;
    }

    /**
     * finds the max using given comparator
     * @param c comparator
     * @return max value , compared by given comparator
     */
    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }
        T currMax = get(0);
        for (T item : this){
            if (c.compare(currMax, item) < 0) {
                currMax = item;
            }
        }
        return currMax;
    }
}
