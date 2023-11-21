package simpletexteditor;
//import simpletexteditor.AVLSearchTree; 
//import simpletexteditor.WordHashMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;
import java.awt.datatransfer.*;
import java.io.*;
import java.nio.file.Files;
import java.util.StringTokenizer;



public class TextEditor {
    private JFrame frame;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private LinkedList<Character> textBuffer;
    private Stack<LinkedList<Character>> undoStack;
    private Queue<LinkedList<Character>> redoQueue;
    private boolean isCtrlPressed = false;
    private Clipboard clipboard;
    private WordHashMap wordHashMap;  // Declare the WordHashMap variable

    private AVLSearchTree searchTree;  // Declare the AVLSearchTree variable

    private LinkedList<Integer> fontSizeHistory = new LinkedList<>();
    private int currentFontSize = 12; // Default font size

    public TextEditor() {
        frame = new JFrame("Text Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        textArea = new JTextArea(10, 40); // Fixed size
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, currentFontSize));
        scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);

        textBuffer = new LinkedList<>();
        undoStack = new Stack<>();
        redoQueue = new LinkedList<>();
        searchTree = new AVLSearchTree();
        wordHashMap = new WordHashMap();  // Initialize the WordHashMap

        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        searchTree = new AVLSearchTree();  // Initialize the AVLSearchTree

        createMenuBar();

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (isCtrlPressed) {
                    if (c == 'c') {
                        copyToClipboard();
                    } else if (c == 'v') {
                        pasteFromClipboard();
                    }
                } else {
                    if (c == '\b') {
                        // Handle backspace
                        deleteCharacter();
                    } else {
                        insertCharacter(c);
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isCtrlPressed = true;
                }
                if (isCtrlPressed) {
                    if (e.getKeyCode() == KeyEvent.VK_Z) {
                        undo();
                    } else if (e.getKeyCode() == KeyEvent.VK_Y) {
                        redo();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isCtrlPressed = false;
                }
            }
        });

        frame.setVisible(true);
    }

    private void insertCharacter(char c) {
        textBuffer.add(c);
        undoStack.push(new LinkedList<>(textBuffer));
        redoQueue.clear();
        updateTextArea();
        scrollDown();
        // Update the AVL search tree with the new word
        updateSearchTree();
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            redoQueue.offer(new LinkedList<>(textBuffer));
            textBuffer = undoStack.pop();
            updateTextArea();
            scrollDown();
            // Update the AVL search tree after undo
            updateSearchTree();
        }
    }

    private void redo() {
        if (!redoQueue.isEmpty()) {
            undoStack.push(new LinkedList<>(textBuffer));
            textBuffer = redoQueue.poll();
            updateTextArea();
            scrollDown();
            // Update the AVL search tree after redo
            updateSearchTree();
        }
    }

    private void updateTextArea() {
        StringBuilder text = new StringBuilder();
        for (char c : textBuffer) {
            text.append(c);
        }
        textArea.setText(text.toString());
    }

    private void copyToClipboard() {
        String selectedText = textArea.getSelectedText();
        StringSelection selection = new StringSelection(selectedText);
        clipboard.setContents(selection, null);
    }

    private void pasteFromClipboard() {
        Transferable clipboardData = clipboard.getContents(this);
        if (clipboardData != null && clipboardData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = (String) clipboardData.getTransferData(DataFlavor.stringFlavor);
                insertTextAtCaret(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void insertTextAtCaret(String text) {
        int caretPosition = textArea.getCaretPosition();
    
        // Convert the input text to a LinkedList of characters
        LinkedList<Character> textToAdd = new LinkedList<>();
        for (char c : text.toCharArray()) {
            textToAdd.add(c);
        }
    
        // Insert the characters at the caret position
        textBuffer.addAll(caretPosition, textToAdd);
    
        // Update the undo stack, clear redo queue, and update the UI
        undoStack.push(new LinkedList<>(textBuffer));
        redoQueue.clear();
        updateTextArea();
        scrollDown();
    
        // Update the AVL search tree after inserting text
        updateSearchTree();
    }
    

    private void deleteCharacter() {
        if (!textBuffer.isEmpty()) {
            textBuffer.removeLast();
            undoStack.push(new LinkedList<>(textBuffer));
            redoQueue.clear();
            updateTextArea();
            scrollDown();
            // Update the AVL search tree after deleting character
            updateSearchTree();
        }
    }

    private void scrollDown() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        JMenuItem fontSizeItem = new JMenuItem("Change Font Size");
        JMenuItem findItem = new JMenuItem("Find");
        JMenuItem replaceItem = new JMenuItem("Replace"); // Added new menu item
        JMenuItem wordCountItem = new JMenuItem("Word Count"); // Added new menu item

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.add(fontSizeItem);
        editMenu.add(findItem);
        editMenu.add(replaceItem); // Added new menu item
        editMenu.add(wordCountItem); // Added new menu item

        menuBar.add(fileMenu);
        menuBar.add(editMenu);


        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        exitItem.addActionListener(e -> System.exit(0));
        undoItem.addActionListener(e -> undo());
        redoItem.addActionListener(e -> redo());
        fontSizeItem.addActionListener(e -> changeFontSize());
        findItem.addActionListener(e -> showFindDialog());
        replaceItem.addActionListener(e -> showReplaceDialog()); // Added action for Replace menu item
        wordCountItem.addActionListener(e -> showWordCount());


        frame.setJMenuBar(menuBar);
    }

    private void showWordCount() {
        int wordCount = searchTree.countWords();
        JOptionPane.showMessageDialog(frame, "Total Word Count: " + wordCount, "Word Count", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateSearchTree() {
        searchTree = new AVLSearchTree();
        for (char c : textBuffer) {
            if (Character.isAlphabetic(c)) {
                searchTree.insert(String.valueOf(c).toLowerCase());
            }
        }
        updateWordHashMap();
    }

    private void showFindDialog() {
        String searchTerm = JOptionPane.showInputDialog(frame, "Enter word to find:", "Find", JOptionPane.QUESTION_MESSAGE);
        if (searchTerm != null && !searchTerm.isEmpty()) {
            boolean found = searchTree.search(searchTerm.toLowerCase());
            String resultMessage = found ? "Word found!" : "Word not found.";
            JOptionPane.showMessageDialog(frame, resultMessage, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showReplaceDialog() {
        String searchTerm = JOptionPane.showInputDialog(frame, "Enter word to find:", "Find", JOptionPane.QUESTION_MESSAGE);
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String replacement = JOptionPane.showInputDialog(frame, "Enter replacement word:", "Replace Word", JOptionPane.QUESTION_MESSAGE);
            if (replacement != null) {
                replaceOccurrences(searchTerm.toLowerCase(), replacement);
            }
        }
    }

    private void updateWordHashMap() {
        wordHashMap = new WordHashMap();
        StringTokenizer tokenizer = new StringTokenizer(textArea.getText());
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            if (Character.isAlphabetic(word.charAt(0))) {
                wordHashMap.insert(word.toLowerCase());
            }
        }
    }
    private void replaceOccurrences(String wordToReplace, String replacement) {
        LinkedList<Character> newTextBuffer = new LinkedList<>();
        StringBuilder word = new StringBuilder();
        String lastWord = "";
    
        for (char c : textBuffer) {
            if (Character.isAlphabetic(c)) {
                word.append(c);
            } else {
                if (lastWord.equalsIgnoreCase(wordToReplace)) {
                    newTextBuffer.addAll(replacement.chars().mapToObj(i -> (char) i).collect(LinkedList::new, LinkedList::addLast, LinkedList::addAll));
                } else {
                    newTextBuffer.addAll(lastWord.chars().mapToObj(i -> (char) i).collect(LinkedList::new, LinkedList::addLast, LinkedList::addAll));
                }
                word.setLength(0);
                newTextBuffer.add(c);
            }
    
            lastWord = word.toString();  // Update lastWord in each iteration
        }
    
        // Handle the case where the last word in the text is the one to be replaced
        if (lastWord.equalsIgnoreCase(wordToReplace)) {
            newTextBuffer.addAll(replacement.chars().mapToObj(i -> (char) i).collect(LinkedList::new, LinkedList::addLast, LinkedList::addAll));
        } else {
            newTextBuffer.addAll(lastWord.chars().mapToObj(i -> (char) i).collect(LinkedList::new, LinkedList::addLast, LinkedList::addAll));
        }
    
        textBuffer = newTextBuffer;
        undoStack.push(new LinkedList<>(textBuffer));
        redoQueue.clear();
        updateTextArea();
        scrollDown();
        updateSearchTree();
    }
    

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String fileContent = new String(Files.readAllBytes(selectedFile.toPath()));
                textBuffer.clear();
                for (char c : fileContent.toCharArray()) {
                    textBuffer.add(c);
                }
                undoStack.clear();
                redoQueue.clear();
                undoStack.push(new LinkedList<>(textBuffer));
                updateTextArea();
                scrollDown();
                updateSearchTree();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeFontSize() {
        String sizeInput = JOptionPane.showInputDialog(frame, "Enter Font Size:", "Change Font Size", JOptionPane.QUESTION_MESSAGE);
        if (sizeInput != null) {
            try {
                int newFontSize = Integer.parseInt(sizeInput);
                if (newFontSize > 0) {
                    fontSizeHistory.add(currentFontSize);
                    currentFontSize = newFontSize;
                    Font currentFont = textArea.getFont();
                    Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), currentFontSize);
                    textArea.setFont(newFont);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid positive font size.", "Invalid Font Size", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid font size. Please enter a number.", "Invalid Font Size", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextEditor());
    }
}
