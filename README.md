日本語 | [English](docs/README_EN.md)

# ScriptBlock-Improved [![Latest release](https://img.shields.io/github/v/release/kuro46/ScriptBlock-Improved)](https://github.com/kuro46/ScriptBlock-Improved/releases)[![GitHub Workflows](https://github.com/kuro46/ScriptBlock-Improved/workflows/Build/badge.svg)](https://github.com/kuro46/ScriptBlock-Improved/actions)

[ScriptBlock](https://dev.bukkit.org/projects/scriptblock)の設計や機能を改善したプラグインです。  
使い方など、詳細は[Wiki](https://github.com/kuro46/ScriptBlock-Improved/wiki/)を参照してください。

## 類似プラグインとの違い

[Wiki/類似プラグインとの比較](https://github.com/kuro46/ScriptBlock-Improved/wiki/%E9%A1%9E%E4%BC%BC%E3%83%97%E3%83%A9%E3%82%B0%E3%82%A4%E3%83%B3%E3%81%A8%E3%81%AE%E6%AF%94%E8%BC%83)を参照してください。

## 動作環境

- Java8以上
- 1.8.8~1.15.2までのBukkitを実装したサーバー

## 導入方法

[ここをクリック](https://github.com/kuro46/ScriptBlock-Improved/releases/latest/download/ScriptBlock-Improved.jar)
して最新のjarファイルをダウンロードし、サーバーのpluginsフォルダに入れてください。

## ソースコードからビルドする

このリポジトリをクローンしたあと、  
サーバー導入用のjarファイルを生成したい場合は`./gradlew shadowjar`を、  
各種テスト(単体テストなど)を実行したい場合は`./gradlew build`を実行してください。

## APIを使う

[Wikiのページ](https://github.com/kuro46/ScriptBlock-Improved/wiki/%E9%96%8B%E7%99%BA%E8%80%85%E5%90%91%E3%81%91)を参照してください。
