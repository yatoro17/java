package simpletexteditor;

public class LinkedList<T> {

    private Node<T> head;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
        size++;
    }

    public T remove() {
        if (head == null) {
            return null;
        }
        T data = head.data;
        head = head.next;
        size--;
        return data;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node<T> temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp.data;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void addAll(T[] array) {
        for (T element : array) {
            add(element);
        }
    }

    public T[] toArray() {
        T[] array = (T[]) new Object[size];
        Node<T> temp = head;
        int index = 0;
        while (temp != null) {
            array[index++] = temp.data;
            temp = temp.next;
        }
        return array;
    }

    public void clear() {
        head = null;
        size = 0;
    }
}
