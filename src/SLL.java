import java.util.Iterator;

public class SLL<T> {
    private SLLNode head;
    private int size;

    public SLL() {
        head = null;
        size = 0;
    }

    public void add(T data) {

        SLLNode newNode = new SLLNode(data);
        if (head == null) {
            head = newNode;
            size++;
            return;
        }
        SLLNode temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = newNode;
        size++;
    }
    public Iterator<T> getIterator() {
        return new ListIterator();
    }

    public class SLLNode {
        public SLLNode next;
        private T data;

        public SLLNode(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private class ListIterator implements Iterator<T> {
        private SLLNode currentNode; // Current position in List

        private ListIterator() {
            currentNode = head;
        } // end default constructor

        public boolean hasNext() {
            return currentNode != null;
        } // end hasNext

        public T next() {
            T result = currentNode.data;
            currentNode = currentNode.next;
            return result;
        } // end next


        public void remove() {
            throw new UnsupportedOperationException();
        } // end remove
    }

    public SLLNode getHead() {
        return head;
    }

    public void setHead(SLLNode head) {
        this.head = head;
    }

    public int getSize() {
        return size;
    }
}
