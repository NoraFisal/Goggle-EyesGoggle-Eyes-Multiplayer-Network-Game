package com.mycompany.network_project;




import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Random;

public class GameClient extends JFrame {
  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;

  
 private JLabel timerLabel; // Label to display the timer
private Timer roundTimer; // Timer for the countdown logic
private int timeRemaining = 300; // Time remaining for each round (5 minutes in seconds)

  
  
  private String playerName;
  private String[] players = new String[4];
  private String[] waitingRoomPlayers = new String[4];
  private int waitingNumber = 0;

  private JTextArea scoresDisplay;

  // Frames
  private RegistrationFrame registrationFrame;
  private ConnectedPlayersFrame connectedDevicesFrame;
  private WaitingRoomFrame waitingRoomFrame;
  private JFrame gameFrame;

  private JTextArea gameDisplay;
  private JTextField answerField;
  private JButton submitButton;
  private JButton leaveButton;
  private int currentRound = 0;
  private int score = 0;
  private int differentDigit;

  public GameClient() {
    registrationFrame = new RegistrationFrame();
    connectedDevicesFrame = new ConnectedPlayersFrame();
    waitingRoomFrame = new WaitingRoomFrame();

    connectToServer();

    // Register the enter button listener
    registrationFrame.addEnterButtonListener(e -> {
      playerName = registrationFrame.getUsername();
      if (!playerName.isEmpty()) {
        sendMessageToServer("ADD_PLAYER:" + playerName);
        waitingNumber++;

        registrationFrame.setVisible(false);
        showConnectedDevicesFrame();
      } else {
        JOptionPane.showMessageDialog(null, "Please enter a username.", "Warning", JOptionPane.WARNING_MESSAGE);
      }
    });

    // Play button logic
    connectedDevicesFrame.addPlayButtonListener(e -> {
      int playerCount = 0;
      for (String player : waitingRoomPlayers) {
        if (player != null && !player.isEmpty()) {
          playerCount++;
        }
      }

      if (playerCount >= 4) {
        if (playerName == null || !playerName.equals(waitingRoomPlayers[0]) &&
            !playerName.equals(waitingRoomPlayers[1]) &&
            !playerName.equals(waitingRoomPlayers[2]) &&
            !playerName.equals(waitingRoomPlayers[3])) {
          JOptionPane.showMessageDialog(connectedDevicesFrame,
              "The game is already in progress. You cannot join now.",
              "Game Full",
              JOptionPane.WARNING_MESSAGE);
          System.exit(0); // Exit if game is full
        } else {
          sendMessageToServer("ENTER_WAITING_ROOM:" + playerName);
          showWaitingRoomFrame();
          updateWaitingRoomPlayers();
        }
      } else {
        sendMessageToServer("ENTER_WAITING_ROOM:" + playerName);
        showWaitingRoomFrame();
        updateWaitingRoomPlayers();
      }
    });

    registrationFrame.setVisible(true);
  }
  public GameClient(int number) {
    registrationFrame = new RegistrationFrame();
    connectedDevicesFrame = new ConnectedPlayersFrame();
    waitingRoomFrame = new WaitingRoomFrame();

    connectToServer();

    // Register the enter button listener
    registrationFrame.addEnterButtonListener(e -> {
      playerName = registrationFrame.getUsername();
      if (!playerName.isEmpty()) {
        sendMessageToServer("ADD_PLAYER:" + playerName);
        waitingNumber++;

        registrationFrame.setVisible(false);
        showConnectedDevicesFrame();
      } else {
        JOptionPane.showMessageDialog(null, "Please enter a username.", "Warning", JOptionPane.WARNING_MESSAGE);
      }
    });

    // Play button logic
    connectedDevicesFrame.addPlayButtonListener(e -> {
      int playerCount = 0;
      for (String player : waitingRoomPlayers) {
        if (player != null && !player.isEmpty()) {
          playerCount++;
        }
      }

      if (playerCount >= 4) {
        if (playerName == null || !playerName.equals(waitingRoomPlayers[0]) &&
            !playerName.equals(waitingRoomPlayers[1]) &&
            !playerName.equals(waitingRoomPlayers[2]) &&
            !playerName.equals(waitingRoomPlayers[3])) {
          JOptionPane.showMessageDialog(connectedDevicesFrame,
              "The game is already in progress. You cannot join now.",
              "Game Full",
              JOptionPane.WARNING_MESSAGE);
          System.exit(0); // Exit if game is full
        } else {
          sendMessageToServer("ENTER_WAITING_ROOM:" + playerName);
          showWaitingRoomFrame();
          updateWaitingRoomPlayers();
        }
      } else {
        sendMessageToServer("ENTER_WAITING_ROOM:" + playerName);
        showWaitingRoomFrame();
        updateWaitingRoomPlayers();
      }
    });

  }

