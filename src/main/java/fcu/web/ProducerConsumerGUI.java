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
    private volatile boolean isProducing = false;

    public ProducerConsumerGUI() {
        setTitle("生產者-消費者模式");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Buffer大小:"));
        bufferSizeField = new JTextField(5);
        topPanel.add(bufferSizeField);
        startButton = new JButton("開始生產");
        topPanel.add(startButton);

        add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isProducing) {
                    startProducing();
                } else {
                    stopProducing();
                }
            }
        });
    }

    private void startProducing() {
        try {
            int bufferSize = Integer.parseInt(bufferSizeField.getText());
            buffer = new ArrayBlockingQueue<>(bufferSize);
            isProducing = true;
            startButton.setText("停止生產");
            new Thread(new Producer()).start();
            logArea.append("開始生產...\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入有效的Buffer大小");
        }
    }

    private void stopProducing() {
        isProducing = false;
        startButton.setText("開始生產");
        logArea.append("停止生產...\n");
    }

    private class Producer implements Runnable {
        private Random random = new Random();
        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public void run() {
            while (isProducing) {
                try {
                    String item = createItem();
                    buffer.put(item);
                    logArea.append("生產: " + item + "\n");
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private String createItem() {
            Date now = new Date();
            int id = random.nextInt(900) + 100;
            return sdf.format(now) + " #" + id;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ProducerConsumerGUI().setVisible(true);
            }
        });
    }
}