package editor;

import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import textrendering.TrackLines;
import textrendering.SavedText;
import editorfeatures.Cursor;
import editorfeatures.SaveAndLoad;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

/**
 * Editor is the main class for the application that takes all the information and uses javafx to create a
 * GUI that loads a text file that can be edited and saved
 */
public class Editor extends Application {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;
    Group root = new Group();
    private String inputFileName;

    /**
     * An EventHandler to handleKeyEvent keys that getLine pressed.
     */
    //TODO: seperate the event handler classes from the main Editor class
    private class KeyEventHandler implements EventHandler<Event> {
        private int xPosition;
        private int yPosition;
        private int linesInFile;
        private int textHeight;
        private static final int STARTING_FONT_SIZE = 12;
        private static final int STARTING_TEXT_POSITION_X = 0;
        private static final int STARTING_TEXT_POSITION_Y = 0;
        private SaveAndLoad saveAndLoad = new SaveAndLoad(inputFileName);
        private SavedText savedText = saveAndLoad.loadFile();
        private TrackLines trackLines = new TrackLines();
        private Cursor cursor;

        /**
         * The Text to display on the screen.
         */
        private Text getTextHeight = new Text(STARTING_TEXT_POSITION_X, STARTING_TEXT_POSITION_Y, "");
        private int fontSize = STARTING_FONT_SIZE;
        private String fontName = "Verdana";

        KeyEventHandler(Group root) {
            // Initialize some empty text and add it to root so that it will be displayed.
            getTextHeight = new Text(xPosition, yPosition, "");
            getTextHeight.setFont(Font.font(fontName, fontSize));
            textHeight = (int) getTextHeight.getBoundsInLocal().getHeight();
            xPosition = 0;
            yPosition = 0;
            // Any value that is take from the text is cast to an int as when javafx displays characters
            // it rounds to the closest int so to make it display correctly all measurements are case to ints
            //textHeight = savedText.getCurrentNodeHeight();
            linesInFile = (yPosition / textHeight) + 1;
            cursor = new Cursor(textHeight);
            // Cursor and Text is added so they can displayed on the screen
            AddToDisplay();
            root.getChildren().add(cursor.getCursor());
            cursor.makeCursorBlink();
        }

        public void handleKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown()) {
                Text characterTyped = new Text(keyEvent.getCharacter());
                if (keyEvent.getCharacter().length() > 0 && keyEvent.getCharacter().charAt(0) != 8) {
                    // Ignore control keys, which have non-zero length, as well as the backspace
                    // key, which is represented as a character of value = 8 on Windows.
                    if (keyEvent.getCharacter().equals("\r")) {
                        savedText.addChar(new Text("\n"));
                    }
                    savedText.addChar(characterTyped);
                    renderCharacter(characterTyped);
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                //TODO: OPTIMISE: Change else if statements to overloaded method call
                if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                    deleteCharacter();
                }
                if (keyEvent.getCode() == KeyCode.UP) {
                    cursor.upArrorPressed(savedText, trackLines);
                } else if (keyEvent.getCode() == KeyCode.DOWN) {
                    cursor.downArrowPressed(savedText, trackLines);
                } else if (keyEvent.getCode() == KeyCode.LEFT) {
                    cursor.leftArrowPressed(savedText, trackLines);
                } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                    cursor.rightArrowPressed(savedText, trackLines, linesInFile);
                }

