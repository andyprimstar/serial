# 流水號測驗

### 以下題目需考慮有做負載均衡的情況

## 題目一

SerialService.next(Integer id, Integer limitCount)

說明:實作特定ID，下一個流水號功能，並且有上限控制(limitCount)，超過上限就不能要號。

### 測試情境

針對特定ID的流水號，上限為10萬，各Thread分別去要號

API：/serials/q1/next/test

### 期望

特定ID的流水號，達到上限後，要號總數跟上限數要一樣，並且要號不重複

## 題目二

實作 SerialService.next(Integer id, Integer groupCount, Integer limitCount)

說明:實作下一個流水號功能，groupCount代表區塊總數，limitCount代表每個區塊的上限值，
會先從區塊1開始要號，達到該區塊上限時會轉到下一個區塊要號(從0開始)，只要區塊總數已滿並且達到該區
塊上限值就不能要號。

### 測試情境

總數為10個區塊，每個區塊上限為1萬，各Thread分別去要號

API：/serials/q2/next/test

### 期望

區塊總數、每個區塊上限已滿時，要號總數跟(區塊總數*上限數)要一樣，並且每個區塊要號不重複
