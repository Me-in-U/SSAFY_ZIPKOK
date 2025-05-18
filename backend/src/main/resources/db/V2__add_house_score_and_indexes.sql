-- 1) 점수 집계용 테이블
CREATE TABLE IF NOT EXISTS ssafyproj.house_score (
  apt_seq      VARCHAR(20) PRIMARY KEY,
  price_now    INT,
  price_1y_ago INT,
  score        DOUBLE,
  last_date    DATETIME,
  img_path     VARCHAR(255)
);

-- 2) 인덱스 추가
ALTER TABLE ssafyproj.house_score
  ADD INDEX idx_hs_score (score DESC);

ALTER TABLE ssafyproj.house_deal
  ADD INDEX idx_hd_apt_confirmed (apt_seq, confirmed_at),
  ADD INDEX idx_hd_price_apt    (price, apt_seq);

ALTER TABLE ssafyproj.house_image
  ADD INDEX idx_hi_apt_img (apt_seq, img_path);

ALTER TABLE ssafyproj.house_deals_done
  ADD INDEX idx_hdd_apt (apt_seq);
