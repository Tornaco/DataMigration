### 数据迁移source code

## 准备编译

### 编译开始前
**wfdhook** 模块使用了一些android隐藏api，我们需要去 [Android-Hidden-API](https://github.com/anggrayudi/android-hidden-api), 下载
隐藏版本sdk。

简要步骤:
1. clone [Android-Hidden-API](https://github.com/anggrayudi/android-hidden-api) project.
2. Go to <SDK location>/platforms/.
3. Copy, paste and replace the downloaded hidden API file into this directory, e.g. android-25/android.jar.
4. Build **DataMigration**.

### 使用gradle命令编译:
```
./gradlew app:assembleDebug
```
