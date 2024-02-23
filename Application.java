import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class CustomActionListener implements ActionListener {
    private int row;
    private int column;

    public CustomActionListener(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton pressedButton = (JButton) e.getSource();

        if (!(Application.moveMarks[row][column] == 0 ||
                Application.moveMarks[row][column] == 1)) {
            if (Application.player1Active) {
                pressedButton.setText("X");
                Application.turnLabel1.setText("(waiting)");
                Application.turnLabel2.setText("(in turn)");
                Application.moveMarks[row][column] = 1;
            } else {
                pressedButton.setText("O");
                Application.turnLabel1.setText("(in turn)");
                Application.turnLabel2.setText("(waiting)");
                Application.moveMarks[row][column] = 0;
            }

            Application.writer.println("update".concat(String.valueOf(row).concat(String.valueOf(column))));
        } else {
            System.out.printf("[SERVER] Already Marked at row: %d, col: %d\n", row, column);
        }
    }

}

class Application extends JFrame {
    private static Application instance;
    static PrintWriter writer;
    private static JPanel buttonPanel;
    private JPanel infoContainerPanel;
    private JPanel infoPanel;
    private JLabel player1Label;
    private JLabel player2Label;
    static JLabel turnLabel1;
    static JLabel turnLabel2;
    static JPanel winningStatusPanel;
    static JLabel winningStatusLabel;
    static boolean player1Active = true;
    static boolean player2Active = false;
    private static boolean gameWon = false;

    // O marks -> 0, X marks -> 1
    // 9 -> not marked yet
    static int[][] moveMarks = { { 9, 9, 9}, { 9, 9, 9 }, { 9, 9, 9 } };
    private static JButton[][] allButtons = new JButton[3][3];

    public static void setInstance(Application instance) {
        Application.instance = instance;
    }

    public Application(Socket currentClient, boolean player1Active, boolean player2Active) throws IOException {
        Application.player1Active = player1Active;
        Application.player2Active = player2Active;

        writer = new PrintWriter(currentClient.getOutputStream(), true);
        buttonPanel = new JPanel();
        infoContainerPanel = new JPanel();
        infoPanel = new JPanel();

        SwingUtilities.invokeLater(() -> {
            setLayout(new BorderLayout());

        buttonPanel.setBackground(Color.LIGHT_GRAY);
        infoContainerPanel.setBackground(Color.BLACK);

        buttonPanel.setLayout(new GridLayout(3, 3));

        //initializeButtons(true, "");

        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoContainerPanel.setLayout(new GridLayout(1, 2));

        JPanel iJPanel1 = new JPanel();
        JPanel iJPanel2 = new JPanel();

        // Left Panel
        iJPanel1.setLayout(new BoxLayout(iJPanel1, BoxLayout.Y_AXIS));

        player1Label = new JLabel("Player 1");
        player1Label.setAlignmentX(CENTER_ALIGNMENT);
        player1Label.setFont(new Font("Arial", Font.BOLD, 24));

        turnLabel1 = new JLabel("(in turn)");
        turnLabel1.setAlignmentX(CENTER_ALIGNMENT);
        turnLabel1.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel symbolLabel1 = new JLabel("X");
        symbolLabel1.setAlignmentX(CENTER_ALIGNMENT);
        symbolLabel1.setFont(new Font("Arial", Font.BOLD, 24));

        iJPanel1.add(player1Label);
        iJPanel1.add(turnLabel1);
        iJPanel1.add(symbolLabel1);

        // Right Panel
        iJPanel2.setLayout(new BoxLayout(iJPanel2, BoxLayout.Y_AXIS));

        player2Label = new JLabel("Player 2");
        player2Label.setAlignmentX(CENTER_ALIGNMENT);
        player2Label.setFont(new Font("Arial", Font.BOLD, 24));

        turnLabel2 = new JLabel("(waiting)");
        turnLabel2.setAlignmentX(CENTER_ALIGNMENT);
        turnLabel2.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel symbolLabel2 = new JLabel("O");
        symbolLabel2.setAlignmentX(CENTER_ALIGNMENT);
        symbolLabel2.setFont(new Font("Arial", Font.BOLD, 24));

        iJPanel2.add(player2Label);
        iJPanel2.add(turnLabel2);
        iJPanel2.add(symbolLabel2);

        iJPanel1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        iJPanel2.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        infoContainerPanel.add(iJPanel1);
        infoContainerPanel.add(iJPanel2);

        winningStatusPanel = new JPanel();
        winningStatusLabel = new JLabel("Dummy Text!");
        winningStatusPanel.setVisible(false);
        winningStatusPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        winningStatusPanel.setBackground(Color.WHITE);
        winningStatusLabel.setAlignmentX(CENTER_ALIGNMENT);

        winningStatusPanel.add(winningStatusLabel);

        infoPanel.add(infoContainerPanel);
        infoPanel.add(winningStatusPanel);

        infoPanel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));

        add(buttonPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        setTitle("Tic Tac Toe - ".concat((player1Active) ? "Player 1" : "Player 2"));
        setSize(480, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        });
    }

