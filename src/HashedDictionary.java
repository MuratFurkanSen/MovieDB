import java.util.Iterator;

public class HashedDictionary<K, V> implements DictionaryInterface<K, V> {
    // The dictionary:
    private static final int DEFAULT_CAPACITY = 5;     // Must be prime
    private static final int MAX_CAPACITY = 100000;
    private int numberOfEntries;
    private boolean integrityOK = false;
    private static final int MAX_SIZE = 2 * MAX_CAPACITY;
    // The hash table:
    private HashNode<K, V>[] hashTable;
    private int tableSize;  // Must be prime
    private String primaryHash;
    private String collisionSolve;
    private int second_hash_q;
    private static double loadFactor = 0.5; // Fraction of hash table
    private long collisionCount;

    public HashedDictionary() {
        this(DEFAULT_CAPACITY, "SSF", "LP", 0.5);
    }

    public HashedDictionary(int initialCapacity, String primaryHash, String collisionSolve, Double loadFactor) {
        initialCapacity = checkCapacity(initialCapacity);
        primaryHash = primaryHash.toUpperCase();

        // Hash Function Validation
        if (primaryHash.equals("SSF") || primaryHash.equals("PAF")) {
            this.primaryHash = primaryHash;
        } else {
            throw new UnsupportedOperationException("Primary Hash Function Should be Either 'SSF' or 'PAF'");
        }
        // Collision Solve Validation
        if (collisionSolve.equals("LP") || collisionSolve.equals("DH")) {
            this.collisionSolve = collisionSolve;
        } else {
            throw new UnsupportedOperationException("Collision Solve Should be Either 'LP' or 'DH'");
        }

        // Initial Parameters
        numberOfEntries = 0;    // Dictionary is empty
        collisionCount = 0;
        this.loadFactor = loadFactor;
        tableSize = getNextPrime(initialCapacity);
        checkSize(tableSize);
        @SuppressWarnings("unchecked")
        HashNode<K, V>[] temp = (HashNode<K, V>[]) new HashNode[tableSize];
        hashTable = temp;
        second_hash_q = getNextPrime(initialCapacity / 20);
        integrityOK = true;
    }

    public void displayHashTable() {
        checkIntegrity();
        for (HashNode current : hashTable) {
            if (current != null && current.getState() != HashNode.States.REMOVED) {
                System.out.println(current.getKey() + " : " + current.getValue());
            }
        }
        System.out.println();
    }

