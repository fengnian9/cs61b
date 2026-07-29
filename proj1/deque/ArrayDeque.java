package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Deque<T> , Iterable<T>{
    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] items;

    public ArrayDeque(){
        items = (T[])new Object[8];
        size=0;
        nextFirst=0;
        nextLast=1;

    }

    @Override
    public void addFirst(T item){
        if (size == items.length){
            resize(size*2);
        }
        items[nextFirst]=item;
        nextFirst = minusOne(nextFirst);
        size+=1;
    }

    private int minusOne(int index){
        if (index-1 < 0){return index-1+items.length;}
        return (index - 1);
    }

    @Override
    public void addLast(T item){
        if (size == items.length){
            resize(size*2);
        }
        items[nextLast]=item;
        nextLast = addOne(nextLast);
        size+=1;
    }

    private int addOne(int index){
        return (index+1) % items.length;
    }

    @Override
    public boolean isEmpty(){
        return (size == 0);
    }

    @Override
    public int size(){return size;}

    @Override
    public void printDeque(){
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
        nextFirst = capacity-1;
        nextLast = size;
    }

    @Override
    public T removeFirst(){
        if (size == 0) {
            return null;
        }
        if (size >= 16 && (items.length/size)>=4){
            resize(size/2);
        }
        nextFirst = addOne(nextFirst);
        T removedItem = items[nextFirst];
        items[nextFirst]=null;
        size-=1;
        return removedItem;

    }

    @Override
    public T removeLast(){
        if (size == 0) {
            return null;
        }
        if (size >= 16 && (items.length/size)>=4){
            resize(size/2);
        }
        nextLast = minusOne(nextLast);
        T removedItem = items[nextLast];
        items[nextLast]=null;
        size-=1;
        return removedItem;

    }


    @Override
    public T get(int index) {
        return items[(nextFirst+index+1)%items.length];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>{
        int currIndex;
        int elemLeft = size;

        public ArrayDequeIterator(){
            currIndex = addOne(nextFirst);
        }

        @Override
        public boolean hasNext(){
            return elemLeft != 0;
        }

        @Override
        public T next(){
            if (!hasNext()){
                throw new NoSuchElementException();
            }
            T returnItem = items[currIndex];
            currIndex = addOne(currIndex);
            elemLeft -=1;
            return returnItem;
        }



    }


}
