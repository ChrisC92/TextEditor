package editorfeatures;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import textrendering.SavedText;
import textrendering.TrackLines;

/**
 * Class creates the cursor that shows where the current position is in the code
 */
// TODO: class declares a lot of variables that can be passed from editor class, might need to refactor
// TODO: make other methods within the SavedText to make code more readable
public class Cursor {

    private final Rectangle cursor;

    public Cursor(int height) {
        cursor = new Rectangle(1, height);
        cursor.setX(0);
        cursor.setY(0);
    }


    /**
     * An EventHandler to handleKeyEvent the cursor blinking
     */
    public class CursorBlinking implements EventHandler<ActionEvent> {
        private int currentColourIndex = 0;
        private Color[] cursorColours = {Color.BLACK, Color.WHITE};

        CursorBlinking() {
            blink();
        }

        private void blink() {
            cursor.setFill(cursorColours[currentColourIndex]);
            currentColourIndex = (currentColourIndex + 1) % cursorColours.length;
        }

        @Override
        public void handle(ActionEvent event) {
            blink();
        }
    }

    public void makeCursorBlink() {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        CursorBlinking cursorBlink = new CursorBlinking();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorBlink);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public Rectangle getCursor() {
        return cursor;
    }

    /**
     * When the user types or deletes a character this method is called to change the x variable for
     * the cursors x position
     */
    public void setXPos(int xPos) {
        cursor.setX(xPos);
    }

    /**
     * When a new line is created or the text goes back up to the above line then this method
     * is called to update the cursors Y position
     */
    public void setYPos(int yPos) {
        cursor.setY(yPos);
    }

    //TODO: BUG: need to implement typing from the start of the file

    public void leftArrowPressed(SavedText list, TrackLines lines) {
        if (cursor.getX() != 0) {
            cursor.setX(list.getCurrentNode().getText().getX());
            list.moveCurrentNodeLeft();
        } else {
            if (lines.numberOfLines() > 1) {
                SavedText.Node node = list.getCurrentNode();
                cursor.setY(node.getText().getY());
                cursor.setX(node.getText().getX());
                list.moveCurrentNodeLeft();
            }
        }
    }

    //TODO: bug seems to have a bug with new lines
    public void rightArrowPressed(SavedText text, TrackLines lines, int linesInFile) {
        if (text.isNotEndOfList(text.getCurrentNode().getNext())) {
            int newX = (int) cursor.getX() + text.getNextNodesWidth();
            cursor.setX(newX);
            text.moveCurrentNodeRight();
            int currentNodeY = text.getCurrentNodeY();
            int nextNodeY = text.getNextNodeY();
            if (currentNodeY != nextNodeY) {
                int currentLine = text.getCurrentNodeY() / text.getCurrentNodeHeight();
                if ((currentLine / lines.numberOfLines()) != linesInFile) {
                    cursor.setX(0);
                    cursor.setY(text.getNextNodeYPos());
                }
            }
        }
    }

    public void upArrorPressed(SavedText text, TrackLines lines) {
        int currentXPos = text.getCurrentNodeX();
        int previousLine = text.getCurrentNodeY() / text.getCurrentNodeHeight();
        if (cursor.getY() != 0) {
            if (cursor.getX() == 0 && previousLine != 0) {
                /** As the currentNode is on the above line when the cursor is at the start of the line */
                SavedText.Node lineStart = lines.getLine(previousLine);
                atStartOfLine(text, lineStart.getPrev());
            } else if (cursor.getX() == 0 && previousLine == 0) {
                /** Will set current node as the first letter in the text */
                SavedText.Node lineStart = lines.getLine(previousLine);
                atStartOfLine(text, lineStart);
            } else {
                SavedText.Node lineStart = lines.getLine(previousLine - 1);
                arrowPressed(text, lineStart, currentXPos);
            }
        }
    }

    public void downArrowPressed(SavedText text, TrackLines lines) {
        int currentXpos = text.getCurrentNodeX();
        int nextLine = text.getCurrentNodeY() / text.getCurrentNodeHeight() + 2;
        if (nextLine <= lines.numberOfLines()) {
            if (cursor.getX() == 0 && nextLine != lines.numberOfLines()) {
                SavedText.Node lineStart = lines.getLine(nextLine);
                atStartOfLine(text, lineStart.getPrev());
            } else if (cursor.getX() == 0 && nextLine != lines.numberOfLines()) {
                SavedText.Node lineStart = lines.getLine(nextLine);
                atStartOfLine(text, lineStart);
            } else {
                SavedText.Node lineStart = lines.getLine(nextLine-1);
                arrowPressed(text, lineStart, currentXpos);
            }
        }
    }

    public void setXandY(SavedText text, int xPos, int yPos) {
        if (text.isNotEndOfList(text.getCurrentNode().getNext())) {
            cursor.setX(text.getNextNodeX());
            cursor.setY(text.getNextNodeYPos());
        } else {
            cursor.setX(xPos);
            cursor.setY(yPos);
        }
    }

    private void atStartOfLine(SavedText text, SavedText.Node newCurrent) {
        text.setCurrentNode(newCurrent);
        cursor.setX(text.getNextNodeX());
        cursor.setY(text.getNextNodeYPos());
    }

    private void arrowPressed(SavedText text, SavedText.Node lineStart, int currentXPos) {
        SavedText.Node lineEnd = text.getNodeEndOfLine(lineStart);
        text.CurrentNodeToPos(lineStart, lineEnd, currentXPos);
        cursor.setX(text.getNextNodeX());
        cursor.setY(text.getNextNodeYPos());
    }

    public String printCoorinates() {
        return "CURSOR: xPos: " + cursor.getX() + " yPos: " + cursor.getY();
    }

}

