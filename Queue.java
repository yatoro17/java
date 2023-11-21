package simpletexteditor;

public class Queue {
    private LinkedList list;
  
  public Queue() {
    list = new LinkedList();
  }

  public void enqueue(int data) {
    list.add(data);
  }

  public int dequeue() {
    return list.remove();
  }

  public int peek() {
    return list.get(0);
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

}

