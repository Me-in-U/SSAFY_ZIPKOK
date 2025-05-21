# https://www.vworld.kr/dev/v4dv_geocoderguide2_s001.do
import time

import pymysql
import requests

# DB 연결
conn = pymysql.connect(
    host="ssafy.blog", user="maple", password="story", db="ssafyproj", charset="utf8mb4"
)
cursor = conn.cursor(pymysql.cursors.DictCursor)

apiurl = "https://api.vworld.kr/req/address?"
base_params = {
    "service": "address",
    "request": "getcoord",
    "crs": "epsg:4326",
    "format": "json",
    "type": "PARCEL",
    "key": "572DE249-AC29-3AA1-8697-04BABC9B9043",
}

# 1) 전체 레코드 조회
cursor.execute("SELECT apt_seq, umd_nm, jibun FROM house_info")
rows = cursor.fetchall()

for idx, row in enumerate(rows, start=1):
    address = f"{row['umd_nm']} {row['jibun']}"
    params = base_params.copy()
    params["address"] = address

    try:
        resp = requests.get(apiurl, params=params, timeout=5)
        data = resp.json()
        # status 확인 후 정확한 경로로 point 추출
        if data.get("response", {}).get("status") == "OK":
            point = data["response"]["result"]["point"]
            lon = point.get("x")
            lat = point.get("y")
        else:
            lon = lat = None

        if lon and lat:
            # 2) 업데이트
            cursor.execute(
                "UPDATE house_info SET longitude=%s, latitude=%s WHERE apt_seq=%s",
                (lon, lat, row["apt_seq"]),
            )
            conn.commit()
            print(f"[{idx}/{len(rows)}] 업데이트 완료: {address} ({lon}, {lat})")
        else:
            print(f"[{idx}/{len(rows)}] 좌표 없음: {address}")

    except Exception as e:
        print(f"[{idx}/{len(rows)}] 오류({address}): {e}")

    # Rate limit 고려 지연 (0.05초)
    time.sleep(0.05)

cursor.close()
conn.close()
print("업데이트 완료")
