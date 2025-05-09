// package com.ssafy.house.model.service;

// import com.openai.client.OpenAIClient;
// import com.openai.client.okhttp.OpenAIOkHttpClient;
// import com.openai.models.ChatModel;
// import com.openai.models.chat.completions.ChatCompletion;
// import
// com.openai.models.chat.completions.ChatCompletionAssistantMessageParam;
// import com.openai.models.chat.completions.ChatCompletionCreateParams;
// import com.openai.models.chat.completions.ChatCompletionMessageParam;
// import com.openai.models.chat.completions.ChatCompletionSystemMessageParam;
// import com.openai.models.chat.completions.ChatCompletionUserMessageParam;

// import jakarta.annotation.PostConstruct;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// @Service
// public class OpenAiService {
// private static Logger logging = LoggerFactory.getLogger(OpenAiService.class);

// @Value("${openai.api.key}")
// private String apiKey;

// private OpenAIClient client;

// @PostConstruct
// public void initClient() {
// client = OpenAIOkHttpClient.builder()
// .apiKey(apiKey)
// .build();
// }

// public String getChatResponse(List<Map<String, String>> messages) {
// // 기존 대화내역 구성
// List<ChatCompletionMessageParam> messageParams = messages.stream()
// .map(m -> {
// String role = m.get("role");
// String content = m.get("content");
// return switch (role) {
// case "system" -> ChatCompletionMessageParam.ofSystem(
// ChatCompletionSystemMessageParam.builder()
// .content(content).build());
// case "user" -> ChatCompletionMessageParam.ofUser(
// ChatCompletionUserMessageParam.builder()
// .content(content).build());
// case "assistant" -> ChatCompletionMessageParam.ofAssistant(
// ChatCompletionAssistantMessageParam.builder()
// .content(content).build());
// default -> throw new IllegalArgumentException("Unknown role: " + role);
// };
// })
// .collect(Collectors.toList());

// // 시스템 메시지 추가
// messageParams.add(0, ChatCompletionMessageParam.ofSystem(
// ChatCompletionSystemMessageParam.builder()
// .content("너는 '전문 부동산 컨설턴트' 역할을 맡아 사용자 질문에 응답해.")
// .build()));

// // 요청
// ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
// .model(ChatModel.GPT_4O_MINI)
// .messages(messageParams)
// .build();

// // 대답만 추출
// ChatCompletion chatCompletion = client.chat().completions().create(params);
// String reply =
// chatCompletion.choices().get(0).message().content().orElse("");
// logging.info("ChatGPT Response: {}", reply);
// return reply;
// }
// }