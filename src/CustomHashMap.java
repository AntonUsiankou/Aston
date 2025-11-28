import java.util.Objects;

public class CustomHashMap<K, V> {
    private int capacity = 16;
    private Entry<K, V>[] table;
    public CustomHashMap(){
        table = new Entry[capacity];
    }
    public CustomHashMap(int capacity){
        this.capacity = capacity;
        table = new Entry[capacity];
    }
    public void put(K key, V value) {
        int index = index(key);
        Entry<K, V> current = table[index];

        if (current == null) {
            table[index] = new Entry<>(key, value, null);
            return;
        }
        Entry<K, V> prev = null;
        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                current.setValue(value);
                return;
            }
            prev = current;
            current = current.getNext();
        }
        prev.setNext(new Entry<>(key, value, null));
    }

    public V get(K key) {
        int index = index(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                return current.getValue();
            }
            current = current.getNext();
        }
        return null;
    }

    public void remove(K key) {
        int index = index(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                if (prev == null) {
                    table[index] = current.getNext();
                } else {
                    prev.setNext(current.getNext());
                }
                return;
            }
            prev = current;
            current = current.getNext();
        }
    }
    public void display(){
        for(int i = 0; i < capacity; i++){
            if(table[i] != null){
                Entry<K, V> currentNode = table[i];
                while (currentNode != null){
                    System.out.println(String.format("KEY = %s ; VALUE = %s", currentNode.getKey(), currentNode.getValue()));
                    currentNode = currentNode.getNext();
                }
            }
        }
    }
    private int index(K key) {
        if (key == null) return 0;
        return Math.abs(key.hashCode()) % capacity;
    }

    public static void main(String[] args) {
        CustomHashMap<Integer, String> map =  new CustomHashMap<Integer, String>();
        map.put(null, "Nothing");
        for(int i = 1; i < 30; i++){
            map.put(i, "Value" + i);
        }
        map.put(1, "ETC");
        map.put(2, "John");
        map.display();
        System.out.println("<---------------------------------->");
        System.out.println("DELETE key 2");
        map.remove(2);
        map.display();
        System.out.println("<---------------------------------->");
        System.out.println("ADDING double key 1");
        map.put(1, "CSE");
        map.put(2, "John again");
        map.display();
        System.out.println("<---------------------------------->");
        System.out.println("ADDING double key 17");
        map.put(17, "CS");
        map.remove(null);
        map.display();
        System.out.println("<---------------------------------->");
        System.out.println("SHOWS value with key 3");
        System.out.println(map.get(3));
    }

    public class Entry<K, V>{
        private K key;
        private V value;
        private Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next){
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Entry<K, V> getNext() {
            return next;
        }

        public void setNext(Entry<K, V> next) {
            this.next = next;
        }
    }
}
