package textrendering;

import javafx.scene.text.Text;

/**
 * Data structure used to store the text. Also provides functions for adding and deleting from the text
 * efficiently, this class alone does not allow for navigating through the text to be efficicent so
 * another class deals with tracking the lines of the text used for tracking clicks quickly and also
 * word wrapping.
 */
//TODO: check each method and ensure they can crash the app by calling null
public class SavedText {

    /**
     * class node contains the information to be stored within the SavedText
     */
    public class Node {
        private Text item;
        private Node next;
        private Node prev;

        public Node(Text item, Node next, Node prev) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }

        public Node getNext() {
            if (next != null) {
                return next;
            } else {
                throw new IllegalArgumentException("next is null");
            }

        }

        public Node getPrev() {
            if (prev.item != null) {
                return prev;
            } else {
                throw new IllegalArgumentException("prev is null");
            }
        }

        public Text getText() {
            return item;
        }
    }

    private Node sentinel;
    private Node currentNode;
    private int currentPos;

    public SavedText() {
        sentinel = new Node(null, null, null);
        currentNode = sentinel;
        currentPos = -1; // any negative number can be used here
    }

    /**
     * The sentinel node is at the beginning of the list and links with the first and last
     * characters within the list
     */
    private void addToEmpty(Text item) {
        sentinel.next = new Node(item, sentinel, sentinel);
        sentinel.prev = sentinel.next;
        currentNode = sentinel.next;
        currentPos = 0;
    }

    public void addChar(Text text) {
        if (currentPos < 0) {
            addToEmpty(text);
        } else {
            addNode(text, currentNode);
        }
    }

    public void deleteChar() {
        if (currentPos <= 0) {
            throw new IllegalArgumentException("Nothing to delete from current position");
        }
        Node toDelete = currentNode;
        currentNode.next.prev = toDelete.prev;
        currentNode.prev.next = toDelete.next;
        currentNode = sentinel.prev;
        currentPos -= 1;
    }

    public Node getNodeAfterLastSpace(Node lineStart, Node lineEnd) {
        Node iterateNode = lineStart;
        Node lastSpace = null;
        while (iterateNode != lineEnd) {
            if (iterateNode.getText().getText().equals(" ")) {
                lastSpace = iterateNode;
            }
            iterateNode = iterateNode.next;
        }
        if (lastSpace == null) {
            lastSpace = lineEnd.getPrev();
        }
        if (lastSpace.getNext() != null) {
            return lastSpace.getNext();
        } else {
            return lastSpace;
        }
    }


    public Node getCurrentNode() {
        return currentNode;
    }

    public Text getCurrentNodeText() {
        return currentNode.getText();
    }

    public int getCurrentNodeWidth() {
        return (int) currentNode.item.getBoundsInLocal().getWidth();
    }

    public int getCurrentNodeHeight() {
        if (currentNode.getText().getText().equals("\r")) {
            return (int) currentNode.getText().getBoundsInLocal().getHeight() / 2;
        } else {
            return (int) currentNode.getText().getBoundsInLocal().getHeight();
        }
    }

    public int getCurrentNodeY() {
        return (int) currentNode.item.getY();
    }

    public int getCurrentNodeX() {
        return (int) currentNode.item.getX();
    }

    public int getNextNodesWidth() {
        return (int) currentNode.next.item.getBoundsInLocal().getWidth();
    }

    public int getNextNodeYPos() {
        return (int) currentNode.next.item.getY();
    }

    public int getNextNodeX() {
        return (int) currentNode.next.item.getX();
    }

    public int getNextNodeY() {
        return (int) currentNode.next.item.getY();
    }

    public Node getPreviousNode() {
        if (currentNode.prev != sentinel) {
            return currentNode.prev;
        }
        return currentNode;
    }

    /**
     * Prints each item of the SavedText each value on a  new line
     */

    public void printList() {
        Node p = sentinel;

        while (p.next != sentinel) {
            System.out.print(p.next.item.getText() + ", ");
            p = p.next;
        }
    }

    /**
     * Sets current node to inputted node
     */

    public void setCurrentNode(Node node) {
        currentNode = node;
    }


    /**
     * CurrentNode is changed to the node at the index given into the method
     */

    public void setCurrentNode(int index) {

        if (sentinel.next == null) {
            throw new IllegalArgumentException("The list is empty");
        }
        currentPos = index;
        Node p = sentinel;
        int counter = 0;
        while (p.next != null) {
            if (counter == index) {
                currentNode = p;
                break;
            } else {
                p = p.next;
                counter += 1;
            }
        }
    }


    public Node getFirstNode() {
        return sentinel.next;
    }

    private void addNode(Text text, Node addAfter) {
        Node nodeToAdd = new Node(text, addAfter.next, addAfter);
        Node addAfterNext = addAfter.next;
        addAfter.next = nodeToAdd;
        currentPos += 1;
        if (addAfter.equals(currentNode)) {
            currentNode = nodeToAdd;
        } else {
            addAfterNext.prev = nodeToAdd;
        }
        if (nodeToAdd.next.equals(sentinel)) {
            sentinel.prev = nodeToAdd;
        }
    }

    public boolean isNotEndOfLine() {
        if (currentNode.next.getText().getX() == 0) {
            return false;
        }
        return true;
    }

    public boolean isNotEndOfList(Node inputNode) {
        return inputNode != sentinel;
    }


    public void moveCurrentNodeLeft() {
        if (!currentNode.prev.equals(sentinel)) {
            currentNode = currentNode.prev;
        }
    }


    public void moveCurrentNodeRight() {
        if (!currentNode.next.equals(sentinel)) {
            currentNode = currentNode.next;
        }
    }

    public void CurrentNodeToPos(Node lineStart, Node lineEnd, int toPosition) {
        Node newCurrent = lineStart;
        currentNode = newCurrent;
        toPosition +=2;
        if (toPosition != 0) {
            int i = 0;
            while (i <= toPosition && !newCurrent.equals(lineEnd)) {
                currentNode = newCurrent;
                newCurrent = newCurrent.next;
                i = (int) newCurrent.getText().getX();
            }
            xPosIsInBetweenLetter(getCurrentNodeX(), getNextNodeX(), toPosition);
        }
    }

    /**
     * Take the x cordinates of the start and end of the letter at the click
     * decide whether the click is closer to the left or right and move the currentNode to
     * that position
     */
    // TODO: Test this method
    private void xPosIsInBetweenLetter(int lowerX, int upperX, int clickX) {
        int lowerDiff = clickX - lowerX;
        int higherDiff = upperX - clickX;

        if(lowerDiff < higherDiff) {
            if(!currentNode.prev.equals(sentinel)) {
                currentNode = currentNode.prev;
            }
        }
    }

    public Node getNodeEndOfLine(Node lineStart) {
        Node endOfLine = lineStart;
        while (!lineStart.next.equals(sentinel) && lineStart.next.getText().getX() != 0) {
            lineStart = lineStart.next;
            endOfLine = lineStart;
        }

        return endOfLine;
    }

    public boolean listIsEmpty() {
        return sentinel.next == null;
    }


    public String printCurrentPos() {
        return "NODE: xPos: " + currentNode.item.getX() + " yPos: " + currentNode.item.getY();
    }

    public char printCharacter() {
        return currentNode.item.getText().charAt(0);
    }

    public void printCharAndPos(Node node) {
        System.out.println(node.getText());
    }

    public String toString() {
        Node p = sentinel;
        StringBuilder toReturn = new StringBuilder();
        while (p.next != sentinel) {
            toReturn.append(p.next.item.getText());
            p = p.next;
        }
        return toReturn.toString();
    }


}
