package com.ssafy.house.ai.service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.AdvisorSpec;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import com.ssafy.house.ai.tools.DateTimeTools;
import com.ssafy.house.ai.tools.MemberTools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAiChatService implements AiChatService {
    private final ChatClient simpleChatClient;

    // TODO: 03-1. simpleChatClient를 주입받아 간단한 {message:userInput}을 처리해보고 다양한 응답 형태로
    // 반환해보자.
    // ChatClient 타입 빈 주입 시 주의점: ChatClient 타입의 빈은 여러개 만들 계획 - Qualifier 사용 필요
    // 다양한 응답의 형태를 테스트 해보자.
    public Object simpleGeneration(String userInput) {
        return simpleChatClient.prompt()
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill"))
                .user(userInput)
                .call()
                .content();
    }

    // END

    public Quiz quiz(String category) {
        // TODO: 04-1. Quiz 형태로 반환하도록 prompt를 구성해서 simpleChatClient를 통해 질문해보자.
        PromptTemplate promptTemplate = new PromptTemplate("""
                {category}에 대한 퀴즈를 내놔라.
                question, answer, reason 이런 내용으로 구성해라.
                """);
        Prompt prompt = promptTemplate.create(Map.of("category", category));
        return simpleChatClient.prompt(prompt)
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill"))
                .call()
                .entity(new ParameterizedTypeReference<Quiz>() {
                });
        // END
    }

    public String multiModal(String userInput, MimeType mime, Resource resource) {
        // TODO: 05-1.multiModal을 처리해보자.
        return simpleChatClient.prompt()
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill"))
                .user(spec -> spec.media(mime, resource)
                        .text(userInput))
                .call()
                .content();
    }
    // END

    // TODO: 06-4. reReadingChatClient를 이용하는 reReadingGeneration을 구성해보자.
    private ChatClient reReadingChatClient;

    public String reReadingGeneration(String userInput) {
        return reReadingChatClient.prompt()
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill"))
                .user(userInput)
                .call()
                .content();
    }
    // END

    @Qualifier("advisedChatClient")
    private final ChatClient advisedChatClient;

    // TODO: 07-3 ChatClient(advisedChatClient) 를 이용하는 advisedGeneration 서비스를 구성해보자.
    public String advisedGeneration(String userInput) {
        return advisedChatClient.prompt()
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill"))
                .user(userInput)
                .call()
                .content();
    }
    // END

    private final DateTimeTools dateTimeTools;

    @Override
    public String timeToolGeneration(String userInput) {
        // TODO: 08-2. advisedChatClient에 dataTimeTools를 설정하고 사용해보자.
        return this.advisedChatClient
                .prompt()
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill한"))
                .user(userInput)
                .tools(dateTimeTools)
                .call()
                .content();
    }
    // END

    private final MemberTools memberTools;

    // TODO: 08-5. member를 관리하기 위한 Tool의 동작을 확인하세요.
    @Override
    public String memberToolGeneration(String userInput) {
        return this.advisedChatClient
                .prompt()
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill한"))
                .user(userInput)
                .tools(memberTools)
                .call()
                .content();
    }
    // END

    private final ChatClient ragChatDefaultClient;

    private final ChatClient ragChatCustomClient;

    // TODO: 10-3. RAG 서비스를 위한 메서드를 확인하세요.
    @Override
    public String ragGeneration(String userInput, boolean contextOnly, Consumer<AdvisorSpec> advisorSpec) {
        ChatClient chatClient = contextOnly ? ragChatDefaultClient : ragChatCustomClient;
        var spec = chatClient.prompt()
                .system(c -> c.param("language", "Korean").param("character", "Chill한"))
                .user(userInput);
        // 런타임에 filter 표현식에 동적 filtering
        if (advisorSpec != null) {
            spec.advisors(advisorSpec);
        }
        return spec.call().content();
    }
}
