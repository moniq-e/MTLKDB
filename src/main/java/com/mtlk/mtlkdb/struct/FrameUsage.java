package com.mtlk.mtlkdb.struct;

public class FrameUsage {
    private Node[] tracker;
    private Node first;
    private Node last;

    public FrameUsage(int size) {
        tracker = new Node[size];

        for (int i = 0; i < size; i++) {
            var node = new Node(i);
            if (i > 0) {
                var prev = tracker[i - 1];
                prev.next = node;
                node.previous = prev;
            }
            tracker[i] = node;
        }
        first = tracker[0];
        last = tracker[size - 1];
    }

    public void update(int slot) {
        var node = tracker[slot];

        if (node == last) return;

        if (node == first) {
            first = node.getNext();
            first.setPrevious(null);
        } else {
            node.getPrevious().setNext(node.getNext());
            node.getNext().setPrevious(node.getPrevious());
        }

        last.setNext(node);
        node.setPrevious(last);
        node.setNext(null);

        last = node;
    }

    public int getLRU() {
        return first.getValue();
    }

    private class Node {
        private Node previous;
        private Node next;
        private int value;

        public Node(int value) {
            this.value = value;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setPrevious(Node previous) {
            this.previous = previous;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public int getValue() {
            return value;
        }
    }
}
