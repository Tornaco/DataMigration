## 数据迁移OpenSource

![Logo](app/src/main/res/mipmap-xhdpi/ic_launcher_dark.png)

--------------

[![Build Status](https://travis-ci.org/Tornaco/DataMigration.svg?branch=master)](https://travis-ci.org/Tornaco/DataMigration)

### 准备编译

> 编译开始前
**wfdhook** 模块使用了一些android隐藏api，我们需要去 [Android-Hidden-API](https://github.com/anggrayudi/android-hidden-api) 或者 [24版本](https://github.com/Tornaco/Hidden-api-android-24)或者[百度网盘](http://pan.baidu.com/s/1dF6EcSx)下载隐藏版本sdk。

**简要步骤:**           
1. 下载上述sdk.
2. 进入 ```<SDK location>/platforms/```。
3. 用下载的版本的```android.jar```替换原来的jar, e.g. ```android-24/android.jar```。
4. 将本项目导入android studio编译或者使用gradle命令编译。

### 使用gradle命令编译:
```
./gradlew app:assembleDebug
```
