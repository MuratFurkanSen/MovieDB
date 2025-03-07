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
                    if (i != capacity) { // Swap elements
                        array[i] = array[i - 1];
                    }
                } else {
                    if (i != capacity) { // Place Element
                        array[i] = element;
                    }
                    break;
                }
            }
            else { // Add to top of the array
                array[i] = element;
            }
        }
        if (lastIndex != capacity) {
            lastIndex++;
        }
    }

    private class iterator implements Iterator<T> {
        private int currentIndex; // Current position

        private iterator() {
            currentIndex = 0;
        }

        public boolean hasNext() {
            return currentIndex != lastIndex;
        }

        public T next() {
            T result = array[currentIndex];
            currentIndex++;
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public Iterator<T> getIterator() {
        return new iterator();
    }
}
