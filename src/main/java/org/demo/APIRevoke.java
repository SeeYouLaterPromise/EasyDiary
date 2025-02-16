package org.demo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.entity.StringEntity;
import org.clip.TxtFileManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class APIRevoke {

    private static final String BASE_URL = "https://api.deepseek.com/chat/completions";
    private static String API_KEY = "";  // 将 YOUR_OPENAI_API_KEY 替换为您的 API 密钥
    private static String modelType = "reasoner";

    private String getApiKey() {
        // 获取 resources 文件夹下的 txt 文件
        return TxtFileManager.getFirstLine("/api_key.txt");
    }

    private static String StringToJSon(String responseString) {
        String res = "";
        try {
            // 尝试创建 JSONObject
            JSONObject jsonResponse = null;
            try {
                jsonResponse = new JSONObject(responseString);
//                System.out.println("JSON response created successfully.");
            } catch (JSONException e) {
                System.out.println("Failed to create JSONObject: " + e.getMessage());
                return res; // 结束方法
            }

            // 如果成功创建 JSON 对象，继续执行
            // 打印整个 JSON 响应
            System.out.println("JSON: " + jsonResponse.toString(2)); // 格式化输出

            // 检查是否包含 "choices" 字段
            if (jsonResponse.has("choices")) {
                System.out.println("'choices' field found in the response.");
                // 进一步处理
                String generatedText = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)  // 获取第一个 choice
                        .getJSONObject("message")  // 获取 message 对象
                        .getString("content");  // 获取 content 字段（生成的文本）

                System.out.println("Generated text: " + generatedText);
                res = generatedText;
            } else {
//                    System.out.println("'choices' field not found in the response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public static void main(String[] args) {
//        Scanner input = new Scanner(System.in);
//        System.out.println("Enter your prompt:");
//        String prompt = input.nextLine();

        API_KEY = new APIRevoke().getApiKey();

        try {
            // 创建 HTTP 客户端
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // 构建 OpenAI API 请求
            HttpPost request = Post("nihao!");

            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(request);

            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder responseString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseString.append(line);
            }

            // 打印响应内容
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                System.out.println("Error: Received non-OK HTTP response: " + statusCode);
                System.out.println("Response body: " + responseString);  // 输出响应内容
            } else {
                // 打印响应内容，帮助调试
                // FIXME: What I found without `toString()`, there is no content printed.
//                System.out.println("API Response: " + responseString.toString());
                // 解析并处理有效响应
                JSONObject jsonResponse = new JSONObject(responseString.toString());

                // 获取 `choices` 数组中的 `message` 对象，然后提取 `content` 字段
                String generatedText = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)  // 获取第一个 choice
                        .getJSONObject("message")  // 获取 message 对象
                        .getString("content");  // 获取 content 字段（生成的文本）

                System.out.println("Generated text: \n" + generatedText);

                TxtFileManager txtFileManager = new TxtFileManager("novel.txt");
                txtFileManager.WriteToFile(generatedText, true);

            }



            // 关闭客户端
            httpClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HttpPost Post(String prompt) throws UnsupportedEncodingException {
        HttpPost request = new HttpPost(BASE_URL);

        // 设置请求头，包括 API 密钥
        request.setHeader("Authorization", "Bearer " + API_KEY);
        request.setHeader("Content-Type", "application/json");

        // 创建请求体（JSON 格式）
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("model", "deepseek-chat");  // 使用正确的模型名称

        // 创建 messages 数组
        JSONArray messagesArray = new JSONArray();


//        JSONObject systemMessage = new JSONObject();
//        systemMessage.put("role", "system");
//        systemMessage.put("content", "你是一个经验老道、想象力丰富的小说家，请你根据我的提示帮我写故事。");
//        messagesArray.put(systemMessage);


        JSONObject userMessage = new JSONObject();

        String chineseTxt = "你是一个经验老道、想象力丰富的小说家，请你根据我的提示帮我写故事: ";

//        String chineseTxt = "你是一个经验老道、想象力丰富的小说家，请你根据我的提示帮我写故事，请用中文回复。从前有一个老人，他每次钓鱼都一无所获，直到有一次一条巨大的金枪鱼，别人都不相信老人能够征服，知道老人拖着疲惫的身体带回了金枪鱼......";
        String encodedText = URLEncoder.encode(chineseTxt, "UTF-8");

        userMessage.put("role", "user");
        userMessage.put("content", encodedText);
        messagesArray.put(userMessage);

        // 将消息数组添加到请求体
        jsonRequest.put("messages", messagesArray);

        // 设置流模式为 false
        jsonRequest.put("stream", false);

        System.out.println(jsonRequest.toString());
        StringToJSon(jsonRequest.toString());

        // 设置请求体
        StringEntity entity = new StringEntity(jsonRequest.toString());
        request.setEntity(entity);
        return request;
    }
}
