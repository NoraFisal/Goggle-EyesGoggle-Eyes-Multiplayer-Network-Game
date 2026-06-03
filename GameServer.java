package com.mycompany.network_project;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
  private static final int PORT = 3333;
  private static final int MAX_PLAYERS_IN_WAITING_ROOM = 4;

  private static List<PrintWriter> clientWriters = new ArrayList<>();
  private static List<String> playerNames = new ArrayList<>();
  private static List<String> waitingRoomPlayers = new ArrayList<>();
private static String win="";
  private static Map<String, PrintWriter> playerWriters = new HashMap<>();
  private static Map<String, Integer> scores = new HashMap<>();

  public static void main(String[] args) {
    System.out.println("Game Server is running...");

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      while (true) {
        new ClientHandler(serverSocket.accept()).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String playerName;

    public ClientHandler(Socket socket) {
      this.socket = socket;
    }

    public void run() {
      try {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
          String message = in.readLine();
          if (message == null) break;

          if (message.startsWith("ADD_PLAYER:")) {
            handleAddPlayer(message);
          } else if (message.startsWith("ENTER_WAITING_ROOM:")) {
            handleEnterWaitingRoom();
          } else if (message.startsWith("LEAVE_WAITING_ROOM:")) {
            handleLeaveWaitingRoom(message);
          } else if (message.startsWith("PLAYER_SCORE:")) {
            handlePlayerScore(message);
          } else if (message.startsWith("PLAYER_LEFT:")) {
            handlePlayerLeft(message);
          }
          else if (message.startsWith("START_GAME")) {
                        sendMessageToAll("START_GAME");
         }else if (message.startsWith("PLAYER_SCORE:")) {
            handlePlayerScore(message);}
          
        }
      } catch (IOException e) {
        System.err.println("Connection error with player " + playerName);
      } finally {
        handlePlayerDisconnect();
      }
    }

    private void handleAddPlayer(String message) {
      playerName = message.substring("ADD_PLAYER:".length()).trim();
      if (playerName.isEmpty()) {
        out.println("ERROR: Invalid player name.");
        return;
      }

      synchronized (playerNames) {
        playerNames.add(playerName);
        scores.put(playerName, 0);
        playerWriters.put(playerName, out);
      }

      synchronized (clientWriters) {
        clientWriters.add(out);
      }

      updatePlayers();
    }

    private void handleEnterWaitingRoom() {
      synchronized (waitingRoomPlayers) {
        if (waitingRoomPlayers.size() < MAX_PLAYERS_IN_WAITING_ROOM) {
          waitingRoomPlayers.add(playerName);
          updateWaitingRoomPlayers();

          if (waitingRoomPlayers.size() == MAX_PLAYERS_IN_WAITING_ROOM) {
            sendMessageToWaitingRoom("START_GAME");
          }
        } else {
          out.println("WAITING_ROOM_FULL");
        }
      }
    }

    private void handleLeaveWaitingRoom(String message) {
      String leavingPlayer = message.substring("LEAVE_WAITING_ROOM:".length()).trim();
      synchronized (waitingRoomPlayers) {
        waitingRoomPlayers.remove(leavingPlayer);
        updateWaitingRoomPlayers();
        checkRemainingPlayers();
      }
    }
    
private void handlePlayerScore(String message) {
    String[] parts = message.substring("PLAYER_SCORE:".length()).split(":");
    if (parts.length != 2) {
        out.println("ERROR: Invalid score format.");
        return;
    }

    String player = parts[0].trim();
    int newScore;
    try {
        newScore = Integer.parseInt(parts[1].trim());
    } catch (NumberFormatException e) {
        out.println("ERROR: Invalid score value.");
        return;
    }

    synchronized (scores) {
        if (scores.containsKey(player)) {
            scores.put(player, newScore); // تحديث السكور
        }
    }

    broadcastScores(); // إرسال السكور المحدث لجميع اللاعبين
    sendLeaderboard(); // إرسال ترتيب اللاعبين بعد كل تحديث للسكور

    if (newScore >= 5) { // إذا وصل اللاعب إلى الحد الأعلى
        sendMessageToAll("GAME_OVER:" + win); // إعلان الفائز
        resetGame(); // إعادة تعيين اللعبة
    }
}

    private void handlePlayerLeft(String message) {
      String leftPlayer = message.substring("PLAYER_LEFT:".length()).trim();
      synchronized (waitingRoomPlayers) {
        waitingRoomPlayers.remove(leftPlayer);
      }

      sendMessageToAll("PLAYER_LEFT_NOTIFICATION:" + leftPlayer);
      checkRemainingPlayers();
    }

    private void handlePlayerDisconnect() {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      synchronized (clientWriters) {
        clientWriters.remove(out);
      }

      synchronized (playerNames) {
        playerNames.remove(playerName);
        scores.remove(playerName);
        playerWriters.remove(playerName);
      }

      synchronized (waitingRoomPlayers) {
        waitingRoomPlayers.remove(playerName);
      }

      updatePlayers();
      updateWaitingRoomPlayers();
    }
    
    

    private void updatePlayers() {
      StringBuilder players = new StringBuilder("UPDATE_PLAYERS:");
      synchronized (playerNames) {
        for (String name : playerNames) {
          players.append(name).append(",");
        }
      }
      sendMessageToAll(players.toString());
    }

    private void updateWaitingRoomPlayers() {
      StringBuilder waitingRoomMessage = new StringBuilder("UPDATE_WAITING_ROOM:");
      synchronized (waitingRoomPlayers) {
        for (String name : waitingRoomPlayers) {
          waitingRoomMessage.append(name).append(",");
        }
      }
      sendMessageToAll(waitingRoomMessage.toString());
    }

    private void resetGame() {
      synchronized (scores) {
        scores.clear();
      }
      synchronized (waitingRoomPlayers) {
        waitingRoomPlayers.clear();
      }
      broadcastScores();
      updateWaitingRoomPlayers();
    }
private void broadcastScores() {
    // بناء الرسالة لتحديث السكور
    StringBuilder scoresMessage = new StringBuilder("UPDATE_SCORES:");
    
    // إنشاء قائمة لفرز اللاعبين حسب السكور
    List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(scores.entrySet());
    
    // فرز اللاعبين حسب السكور من الأعلى إلى الأدنى
    sortedScores.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
    
    // حساب المراكز وإرسال الرسالة
    boolean foundWinner = false; // لتحديد إذا وصل أحدهم للسكور 5
    for (int i = 0; i < sortedScores.size(); i++) {
        Map.Entry<String, Integer> entry = sortedScores.get(i);
        
        // إذا وصل اللاعب إلى 5 نقاط، فهو المركز الأول
        if (!foundWinner && entry.getValue() >= 5) {
            scoresMessage.append("1:").append(entry.getKey()).append("=").append(entry.getValue()).append(",");
            foundWinner = true;
        }
        else if (entry.getValue() < 5) {
            // إضافة اللاعبين الذين لم يصلوا للـ 5 بعد
            scoresMessage.append((i + 1)).append(":").append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
    }
    win=scoresMessage.toString();
    // إرسال الرسالة لجميع اللاعبين
    sendMessageToAll(scoresMessage.toString());
}


    private void sendMessageToAll(String message) {
      synchronized (clientWriters) {
        for (PrintWriter writer : clientWriters) {
          writer.println(message);
        }
      }
    }

    private void sendMessageToWaitingRoom(String message) {
      synchronized (waitingRoomPlayers) {
        for (String player : waitingRoomPlayers) {
          PrintWriter writer = playerWriters.get(player);
          if (writer != null) {
            writer.println(message);
          }
        }
      }
    }
    
    private void sendLeaderboard() {
    // تحويل السكورات إلى قائمة من المدخلات (اللاعبين مع السكور)
    List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(scores.entrySet());

    // ترتيب اللاعبين بناءً على السكور (ترتيب تنازلي)
    sortedScores.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

    // بناء رسالة تحتوي على الترتيب
    StringBuilder leaderboardMessage = new StringBuilder("LEADERBOARD:");
    for (int i = 0; i < sortedScores.size(); i++) {
        Map.Entry<String, Integer> entry = sortedScores.get(i);
        leaderboardMessage.append(i + 1).append(". ").append(entry.getKey()).append(" - ").append(entry.getValue()).append(" points, ");
    }

    // إرسال الرسالة لجميع اللاعبين
    sendMessageToAll(leaderboardMessage.toString());
}
       private void announceWinner() {
    String winner = null;
    int maxScore = -1;

    synchronized (scores) {
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                winner = entry.getKey();
            }
        }
    }

    if (winner != null) {
        sendMessageToAll("GAME_OVER:" + winner + ":" + maxScore); // Send winner name and score
    }
}

  private void checkRemainingPlayers() {
    if (waitingRoomPlayers.size() == 1) { // إذا بقي لاعب واحد فقط
        String remainingPlayer = waitingRoomPlayers.get(0);
        sendMessageToAll("END_GAME:" + remainingPlayer); // إرسال إشعار انتهاء اللعبة
        
        updateWaitingRoomPlayers();
        waitingRoomPlayers.clear();
    }
}

  }
}
