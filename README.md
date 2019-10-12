日本語 | [English](docs/README_EN.md)

# ScriptBlock-Improved [![jitpack](https://jitpack.io/v/kuro46/ScriptBlock-Improved.svg)](https://jitpack.io/#kuro46/ScriptBlock-Improved)

[ScriptBlock](https://dev.bukkit.org/projects/scriptblock)の設計や機能を改善したプラグインです

**注意:** このプラグインは現在安定していません。思わぬバグがあったり、更新時にスクリプトが読み込めなくなったりするかもしれません。  
試験的に使う場合を除いて、安定化予定のバージョン1.0.0のリリースまで待つことをおすすめします。

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

### APIを使う

#### 依存関係に追加する

##### Gradle

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.kuro46:ScriptBlock-Improved:v0.4.0'
}
```

### SBIに変更を加える

#### 主なGradleタスク

`./gradlew pluginjar`で、サーバー導入用のjarファイルを生成できます。(ファイル名にバージョンを含みません)  
`./gradlew build`でSpotBugsやcheckstyleを一括で実行できます。
