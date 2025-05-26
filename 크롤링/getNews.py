# 네이버 검색 API 크롤링 → MySQL 저장 (최대 10,000건)
import json
import urllib.request
from datetime import datetime

import pymysql

# 1) 설정
CLIENT_ID = "GZ4w6oMQETuoYK1Etyy6"
CLIENT_SECRET = "0vno6gIV2u"
SERVICE_ID = "news"
QUERY = urllib.parse.quote("부동산")
SORT = "sim"  # sim, date
MAX_START = 1000  # start 최대값
BATCH = 100  # display 최대값
COMMIT_BATCH = 500  # 몇 건마다 DB 커밋할지

# 2) DB 연결
conn = pymysql.connect(
    host="ssafy.blog", user="maple", password="story", db="ssafyproj", charset="utf8mb4"
)
try:
    with conn.cursor() as cur:
        # INSERT IGNORE 로 중복(originallink) 무시
        sql = """
            INSERT IGNORE INTO real_estate_news
                (title, originallink, naverlink, description, pub_date)
            VALUES (%s, %s, %s, %s, %s)
        """

        rows = []
        for start in range(1, MAX_START + 1, BATCH):
            display = BATCH
            url = (
                f"https://openapi.naver.com/v1/search/{SERVICE_ID}"
                f"?query={QUERY}&display={display}&start={start}&sort={SORT}"
            )
            req = urllib.request.Request(url)
            req.add_header("X-Naver-Client-Id", CLIENT_ID)
            req.add_header("X-Naver-Client-Secret", CLIENT_SECRET)
            resp = urllib.request.urlopen(req)

            if resp.getcode() != 200:
                print(f"[Error] code={resp.getcode()} at start={start}")
                break

            data = json.loads(resp.read().decode("utf-8"))
            for item in data.get("items", []):
                dt = datetime.strptime(item["pubDate"], "%a, %d %b %Y %H:%M:%S +0900")
                rows.append(
                    (
                        item["title"].replace("<b>", "").replace("</b>", ""),
                        item["originallink"],
                        item["link"],
                        item["description"].replace("<b>", "").replace("</b>", ""),
                        dt,
                    )
                )

            # 500건 모일 때마다 삽입 & 커밋
            if len(rows) >= COMMIT_BATCH:
                cur.executemany(sql, rows)
                conn.commit()
                print(f"[Commit] {len(rows)}건 저장 완료 (start={start})")
                rows.clear()

        # 남은 데이터 삽입
        if rows:
            cur.executemany(sql, rows)
            conn.commit()
            print(f"[Commit] {len(rows)}건 저장 완료 (final)")

finally:
    conn.close()
    print("DB 연결 종료")
