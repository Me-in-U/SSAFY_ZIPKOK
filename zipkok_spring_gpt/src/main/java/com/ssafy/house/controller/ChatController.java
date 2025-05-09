// package com.ssafy.house.controller;

// import java.util.List;
// import java.util.Map;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;

// import com.ssafy.house.model.service.OpenAiService;

// @Controller
// public class ChatController {
// private static Logger logging = LoggerFactory.getLogger(OpenAiService.class);

// @Autowired
// private OpenAiService openAiService;

// @PostMapping("/chat")
// @ResponseBody
// public String chat(@RequestBody List<Map<String, String>> messages) {
// // 대화 내용 로그 출력
// // messages.forEach(msg -> logging.debug("role: {}, content: {}",
// // msg.get("role"), msg.get("content")));
// String response = openAiService.getChatResponse(messages);

// return response;
// }

// }
