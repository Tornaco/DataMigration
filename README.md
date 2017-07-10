### 数据迁移source code

## 准备编译

### 编译开始前
**wfdhook** 模块使用了一些android隐藏api，我们需要去 [Android-Hidden-API](https://github.com/anggrayudi/android-hidden-api) 或者 [24版本](https://github.com/Tornaco/Hidden-api-android-24)或者(百度网盘)[http://pan.baidu.com/s/1dF6EcSx] 下载隐藏版本sdk。

简要步骤:
1. 下载上述sdk.
2. Go to <SDK location>/platforms/.
3. Copy, paste and replace the downloaded hidden API file into this directory, e.g. android-25/android.jar.
4. Build **DataMigration**.

### 使用gradle命令编译:
```
./gradlew app:assembleDebug
```