                if (keyEvent.isShortcutDown()) {
                    if (keyEvent.getCode() == KeyCode.S) {
                        System.out.println("cmd s has been pressed, this will save the file");
                        saveAndLoad.saveFile(savedText);
                    }
                }
            }
        }

        public void handleMouseEvent(MouseEvent mouseEvent) {
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
                //TODO: Change savedText.CurrentNodeToPos so it doesn't need the line end
                //TODO: make the clicking more accurate
                int mousePressedY = (int) mouseEvent.getY();
                int mousePressedX = (int) mouseEvent.getX();
                int currentLine = mousePressedY/textHeight;
                SavedText.Node lineStart = trackLines.getLine(currentLine);
                SavedText.Node lineEnd = savedText.getNodeEndOfLine(lineStart);
                savedText.CurrentNodeToPos(lineStart, lineEnd, mousePressedX);
                //Current nodes x is the left bound and next node end is the end
                xPosition = savedText.getNextNodeX();
                yPosition = savedText.getCurrentNodeY();
                cursor.setXandY(savedText, xPosition, yPosition);
                System.out.println(savedText.getCurrentNodeText());
            }
        }

        @Override
        public void handle(Event event) {
            if(event.getEventType() == KeyEvent.KEY_PRESSED || event.getEventType() == KeyEvent.KEY_TYPED) {
                handleKeyEvent((KeyEvent) event);
            } else {
                handleMouseEvent((MouseEvent) event);
            }
        }

        private void renderText() {
            SavedText.Node renderNode = savedText.getFirstNode();
            trackLines.setArrayBackToStart();
            xPosition = 0;
            yPosition = 0;
            linesInFile = 1;
            while (savedText.isNotEndOfList(renderNode)) {

                if (xPosition >= (WINDOW_WIDTH - 15)) {
                    /** Word wraps the text and returns the first letter of the line */
                    renderNode = wordWrap(renderNode);
                }
                renderNode.getText().setX(xPosition);
                renderNode.getText().setY(yPosition);
                xPosition += (int) renderNode.getText().getBoundsInLocal().getWidth();
                addNewLineToArray(renderNode);
                newLineChangeXandYPosition(renderNode);
                renderNode = renderNode.getNext();
            }
            cursor.setXandY(savedText, xPosition, yPosition);
            linesInFile = yPosition / textHeight + 1;
        }

        private void AddTextToDisplay() {
            SavedText.Node toDisplay = savedText.getFirstNode();

            while (savedText.isNotEndOfList(toDisplay)) {
                toDisplay.getText().setTextOrigin(VPos.TOP);
                toDisplay.getText().setFont(Font.font(fontName, fontSize));
                root.getChildren().add(toDisplay.getText());
                toDisplay = toDisplay.getNext();
            }
            renderText();
        }

        /**
         * If the text goes to a new line then this method will add the next character to the arrayDeque
         */
        private void addNewLineToArray(SavedText.Node inputNode) {
            if (savedText.getFirstNode().equals(inputNode)) {
                trackLines.add(savedText.getFirstNode());
            } else if (inputNode.getText().getX() == 0) {
                trackLines.add(inputNode);
            }
        }

        /**
         * Changes the xPosition and yPosition when text is onto a new line
         */
        private void newLineChangeXandYPosition(SavedText.Node currentNode) {
            if (currentNode.getText().getText().equals("\r") && savedText.isNotEndOfList(currentNode.getNext())) {
                xPosition = 0;
                yPosition += savedText.getCurrentNodeHeight();
                linesInFile = yPosition / textHeight + 1;
                cursor.setXPos(xPosition);
                cursor.setYPos(yPosition);
            }
        }

        private void AddToDisplay() {
            if (savedText.listIsEmpty()) {
                savedText = new SavedText();
            } else {
                AddTextToDisplay();
            }
            System.out.println(cursor.printCoorinates());
            System.out.println(savedText.printCurrentPos());
            System.out.println("Text: " + savedText.printCharacter());
        }

        private void renderCharacter(Text character) {
            character.setTextOrigin(VPos.TOP);
            character.setFont(Font.font(fontName, fontSize));
            root.getChildren().add(character);
            renderText();
        }

        private void deleteCharacter() {
            root.getChildren().remove(savedText.getCurrentNode().getText());
            savedText.deleteChar();
            renderText();
        }

        private SavedText.Node wordWrap(SavedText.Node renderNode) {
            if (renderNode.equals(" ")) {
                savedText.deleteChar();
                return renderNode;
            } else {
                xPosition = 0;
                yPosition += textHeight;
                SavedText.Node lineStart = trackLines.getLine(linesInFile - 1);
                renderNode = savedText.getNodeAfterLastSpace(lineStart, renderNode);
                return renderNode;
            }
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);

        Parameters param = getParameters();
        List<String> parameter = param.getRaw();
        inputFileName = parameter.get(0);
        EventHandler<Event> eventHandler =
                new KeyEventHandler(root);
        scene.setOnKeyTyped(eventHandler);
        scene.setOnKeyPressed(eventHandler);
        scene.setOnMouseClicked(eventHandler);
        primaryStage.setTitle("Editor");

        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setPrefHeight(WINDOW_HEIGHT);
        scrollBar.setMin(0);
        scrollBar.setMax(WINDOW_HEIGHT);
        root.getChildren().add(scrollBar);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}