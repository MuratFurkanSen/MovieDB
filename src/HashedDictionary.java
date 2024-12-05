import java.util.Iterator;

public class HashedDictionary<K, V> implements DictionaryInterface<K, V> {
    // The dictionary:
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 5;     // Must be prime
    private static final int MAX_CAPACITY = 100000;

    // The hash table:
    private HashNode<K, V>[] hashTable;
    private int tableSize;                             // Must be prime
    private int second_hash_q;
    private boolean integrityOK = false;
    private static final int MAX_SIZE = 2 * MAX_CAPACITY;
    private static double loadFactor = 0.5; // Fraction of hash table
    private String primaryHash;
    private String collisionSolve;
    private long collisionCount;
    // that can be filled

    public HashedDictionary() {
        this(DEFAULT_CAPACITY, "SSF", "LP", 0.5); // Call next constructor
    } // end default constructor

    public HashedDictionary(int initialCapacity, String primaryHash, String collisionSolve, Double loadFactor) {
        initialCapacity = checkCapacity(initialCapacity);
        primaryHash = primaryHash.toUpperCase();
        if (primaryHash.equals("SSF") || primaryHash.equals("PAF")) {
            this.primaryHash = primaryHash;
        } else {
            throw new UnsupportedOperationException("Primary Hash Function Should be Either 'SSF' or 'PAF'");
        }
        if (collisionSolve.equals("LP") || collisionSolve.equals("DH")) {
            this.collisionSolve = collisionSolve;
        } else {
            throw new UnsupportedOperationException("Collision Solve Should be Either 'LP' or 'DH'");
        }
        numberOfEntries = 0;    // Dictionary is empty
        collisionCount = 0;
        this.loadFactor = loadFactor;

        // Set up hash table:
        // Initial size of hash table is same as initialCapacity if it is prime;
        // otherwise increase it until it is prime size
        int tableSize = getNextPrime(initialCapacity);
        checkSize(tableSize);   // Check that size is not too large

        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        HashNode<K, V>[] temp = (HashNode<K, V>[]) new HashNode[tableSize];
        hashTable = temp;
        integrityOK = true;
        second_hash_q = getNextPrime(initialCapacity / 20);
    } // end constructor

    // -------------------------
    // We've added this method to display the hash table for illustration and testing
    // -------------------------
    public void displayHashTable() {
        checkIntegrity();
        for (HashNode current : hashTable) {
            if (current != null && current.getState() != HashNode.States.REMOVED) {
                System.out.println(current.getKey() + " : " + current.getValue());
            }
        } // end for
        System.out.println();
    } // end displayHashTable
    // -------------------------

    public V add(K key, V value) {
        checkIntegrity();
        if ((key == null) || (value == null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        else {
            V oldValue = null;

            int index = getHashIndex(key, true);

            if (hashTable[index] == null || hashTable[index].getState() == HashNode.States.REMOVED) {
                hashTable[index] = new HashNode<>(key, value);
            } else {
                oldValue = hashTable[index].getValue();
                hashTable[index] = new HashNode<>(key, value);
            }
            numberOfEntries++;
            if (isHashTableTooFull())
                enlargeHashTable();

            return oldValue;
        } // end if
    } // end add

    public V remove(K key) {
        checkIntegrity();
        V removedValue = null;

        int index = getHashIndex(key, false);

        if (index != -1) {
            removedValue = hashTable[index].getValue();
            hashTable[index].setRemoved();
            numberOfEntries--;
        }
        return removedValue;
    } // end remove

    public V getValue(K key) {
        checkIntegrity();

        V result = null;
        int index = getHashIndex(key, false);
        if (index != -1) {
            result = hashTable[index].getValue();
        }

        return result;
    } // end getValue

    public boolean contains(K key) {
        return getValue(key) != null;
    } // end contains

    public boolean isEmpty() {
        return numberOfEntries == 0;
    } // end isEmpty

    public int getSize() {
        return numberOfEntries;
    } // end getSize

    public final void clear() {
        checkIntegrity();
        for (int index = 0; index < hashTable.length; index++)
            hashTable[index] = null;

        numberOfEntries = 0;
    } // end clear

    public Iterator<K> getKeyIterator() {
        return new KeyIterator();
    } // end getKeyIterator

    public Iterator<V> getValueIterator() {
        return new ValueIterator();
    } // end getValueIterator

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
            while (hashTable[index] != null && hashTable[index].getState() != HashNode.States.REMOVED) {
                if (key.equals(hashTable[index].getKey())) {
                    numberOfEntries--;
                    break;
                }
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
    } // end getHashIndex

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

            sum += (int) (ch_val * (Math.pow(z, length - i - 1)%(10*9+7)));
        }
        return sum;
    }

    private int createSecondaryHash(K key) {

        return second_hash_q - (createSSFHash(key) % second_hash_q);
    }

