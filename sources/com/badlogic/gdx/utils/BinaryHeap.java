package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.BinaryHeap.Node;

public class BinaryHeap<T extends Node> {
    private final boolean isMaxHeap;
    private Node[] nodes;
    public int size;

    public BinaryHeap() {
        this(16, false);
    }

    public BinaryHeap(int capacity, boolean isMaxHeap2) {
        this.size = 0;
        this.isMaxHeap = isMaxHeap2;
        this.nodes = new Node[capacity];
    }

    public T add(T node) {
        if (this.size == this.nodes.length) {
            Node[] newNodes = new Node[(this.size << 1)];
            System.arraycopy(this.nodes, 0, newNodes, 0, this.size);
            this.nodes = newNodes;
        }
        node.index = this.size;
        this.nodes[this.size] = node;
        int i = this.size;
        this.size = i + 1;
        m4up(i);
        return node;
    }

    public Node peek() {
        if (this.size != 0) {
            return this.nodes[0];
        }
        throw new IllegalStateException("The heap is empty.");
    }

    public T pop() {
        Node[] nodes2 = this.nodes;
        Node popped = nodes2[0];
        int i = this.size - 1;
        this.size = i;
        nodes2[0] = nodes2[i];
        nodes2[this.size] = null;
        if (this.size > 0) {
            down(0);
        }
        return popped;
    }

    public void setValue(T node, float value) {
        float oldValue = node.value;
        node.value = value;
        if ((value < oldValue) ^ this.isMaxHeap) {
            m4up(node.index);
        } else {
            down(node.index);
        }
    }

    /* renamed from: up */
    private void m4up(int index) {
        Node[] nodes2 = this.nodes;
        Node node = nodes2[index];
        float value = node.value;
        while (index > 0) {
            int parentIndex = (index - 1) >> 1;
            Node parent = nodes2[parentIndex];
            if (!((value < parent.value) ^ this.isMaxHeap)) {
                break;
            }
            nodes2[index] = parent;
            parent.index = index;
            index = parentIndex;
        }
        nodes2[index] = node;
        node.index = index;
    }

    private void down(int index) {
        Node rightNode;
        float rightValue;
        boolean z;
        boolean z2;
        boolean z3;
        Node[] nodes2 = this.nodes;
        int size2 = this.size;
        Node node = nodes2[index];
        float value = node.value;
        while (true) {
            int leftIndex = (index << 1) + 1;
            if (leftIndex < size2) {
                int rightIndex = leftIndex + 1;
                Node leftNode = nodes2[leftIndex];
                float leftValue = leftNode.value;
                if (rightIndex >= size2) {
                    rightNode = null;
                    rightValue = this.isMaxHeap ? Float.MIN_VALUE : Float.MAX_VALUE;
                } else {
                    rightNode = nodes2[rightIndex];
                    rightValue = rightNode.value;
                }
                if (leftValue < rightValue) {
                    z = true;
                } else {
                    z = false;
                }
                if (!(z ^ this.isMaxHeap)) {
                    if (rightValue != value) {
                        if (rightValue > value) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        if (z2 ^ this.isMaxHeap) {
                            break;
                        }
                        nodes2[index] = rightNode;
                        rightNode.index = index;
                        index = rightIndex;
                    } else {
                        break;
                    }
                } else if (leftValue != value) {
                    if (leftValue > value) {
                        z3 = true;
                    } else {
                        z3 = false;
                    }
                    if (z3 ^ this.isMaxHeap) {
                        break;
                    }
                    nodes2[index] = leftNode;
                    leftNode.index = index;
                    index = leftIndex;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        nodes2[index] = node;
        node.index = index;
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        Object[] nodes2 = this.nodes;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(nodes2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(", ");
            buffer.append(nodes2[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    public static class Node {
        int index;
        float value;

        public Node(float value2) {
            this.value = value2;
        }

        public float getValue() {
            return this.value;
        }
    }
}
