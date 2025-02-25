package org.clip;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DeepSeek {
    private static final String BASE_URL = "https://api.deepseek.com/chat/completions";
    private static final String TEST_SYSTEM = "You are a helpful assistant.";
    private static final String KNOWLEDGE_SYSTEM = "你是一位学习助手，请你帮助我总结知识点并给予知识拓展学习" +
            "我会以[Content]:为开头来上传学习内容，请你帮助我总结我上传的内容，如果知识可以进一步拓展，你可以进行补充。最后，设计3个问题来检验我的掌握情况。" +
            "请注意：是你出问题，我来回答。请在给出归纳内容、拓展知识内容以及设计的问题后，请等待我的答案回复。" +
            "我会以[Answer]:作为开头回答你的问题，请你检查我的答案是否有误，评估我的掌握情况，并给出一些提升建议。";
    private static final String modelType = "chat";

    // 1. get the content of that day => TxtFileManager
    // 2. should I directly pass the whole content or separate `Excerpt` and `Thought`?
    // 先尝试整个输入看一下效果吧

    public static String concludeAndQuestion(String content) {
        content = "[Content]:\n" + content;
        return call(KNOWLEDGE_SYSTEM, content);
    }

    public static String answerQuestion(String answer) {
        answer = "[Answer]:\n" + answer;
        return call(KNOWLEDGE_SYSTEM, answer);
    }

    // for you to test your api call is correct.
//    public static void main(String[] args) {
//        System.out.println(call(TEST_SYSTEM, "hello"));
//    }

    public static String call(String system, String prompt) {
        String res = "";
        try {
            // 创建 HTTP 客户端
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // 构建 OpenAI API 请求
            HttpPost request = getRequest(system, prompt);

            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(request);

            // 处理响应内容，做成字符串
            res = processResponse(response);

            // 关闭客户端
            httpClient.close();
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    /**
     * Configure request for post to deepseek api.
     * @param system
     * @param prompt
     * @return
     * @throws UnsupportedEncodingException
     */
    private static HttpPost getRequest(String system, String prompt) throws UnsupportedEncodingException {
        // UTF-8 encoding is needed for chinese words

        HttpPost request = new HttpPost(BASE_URL);

        // 设置请求头，包括 API 密钥
        String API_KEY = TxtFileManager.getFirstLine("/api_key.txt");
        request.setHeader("Authorization", "Bearer " + API_KEY);
        request.setHeader("Content-Type", "application/json");

        // 创建请求体（JSON 格式）
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("model", "deepseek-" + modelType);  // 使用正确的模型名称

        // 创建 messages 数组
        JSONArray messagesArray = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", URLEncoder.encode(system, "UTF-8"));
        messagesArray.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", URLEncoder.encode(prompt, "UTF-8"));
        messagesArray.put(userMessage);

        // 将消息数组添加到请求体
        jsonRequest.put("messages", messagesArray);

        // 设置流模式为 false
        jsonRequest.put("stream", false);

        // don't remove the toString(), which would lead json for hard resolution.
//        System.out.println(jsonRequest.toString());

        // 设置请求体
        StringEntity entity = new StringEntity(jsonRequest.toString());
        request.setEntity(entity);
        return request;
    }

    /**
     * Process response, get the core content, return it as a String.
     * @param response
     * @return
     * @throws IOException
     */
    private static String processResponse(HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        String generatedText = "";

        // 读取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder responseString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }


        if (statusCode != 200) {
            generatedText = "Error: Received non-OK HTTP response: " + statusCode + "\nResponse body: " + responseString;
        } else {
            // 打印响应内容，帮助调试
            // What I found without `toString()`, there is no content printed.
            // System.out.println("API Response: " + responseString.toString());
            // 解析并处理有效响应
            JSONObject jsonResponse = new JSONObject(responseString.toString());

            // 获取 `choices` 数组中的 `message` 对象，然后提取 `content` 字段
            generatedText = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)  // 获取第一个 choice
                    .getJSONObject("message")  // 获取 message 对象
                    .getString("content");  // 获取 content 字段（生成的文本）
        }
        return generatedText;
    }
}
