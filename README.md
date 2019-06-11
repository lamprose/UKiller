# 前言

最近大三信息安全课程设计课大作业是制作一个U盘防火墙，我用JAVA编写了一个简单的程序，现在分享出来供大家参考。

# 原理

​	光盘放入光驱之后就会自动运行，它主要是依靠两个文件来实现，光盘上的`AutoRun.inf`文件和操作系统自身的系统文件`Cdvsd.vxd`。`Cdvsd.vxd`会随时侦测光驱中是否有放入光盘的动作，如果有的话，便开始寻找目录下的`AutoRun.inf`文件，如果存在`AutoRun.inf`文件则执行它里面的预设程序。类似的道理，当U盘中存在特定的`AutoRun.inf`文件时，插上U盘后，也能让U盘自动运行程序。因此，病毒通过构造特定的`AutoRun.inf`文件，当用户双击U盘盘符时，自动运行U盘里的病毒。

一个“功能”比较完备的`AutoRun.inf`文件示例如下：

```bash
[autorun]
Open = virus.exe      //自动运行
Shellexecute = virus.exe       //双击盘符时自动运行
Shell\open = 打开（&O）
Shell\open\Command = virus.exe  //修改右击运行的程序
Shell\open\Default = 1
Shell\explore = 资源管理器（&X）
Shell\explore\Command = virus.exe //修改右击资源管理器时运行的程序
```

​	在这种情况下，假如在U盘根目录下存在这个`AutoRun.inf`和`virus.exe`，而`virus.exe`是一个病毒的话，电脑就很容易感染病毒。因此，需要防止用户双击打开U盘造成病毒感染。

# 功能

- 应用程序要有界面，并提供相关用户帮助信息（例如，病毒种类、个数、位置等）。
- 应用程序能够实时监控多种可移动存储设备（U盘，硬盘，MPS，SD卡）的插入，并自动弹出提醒。
- 在U盘插入后，能够实时自动对其中是否存在AutoRun.inf文件进行安全检测，并可以使用记事本将AutoRun.inf文件打开，以及删除功能。
- 在检测到AutoRun.inf文件存在的基础上，能够对AutoRun.inf文件进行简单的识别，并识别出AutoRun.inf文件中的病毒信息。
- 提供安全打开U盘功能，防止用户在拔下U盘前再次双击打开U盘，避免U盘再次中毒。
- 对检测到的多种病毒，能够进行查杀
- 添加其他一些实用功能，比如，开机自启动，最小化到系统托盘

# 更新日志

## [1.3](https://github.com/lamprose/UKiller/releases/tag/1.3)

1. 增加开机自启动
2. 增加无需更新弹框
3. 优化代码结构
4. 修复一些bug
5. 提高稳定性

## [1.2](https://github.com/lamprose/UKiller/releases/tag/1.2)

1. 修复更新日志中文乱码
2. 完善更新功能
3. 检测更新文件后自动下载
4. 修复bugs
5. 提高流畅度

## [1.0](https://github.com/lamprose/UKiller/releases/tag/1%2C0)

1. 增加托盘图标
2. 插入磁盘时自动检测病毒
3. 扫描结果显示病毒信息
4. 自动删除病毒文件
5. 修复bug
6. 提高稳定性