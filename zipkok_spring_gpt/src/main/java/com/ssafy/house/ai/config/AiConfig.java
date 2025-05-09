package com.ssafy.house.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore.MetadataField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import redis.clients.jedis.JedisPooled;

@Configuration
public class AiConfig {

    @Value("${ssafy.ai.system-prompt}")
    String systemPrompt;

    @Bean
    ChatClient simpleChatClient(ChatClient.Builder builder) {
        // TODO: 02. ChatClient 타입의 빈을 생성해보자.

        return builder.defaultSystem(systemPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                .build();

        // END
    }
    // TODO: 06-1. 위에서 선언한 빈에 SimpleLoggerAdvisor를 default로 선언해보자.

    @Bean
    ChatClient reReadingChatClient(ChatClient.Builder builder) {
        // TODO: 06-3. SimpleLoggerAdvisor와 ReReadingAdvisor를 적용한 ChatClient를 생성해보자.
        return null;

        // END
    }

    // TODO: 07-2 In Memory 기반의 ChatMemory빈과 ChatClient(advisedChatClient)빈을 구성하세요.
    // ChatClient(advisedChatClient)는 SimpleLoggerAdvisor와 MessageChatMemoryAdvisor를
    // 갖는다.
    @Bean
    ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    ChatClient advisedChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder.defaultSystem(systemPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1),
                        new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }
    // END

    // TODO: 09-1. VectorStore의 구성을 확인하세요.
    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel,
            @Value("${spring.ai.vectorstore.redis.index}") String index,
            @Value("${spring.ai.vectorstore.redis.prefix}") String prefix) {
        RedisVectorStore vs = RedisVectorStore.builder(new JedisPooled("localhost", 6379), embeddingModel)
                .indexName(index)
                .prefix(prefix)
                .metadataFields( // Optional: filtering을 위한 metadata field 설정
                        MetadataField.tag("category"),
                        MetadataField.numeric("meta_num"),
                        MetadataField.text("meta_txt"))
                .initializeSchema(true) // Optional: schema 초기화 여부(default: false)
                .build();
        return vs;
    }

    // TODO: 10-1. 검색된 내용만을 가지고 RAG 서비스를 하기 위한 ChatClient 타입 빈의 생성 코드를 확인하세요.
    @Bean
    ChatClient ragChatDefaultClient(ChatClient.Builder builder, ChatMemory chatMemory, VectorStore store) {
        SearchRequest request = SearchRequest.builder().topK(3).similarityThreshold(0.75).build();
        return builder.defaultSystem(systemPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                // .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .defaultAdvisors(new QuestionAnswerAdvisor(store, request))
                .build();
    }

    // TODO: 10-2. 검색된 내용에 모델의 데이터를 추가해서 서비스 하기 위한 ChatClient 타입 빈의 생성 코드를 확인하세요.
    @Bean
    ChatClient ragChatCustomClient(ChatClient.Builder builder, ChatMemory chatMemory, VectorStore store,
            @Value("${ssafy.ai.rag-prompt}") String ragPrompt) {
        SearchRequest request = SearchRequest.builder().topK(3).similarityThreshold(0.75).build();
        return builder.defaultSystem(systemPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                // .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .defaultAdvisors(new QuestionAnswerAdvisor(store, request, ragPrompt))
                .build();
    }

}
