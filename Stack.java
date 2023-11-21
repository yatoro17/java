package simpletexteditor;

public class Stack {

  private LinkedList list;
  
  public Stack() {
    list = new LinkedList();
  }

  public void push(int data) {
    list.add(data);
  }

  public int pop() {
    return list.remove();
  }
  
  public int peek() {
    return list.get(list.size() - 1);
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

}
