package com.ssafy.house.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.test.util.ReflectionTestUtils;

import com.ssafy.house.ai.tools.DateTimeTools;
import com.ssafy.house.ai.tools.HouseTools;
import com.ssafy.house.model.dto.CustomChatResponseDto;

@ExtendWith(MockitoExtension.class)
class BasicAiChatServiceTest {

    @Mock
    private DateTimeTools dateTimeTools;

    @Mock
    private HouseTools houseTools;

    @Mock
    private ChatModel chatModel;

    @Mock
    private ToolCallingManager toolCallingManager;

    @Test
    void userControlledChat_usesConversationHistoryAfterToolExecution() {
        BasicAiChatService service = createService();
        String convoId = "convo-1";

        AssistantMessage toolCallMessage = AssistantMessage.builder()
                .content("")
                .toolCalls(List.of(new AssistantMessage.ToolCall("tool-call-1", "function", "findHouse", "{}")))
                .build();
        ChatResponse initialResponse = new ChatResponse(List.of(new Generation(toolCallMessage)));

        ToolResponseMessage toolResponseMessage = ToolResponseMessage.builder()
                .responses(List.of(new ToolResponseMessage.ToolResponse("tool-call-1", "findHouse",
                        "{\"aptSeqList\":[\"1001\"]}")))
                .build();
        List<Message> conversationHistory = List.of(new UserMessage("잠실 아파트 찾아줘"), toolCallMessage,
                toolResponseMessage);
        ToolExecutionResult toolExecutionResult = ToolExecutionResult.builder()
                .conversationHistory(conversationHistory)
                .build();

        AssistantMessage finalMessage = new AssistantMessage(
                "{\"message\":\"찾았어요\",\"aptSeqList\":[\"1001\"],\"relatedQuestionList\":[]}");
        ChatResponse finalResponse = new ChatResponse(List.of(new Generation(finalMessage)));

        when(chatModel.call(any(Prompt.class))).thenReturn(initialResponse, finalResponse);
        when(toolCallingManager.executeToolCalls(any(Prompt.class), any(ChatResponse.class))).thenReturn(toolExecutionResult);

        CustomChatResponseDto response = service.userControlledChat("잠실 아파트 찾아줘", convoId);

        assertThat(response.getMessage()).isEqualTo("찾았어요");
        assertThat(response.getAptSeqList()).containsExactly("1001");
        assertThat(response.getConvoId()).isEqualTo(convoId);

        ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        verify(chatModel, org.mockito.Mockito.times(2)).call(promptCaptor.capture());
        List<Prompt> prompts = promptCaptor.getAllValues();
        assertThat(prompts.get(1).getInstructions()).containsExactlyElementsOf(conversationHistory);
    }

    @Test
    void userControlledChat_handlesSimpleApartmentSearchShortcut() throws Exception {
        BasicAiChatService service = createService();

        when(houseTools.searchHouseByPartialName("반포자이")).thenReturn(List.of("1001", "1002"));

        CustomChatResponseDto response = service.userControlledChat("반포자이 찾아줘", "convo-shortcut");

        assertThat(response.getMessage()).isEqualTo("반포자이 아파트를 2개 찾았어요.");
        assertThat(response.getAptSeqList()).containsExactly("1001", "1002");
        assertThat(response.getRelatedQuestionList()).containsExactly(
                "반포자이 아파트 매매 가격 알려줘",
                "반포자이 아파트 전세 매물 찾아줘",
                "반포자이 아파트 주변 학교 알려줘");

        verify(houseTools).searchHouseByPartialName("반포자이");
        verifyNoInteractions(chatModel, toolCallingManager);
    }

    @Test
    void userControlledChat_returnsFallbackWithToolPayloadWhenJsonExtractionFails() {
        BasicAiChatService service = createService();
        String convoId = "convo-2";

        AssistantMessage toolCallMessage = AssistantMessage.builder()
                .content("")
                .toolCalls(List.of(new AssistantMessage.ToolCall("tool-call-2", "function", "findHouse", "{}")))
                .build();
        ChatResponse initialResponse = new ChatResponse(List.of(new Generation(toolCallMessage)));

        ToolResponseMessage toolResponseMessage = ToolResponseMessage.builder()
                .responses(List.of(new ToolResponseMessage.ToolResponse("tool-call-2", "findHouse",
                        "{\"aptSeqList\":[\"2001\",\"2002\"]}")))
                .build();
        ToolExecutionResult toolExecutionResult = ToolExecutionResult.builder()
                .conversationHistory(List.of(new UserMessage("매물 보여줘"), toolCallMessage, toolResponseMessage))
                .build();

        AssistantMessage finalMessage = new AssistantMessage("JSON 아님");
        ChatResponse finalResponse = new ChatResponse(List.of(new Generation(finalMessage)));

        when(chatModel.call(any(Prompt.class))).thenReturn(initialResponse, finalResponse);
        when(toolCallingManager.executeToolCalls(any(Prompt.class), any(ChatResponse.class))).thenReturn(toolExecutionResult);

        CustomChatResponseDto response = service.userControlledChat("매물 보여줘", convoId);

        assertThat(response.getMessage()).isEqualTo("결과를 구조화하지 못해 도구 결과만 반환합니다. 2개 찾았어요.");
        assertThat(response.getAptSeqList()).containsExactly("2001", "2002");
        assertThat(response.getConvoId()).isEqualTo(convoId);
    }

    private BasicAiChatService createService() {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .build();
        BasicAiChatService service = new BasicAiChatService(dateTimeTools, houseTools, chatModel, toolCallingManager,
                chatMemory);
        ReflectionTestUtils.setField(service, "customSystemPrompt", "테스트 시스템 프롬프트");
        return service;
    }

}
