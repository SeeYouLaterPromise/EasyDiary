from fastapi import FastAPI
from io import BytesIO
from fastapi.responses import StreamingResponse
import pyttsx3

BASE_DIR = "/\\"

app = FastAPI()

# uvicorn main:app --reload --host 0.0.0.0 --port 8001

@app.get("/synthesize")
def synthesize_text(text: str):
    if not text:
        return {"error": "No text provided"}

    try:
        print(f"Received text: {text}")  # 打印接收到的文本
        # 初始化 TTS 引擎
        engine = pyttsx3.init()

        # 设置中文语音（请确保您已经安装了中文语音）
        voices = engine.getProperty('voices')
        for voice in voices:
            if 'zh' in voice.languages:
                engine.setProperty('voice', voice.id)
                break

        # 保存合成的语音到文件
        # engine.say(text)
        audio_path = BASE_DIR + "output.mp3"
        engine.save_to_file(text, audio_path)
        engine.runAndWait()

        # 读取生成的音频文件到 BytesIO 对象
        audio_file = BytesIO()
        with open(audio_path, "rb") as f:
            audio_file.write(f.read())
        audio_file.seek(0)  # 将指针移动到文件开始

        # 返回音频文件流
        return StreamingResponse(audio_file, media_type="audio/mp3")
    except Exception as e:
        return {"error": str(e)}
