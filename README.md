日本語 | [English](docs/README_EN.md)

# ScriptBlock-Improved [![jitpack](https://jitpack.io/v/kuro46/ScriptBlock-Improved.svg)](https://jitpack.io/#kuro46/ScriptBlock-Improved)

[ScriptBlock](https://dev.bukkit.org/projects/scriptblock)の設計や機能を改善したプラグインです。

**注意:** このプラグインは現在安定していません。思わぬバグがあったり、更新時にスクリプトが読み込めなくなったりするかもしれません。  
試験的に使う場合を除いて、安定化予定のバージョン1.0.0のリリースまで待つことをおすすめします。

## 特徴

- 外部プラグインに依存しないため、プラグイン単体で動作させることができます
- コンソールからコマンドを実行するためのオプションなど、便利なオプションを実装しています
- 使いやすいAPIを実装しています

コマンドリスト/オプションリスト/APIの使用方法は[Wiki](https://github.com/kuro46/ScriptBlock-Improved/wiki/)を参照してください。

## 動作環境

- Java8以上
- 1.8.8~1.14.4までのBukkitを実装したサーバー

## 導入方法

[ここをクリック](https://github.com/kuro46/ScriptBlock-Improved/releases/latest/download/ScriptBlock-Improved.jar)
して最新のjarファイルをダウンロードし、サーバーのpluginsフォルダに入れてください。

## ソースコードからビルドする

このリポジトリをクローンしたあと、  
サーバー導入用のjarファイルを生成したい場合は`./gradlew pluginjar`を、  
各種テスト(checkstyleや単体テスト)を実行したい場合は`./gradlew build`を実行してください。
