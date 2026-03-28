import pymysql

from env_loader import get_env, require_env

# 1) MySQL 접속 정보 설정
conn = pymysql.connect(
    host=require_env("CRAWLING_DB_HOST"),
    port=int(get_env("CRAWLING_DB_PORT", "3306")),
    user=require_env("CRAWLING_DB_USER"),
    password=require_env("CRAWLING_DB_PASSWORD"),
    db=require_env("CRAWLING_DB_NAME"),
    charset="utf8mb4",
)

try:
    with conn.cursor() as cursor:
        # 2) SQL 실행
        sql = """
        SELECT
          h.apt_seq,
          CONCAT(d.gugun_name, ' ', d.dong_name, ' ', h.apt_nm) AS full_address
        FROM house_info h
        JOIN dong_code d
          ON d.dong_code = CONCAT(h.sgg_cd, h.umd_cd)
        """
        cursor.execute(sql)
        rows = cursor.fetchall()

    # 3) txt 파일로 쓰기
    with open("apartments.txt", "w", encoding="utf-8") as f:
        # 옵션: 탭으로 구분, 혹은 원하는 구분자 사용
        for apt_seq, address in rows:
            f.write(f"{apt_seq}\t{address}\n")

    print(f"총 {len(rows)}건을 apartments.txt에 저장했습니다.")

finally:
    conn.close()
