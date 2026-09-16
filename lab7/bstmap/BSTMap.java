package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private Node rootNode;
    private int size;

    private class Node {
        K key;
        V value;
        Node leftChild;
        Node rightChild;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public BSTMap() {
        this.rootNode = null;
        this.size = 0;
    }

    @Override
    public void clear() {
        rootNode = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        Node node = search(rootNode, key);
        return node != null;
    }

    private Node search(Node node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return search(node.leftChild, key);
        } else if (cmp > 0) {
            return search(node.rightChild, key);
        } else {
            return node;
        }

    }

    @Override
    public V get(K key) {
        Node node = search(rootNode,key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public int size() {
        return this.size;
    }

    private Node put(Node node, K key, V value) {
        if (node == null) {
            size += 1;
            return new Node(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.leftChild = put(node.leftChild, key, value);
        } else if (cmp > 0) {
            node.rightChild = put(node.rightChild, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    @Override
    public void put(K key, V value) {

        rootNode = put(rootNode, key, value);
    }

    public void printInOrder() {
        Node node = rootNode;
        inOrderPrint(rootNode);

    }

    private void inOrderPrint(Node node) {
        if (node == null) {
            return;
        }
        inOrderPrint(node.leftChild);
        System.out.println(node.value);
        inOrderPrint(node.rightChild);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