    public void initializeButtons(boolean enableButtons, String pos) {
        SwingUtilities.invokeLater(() -> {
            if (pos.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        JButton button = new JButton();
                        button.setFont(new Font("Arial", Font.BOLD, 24));
                        button.addActionListener(new CustomActionListener(i, j));
    
                        if (enableButtons) {
                            button.setEnabled(true);
                        } else {
                            button.setEnabled(false);
                        }
    
                        buttonPanel.add(button);
                        allButtons[i][j] = button;
                    }
                }
            } else {
                int row = Integer.parseInt(String.valueOf(pos.charAt(0)));
                int col = Integer.parseInt(String.valueOf(pos.charAt(1)));
                String mark = (player1Active) ? "O" : "X";
    
                if (enableButtons) {
                    if (player1Active) {
                        turnLabel1.setText("(in turn)");
                        turnLabel2.setText("(waiting)");
                    } else {
                        turnLabel1.setText("(waiting)");
                        turnLabel2.setText("(in turn)");
                    }
                }
    
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (enableButtons) {
                            allButtons[i][j].setEnabled(true);
    
                            if (i == row && j == col) {
                                allButtons[i][j].setText(mark);
                                moveMarks[i][j] = (player1Active) ? 0 : 1;
                            }
    
                        } else {
                            allButtons[i][j].setEnabled(false);
                        }
                    }
                }
    
                won();
            }
            
        });
    }

    static void won() {
        int totalSum = 0;
        boolean playerWon = false;
        int[] row1 = { moveMarks[0][0], moveMarks[0][1], moveMarks[0][2] };
        int[] row2 = { moveMarks[1][0], moveMarks[1][1], moveMarks[1][2] };
        int[] row3 = { moveMarks[2][0], moveMarks[2][1], moveMarks[2][2] };
        int[] col1 = { moveMarks[0][0], moveMarks[1][0], moveMarks[2][0] };
        int[] col2 = { moveMarks[0][1], moveMarks[1][1], moveMarks[2][1] };
        int[] col3 = { moveMarks[0][2], moveMarks[1][2], moveMarks[2][2] };
        int[] diag1 = { moveMarks[0][0], moveMarks[1][1], moveMarks[2][2] };
        int[] diag2 = { moveMarks[0][2], moveMarks[1][1], moveMarks[2][0] };
        int[][] allPossibleMoves = { row1, row2, row3, col1, col2, col3, diag1, diag2 };

        for (int i = 0; i < allPossibleMoves.length; i++) {
            int sum = 0;
            for (int index = 0; index < 3; index++) {
                sum += allPossibleMoves[i][index];
            }

            if (sum == 3) {
                playerWon = true;
                Application.gameWon = (player1Active) ? true : false;
                System.out.println("[SERVER] Player 1 Won!");
                winningStatusPanel.setVisible(true);
                winningStatusLabel.setText("Player 1 Won!");
                showPopUp("Player 1 Won!");
            }
            else if (sum == 0) {
                playerWon = true;
                Application.gameWon = (player2Active) ? true : false;
                System.out.println("[SERVER] Player 2 Won!");
                winningStatusPanel.setVisible(true);
                winningStatusLabel.setText("Player 2 Won!");
                showPopUp("Player 2 Won!");
            } 

            if (i <= 2) totalSum += sum;
        }

        if (!playerWon && (totalSum == 4 || totalSum == 5)) {
            Application.gameWon = (player1Active) ? true : false;
            System.out.println("[SERVER] It's a Tie!");
            winningStatusPanel.setVisible(true);
            winningStatusLabel.setText("It's a Tie!");
            showPopUp("It's a Tie!"); 
        }


    }

    private static void showPopUp(String status) {
        String[] responses = { "Retry", "Quit" };

        int response = JOptionPane.showOptionDialog(null, status, "Game Over", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, responses, 0);

        if (response == 1) {
            for (int i = 0; i < moveMarks.length; i++) {
                for (int j = 0; j < 3; j++) {
                    moveMarks[i][j] = 9;
                }
            }

            disposeApplication();
            System.out.println("[SERVER] Game Over");
            writer.println("*QUITTHEGAME*");
            
        } else {
            winningStatusPanel.setVisible(false);

            SwingUtilities.invokeLater(() -> {
                if (Application.gameWon) {
                    turnLabel1.setText(player1Active ? "(in turn)" : "(waiting)");
                    turnLabel2.setText(player1Active ? "(waiting)" : "(in turn)");
                } else {
                    turnLabel1.setText(player1Active ? "(waiting)" : "(in turn)");
                    turnLabel2.setText(player1Active ? "(in turn)" : "(waiting)");
                }            

                for (int i = 0; i < moveMarks.length; i++) {
                    for (int j = 0; j < 3; j++) {
                        moveMarks[i][j] = 9;
                        allButtons[i][j].setText("");
    
                        if (Application.gameWon) {
                            allButtons[i][j].setEnabled(true);
                        } else {
                            allButtons[i][j].setEnabled(false);
                        }
                    }
                } 
            });

        }
    }

    public static void disposeApplication() {
        instance.dispose();
    }
}
