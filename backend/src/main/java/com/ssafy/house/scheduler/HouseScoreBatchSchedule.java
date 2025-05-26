package com.ssafy.house.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HouseScoreBatchSchedule {
  private final JdbcTemplate jdbc;

  // 매일 새벽 3시에 점수를 갱신
  @Scheduled(cron = "0 0 3 * * ?")
  public void rebuildHouseScore() {
    String sql = """
        REPLACE INTO ssafyproj.house_score
        SELECT
          a.apt_seq,
          a.price_now,
          a.price_1y_ago,
          (a.price_now - a.price_1y_ago) / NULLIF(a.price_1y_ago,0) AS score,
          MAX(d.confirmed_at) AS last_date,
          hi.img_path
        FROM (
          SELECT
            apt_seq,
            MAX(CASE WHEN confirmed_at >= DATE_SUB(CURDATE(), INTERVAL 0 MONTH) THEN price END) AS price_now,
            MAX(CASE WHEN confirmed_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
                      AND confirmed_at <  DATE_SUB(CURDATE(), INTERVAL 11 MONTH) THEN price END) AS price_1y_ago
          FROM ssafyproj.house_deal
          WHERE price IS NOT NULL
          GROUP BY apt_seq
        ) a
        JOIN ssafyproj.house_deal d
          ON d.apt_seq = a.apt_seq
         AND d.confirmed_at = (
           SELECT MAX(confirmed_at)
           FROM ssafyproj.house_deal
           WHERE apt_seq = a.apt_seq
             AND price IS NOT NULL
         )
        JOIN (
          SELECT apt_seq, MIN(img_path) AS img_path
          FROM ssafyproj.house_image
          GROUP BY apt_seq
        ) hi ON hi.apt_seq = a.apt_seq
        GROUP BY a.apt_seq
        """;
    jdbc.update(sql);
  }
}
