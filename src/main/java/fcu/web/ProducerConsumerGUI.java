package fcu.web;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerGUI extends JFrame {
    // GUI 元件
    private JTextField bufferSizeField;
    private JButton startButton;
    private JTextArea logArea;
    private JTextArea bufferContentArea;
    private JProgressBar bufferProgressBar;
    private BufferChart bufferChart;

    // 緩衝區和執行緒控制
    private PriorityBlockingQueue<Item> buffer;
    private volatile boolean isRunning = false;
    private ExecutorService executorService;
    private final AtomicInteger currentBufferSize = new AtomicInteger(0);
    private int maxBufferSize;

    // 構造函數
    public ProducerConsumerGUI() {
        setTitle("D1204433 林俊傑 生產者-消費者模式");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 頂部面板設置
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Buffer大小:"));
        bufferSizeField = new JTextField(5);
        topPanel.add(bufferSizeField);
        startButton = new JButton("開始");
        topPanel.add(startButton);
        add(topPanel, BorderLayout.NORTH);

        // 中央面板設置
        JPanel centerPanel = new JPanel(new GridLayout(2, 2));
        logArea = new JTextArea();
        logArea.setEditable(false);
        centerPanel.add(new JScrollPane(logArea));

        bufferContentArea = new JTextArea();
        bufferContentArea.setEditable(false);
        centerPanel.add(new JScrollPane(bufferContentArea));

        bufferProgressBar = new JProgressBar(0, 100);
        bufferProgressBar.setStringPainted(true);
        centerPanel.add(bufferProgressBar);

        bufferChart = new BufferChart();
        centerPanel.add(bufferChart);

        add(centerPanel, BorderLayout.CENTER);

        // 按鈕事件監聽器
        startButton.addActionListener(e -> {
            if (!isRunning) {
                startSimulation();
            } else {
                stopSimulation();
            }
        });
    }

    // 開始模擬
    private void startSimulation() {
        try {
            maxBufferSize = Integer.parseInt(bufferSizeField.getText());
            if (maxBufferSize <= 0) {
                throw new IllegalArgumentException("Buffer大小必須大於0");
            }
            // 初始化優先隊列作為緩衝區
            buffer = new PriorityBlockingQueue<>(maxBufferSize, Comparator.comparingInt(Item::getId));
            currentBufferSize.set(0);
            isRunning = true;
            startButton.setText("停止");
            executorService = Executors.newFixedThreadPool(2);
            executorService.submit(new Producer());
            executorService.submit(new Consumer());
            bufferProgressBar.setMaximum(maxBufferSize);
            log("開始模擬...");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入有效的Buffer大小");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // 停止模擬
    private void stopSimulation() {
        isRunning = false;
        startButton.setText("開始");
        if (executorService != null) {
            executorService.shutdownNow();
        }
        log("停止模擬...");
    }

    // 記錄日誌
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // 更新緩衝區內容顯示
    private void updateBufferContent() {
        SwingUtilities.invokeLater(() -> {
            bufferContentArea.setText("");
            PriorityQueue<Item> tempQueue = new PriorityQueue<>(buffer);
            while (!tempQueue.isEmpty()) {
                bufferContentArea.append(tempQueue.poll().toString() + "\n");
            }
            int size = currentBufferSize.get();
            bufferProgressBar.setValue(size);
            bufferProgressBar.setString(size + " / " + maxBufferSize);
            bufferChart.updateChart(size);
        });
    }

    // 物品類別
    private static class Item implements Comparable<Item> {
        private final String timestamp;
        private final int id;

        public Item(String timestamp, int id) {
            this.timestamp = timestamp;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return timestamp + " #" + id;
        }

        @Override
        public int compareTo(Item other) {
            return Integer.compare(this.id, other.id);
        }
    }

    // 生產者類別
    private class Producer implements Runnable {
        private final Random random = new Random();
        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public void run() {
            while (isRunning) {
                try {
                    if (currentBufferSize.get() < maxBufferSize) {
                        Item item = createItem();
                        buffer.put(item);  // 將物品加入緩衝區，O(log n)複雜度
                        int newSize = currentBufferSize.incrementAndGet();
                        log("生產: " + item + " (Buffer大小: " + newSize + ")");
                        updateBufferContent();
                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log("生產者發生錯誤: " + e.getMessage());
                }
            }
        }

        private Item createItem() {
            Date now = new Date();
            int id = random.nextInt(900) + 100;
            return new Item(sdf.format(now), id);
        }
    }

    // 消費者類別
    private class Consumer implements Runnable {
        public void run() {
            while (isRunning) {
                try {
                    Item item = buffer.take();  // 自動取出最小編號的物品，O(log n)複雜度
                    int newSize = currentBufferSize.decrementAndGet();
                    log("消費: " + item + " (Buffer大小: " + newSize + ")");
                    updateBufferContent();
                    Thread.sleep(300);  // 消費者速度稍慢於生產者
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log("消費者發生錯誤: " + e.getMessage());
                }
            }
        }
    }

    // 緩衝區圖表類別
    private class BufferChart extends JPanel {
        private static final int MAX_POINTS = 100;
        private final LinkedList<Integer> dataPoints = new LinkedList<>();

        public BufferChart() {
            setPreferredSize(new Dimension(300, 200));
        }

        public void updateChart(int value) {
            dataPoints.addLast(value);
            if (dataPoints.size() > MAX_POINTS) {
                dataPoints.removeFirst();
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dataPoints.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            g2.setColor(Color.BLACK);
            g2.drawLine(0, h - 1, w, h - 1);
            g2.drawLine(0, 0, 0, h - 1);

            int xScale = w / MAX_POINTS;
            int yScale = h / maxBufferSize;

            g2.setColor(Color.BLUE);
            for (int i = 0; i < dataPoints.size() - 1; i++) {
                int x1 = i * xScale;
                int y1 = h - dataPoints.get(i) * yScale;
                int x2 = (i + 1) * xScale;
                int y2 = h - dataPoints.get(i + 1) * yScale;
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    // 主方法
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProducerConsumerGUI().setVisible(true));
    }
}