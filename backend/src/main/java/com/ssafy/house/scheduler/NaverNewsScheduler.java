// ===== 추가할 Java 스케줄러 서비스 =====
package com.ssafy.house.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class NaverNewsScheduler {

    // OS에 따라 python 커맨드 자동 선택
    private static final String PYTHON_CMD = "C:\\Users\\SSAFY\\Documents\\GitHub\\ssafy_home\\크롤링\\.venv\\Scripts\\python.exe";
    private static final String SCRIPT_PATH = "C:\\Users\\SSAFY\\Documents\\GitHub\\ssafy_home\\크롤링\\getNews.py";

    /**
     * 매시 정각에 한 번씩 크롤러 실행 (1시간마다)
     * cron 표현식: 초 분 시 일 월 요일
     * "0 0 * * * *" → 매시간 0분 0초
     */
    @Scheduled(cron = "0 0 * * * *")
    public void scheduledRun() {
        long start = System.currentTimeMillis();
        log.info(">> Naver News 크롤러 시작");
        try {
            ProcessBuilder pb = new ProcessBuilder(PYTHON_CMD, SCRIPT_PATH);
            pb.redirectErrorStream(true);
            Process proc = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[파이썬] {}", line);
                }
            }

            boolean finished = proc.waitFor(10, TimeUnit.MINUTES);
            int exitCode = finished ? proc.exitValue() : -1;
            log.info("<< 크롤러 종료 (exitCode={}, 소요={}ms)",
                    exitCode, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("크롤러 실행 중 오류 발생", e);
        }
    }
}
