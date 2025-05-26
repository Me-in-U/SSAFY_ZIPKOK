// src/main/java/com/ssafy/house/scheduler/RealEstateScheduler.java
package com.ssafy.house.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RealEstateScheduler {

    // Windows에서 배치 파일 실행을 위해 cmd.exe 사용
    private static final String CMD = "cmd.exe";
    // start.bat 절대 경로
    private static final String SCRIPT_PATH = "C:\\Users\\SSAFY\\Documents\\GitHub\\ssafy_home\\크롤링\\start.bat";
    // start.bat 이 위치한 폴더 (작업 디렉터리)
    private static final File WORK_DIR = new File("C:\\Users\\SSAFY\\Documents\\GitHub\\ssafy_home\\크롤링");

    /**
     * 애플리케이션 실행 후, 매 2일마다 자정(0시 0분 0초)에 start.bat 실행
     * cron: 초 분 시 일(
     */

    @Scheduled(cron = "0 0 0 */7 * *")
    public void scheduledRun() {
        long start = System.currentTimeMillis();
        log.info(">> start.bat 실행 시작 (7일마다 자정)");
        try {
            // cmd.exe /c start.bat
            ProcessBuilder pb = new ProcessBuilder(CMD, "/c", SCRIPT_PATH);
            pb.directory(WORK_DIR);
            pb.redirectErrorStream(true);

            Process proc = pb.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[배치] {}", line);
                }
            }

            boolean finished = proc.waitFor(30, TimeUnit.MINUTES);
            int exitCode = finished ? proc.exitValue() : -1;
            log.info("<< start.bat 종료 (exitCode={}, 소요={}ms)",
                    exitCode, System.currentTimeMillis() - start);

        } catch (Exception e) {
            log.error("start.bat 실행 중 오류 발생", e);
        }
    }
}