  private void showConnectedDevicesFrame() {
    registrationFrame.setVisible(false);
    connectedDevicesFrame.setVisible(true);
    updateConnectedPlayers();
  }

  private void showWaitingRoomFrame() {
    connectedDevicesFrame.setVisible(false);
    waitingRoomFrame.setVisible(true);
  }
public void showGameFrame() {
    if (gameFrame != null && gameFrame.isVisible()) {
        return; // Prevent opening multiple frames
    }

    registrationFrame.dispose();
    waitingRoomFrame.setVisible(false);

    gameFrame = new JFrame("Find the Different Number");
    gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
            int confirm = JOptionPane.showConfirmDialog(gameFrame,
                "Are you sure you want to exit the game?", "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                sendMessageToServer("PLAYER_LEFT:" + playerName);
                closeConnection();
                System.exit(0);
            }
        }
    });

    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    JLabel instructionLabel = new JLabel("Find the different number!", SwingConstants.CENTER);
    instructionLabel.setOpaque(true);
    instructionLabel.setBackground(new Color(233, 87, 48));
    instructionLabel.setForeground(Color.WHITE); // Set the text color for contrast (optional)
    instructionLabel.setPreferredSize(new Dimension(150, 50));
    instructionLabel.setFont(loadCustomFont(20f));
    
    
    gameDisplay = new JTextArea();
    gameDisplay.setEditable(false);
    gameDisplay.setFont(loadCustomFont(20f));

    timerLabel = new JLabel(formatTime(timeRemaining), SwingConstants.CENTER);
   
// Add timer label
    timerLabel.setFont(loadCustomFont(20f));
    timerLabel.setForeground(new Color(233, 87, 48));// Make it red for visibility

    answerField = new JTextField();
    submitButton = new JButton("Submit");
    leaveButton = new JButton("Leave");

    
    submitButton.setFont(loadCustomFont(20f));
    submitButton.setForeground(Color.white);
    submitButton.setBackground(new Color(255, 82, 71));
    
    leaveButton.setFont(loadCustomFont(20f));
    leaveButton.setForeground(Color.white);
    leaveButton.setBackground(Color.RED);
            
    scoresDisplay = new JTextArea("Scores:\n", 1, 20);
    scoresDisplay.setEditable(false);
    scoresDisplay.setFont(loadCustomFont(20f));

    JPanel scoresPanel = new JPanel();
scoresPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
scoresPanel.add(scoresDisplay);
    gameFrame.getRootPane().setDefaultButton(submitButton);

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(instructionLabel, BorderLayout.NORTH);
    topPanel.add(timerLabel, BorderLayout.SOUTH); // Add the timer to the top panel
    gameFrame.add(topPanel, BorderLayout.NORTH);
    gameFrame.add(new JScrollPane(gameDisplay), BorderLayout.CENTER);
    gameFrame.add(scoresPanel, BorderLayout.EAST);

    JPanel inputPanel = new JPanel(new BorderLayout());
    inputPanel.add(leaveButton, BorderLayout.WEST);
    inputPanel.add(answerField, BorderLayout.CENTER);
    inputPanel.add(submitButton, BorderLayout.EAST);

    gameFrame.add(inputPanel, BorderLayout.SOUTH);

    submitButton.addActionListener(e -> checkAnswer());
    leaveButton.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(gameFrame,
            "Are you sure you want to leave the game?", "Confirm Leave", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            sendMessageToServer("PLAYER_LEFT:" + playerName);
            gameFrame.dispose();
            resetGame();
            connectedDevicesFrame.setVisible(true);
        }
    });

    startRoundTimer(); // Start the timer
    nextRound();

    gameFrame.setVisible(true); 
}

  private void connectToServer() {
    try {
      socket = new Socket("localhost", 3333);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      new Thread(() -> {
        try {
          String message;
          while ((message = in.readLine()) != null) {
            handleServerMessage(message);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendMessageToServer(String message) {
    out.println(message);
  }
  
  public void tellshowGameFrame(){
      sendMessageToServer("START_GAME");
}

  private void handleServerMessage(String message) {
    if (message.startsWith("UPDATE_PLAYERS:")) {
      String playerList = message.substring("UPDATE_PLAYERS:".length());
      players = playerList.split(",");
      updateConnectedPlayers();
    } else if (message.startsWith("UPDATE_SCORES:")) {
      String scoresList = message.substring("UPDATE_SCORES:".length());
      updateScoresDisplay(scoresList);
    } else if (message.startsWith("UPDATE_WAITING_ROOM:")) {
      String waitingList = message.substring("UPDATE_WAITING_ROOM:".length());
      waitingRoomPlayers = waitingList.split(",");
      updateWaitingRoomPlayers();
    } else if (message.equals("START_GAME")) {
      startGame();
    } else if (message.startsWith("GAME_OVER:")) {
    String winner = message.substring("GAME_OVER:".length()+14);
    String formattedWinner = winner.replace(",", "\n");
    JLabel messageLabel = new JLabel("<html><center>Game over!<br><br>The winner is:<br>" +
                formattedWinner.replace("\n", "<br>") + "</center></html>");
        messageLabel.setFont(loadCustomFont(20f));
        messageLabel.setForeground(new Color(233, 87, 48)); // لون النص الأحمر
        
        ImageIcon icon = new ImageIcon("C:\\Users\\HUAWEI\\Desktop\\Eye.jpg"); // ضع مسار الصورة هنا
        
        Image img = icon .getImage(); // الحصول على الصورة
        Image scaledImg = img.getScaledInstance(75, 75, Image.SCALE_SMOOTH); // تغيير الحجم إلى 50x50 بكسل
        ImageIcon scaledIcon = new ImageIcon(scaledImg);
        
        JOptionPane.showMessageDialog(null, messageLabel, "Game Over", JOptionPane.INFORMATION_MESSAGE, scaledIcon);
    
    if (gameFrame != null) {
        gameFrame.dispose(); // إغلاق نافذة اللعبة
    }
    
    resetGame(); // إعادة تعيين اللعبة
    showConnectedDevicesFrame(); // إعادة الجميع إلى نافذة Connected Players
}
else if (message.startsWith("END_GAME:")) {
    String remainingPlayer = message.substring("END_GAME:".length());
    JOptionPane.showMessageDialog(null,
        "Game over! Only one player remains: " + remainingPlayer,
        "Game Over", JOptionPane.INFORMATION_MESSAGE);
    if (gameFrame != null) {
        gameFrame.dispose(); // إغلاق نافذة اللعبة
    }
    resetGame(); // إعادة تعيين اللعبة
    showConnectedDevicesFrame(); // إظهار نافذة التسجيل من جديد
}

    
    else if (message.startsWith("PLAYER_LEFT_NOTIFICATION:")) {
      String leftPlayer = message.substring("PLAYER_LEFT_NOTIFICATION:".length());
      JOptionPane.showMessageDialog(gameFrame,
          leftPlayer + " has left the game.", "Player Left", JOptionPane.INFORMATION_MESSAGE);
      updateConnectedPlayers();
     } else if (message.startsWith("GAME_OVER:")) {
    String[] parts = message.split(":");
    String winnerName = parts[1];
    String winnerScore = parts[2];
    JOptionPane.showMessageDialog(null, 
        "Game Over! The winner is: " + winnerName + " with a score of " + winnerScore, 
        "Game Over", 
        JOptionPane.INFORMATION_MESSAGE);
    gameFrame.dispose();
    resetGame();
    showConnectedDevicesFrame();


      resetGame();
      registrationFrame.setVisible(true);
    } else if (message.startsWith("ERROR:")) {
      JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void updateConnectedPlayers() {
    connectedDevicesFrame.updateConnectedPlayers(players);
  }

  private void updateWaitingRoomPlayers() {
    waitingRoomFrame.updatePlayerTable(waitingRoomPlayers);
  }

 private void startGame() {
    int playerCount = 0;
    for (String player : waitingRoomPlayers) {
      if (player != null && !player.isEmpty()) {
        playerCount++;
      }
    }

    if (playerCount <= 4 && playerCount>=2) {
      
      showGameFrame();
    }
  }
private void resetGame() {
    
    
    waitingRoomPlayers = new String[4];
    currentRound = 0;
    score = 0;
    if (gameFrame != null) {
        gameFrame.dispose(); // تأكد من إغلاق النافذة إذا كانت موجودة
        gameFrame = null;
    } }



// Format time as MM:SS
private String formatTime(int seconds) {
    int minutes = seconds / 60;
    int remainingSeconds = seconds % 60;
    return String.format("%02d:%02d", minutes, remainingSeconds);
}

// Start the round timer
private void startRoundTimer() {
    if (roundTimer != null) {
        roundTimer.stop(); // Stop any previous timer
    }

    timeRemaining = 300; // Reset timer to 5 minutes
    roundTimer = new Timer(1000, e -> {
        if (timeRemaining > 0) {
            timeRemaining--;
            timerLabel.setText(formatTime(timeRemaining));
        } else {
            roundTimer.stop();
            handleRoundTimeout();
        }
    });
    roundTimer.start();
}

// Handle timeout for the round
private void handleRoundTimeout() {
    JOptionPane.showMessageDialog(gameFrame, "Time's up! No one found the answer.", "Round Timeout", JOptionPane.WARNING_MESSAGE);
    if (totalRounds >= 5) { // End game after 5 rounds
        announceWinner();
    } else {
        nextRound();
    }
}


private int totalRounds = 0; // Counter for the number of rounds played

private void nextRound() {
    if (score >= 5) { // If 5 rounds are completed, end the game
        sendMessageToServer("PLAYER_WON:");
         
        return;
    }

    Random rand = new Random();
    int rowCount = 13; // Increased number of rows
    int colCount = 50; // Increased number of columns

    int similarDigit = rand.nextInt(10);
    differentDigit = rand.nextInt(10);

    // Ensure the different digit is not the same as the similar digit
    while (differentDigit == similarDigit) {
        differentDigit = rand.nextInt(10);
    }

    int differentRow = rand.nextInt(rowCount);
    int differentCol = rand.nextInt(colCount);

    // Build the game grid
    char[][] grid = new char[rowCount][colCount];
    for (int row = 0; row < rowCount; row++) {
        for (int col = 0; col < colCount; col++) {
            if (row == differentRow && col == differentCol) {
                grid[row][col] = (char) ('0' + differentDigit);
            } else {
                grid[row][col] = (char) ('0' + similarDigit);
            }
        }
    }

    // تحويل الشبكة إلى نص واحد باستخدام الحلقات
    StringBuilder gridText = new StringBuilder();
    for (int row = 0; row < rowCount; row++) {
        for (int col = 0; col < colCount; col++) {
            gridText.append(grid[row][col]);
        }
        gridText.append("\n");
    }

    // عرض الشبكة
    gameDisplay.setText(gridText.toString());
    gameDisplay.setFont(new Font("Monospaced", Font.BOLD, 28)); // تعيين خط بحجم أكبر
    answerField.setText(""); // مسح حقل الإجابة للجولة الجديدة
    
}

 

private void checkAnswer() {
    String answer = answerField.getText().trim();
    if (!answer.isEmpty()) {
        try {
            int answerInt = Integer.parseInt(answer);
            if (answerInt == differentDigit) {
                score++;
                sendMessageToServer("PLAYER_SCORE:" + playerName + ":" + score); // Update score on the server
            }
            nextRound(); // Automatically proceed to the next round
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(gameFrame, "Please enter a valid number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    answerField.setText(""); // Clear the input field
}


private void announceWinner() {
    sendMessageToServer("GAME_OVER:" + playerName + ":" + score); // Send player name and score to the server
    JOptionPane.showMessageDialog(gameFrame, 
        "Game Over! The winner is: " + playerName + " with a score of " + score, 
        "Game Over", 
        JOptionPane.INFORMATION_MESSAGE);

    gameFrame.dispose(); // Close the game frame
    resetGame(); // Reset the game state
    showConnectedDevicesFrame(); // Return to the connected devices screen



    resetGame(); // Reset the game state
    showConnectedDevicesFrame(); // Return to the connected devices screen
}



  private void updateScoresDisplay(String scoresList) {
    String[] scores = scoresList.split(",");
    scoresDisplay.setText("Scores:\n");
    for (String score : scores) {
      scoresDisplay.append(score + "\n");
    }
  }

  private void closeConnection() {
    try {
      if (socket != null) {
        socket.close();
      }
      if (out != null) {
        out.close();
      }
      if (in != null) {
        in.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  private Font loadCustomFont(float size) {
        try {
            // Load the custom font file
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\\\Users\\\\HUAWEI\\\\Desktop\\\\Super Morning.ttf"));
            return customFont.deriveFont(size); // Return the font with the specified size
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) size); // Fallback font
        }
    }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GameClient());
  }
}
