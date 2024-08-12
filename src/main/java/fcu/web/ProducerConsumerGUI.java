package fcu.web;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class ProducerConsumerGUI extends JFrame {
    private JTextField bufferSizeField;
    private JButton startButton;
    private JTextArea logArea;
    private BlockingQueue<String> buffer;
    private volatile boolean isRunning = false;
    private ExecutorService executorService;

    public ProducerConsumerGUI() {
        setTitle("生產者-消費者模式");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Buffer大小:"));
        bufferSizeField = new JTextField(5);
        topPanel.add(bufferSizeField);
        startButton = new JButton("開始");
        topPanel.add(startButton);

        add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isRunning) {
                    startSimulation();
                } else {
                    stopSimulation();
                }
            }
        });
    }

    private void startSimulation() {
        try {
            int bufferSize = Integer.parseInt(bufferSizeField.getText());
            if (bufferSize <= 0) {
                throw new IllegalArgumentException("Buffer大小必須大於0");
            }
            buffer = new ArrayBlockingQueue<>(bufferSize);
            isRunning = true;
            startButton.setText("停止");
            executorService = Executors.newFixedThreadPool(2);
            executorService.submit(new Producer());
            executorService.submit(new Consumer());
            log("開始模擬...");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入有效的Buffer大小");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void stopSimulation() {
        isRunning = false;
        startButton.setText("開始");
        if (executorService != null) {
            executorService.shutdownNow();
        }
        log("停止模擬...");
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private class Producer implements Runnable {
        private Random random = new Random();
        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public void run() {
            while (isRunning) {
                try {
                    String item = createItem();
                    buffer.put(item);
                    log("生產: " + item + " (Buffer大小: " + buffer.size() + ")");
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private String createItem() {
            Date now = new Date();
            int id = random.nextInt(900) + 100;
            return sdf.format(now) + " #" + id;
        }
    }

    private class Consumer implements Runnable {
        public void run() {
            while (isRunning) {
                try {
                    String item = buffer.take();
                    log("消費: " + item + " (Buffer大小: " + buffer.size() + ")");
                    Thread.sleep(300); // 消費者速度稍慢於生產者
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProducerConsumerGUI().setVisible(true));
    }
}