package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * array deque, resizes at run time
 * @param <T> the generic
 */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] items;

    /**
     * constructor method
     */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;

    }

    /**
     * adds an item to the start of the deque
     * @param item value of the item added
     */
    @Override
    public void addFirst(T item){
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size += 1;
    }

    private int minusOne(int index) {
        if (index - 1 < 0) {
            return index-1+items.length;
        }
        return (index - 1);
    }

    /**
     * adds an item to the start of the deque
     * @param item value of the item added
     */
    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast = addOne(nextLast);
        size+=1;
    }

    private int addOne(int index) {
        return (index + 1) % items.length;
    }


    /**
     *
     * @return returns the size of the deque
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * prints the deque
     */
    @Override
    public void printDeque() {
        for (T item : this) {
            System.out.print(item + " ");
        }
        System.out.println();
    }


    /** Resizes the underlying array to the target capacity. */
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i += 1) {
            a[i] = get(i);
        }
        items = a;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    /**
     * removes the first item
     * @return returns the item removed
     */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (size >= 16 && (items.length / size) >= 4) {
            resize(items.length / 2);
        }
        nextFirst = addOne(nextFirst);
        T removedItem = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        return removedItem;

    }

    /**
     * removes the last item
     * @return returns the item removed
     */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (size >= 16 && (items.length / size) >= 4){
            resize(items.length / 2);
        }
        nextLast = minusOne(nextLast);
        T removedItem = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        return removedItem;

    }

    /**
     * get the value at given index
     * @param index index to be searched
     * @return return the item at index given
     */
    @Override
    public T get(int index) {
        if (index < 0 || index >= size){
            return null;
        }
        return items[(nextFirst + index + 1) % items.length];
    }

    public boolean equals(Object O) {
        if (!(O instanceof Deque)) {
            return false;
        }

        Deque<?> other = (Deque<?>) O;

        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            T myItem = this.get(i);
            Object otherItem = other.get(i);

            if (!myItem.equals(otherItem)) {
                return false;
            }
        }
        return true;
    }

    /**
     * iterator
     * @return returns another iterator
     */
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>{
        int currIndex;
        int elemLeft = size;

        /**
         * iterator
         */
        public ArrayDequeIterator() {
            currIndex = addOne(nextFirst);
        }

        /**
         * has next method
         * @return returns if it reaches the end
         */
        @Override
        public boolean hasNext() {
            return elemLeft != 0;
        }

        /**
         * next method
         * @return returns the value of current item
         */
        @Override
        public T next() {
            if (!hasNext()){
                throw new NoSuchElementException();
            }
            T returnItem = items[currIndex];
            currIndex = addOne(currIndex);
            elemLeft -= 1;
            return returnItem;
        }
    }
}
