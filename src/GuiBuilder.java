import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class GuiBuilder implements Serializable {
    JFrame frame;
    JPanel panel;
    JButton nextButton;
    JTextArea qArea, aArea;
    JLabel qLabel, aLabel;
    ArrayList<Card> cardList;
    int currentId, mode;
    boolean isShown,
    directoryExists = false;
    Properties p = System.getProperties();
    String dir = p.getProperty("user.home") + "//Desktop";
    File currentDirectory;

    public void buildGui() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        cardList = new ArrayList<Card>();

        currentId = 0;

        frame = new JFrame("Quiz cards");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 520);
        frame.setResizable(false);

        Font boldFont = new Font("Serif", Font.BOLD, 20);
        Font normalFont = new Font("Serif", Font.PLAIN, 16);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem newItem = new JMenuItem("Новый");
        newItem.addActionListener(new NewListener());
        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.addActionListener(new SaveListener());
        JMenuItem loadItem = new JMenuItem("Загрузить");
        loadItem.addActionListener(new LoadListener());

        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);

        qLabel = new JLabel("Вопрос");
        aLabel = new JLabel("Ответ");

        qLabel.setFont(boldFont);
        aLabel.setFont(boldFont);
        aLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        qArea = new JTextArea(7, 50);
        qArea.setFont(normalFont);
        qArea.setLineWrap(true);
        JScrollPane qScroller = new JScrollPane(qArea);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        aArea = new JTextArea(7, 50);
        aArea.setFont(normalFont);
        aArea.setLineWrap(true);
        JScrollPane aScroller = new JScrollPane(aArea);
        aScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        aScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        nextButton = new JButton("Сохранить карту");
        nextButton.addActionListener(new ButtonListener());

        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(qLabel);
        panel.add(qScroller);
        panel.add(aLabel);
        panel.add(aScroller);
        panel.add(nextButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }

    private void makeCard(String question, String answer) {
        Card card = new Card(question, answer);
        cardList.add(card);
    }

    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (mode == 2) {
                if (!isShown) {
                    aArea.setText(cardList.get(currentId).getAnswer());
                    nextButton.setText("Показать следующую карту");
                    isShown = true;
                } else {
                    showNextCard();
                    nextButton.setText("Показать ответ");
                }

            } else {
                makeCard(qArea.getText(), aArea.getText());
                qArea.setText("");
                aArea.setText("");
                qArea.requestFocus();
            }
        }
    }

    private void showNextCard() {
        currentId++;

        if (currentId == cardList.size()) {
            JOptionPane.showMessageDialog(frame, "Карточки кончились");
            nextButton.setEnabled(false);
            return;
        }

        qArea.setText(cardList.get(currentId).getQuestion());
        aArea.setText("");
        isShown = false;
    }

    class NewListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            mode = 1;
            currentId = 0;

            qArea.setText("");
            aArea.setText("");
            qArea.setEditable(true);
            aArea.setEditable(true);

            nextButton.setEnabled(true);
            nextButton.setText("Сохранить карту");
        }
    }

    class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            JFileChooser fc = new JFileChooser();

            UIManager.put("FileChooser.saveButtonText", "Сохранить");

            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (!directoryExists)
                fc.setCurrentDirectory(new File(dir));
            else
                fc.setCurrentDirectory(currentDirectory);

            fc.showSaveDialog(frame);

            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fc.getSelectedFile()));
                directoryExists = true;
                currentDirectory = fc.getCurrentDirectory();
                oos.writeObject(cardList);
                oos.flush();
                oos.close();
            } catch (Exception ex) {
                if (fc.getSelectedFile() == null)
                    return;
                else {
                    JOptionPane.showMessageDialog(frame, "Не могу сохранить файл");
                    ex.printStackTrace();
                }
            }
        }
    }

    class LoadListener implements  ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            mode = 2;

            JFileChooser fc = new JFileChooser();

            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (!directoryExists)
                fc.setCurrentDirectory(new File(dir));
            else
                fc.setCurrentDirectory(currentDirectory);

            fc.showOpenDialog(frame);

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()));
                cardList = (ArrayList<Card>) ois.readObject();

                directoryExists = true;
                currentDirectory = fc.getCurrentDirectory();

                qArea.setEditable(false);
                aArea.setEditable(false);

                qArea.setText(cardList.get(currentId).getQuestion());

                nextButton.setText("Показать ответ");
                isShown = false;

            } catch (Exception ex) {
                if (fc.getSelectedFile() == null)
                    return;
                else
                JOptionPane.showMessageDialog(frame, "Не могу загрузить файл");
                ex.printStackTrace();
            }
        }
    }
}