    // Increases the size of the hash table to a prime >= twice its old size.
    // In doing so, this method must rehash the table entries.
    // Precondition: checkIntegrity has been called.
    private void enlargeHashTable() {
        HashNode<K, V>[] oldTable = hashTable;
        int oldSize = hashTable.length;
        int newSize = getNextPrime(oldSize + oldSize);
        second_hash_q = getNextPrime(newSize / 20);
        checkSize(newSize);

        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        HashNode<K, V>[] tempTable = (HashNode<K, V>[]) new HashNode[newSize]; // Increase size of array
        hashTable = tempTable;
        numberOfEntries = 0; // Reset number of dictionary entries, since
        collisionCount = 0;
        // it will be incremented by add during rehash

        // Rehash dictionary entries from old array to the new and bigger array;
        // skip both null locations and removed entries
        for (int i = 0; i < oldSize; i++) {
            if (oldTable[i] != null && oldTable[i].state != HashNode.States.REMOVED) {
                add(oldTable[i].getKey(), oldTable[i].getValue());
            }
        }
    } // end enlargeHashTable

    // Returns true if lambda > MAX_LOAD_FACTOR for hash table;
    // otherwise returns false.
    private boolean isHashTableTooFull() {
        return numberOfEntries > loadFactor * hashTable.length;
    } // end isHashTableTooFull

    // Returns a prime integer that is >= the given integer.
    private int getNextPrime(int integer) {
        // if even, add 1 to make odd
        if (integer % 2 == 0) {
            integer++;
        } // end if

        // test odd integers
        while (!isPrime(integer)) {
            integer = integer + 2;
        } // end while

        return integer;
    } // end getNextPrime

    // Returns true if the given integer is prime.
    private boolean isPrime(int integer) {
        boolean result;
        boolean done = false;

        // 1 and even numbers are not prime
        if ((integer == 1) || (integer % 2 == 0)) {
            result = false;
        }

        // 2 and 3 are prime
        else if ((integer == 2) || (integer == 3)) {
            result = true;
        } else // integer is odd and >= 5
        {
            assert (integer % 2 != 0) && (integer >= 5);

            // a prime is odd and not divisible by every odd integer up to its square root
            result = true; // assume prime
            for (int divisor = 3; !done && (divisor * divisor <= integer); divisor = divisor + 2) {
                if (integer % divisor == 0) {
                    result = false; // divisible; not prime
                    done = true;
                } // end if
            } // end for
        } // end if

        return result;
    } // end isPrime

    // Throws an exception if this object is not initialized.
    private void checkIntegrity() {
        if (!integrityOK)
            throw new SecurityException("HashedDictionary object is corrupt.");
    } // end checkIntegrity

    // Ensures that the client requests a capacity
    // that is not too small or too large.
    private int checkCapacity(int capacity) {
        if (capacity < DEFAULT_CAPACITY)
            capacity = DEFAULT_CAPACITY;
        else if (capacity > MAX_CAPACITY)
            throw new IllegalStateException("Attempt to create a dictionary " +
                    "whose capacity is larger than " +
                    MAX_CAPACITY);
        return capacity;
    } // end checkCapacity

    // Throws an exception if the hash table becomes too large.
    private void checkSize(int size) {
        if (tableSize > MAX_SIZE)
            throw new IllegalStateException("Dictionary has become too large.");
    } // end checkSize

    private class KeyIterator implements Iterator<K> {
        private int currentIndex; // Current position in hash table
        private int numberLeft;   // Number of entries left in iteration

        private KeyIterator() {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        } // end default constructor

        public boolean hasNext() {
            return numberLeft > 0;
        } // end hasNext

        public K next() {
            K result = null;

            while (hasNext() && (hashTable[currentIndex] == null || hashTable[currentIndex].getState() == HashNode.States.REMOVED)) {
                currentIndex++;
            }
            result = hashTable[currentIndex].getKey();
            currentIndex++;
            numberLeft--;
            return result;
        } // end next

        public void remove() {
            throw new UnsupportedOperationException();
        } // end remove
    } // end KeyIterator

    private class ValueIterator implements Iterator<V> {
        private int currentIndex;
        private int numberLeft;
        private HashNode<K, V> currentNode;

        private ValueIterator() {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        } // end default constructor

        public boolean hasNext() {
            return numberLeft > 0;
        } // end hasNext

        public V next() {
            V result = null;

            while (hasNext() && (hashTable[currentIndex] == null || hashTable[currentIndex].getState() == HashNode.States.REMOVED)) {
                currentIndex++;
            }
            result = hashTable[currentIndex].getValue();
            currentIndex++;
            numberLeft--;
            return result;
        } // end next

        public void remove() {
            throw new UnsupportedOperationException();
        } // end remove
    } // end ValueIterator

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

    public long getCollisionCount() {
        return collisionCount;
    }
} // end HashedDictionary
