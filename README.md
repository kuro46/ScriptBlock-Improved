日本語 | [English](docs/README_EN.md)

# ScriptBlock-Improved

[ScriptBlock](https://dev.bukkit.org/projects/scriptblock)の設計や機能を改善したプラグインです

## 特徴

このプラグインは外部のプラグインに依存しないため、プラグイン単体で動作させる事が可能です。  
また、他の似たプラグインにはない便利なオプションを実装しています。

将来的には開発者向けのAPIも実装する予定です([#3](https://github.com/kuro46/ScriptBlock-Improved/issues/3))

## テスト済みのバージョン

- Java 8, Paper 1.12.2

## 恐らく動作するバージョン

- Java8かそれ以上
- 殆どのバージョンのBukkitを実装したサーバー

## 開発者向け

`./gradlew shadowjar`でshadeされたjarファイルをbuild/libs内に出力します。ファイル名は`ScriptBlock-Improved.jar`です。  
`./gradlew build`でSpotBugsやcheckstyleを一括で実行できます。shadeされたjarは出力しません。
