/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.network_project;


// WaitingRoomFrame.java
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
 
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WaitingRoomFrame extends JFrame {
    private JTable playerTable;
    private DefaultTableModel tableModel;
    private ArrayList<String> currentPlayers;
    private JLabel countdownLabel;
    private Timer countdownTimer;
    private int timeLeft = 9;
    private boolean isTimerRunning = false;
    private boolean gameStarted = false;

    public WaitingRoomFrame() {
        setTitle("Waiting Room");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a custom panel with a background image
       
        setLayout(new BorderLayout());

        // Title label with custom font and style
        JLabel titleLabel = new JLabel("Waiting Room", SwingConstants.CENTER);
        titleLabel.setFont(loadCustomFont(24f));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(233, 87, 48));
        titleLabel.setForeground(Color.WHITE);

        // Table for player names
        String[] columnNames = {"Player Names"};
        tableModel = new DefaultTableModel(columnNames, 0);
        playerTable = new JTable(tableModel);
        playerTable.setFillsViewportHeight(true);
        
        // Customize table header
JTableHeader tableHeader = playerTable.getTableHeader();
DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
headerRenderer.setHorizontalAlignment(SwingConstants.CENTER); // محاذاة النص للوسط
headerRenderer.setBackground(Color.WHITE); // خلفية بيضاء
headerRenderer.setForeground(new Color(233, 87, 48)); // لون النص برتقالي
headerRenderer.setFont(new Font("Arial", Font.BOLD, 18)); // حجم النص غامق وكبير

// Apply the renderer to the header
tableHeader.setDefaultRenderer(headerRenderer);
        
    
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
centerRenderer.setHorizontalAlignment(SwingConstants.CENTER); // المحاذاة للوسط
playerTable.setDefaultRenderer(Object.class, centerRenderer);

playerTable.setFont(new Font("Arial",Font.BOLD,18));
playerTable.setRowHeight(30);



        // Countdown timer label with custom font
        countdownLabel = new JLabel(formatTime(timeLeft), SwingConstants.CENTER);
        countdownLabel.setFont(loadCustomFont(24f));
        countdownLabel.setForeground(new Color(233, 87, 48)); 
        countdownLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // حواف متساوية من جميع الجهات

        


        // Bottom panel for countdown
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(countdownLabel, BorderLayout.CENTER);
        bottomPanel.setOpaque(false); // Transparent background

        // Add components to the main layout
        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(playerTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        currentPlayers = new ArrayList<>();

        // Initialize countdown timer
        countdownTimer = new Timer(1000, e -> {
            if (timeLeft > 0) {
                timeLeft--;
                countdownLabel.setText(formatTime(timeLeft));
            } else {
                countdownTimer.stop();
                if (!gameStarted) {
                    gameStarted = true;
                    transitionToGame(); // Move to the game
                }
            }
        });
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public void updatePlayerTable(String[] players) {
        tableModel.setRowCount(0);
        currentPlayers.clear();
        for (String player : players) {
            if (player != null && !player.isEmpty()) {
                tableModel.addRow(new Object[]{player});
                currentPlayers.add(player);
            }
        }
        manageCountdown();
    }

    private void manageCountdown() {
        int playerCount = currentPlayers.size();
        if (playerCount < 2) {
            countdownTimer.stop();
            isTimerRunning = false;
            timeLeft = 9;
            countdownLabel.setText(formatTime(timeLeft));
            gameStarted = false;
        } else if (!isTimerRunning) {
            startCountdown();
        }
    }

    private void startCountdown() {
        countdownLabel.setText(formatTime(timeLeft));
        countdownTimer.start();
        isTimerRunning = true;
    }

    private void transitionToGame() {
        setVisible(false); // Hide the waiting room
        SwingUtilities.invokeLater(() -> {
            GameClient gameClient = new GameClient(1);
            gameClient.tellshowGameFrame(); // Show the integrated game frame
        });
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
        SwingUtilities.invokeLater(() -> new WaitingRoomFrame().setVisible(true));
    }
}