    public V add(K key, V value) {
        checkIntegrity();
        if ((key == null) || (value == null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        else {
            V oldValue = null;

            int index = getHashIndex(key, true);

            // Check for Empty Space
            if (hashTable[index] == null || hashTable[index].getState() == HashNode.States.REMOVED) {
                hashTable[index] = new HashNode<>(key, value);
            } else { // Same Key
                oldValue = hashTable[index].getValue();
                hashTable[index] = new HashNode<>(key, value);
            }
            numberOfEntries++;

            if (isHashTableTooFull())
                enlargeHashTable();

            return oldValue;
        }
    }

    public V remove(K key) {
        checkIntegrity();
        V removedValue = null;

        int index = getHashIndex(key, false);

        // Key Not Found Check
        if (index != -1) {
            removedValue = hashTable[index].getValue();
            hashTable[index].setRemoved();
            numberOfEntries--;
        }
        return removedValue;
    }

    public V getValue(K key) {
        checkIntegrity();

        V result = null;
        int index = getHashIndex(key, false);

        // Key Not Found Check
        if (index != -1) {
            result = hashTable[index].getValue();
        }

        return result;
    }

    public boolean contains(K key) {
        return getValue(key) != null;
    }

    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    public int getSize() {
        return numberOfEntries;
    }

    public final void clear() {
        checkIntegrity();
        for (int index = 0; index < hashTable.length; index++)
            hashTable[index] = null;

        numberOfEntries = 0;
        collisionCount = 0;
    }

    //Hash Functions
    private int getHashIndex(K key, boolean isAddOp) {
        int index;
        if (this.primaryHash.equals("SSF")) {
            index = createSSFHash(key);
        } else if (this.primaryHash.equals("PAF")) {
            index = createPAFHash(key);
        } else {
            throw new UnsupportedOperationException("Primary Hash Function Should be 'SSF' or 'PAF'");
        }
        index = index % hashTable.length;

        if (isAddOp) {
            // Look For Empty Spaces and Same Entries
            while (hashTable[index] != null && hashTable[index].getState() != HashNode.States.REMOVED) {
                if (key.equals(hashTable[index].getKey())) {
                    numberOfEntries--;
                    break;
                }
                // Collision Solving
                if (this.collisionSolve.equals("LP")) {
                    index += 1;
                } else if (this.collisionSolve.equals("DH")) {
                    index += createSecondaryHash(key);
                } else {
                    throw new UnsupportedOperationException("Collision Solve Should be 'LP' or 'DH'");
                }
                index = index % hashTable.length;
                collisionCount++;
            }
        } else {
            while (hashTable[index] != null) {
                if (hashTable[index].getKey().equals(key) && hashTable[index].getState() != HashNode.States.REMOVED) {
                    return index;
                }
                if (this.collisionSolve.equals("LP")) {
                    index += 1;
                } else if (this.collisionSolve.equals("DH")) {
                    index += createSecondaryHash(key);
                } else {
                    throw new UnsupportedOperationException("Collision Solve Should be 'LP' or 'DH'");
                }
                index = index % hashTable.length;
            }
            return -1;
        }
        return index;
    }

    public int createSSFHash(K key) {
        int sum = 0;
        for (char ch : key.toString().toUpperCase().toCharArray()) {
            if (ch > 64) {
                sum += ch - 64;
            } else if (ch >= 48 && ch <= 57) {
                sum += ch - 48;
            }
        }
        return sum;
    }

    private int createPAFHash(K key) {
        int sum = 0;
        int z = 41;
        char[] charArr = key.toString().toUpperCase().toCharArray();
        int length = charArr.length;

        for (int i = 0; i < length; i++) {
            int ch_val = 0;
            if (charArr[i] > 64) {
                ch_val = charArr[i] - 64;
            } else if (charArr[i] >= 48 && charArr[i] <= 57) {
                ch_val = charArr[i] - 48;
            }

            sum += (int) (ch_val * (Math.pow(z, length - i - 1) % (10 * 9 + 7)));
        }
        return sum;
    }

    private int createSecondaryHash(K key) {
        return second_hash_q - (createSSFHash(key) % second_hash_q);
    }

    // Hash Table Checkers and Editors
    private boolean isHashTableTooFull() {
        return numberOfEntries > loadFactor * hashTable.length;
    }

    private void checkIntegrity() {
        if (!integrityOK)
            throw new SecurityException("HashedDictionary object is corrupt.");
    }

    private void checkSize(int size) {
        if (tableSize > MAX_SIZE)
            throw new IllegalStateException("Dictionary has become too large.");
    }

    private int checkCapacity(int capacity) {
        if (capacity < DEFAULT_CAPACITY)
            capacity = DEFAULT_CAPACITY;
        else if (capacity > MAX_CAPACITY)
            throw new IllegalStateException("Attempt to create a dictionary " +
                    "whose capacity is larger than " +
                    MAX_CAPACITY);
        return capacity;
    }
    private void enlargeHashTable() {
        HashNode<K, V>[] oldTable = hashTable;
        int oldSize = hashTable.length;
        int newSize = getNextPrime(oldSize + oldSize);
        second_hash_q = getNextPrime(newSize / 20);
        checkSize(newSize);


        @SuppressWarnings("unchecked")
        HashNode<K, V>[] tempTable = (HashNode<K, V>[]) new HashNode[newSize];
        hashTable = tempTable;
        numberOfEntries = 0;
        collisionCount = 0;


        for (int i = 0; i < oldSize; i++) {
            if (oldTable[i] != null && oldTable[i].state != HashNode.States.REMOVED) {
                add(oldTable[i].getKey(), oldTable[i].getValue());
            }
        }
    }

    public long getCollisionCount() {
        return collisionCount;
    }

    private int getNextPrime(int integer) {

        if (integer % 2 == 0) {
            integer++;
        }


        while (!isPrime(integer)) {
            integer = integer + 2;
        }

        return integer;
    }

    private boolean isPrime(int integer) {
        boolean result;
        boolean done = false;


        if ((integer == 1) || (integer % 2 == 0)) {
            result = false;
        } else if ((integer == 3)) {
            result = true;
        } else {
            assert (integer % 2 != 0) && (integer >= 5);


            result = true; // assume prime
            for (int divisor = 3; !done && (divisor * divisor <= integer); divisor = divisor + 2) {
                if (integer % divisor == 0) {
                    result = false; // divisible; not prime
                    done = true;
                }
            }
        }

        return result;
    }

    public Iterator<K> getKeyIterator() {
        return new KeyIterator();
    }

    public Iterator<V> getValueIterator() {
        return new ValueIterator();
    }
    private class KeyIterator implements Iterator<K> {
        private int currentIndex;
        private int numberLeft;
        private KeyIterator() {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        }

        public boolean hasNext() {
            return numberLeft > 0;
        }

        public K next() {
            K result = null;

            // Increase current index until find result
            while (hasNext() && (hashTable[currentIndex] == null || hashTable[currentIndex].getState() == HashNode.States.REMOVED)) {
                currentIndex++;
            }
            result = hashTable[currentIndex].getKey();
            currentIndex++;
            numberLeft--;
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    private class ValueIterator implements Iterator<V> {
        private int currentIndex;
        private int numberLeft;

        private HashNode<K, V> currentNode;

        private ValueIterator() {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        }

        public boolean hasNext() {
            return numberLeft > 0;
        }

        public V next() {
            V result = null;

            // Increase current index until find result
            while (hasNext() && (hashTable[currentIndex] == null || hashTable[currentIndex].getState() == HashNode.States.REMOVED)) {
                currentIndex++;
            }
            result = hashTable[currentIndex].getValue();
            currentIndex++;
            numberLeft--;
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    public static class HashNode<K, V> {

        private enum States {CURRENT, REMOVED}
        private K key;
        private V value;

        private States state;

        public HashNode(K key, V value) {
            this.key = key;
            this.value = value;
            state = States.CURRENT;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public States getState() {
            return state;
        }

        public void setState(States state) {
            this.state = state;
        }

        public void setRemoved() {
            state = States.REMOVED;
        }

    }
}
