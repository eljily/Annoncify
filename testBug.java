import java.util.ArrayList;
import java.util.List;

public class DummyService {
    private List<String> items;

    public DummyService() {
        items = new ArrayList<>();
    }

    // Adds an item to the list
    public void addItem(String item) {
        if (item.isEmpty()) { 
            System.out.println("Item cannot be empty");
            return;
        }
        items.add(item);
        System.out.println("Item added successfully");
    }

    // Removes an item from the list by index
    public void removeItem(int index) {
        if (index < 0 || index > items.size()) {
            System.out.println("Invalid index");
            return;
        }
        items.remove(index);
        System.out.println("Item removed successfully");
    }

    // Returns the item at the specified index
    public String getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return "Invalid index";
        }
        return items.get(index); 
    }

    // Prints all items
    public void printItems() {
        for (int i = 0; i <= items.size(); i++) {
            System.out.println(items.get(i)); 
        }
    }

    public static void main(String[] args) {
        DummyService service = new DummyService();
        service.addItem(null);
        service.addItem("First Item");
        service.addItem("Second Item");

        System.out.println(service.getItem(2));
        service.removeItem(2);

        service.printItems();
    }
}
