# 生產者-消費者模型模擬器

這個程式實現了一個生產者-消費者模型的圖形化介面，用於展示和模擬緩衝區的運作。它使用了堆積（Heap）數據結構來高效管理緩衝區中的物品。

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
    // ...
}

// 停止模擬
private void stopSimulation() {
    // ...
}

// 更新緩衝區內容顯示
private void updateBufferContent() {
    // ...
}

// 生產者運行邏輯
private class Producer implements Runnable {
    public void run() {
        // ...
    }
}

// 消費者運行邏輯
private class Consumer implements Runnable {
    public void run() {
        // ...
    }
}
