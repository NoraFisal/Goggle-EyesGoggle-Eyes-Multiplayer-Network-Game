/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.network_project;

import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ConnectedPlayersFrame extends JFrame {
    private JTable playerTable;
    private DefaultTableModel tableModel;
    private JButton playButton;

    public ConnectedPlayersFrame() {
        setTitle("Connected Players");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Connected Players", SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(233, 87, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(loadCustomFont(24));

        // إعداد جدول لعرض الأسماء
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
tableHeader.setDefaultRenderer(headerRenderer);


        // محاذاة النص في الخلايا إلى المنتصف
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER); // محاذاة النص للمنتصف
        playerTable.setDefaultRenderer(Object.class, centerRenderer);

        playerTable.setFont(new Font("Arial", Font.BOLD, 18));
        playerTable.setRowHeight(30);

        // زر Play
        playButton = new JButton("Play");
        playButton.setBackground(Color.WHITE);
        playButton.setForeground(new Color(233, 87, 48));
        playButton.setFocusPainted(false);
        playButton.setFont(loadCustomFont(24));

        // إضافة العناصر إلى الواجهة
        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(playerTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateConnectedPlayers(String[] players) {
        tableModel.setRowCount(0);  // مسح البيانات السابقة
        for (String player : players) {
            if (player != null && !player.isEmpty()) {
                tableModel.addRow(new Object[]{player});  // إضافة لاعب جديد إلى الجدول
            }
        }
    }

    public void addPlayButtonListener(ActionListener listener) {
        playButton.addActionListener(listener);
    }

    private Font loadCustomFont(float size) {
        try {
            // تحميل الخط المخصص
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\\\Users\\\\HUAWEI\\\\Desktop\\\\Super Morning.ttf"));
            return customFont.deriveFont(size); // إرجاع الخط بحجم مخصص
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) size); // خط احتياطي في حالة فشل تحميل الخط
        }
    }}
