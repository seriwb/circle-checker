[![CircleCI](https://circleci.com/gh/seriwb/circle-checker.svg?style=shield)](https://circleci.com/gh/seriwb/circle-checker)
[![Coverage Status](https://coveralls.io/repos/github/seriwb/circle-checker/badge.svg)](https://coveralls.io/github/seriwb/circle-checker)

# Circle Checker

**「やばい！サークルチェック全然してない！」**

自分がイベント直前に陥るいつものやつを少しでも緩和するために作った、
Twitterのユーザ名からイベント参加情報を抽出するツールです。

設定ファイル（filter.txt）をいじることで、どんなパターンにも対応できます。


## 使い方

### 事前準備

1. [Java](https://www.java.com/ja/)か[Java SE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)をインストール
2. [releases](https://github.com/seriwb/circle-checker/releases/latest)からzipファイルを取得し、適当な場所に展開
3. リストを対象にする場合は、config/config.txtの`cc.target.list = [""]`にチェックするリスト名を入力  
（フォローユーザを対象にする場合は未入力のままにしておく）
4. config/filter.txtにユーザ名に含まれる可能性のあるイベント名を列挙  
（デフォルトはコミケ用で、イベント名指定には正規表現が利用可能です） 
> 以下のファイルをfilter.txtにリネームすることで、特定イベントのチェックができます。
> - filter_comiket.txt : コミケ用のフィルターサンプルです
> - filter_comitia.txt : コミティア用のフィルターサンプルです

#### 設定値のサンプル

```groovy
cc.target.list = [""]                     // タイムラインの情報を取得
cc.target.list = ["", "リスト名"]          // タイムラインと「リスト名」の情報を取得
cc.target.list = ["リスト１", "リスト１"]   // 「リスト１」と「リスト２」の情報を取得
```


### 実行手順

コンソールからcircle-checker-X.X.X.jarがある場所まで移動し、以下のコマンドを実行  
（X.X.Xはそのときのバージョン番号が入る）
```
java -jar circle-checker-X.X.X.jar
```

※Twitterアカウント情報をリセットしたい場合は、dbディレクトリを削除してください。


### 注意

filter.txtはUTF-8形式で保存してください。  
イベント名の指定には正規表現が利用できますが、()を使うとエラーになるため、()は使用しないようお願いします。

また、実行後に以下のようなメッセージが表示された場合は、Twitterの規制にひっかかっていますので、
15分以上待ってから再度実行してください。

```
Exception in thread "main" 429:Returned in API v1.1 when a request cannot be served due to the application's rate limit having been exhausted for the resource. See Rate Limiting in API v1.1.(https://dev.twitter.com/docs/rate-limiting/1.1)
message - Rate limit exceeded
code - 88
```

### 動作環境

- Javaが動作するOS（Windows、Mac）
- JRE : 1.8 以上


## 機能

- タイムラインと複数のリストを同時にチェックすることができます
- 固定ツイートの情報を取得できます
- 出力結果をヘッダーと一緒にGoogle Spreadsheetsに貼り付けることで、アイコンと固定ツイートの画像が表示されます
  - ヘッダー：`Twitter ID	Twitter Name	アイコン	一致イベント名	スペース番号	画像1	画像2	画像3	画像4	Twitter URL	Twitter Link	固定されたツイート	プロフィール画像	固定されたツイートの画像`



## 今後の予定

- HTMLの結果出力
- TLに流れてくるRTを対象にするオプション追加


## 要望＆バグ報告

要望やバグ報告などあれば、メール、GitHubのIssue、ブログへのコメントなどでご連絡ください。


## License

MIT License
