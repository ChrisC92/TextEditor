package textrendering;

/**
 * Creates an TrackLines with the purpose of tracking each new line
 * which will allow for clicking within the text in a more efficient way
 */

public class TrackLines {
    private SavedText.Node[] items;
    private int size;
    private int next;
    private static final int RFACTOR = 2;

    /**
     * Create an empty array
     */
    public TrackLines() {
        items = new SavedText.Node[8];
        size = 0;
        next = 0;
    }

    /**
     * Adds an item to the back of the Deque
     */
    public void add(SavedText.Node item) {
        if (size == items.length) {
            resizeIncrease(size * RFACTOR);
        }
        items[next] = item;
        next = addOne(next);
        size += 1;
    }

    /**
     * removes the last item in the array deque
     */
    public void remove() {
        if (size == 0) {
            throw new IllegalArgumentException("No characters to delete");
        }
        double arraysSize = items.length;
        double R = size / arraysSize;
        if (R < 0.25) {
            resizeDecrease(items.length / 2);
        }
        items[next - 1] = null;
        next -= 1;
        size -= 1;
    }

    /**
     * Boolean to check if the array deque is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items that are currently in the array deque
     */
    public int numberOfLines() {
        return size;
    }

    /**
     * Returns the size of the array
     */
    public int arraySize() {
        return items.length;
    }

    /**
     * If the array is full then this method will create a new array double
     * the size and copy items from the previous array
     */
    private void resizeIncrease(int capacity) {
        SavedText.Node[] newResize = new SavedText.Node[capacity];
        System.arraycopy(items, 0, newResize, 0, size);
        items = newResize;
        next = (items.length / 2);
    }

    /**
     * If the usage ratio goes below 0.25 then the array is resized by half
     * usage ratio is the number of items within the array / the size of the array
     */
    private void resizeDecrease(int capacity) {
        SavedText.Node[] newResize = new SavedText.Node[capacity];
        System.arraycopy(items, 0, newResize, 0, size);
        items = newResize;
    }

    public void printDeque() {
        for (SavedText.Node i : items) {
            if (i != null) {
                System.out.print(i.getText().getText() + ", ");
            }
        }
    }
    /**
     * Helper method that wraps to the start of the array if it reaches the end
     */
    private int addOne(int index) {
        if (index == items.length - 1) {
            return 0;
        } else {
            return index + 1;
        }
    }
    /**
     *  Sets the next variable back to 0
     */
    public void setArrayBackToStart() {
        next = 0;
        size = 0;
    }
    /**
     * Returns item located at index of the Deque
     */
    public SavedText.Node getLine(int index) {
        return items[index];
    }
}
