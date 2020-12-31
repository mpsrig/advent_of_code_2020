package mpsrig;

import java.util.*;

public class LinkedQueue<E> extends AbstractQueue<E> {
    private static class Node<E> {
        E item;
        Node<E> next;
    }

    private int size = 0;
    private Node<E> head = null;
    private Node<E> tail = null;

    private final HashMap<E, Node<E>> lookupMap = new HashMap<>();

    @Override
    public Iterator<E> iterator() {
        return new Itr<>(head);
    }

    public Iterator<E> iteratorStartingAt(E e) {
        return new Itr<>(Objects.requireNonNull(lookupMap.get(e)));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("SuspiciousMethodCalls")
        var result = lookupMap.containsKey(o);
        return result;
    }

    @Override
    public boolean offer(E e) {
        Objects.requireNonNull(e);
        checkDoesNotContain(e);

        var newNode = new Node<E>();
        newNode.item = e;

        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;

        lookupMap.put(newNode.item, newNode);

        return true;
    }

    @Override
    public E poll() {
        if (head == null) {
            return null;
        }
        var oldHead = head;
        head = head.next;
        size--;
        if (head == null) {
            tail = null;
        }
        lookupMap.remove(oldHead.item);
        return oldHead.item;
    }

    @Override
    public E peek() {
        if (head == null) {
            return null;
        }
        return head.item;
    }

    public void addAfter(E existingElem, E elemToAdd) {
        Objects.requireNonNull(existingElem);
        Objects.requireNonNull(elemToAdd);
        checkDoesNotContain(elemToAdd);
        var node = Objects.requireNonNull(lookupMap.get(existingElem));
        if (node.next == null) {
            offer(elemToAdd);
        } else {
            var newNode = new Node<E>();
            newNode.item = elemToAdd;
            newNode.next = node.next;
            node.next = newNode;
            size++;
            lookupMap.put(newNode.item, newNode);
        }
    }

    private void checkDoesNotContain(E e) {
        if (lookupMap.containsKey(e)) {
            throw new IllegalArgumentException("Already contains element");
        }
    }

    private static class Itr<E> implements Iterator<E> {
        public Itr(Node<E> next) {
            this.next = next;
        }

        private Node<E> next;

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var result = next.item;
            next = next.next;
            return result;
        }
    }
}
