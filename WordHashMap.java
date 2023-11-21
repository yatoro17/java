package simpletexteditor;

import java.util.LinkedList;

class WordHashMap {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private LinkedList<Entry>[] buckets;
    private int size;

    public WordHashMap() {
        this(DEFAULT_CAPACITY);
    }

    public WordHashMap(int initialCapacity) {
        buckets = new LinkedList[initialCapacity];
        size = 0;
    }

    public void insert(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        // Check if resizing is needed
        if ((double) (size + 1) / buckets.length > LOAD_FACTOR) {
            resize();
        }

        int hash = hash(key);
        if (buckets[hash] == null) {
            buckets[hash] = new LinkedList<>();
        }

        // Check if the key already exists in the map
        for (Entry entry : buckets[hash]) {
            if (entry.getKey().equals(key)) {
                entry.incrementCount();
                return;
            }
        }

        // If key doesn't exist, add a new entry
        buckets[hash].add(new Entry(key));
        size++;
    }

    public boolean search(String key) {
        int hash = hash(key);
        if (buckets[hash] != null) {
            for (Entry entry : buckets[hash]) {
                if (entry.getKey().equals(key)) {
                    return true;
                }
            }
        }
        return false; // Key not found
    }

    public void replace(String key, String replacement) {
        int hash = hash(key);
        if (buckets[hash] != null) {
            for (Entry entry : buckets[hash]) {
                if (entry.getKey().equals(key)) {
                    entry.setKey(replacement);
                    return;
                }
            }
        }
    }

    public int countWords() {
        return size;
    }

    private int hash(String key) {
        return key.hashCode() % buckets.length;
    }

    private void resize() {
        int newCapacity = buckets.length * 2;
        LinkedList<Entry>[] newBuckets = new LinkedList[newCapacity];

        for (LinkedList<Entry> bucket : buckets) {
            if (bucket != null) {
                for (Entry entry : bucket) {
                    int hash = hash(entry.getKey()) % newCapacity;
                    if (newBuckets[hash] == null) {
                        newBuckets[hash] = new LinkedList<>();
                    }
                    newBuckets[hash].add(entry);
                }
            }
        }

        buckets = newBuckets;
    }

    private static class Entry {
        private String key;
        private int count;

        public Entry(String key) {
            this.key = key;
            this.count = 1;
        }

        public String getKey() {
            return key;
        }

        public int getCount() {
            return count;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void incrementCount() {
            count++;
        }
    }
}
