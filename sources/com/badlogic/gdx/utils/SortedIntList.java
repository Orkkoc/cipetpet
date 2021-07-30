package com.badlogic.gdx.utils;

public class SortedIntList<E> implements Iterable<Node<E>> {
    Node<E> first;
    private SortedIntList<E>.Iterator iterator;
    private NodePool<E> nodePool = new NodePool<>();
    int size = 0;

    public static class Node<E> {
        public int index;

        /* renamed from: n */
        protected Node<E> f185n;

        /* renamed from: p */
        protected Node<E> f186p;
        public E value;
    }

    public E insert(int index, E value) {
        if (this.first != null) {
            Node<E> c = this.first;
            while (c.f185n != null && c.f185n.index <= index) {
                c = c.f185n;
            }
            if (index > c.index) {
                c.f185n = this.nodePool.obtain(c, c.f185n, value, index);
                if (c.f185n.f185n != null) {
                    c.f185n.f185n.f186p = c.f185n;
                }
                this.size++;
            } else if (index < c.index) {
                Node<E> newFirst = this.nodePool.obtain((Node) null, this.first, value, index);
                this.first.f186p = newFirst;
                this.first = newFirst;
                this.size++;
            } else {
                c.value = value;
            }
        } else {
            this.first = this.nodePool.obtain((Node) null, (Node) null, value, index);
            this.size++;
        }
        return null;
    }

    public E get(int index) {
        if (this.first == null) {
            return null;
        }
        Node<E> c = this.first;
        while (c.f185n != null && c.index < index) {
            c = c.f185n;
        }
        if (c.index == index) {
            return c.value;
        }
        return null;
    }

    public void clear() {
        while (this.first != null) {
            this.nodePool.free(this.first);
            this.first = this.first.f185n;
        }
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public java.util.Iterator<Node<E>> iterator() {
        if (this.iterator == null) {
            this.iterator = new Iterator();
        }
        return this.iterator.reset();
    }

    class Iterator implements java.util.Iterator<Node<E>> {
        private Node<E> position;
        private Node<E> previousPosition;

        Iterator() {
        }

        public boolean hasNext() {
            return this.position != null;
        }

        public Node<E> next() {
            this.previousPosition = this.position;
            this.position = this.position.f185n;
            return this.previousPosition;
        }

        public void remove() {
            if (this.previousPosition != null) {
                if (this.previousPosition == SortedIntList.this.first) {
                    SortedIntList.this.first = this.position;
                } else {
                    this.previousPosition.f186p.f185n = this.position;
                    if (this.position != null) {
                        this.position.f186p = this.previousPosition.f186p;
                    }
                }
                SortedIntList sortedIntList = SortedIntList.this;
                sortedIntList.size--;
            }
        }

        public SortedIntList<E>.Iterator reset() {
            this.position = SortedIntList.this.first;
            this.previousPosition = null;
            return this;
        }
    }

    static class NodePool<E> extends Pool<Node<E>> {
        NodePool() {
        }

        /* access modifiers changed from: protected */
        public Node<E> newObject() {
            return new Node<>();
        }

        public Node<E> obtain(Node<E> p, Node<E> n, E value, int index) {
            Node<E> newNode = (Node) super.obtain();
            newNode.f186p = p;
            newNode.f185n = n;
            newNode.value = value;
            newNode.index = index;
            return newNode;
        }
    }
}
