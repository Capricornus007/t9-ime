# 外來檔案與來源

本倉自己寫的檔案採 0BSD。下列檔案不是自己寫的，來源與**它們原本的授權**逐條記錄在此。

| 檔案 | 來源 | 原始授權 | 說明 |
|---|---|---|---|
| `data/shared/t9_pinyin.schema.yaml` | `lida0407/trime-9key`（osfans/trime 的九鍵分支），commit `e69e2a68` | GPL-3.0-or-later | 九鍵方案本體；`speller/algebra` 的 xlit/derive 把拼音音節轉寫成數字碼 |
| `data/shared/t9_dict.dict.yaml` | 同上，**本倉改寫過** | GPL-3.0-or-later（原稿標 SPDX 為 iDvel 與 rime-ice 貢獻者） | 原稿 `import_tables` 匯入 rime-ice 五張 cn_dicts（約 46MB 文字檔）；本倉改成只匯入 `luna_pinyin`，理由見檔案內註解 |
| `data/shared/lua/t9_preedit.lua` | 同上 | GPL-3.0-or-later | 編碼區顯示處理 |
| `tools/t9probe.cpp` | 本倉自己寫 | 0BSD | 本機 librime 驗證探針 |

## 讀過的參考（未取用檔案，只記行號供日後比對）

- `osfans/trime`（GPL-3）：鍵盤 YAML 格式、剪貼板 Room 儲存、簡繁 switches 的 UI 分派。
- `ximeiorg/Xime`（GPL-3）：`app/src/main/jni/librime-t9/`（`t9_state_machine`、
  `t9_digit_segment_strategy`、`t9_digit_userdict`、`t9_right_commit_handler`）、
  `ui/keyboard/T9KeyboardLayout.kt`、`T9KeyboardSwipeOverlay`。
- `Arcns/fcitx5-android-t9`（LGPL-2.1）：`input/t9/T9SideRailUi.kt`（左側直式已按碼欄）、
  `T9PinyinResolver.kt`、`SwipeSymbolDirection.kt`、`popup/PopupPreset.kt`。
- `BurgerK1ng16/Vertick-IME`（GPL-3）：`data/DictionaryPackManager.kt`（`.scel` 魔數辨識與解析、
  詞庫包下載匯入）。
- `taku910/zinnia`（BSD-3-Clause）＋ `tegaki/tegaki` release v0.3 的日文模型（LGPL-2.1）
  ＋ `wkpark/zinnia`（BSD-3，Android.mk 與 `libzinniajni.cpp`）：日文手寫那條路。
- 搜狗輸入法 Moto 版 10.32（未授權、僅逆向觀察）：九鍵鍵盤 `py_9.ini` 的排法與鍵名、
  剪貼板 150 條上限（`dfm.java:268`）、用戶詞典落地目錄。
- **不取用**：`HoengSaan/trime-gukwan`（CC-BY-NC-SA-4.0，且實為 36 鍵主題）、
  `githubms68/trime-dahi`（倉內無 LICENSE 檔）、`Mgrsc/LIME`（NOASSERTION）——這三個只讀不搬。
