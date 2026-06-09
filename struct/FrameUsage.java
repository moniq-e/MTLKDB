package struct;

import java.util.HashMap;

public class FrameUsage {
    private HashMap<Integer, Node> tracker;
    private Node first;
    private Node last;

    public FrameUsage(int size) {
        tracker = new HashMap<>();

        for (int i = 0; i < size; i++) {
            var node = new Node(i);
            if (tracker.containsKey(i - 1)) {
                var prev = tracker.get(i - 1);
                prev.next = node;
                node.previous = prev;
            }
            tracker.put(i, node);
        }
        first = tracker.get(0);
        last = tracker.get(size - 1);
    }

    public void update(int slot) {
        var node = tracker.get(slot);

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
