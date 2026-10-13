# t9-ime

Android 輸入法，目標是**同時**做到五項，一項不砍：

1. **剪貼板歷史**：上限預設 1000 筆以上、設定裡可調；只按「超過上限才刪最舊」淘汰，
   **不按時間淘汰**；釘選的永不刪除；歷史列表**可搜尋**。
2. **日文離線手寫**：平假名／片假名／漢字，離線可用。
3. **搜狗細胞詞庫**：自動偵測新詞庫 → 下載 → 轉換 → 匯入，且使用者詞頻留得住。
4. **九宮格／九鍵**：按鍵只送抽象碼 2–9，哪個字母由詞典詞頻還原（跟 Google、搜狗同一套）；
   佈局照兩家做**兩套**（搜狗式塞進 3×4、gboard 式右側直排三鍵），設定裡可切；
   同鍵選字母**滑動與長按彈出都做**；**不做多擊循環**。
5. **簡繁一鍵**：上屏後單點轉換（照搜狗 `simToTradWord` 的做法），不換輸入方案。

另外一條貫穿全部的需求：**詞典與使用者詞頻必須是他自己那份、電腦與手機通用的 rime 配置**，
所以底座走 librime（BSD-3），不綁任何一家客戶端的專有詞庫格式。

## 目前狀態

- `data/shared/`：九鍵方案 `t9_pinyin`、詞典 `t9_dict`、`lua/t9_preedit.lua`。
  `t9_dict` 預設只 `import_tables: [luna_pinyin]`，不塞 46MB 的 rime-ice 詞庫；
  要換詞庫改這一行。
- `tools/t9probe.cpp`：本機驗證探針（見下）。
- 還**沒有** Android 端任何程式碼——鍵盤層、候選渲染、剪貼板、設定頁都是接下來的工作。

## 驗證方式：先在桌面上證明方案，再碰 APK

這臺機器有 `rime_deployer` 與 librime 1.17，所以 schema／詞典層的東西在桌面上幾十秒就能驗：

```sh
mkdir -p /tmp/rime-t9/{shared,user,build}
cp -r /usr/share/rime-data/. /tmp/rime-t9/shared/
cp data/shared/t9_* /tmp/rime-t9/shared/ && mkdir -p /tmp/rime-t9/shared/lua && cp data/shared/lua/*.lua /tmp/rime-t9/shared/lua/
printf 'patch:\n  schema_list:\n    - schema: t9_pinyin\n  "menu/page_size": 9\n...\n' > /tmp/rime-t9/user/default.custom.yaml
rime_deployer --build /tmp/rime-t9/user /tmp/rime-t9/shared /tmp/rime-t9/build   # 產出 prism.bin / table.bin
g++ -O0 -o /tmp/rime-t9/t9probe tools/t9probe.cpp -lrime && /tmp/rime-t9/t9probe
```

2026-10-13 實測輸出（詞典就是上游 luna_pinyin 那條鏈）：

```
426      → 好 號 高 搞 汗 幹 甘 感 敢
64       → 你 米 迷 擬 尼 密 泥 呢 咪
943426   → 一點 寫好 字典 疑點 西電 血汗 洗掉 西點 寫稿
48626426 → 湖南好 湖南 國安 昏暗 胡鬧 過從 鼓滿
```

已知缺檔警告（打包時要嘛一起放、要嘛在方案裡關掉）：`predict.db`、opencc 的 `emoji.json`。

## 授權

本倉整體採 **TBWTFPL**（`THE BETTER WTFPL`，作者本人即本倉 owner，全文見 `LICENSE`，
來源 <https://github.com/Capricornus007/tbwtfpl>）。該授權第 2 條明文無視其他授權檔的限制，
所以 `third_party/` 底下那些原本帶 GPL／LGPL 的檔案照搬照用，不需要另走什麼合規程序；
它們各自的原始授權檔仍留在目錄裡，只是為了讓人查得到東西從哪來，逐條記在 `VENDORED.md`。
