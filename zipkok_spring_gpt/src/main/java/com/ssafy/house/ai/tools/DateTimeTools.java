package com.ssafy.house.ai.tools;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DateTimeTools {

    // TODO: 08-1. 현재 사용자의 시간을 가져오는 @Tool을 구성해보자.
    @Tool(description = "현재 사용자의 timezone에 해당하는 현재의 날짜와 시간 정보를 가져온다.")
    public LocalDateTime getCurrentDateTime() {
        // 사용자의 timezone을 가져온다.
        ZonedDateTime zonedDateTime = ZonedDateTime.now(LocaleContextHolder.getTimeZone().toZoneId());
        // ZonedDateTime을 LocalDateTime으로 변환한다.
        return zonedDateTime.toLocalDateTime();
    }
    // END

    // TODO: 08-3. 사용자의 요청에 따라 시스템에 알람을 요청하는 Tool을 만들어보자.
    @Tool(description = "사용자가 요청한 시간에 알람을 설정한다.")
    public void setAlarm(@ToolParam(description = "알람을 울릴 시간") LocalDateTime alarmTime) {
        // 사용자의 OS 이름을 가져온다.
        String osName = System.getProperty("os.name").toLowerCase();
        // 알람을 설정한다.
        try {
            setAlarm(alarmTime, osName);
        } catch (IOException | InterruptedException e) {
            log.error("알람 설정 중 오류 발생", e);
            throw new RuntimeException("알람 설정 중 오류 발생", e);
        }
    }
    // END

    private void setAlarm(LocalDateTime alarmTime, String osName) throws IOException, InterruptedException {
        // 현재 시간과 알람 시간 사이의 초 계산
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, alarmTime);
        long seconds = duration.getSeconds();

        // Mac OS에서의 알람 설정
        String[] macScript = {
                "osascript",
                "-e",
                "do shell script \"sleep " + seconds +
                        " && afplay /System/Library/Sounds/Glass.aiff && osascript -e 'display notification \\\"알람 시간입니다!\\\" with title \\\"알람\\\" sound name \\\"Glass\\\"' &\""
        };
        // Window OS에서의 알람 설정
        String taskName = "AlarmAt" + alarmTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String time = alarmTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        String[] windowScript = {
                "cmd.exe",
                "/c",
                "schtasks /create /tn \"" + taskName + "\" /tr \"msg %username% 알람 시간입니다!\" /sc once /st " + time
        };

        if (osName.contains("mac")) {
            Runtime.getRuntime().exec(macScript);
        } else if (osName.contains("win")) {
            System.out.println("win");
            System.out.println(Arrays.toString(windowScript));
            Runtime.getRuntime().exec(windowScript);
        } else {
            throw new RuntimeException("Unsupported operating system: " + osName + ". Alarm set for " + alarmTime);
        }
        System.out.println(
                "알람이 " + alarmTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")) + "에 설정되었습니다");
    }

}
