package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** double ended linked list queue */
public class LinkedListDeque<T> implements Deque<T> ,Iterable<T>{

    private final Node sentinel;
    private int size;

    private class Node {
        T item;
        Node prev;
        Node next;


        public Node(T i,Node p, Node n){
            item = i;
            prev = p;
            next = n;

        }
    }

    /**
     creates an empty que
     */
    public LinkedListDeque(){
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size=0;

    }

    /**
     * adds an item to the front of the deque
     * @param item the value of the item to be added
     */
    @Override
    public void addFirst(T item){
        Node oldFirst = sentinel.next;
        Node newFirst = new Node(item,sentinel,oldFirst);
        oldFirst.prev = newFirst;
        sentinel.next = newFirst;
        size+=1;
    }

    /**
     * adds an item to the last
     * @param item value of the item
     */
    @Override
    public void addLast(T item){
        Node oldLast = sentinel.prev;
        Node newLast = new Node(item,oldLast,sentinel);
        oldLast.next = newLast;
        sentinel.prev = newLast;
        size+=1;
    }


    /**
     *
     * @return size of the deque
     */
    @Override
    public int size(){
        return size;
    }

    /**
     * Prints the items in the deque from first to last,
     * separated by a space. Once all the items have been
     * printed, print out a new line.
     */
    @Override
    public void printDeque(){
        Node currNode = sentinel.next;
        while (currNode != sentinel){
            System.out.print(currNode.item + "");
            currNode = currNode.next;
        }
        System.out.println();
    }

    /**
     * removes the first item of the deque
     * @return null if empty
     */
    @Override
    public T removeFirst(){
        if (isEmpty()) return null;
        Node oldFirst = sentinel.next;
        Node newFirst = oldFirst.next;
        newFirst.prev = sentinel;
        sentinel.next = newFirst;
        size-=1;

        return oldFirst.item;
    }

    /**
     * removes the last item from the deque
     * @return returns the removed item, null if empty
     */
    @Override
    public T removeLast(){
        if (isEmpty()) return null;
        Node oldLast = sentinel.prev;
        Node newLast = oldLast.prev;
        newLast.next = sentinel;
        sentinel.prev = newLast;
        size-=1;

        return oldLast.item;
    }

    /**
     *
     * @param index index of the wanted item. 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null
     * @return the value of the item
     */
    @Override
    public T get(int index){
        if (index < 0 || index >= size){
            return null;
        }

        Node currNode = sentinel.next;
        for (int i = 0; i < index; i++) {
            currNode = currNode.next;
        }

        return currNode.item;
    }

    /**
     * recursive get
     * @param index index of the wanted item. 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null
     * @return the item at given index
     */
    public T getRecursive(int index){
        if (index < 0 || index >= size){
            return null;
        }
        return getRecursiveHelper(sentinel.next,index);

    }
    private T getRecursiveHelper(Node currNode,int index){
        if (index == 0){
            return currNode.item;
        }
        return getRecursiveHelper(currNode.next,index - 1);

    }

    /**
     * iterator
     * @return returns another iterator
     */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T>{
        Node currNode;

        /**
         * iterator
         */
        public LinkedListDequeIterator(){
            currNode = sentinel.next;
        }

        /**
         * if has next
         * @return
         */
        @Override
        public boolean hasNext(){
            return currNode!=sentinel;
        }

        /**
         * grabs the curr item
         * @return item
         */
        @Override
        public T next(){
            if (!hasNext()){
                throw new NoSuchElementException();
            }
            T returnItem = currNode.item;
            currNode = currNode.next;
            return returnItem;
        }



    }



}