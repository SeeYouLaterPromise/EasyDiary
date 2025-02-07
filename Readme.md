# Easy-Diary
提升: 复盘 + 制定计划

- 目标 = 理想 + 截止日期
- 想要变强就要做一个`清晰`的人

Aimed for help you `look back on yourself`.

## Main page (display data)
![img.png](src/main/resources/image/img.png)
- 制作一个如图所示的年历来展示用户这一整年的使用情况
> A Calendar to track the whole year usage of "learning mode" of user.
- 系统获取当前年月日信息，当用户点击年历里面的项时判断：
  - 如果是过去（昨天以及之前）：调出一个panel来回顾那些使用learning mode产生的数据。
  - 如果是现在（今天）：调出一个panel来观察今天使用learning mode产生的数据。
  - 如果是未来（明天以及之后）：调出一个panel让用户在这个panel里面可以写下对那一天的计划规划。

### What you can learn


### JavaFX
![img.png](src/main/resources/image/javaFX.png)

## Learning mode (collect data)
用户在开启`learning mode`之前先让他输入一个`关键词`来确定他在做什么工作，便于后续数据管理。

- **Excerpt**: 当按下 Ctrl+C，选中的句子会被摘录下来。
  - `StringClip.java`: You can learn how to use `Toolkit` and `Clipboard`.
- **Thought**: 当按下 Ctrl+K，将会快速调出一个panel供用户快速记录下此刻感想。
  - `QuickNoteApp.java`: You can learn how to use `Swing` and `Singleton mode`.

### Local storage scheme
> 在 Maven 项目中，resources 文件夹用于存放所有 非 Java 代码的资源文件，如配置文件、静态资源、文本文件、图片等。

version 0 for `.txt` file storage.
`year_month_day` hierarchy:
- `year` and `month` are both directory.
- `day` is data file.


## Future work: Shadowing method for english learning
User can input `sentences`, then we will help you transform `text` into `audio` for you to shadowing-ly read.