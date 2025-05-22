#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import json
import time
import pymysql
import requests

# ——— 환경 설정 —————————————————————————————
DB_CONFIG = {
    "host": "ssafy.blog",
    "user": "maple",
    "password": "story",
    "db": "ssafyproj",
    "charset": "utf8mb4",
}
API_URL = "https://api.vworld.kr/req/address?"
API_KEY = "D7A6321F-0605-3A7C-A3C7-7F93FFE806E1"
BASE_PARAMS = {
    "service": "address",
    "request": "getcoord",
    "crs": "epsg:4326",
    "format": "json",
    "type": "PARCEL",
    "key": API_KEY,
}
RESUME_FILE = "last_resume.json"
MAX_PER_RUN = 40000
DELAY_SEC = 0.05
# —————————————————————————————————————————————

# 마지막으로 처리한 apt_seq 로드
if os.path.exists(RESUME_FILE):
    with open(RESUME_FILE, "r") as f:
        last_seq = json.load(f).get("last_seq")
else:
    last_seq = None

# DB 연결
conn = pymysql.connect(**DB_CONFIG)
cursor = conn.cursor(pymysql.cursors.DictCursor)

# resume 이후 레코드 조회
query = """
    SELECT
        h.apt_seq,
        d.sido_name,
        d.gugun_name,
        d.dong_name,
        h.jibun
    FROM house_info AS h
    JOIN dong_code  AS d
      ON CONCAT(h.sgg_cd, h.umd_cd) = d.dong_code
"""
if last_seq:
    query += " WHERE h.apt_seq > %s"
    params = (last_seq,)
else:
    params = ()

query += " ORDER BY h.apt_seq"
cursor.execute(query, params)
rows = cursor.fetchall()

# 처리 루프
for idx, row in enumerate(rows, start=1):
    apt_seq = row["apt_seq"]
    address = (
        f"{row['sido_name']} {row['gugun_name']} {row['dong_name']} {row['jibun']}"
    )
    params = BASE_PARAMS.copy()
    params["address"] = address

    try:
        resp = requests.get(API_URL, params=params, timeout=5)
        data = resp.json()
        if data.get("response", {}).get("status") == "OK":
            pt = data["response"]["result"]["point"]
            lon = pt.get("x")
            lat = pt.get("y")
        else:
            lon = lat = None

        if lon and lat:
            cursor.execute(
                "UPDATE house_info SET longitude=%s, latitude=%s WHERE apt_seq=%s",
                (lon, lat, apt_seq),
            )
            print(f"[{idx}/{len(rows)}] 업데이트: {address} → ({lon}, {lat})")
        else:
            cursor.execute("DELETE FROM house_deals_done WHERE apt_seq=%s", (apt_seq,))
            cursor.execute("DELETE FROM house_info WHERE apt_seq=%s", (apt_seq,))
            print(f"[{idx}/{len(rows)}] 삭제(좌표없음): {address}")

    except Exception as e:
        cursor.execute("DELETE FROM house_deals_done WHERE apt_seq=%s", (apt_seq,))
        cursor.execute("DELETE FROM house_info WHERE apt_seq=%s", (apt_seq,))
        print(f"[{idx}/{len(rows)}] 삭제(오류): {address} ({e})")

    conn.commit()
    # 마지막으로 처리한 apt_seq 저장
    with open(RESUME_FILE, "w", encoding="utf-8") as f:
        json.dump({"last_seq": apt_seq}, f)

    # 4만 건 처리 시점에 종료
    if idx >= MAX_PER_RUN:
        print(f"==== 오늘 {MAX_PER_RUN}건 처리 완료, 다음에 이어서 실행하세요 ====")
        break

    time.sleep(DELAY_SEC)

# 마무리
cursor.close()
conn.close()
print("스크립트 종료")
