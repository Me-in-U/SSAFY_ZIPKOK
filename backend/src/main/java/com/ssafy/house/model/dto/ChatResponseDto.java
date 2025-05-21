package com.ssafy.house.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // @code @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDto {
        private String message;
        private List<String> aptSeqList;
        private List<String> relatedQuestionList;
}