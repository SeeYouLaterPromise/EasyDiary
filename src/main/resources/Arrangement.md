提升: 复盘 + 制定计划

- 目标 = 理想 + 截止日期
- 想要变强就要做一个`清晰`的人


刚刚在看视频看到一个新的改进方向：
快速调出deepseek对话窗口，进行知识询问，
后台记录对话记录，提炼归纳知识点。
（还要进一步阅读deepseek技术文档，学习深度思考中思考部分的调用以及流式调用。）

ym聊天启发：概括成一句话


- 初次运行`配置页`或者`login page`: 配置用户名; api_key调用deepseek。
- 美化界面：
   - 学习时长按照`unit`来给予不同的背景颜色来呈现渐变色。
   - LearningMode状态栏美化
   - QuickNotePanel美化
   - CertainDayPanel -> webview to show markdown, code style.
- 主面板：
  - TableView还没有做好，准备放一些目标计划；和learningMode联动。
  > future work: 让AI帮我们做目标拆解。
  - 做一个MenuBar来配置一些个性化设置：
    - light/dark theme.
    - 现在默认的是按键复制存储；网页点击复制开启与否的设置。
- 持久化存储：
  - txt storage, xxx_0217，后面是不是形成一个目录比较好？ fixed.
  - database.








- 用户在开启`learning mode`之前先让他输入一个`关键词`来确定他在做什么工作，便于后续数据管理。

## LLM API
你需要在resource文件下新建一个`api_key.txt`文件来存储你的deepseek的api启用LLM的调用。

> ATTENTION!!! If you post request via HTTP, you should do encoding with `UTF-8` for chinese words.

## Let AI help you organize what you learn today.
1. First stage: text, users' excerpts and thoughts.
2. Second stage: image users uploaded. such as some do-wrong question.