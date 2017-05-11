[![CircleCI](https://circleci.com/gh/seriwb/circle-checker.svg?style=shield)](https://circleci.com/gh/seriwb/circle-checker)
[![Coverage Status](https://coveralls.io/repos/github/seriwb/circle-checker/badge.svg)](https://coveralls.io/github/seriwb/circle-checker)

# Circle Checker

**「やばい！サークルチェック全然してない！」**

自分がイベント直前に陥るいつものやつを少しでも緩和するために作った、
Twitterのユーザ名からイベント参加情報を抽出するツールです。

設定ファイル（filter.txt）をいじることで、どんなパターンにも対応できます。


## 使い方

1. [Java SE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)をインストール
2. [releases](https://github.com/seriwb/circle-checker/releases/latest)からzipファイルを取得し、適当な場所に展開
3. リストを対象にする場合は、config/config.txtの`cc.target.list = ""`にチェックするリスト名を入力  
（フォローユーザを対象にする場合は未入力のままにしておく）
4. config/filter.txtにユーザ名に含まれる可能性のあるイベント名を列挙（デフォルトはコミティア用）
5. コンソールからcircle-checker-X.X.X.jarがある場所まで移動し、以下のコマンドを実行  
（X.X.Xはそのときのバージョン番号が入る）
```
java -jar circle-checker-X.X.X.jar
```

※Twitterアカウント情報をリセットしたい場合は、dbディレクトリを削除してください。


### 動作環境

- Javaが動作するOS（Windows、Mac）
- JRE : 1.8 以上


## 今後の予定

- HTMLの結果出力
- TLに流れてくるRTを対象にするオプション追加


## 要望＆バグ報告

要望やバグ報告などあれば、メール、GitHubのIssue、ブログへのコメントなどでご連絡ください。


## License

MIT License
