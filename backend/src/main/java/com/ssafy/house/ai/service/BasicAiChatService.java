package com.ssafy.house.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ssafy.house.ai.tools.DateTimeTools;
import com.ssafy.house.ai.tools.MemberTools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAiChatService implements AiChatService {
    @Qualifier("advisedChatClient")
    private final ChatClient advisedChatClient;
    private final DateTimeTools dateTimeTools;
    private final MemberTools memberTools;

    @Override
    public String timeToolGeneration(String userInput) {
        return this.advisedChatClient
                .prompt()
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill한"))
                .user(userInput)
                .tools(dateTimeTools)
                .call()
                .content();
    }

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

    // private final ChatClient ragChatDefaultClient;
    // private final ChatClient ragChatCustomClient;

    // @Override
    // public String ragGeneration(String userInput, boolean contextOnly,
    // Consumer<AdvisorSpec> advisorSpec) {
    // ChatClient chatClient = contextOnly ? ragChatDefaultClient :
    // ragChatCustomClient;
    // var spec = chatClient.prompt()
    // .system(c -> c.param("language", "Korean").param("character", "Chill한"))
    // .user(userInput);
    // // 런타임에 filter 표현식에 동적 filtering
    // if (advisorSpec != null) {
    // spec.advisors(advisorSpec);
    // }
    // return spec.call().content();
    // }
}
