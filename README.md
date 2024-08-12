# 生產者-消費者模型模擬器

這個程式實現了一個基於堆積（Heap）數據結構的生產者-消費者模型模擬器，使用圖形化介面展示緩衝區的運作。

## 主要用途

- 模擬生產者-消費者問題
- 視覺化展示緩衝區的狀態和變化
- 提供交互式的操作界面

## 主要功能

1. 設置緩衝區大小
2. 開始/停止模擬
3. 實時顯示生產和消費過程
4. 圖形化展示緩衝區使用情況

## 程式結構

### 主要類別

- `ProducerConsumerGUI`: 主要的GUI類
- `Item`: 表示生產/消費的物品
- `Producer`: 生產者線程
- `Consumer`: 消費者線程
- `BufferChart`: 自定義圖表組件

### 關鍵方法

```java
// 開始模擬
private void startSimulation() {
    // 初始化緩衝區和啟動生產者消費者線程
}

// 停止模擬
private void stopSimulation() {
    // 停止所有線程和清理資源
}

// 更新緩衝區內容顯示
private void updateBufferContent() {
    // 更新GUI顯示的緩衝區內容
}

// 生產者運行邏輯
private class Producer implements Runnable {
    public void run() {
        // 生產物品並放入緩衝區
    }
}

// 消費者運行邏輯
private class Consumer implements Runnable {
    public void run() {
        // 從緩衝區取出物品並消費
    }
}
