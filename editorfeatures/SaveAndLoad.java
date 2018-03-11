package editorfeatures;

import textrendering.SavedText;
import javafx.scene.text.Text;

import java.io.*;

/**
 * Class that handles opening files as a command line arguement and also saving the file to the current file
 * that has been opened, if the command line arguement is empty when opening it opens a blank file
 */
public class SaveAndLoad {
    private String inputString;
    private File textDocument;

    public SaveAndLoad(String fileName) {
        inputString = fileName;
    }

    public SavedText loadFile() {
        SavedText savedText = new SavedText();

        try {
            textDocument = new File(inputString);

            if (!textDocument.exists()) {
                System.out.println(textDocument + "does not exist");
                return null;
            }
            FileReader reader = new FileReader(textDocument);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int intRead = -1;

            /** Continutes reading from the file until read returns -1 which means the end of
             * the file*/

            /**
             * since the file uses \n use this to add in paragraphs
             */
            while ((intRead = bufferedReader.read()) != -1) {
                //Assuming the input is ASCII then we can convert then int input to a char
                char charRead = (char) intRead;
                if (charRead != '\r') {
                    String toAdd = Character.toString(charRead);
                    Text textRead = new Text(toAdd);
                    savedText.addChar(textRead);
                    if(charRead == '\n'){
                        String newLine = Character.toString('\r');
                        Text lineAdd = new Text(newLine);
                        savedText.addChar(lineAdd);
                    }
                }
            }
            System.out.println("Text file " + inputString + " has been added to the editor");
            bufferedReader.close();

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found, Exception: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when loadeing the file; exception: " + ioException);
        }

        return savedText;
    }
    //TODO: test method
    public void saveFile(SavedText document) {
        try {

            FileWriter writer = new FileWriter(textDocument);

            SavedText.Node currentNode = document.getFirstNode();
            while(document.isNotEndOfList(currentNode)){
                if(!currentNode.getText().equals(new Text("\r"))){
                    String string = currentNode.getText().getText();
                    char toAdd = string.charAt(0);
                    writer.write(toAdd);
                    currentNode = currentNode.getNext();
                }
            }

            writer.close();


        } catch(FileNotFoundException fileNotFound) {
            System.out.println("File was not found. Exception: " + fileNotFound);
        } catch(IOException ioException) {
            System.out.println("Error when saving the document. Exception: " + ioException);
        }
    }
}
