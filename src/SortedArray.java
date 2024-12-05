import java.util.Iterator;

public class SortedArray<T extends Comparable<T>> {
    private T[] array;
    private int capacity;
    private int lastIndex;

    @SuppressWarnings("unchecked")
    public SortedArray(int capacity) {
        array = (T[]) new Comparable[capacity];
        this.capacity = capacity;
        lastIndex = 0;
    }

    public void add(T element) {
        for (int i = lastIndex; i >= 0; i--) {
            if (i - 1 >= 0) {
                if (array[i - 1].compareTo(element) < 0) {
                    if (i != capacity) {
                        array[i] = array[i - 1];
                    }
                } else {
                    if (i != capacity) {
                        array[i] = element;
                    }
                    break;
                }
            } else {
                array[i] = element;
            }
        }
        if (lastIndex != capacity) {
            lastIndex++;
        }
    }

    private class iterator implements Iterator<T> {
        private int currentIndex; // Current position in hash table

        private iterator() {
            currentIndex = 0;
        } // end default constructor

        public boolean hasNext() {
            return currentIndex != lastIndex;
        } // end hasNext

        public T next() {
            T result = array[currentIndex];
            currentIndex++;
            return result;
        } // end next

        public void remove() {
            throw new UnsupportedOperationException();
        } // end remove
    } // end KeyIterator

    public Iterator<T> getIterator() {
        return new iterator();
    }
}
