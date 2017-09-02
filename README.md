## 数据迁移OpenSource

## 部分截图

![flow1](art/1.png)
![flow1](art/2.png)
![flow1](art/3.png)
![flow1](art/4.png)
![flow1](art/5.png)
![flow1](art/6.png)
![flow1](art/7.png)


--------------

## 编译

[![Build Status](https://travis-ci.org/Tornaco/DataMigration.svg?branch=master)](https://travis-ci.org/Tornaco/DataMigration)

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

## 应用模块以及介绍

### Vangogh：图片加载框架

一个简单流畅的的，具有以下feature的加载器：
1. 自定义的loader。
2. 自定义的动画 图形效果。
3. 自定义的Displayer。
4. 平滑，以及其他基本的参数定义。

令见：此模块单独的REPO：https://github.com/Tornaco/VanGogh




















