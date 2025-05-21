from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.keys import Keys
from bs4 import BeautifulSoup
from concurrent.futures import ThreadPoolExecutor, as_completed
from selenium.common.exceptions import TimeoutException, NoSuchElementException
import time
import re
import pymysql
import argparse
import math

# ─ MySQL 연결 설정 ─────────────────────────────
conn = pymysql.connect(
    host="ssafy.blog",
    user="maple",
    password="story",
    db="ssafyproj",
    charset="utf8mb4",
    cursorclass=pymysql.cursors.DictCursor,
)


def split_price_range(s: str):
    """'8억 8,000~12억 3,000' → (880_000_000, 1_230_000_000)"""
    parts = [p.strip() for p in s.split("~")]
    if not parts or parts[0] in ("", "-"):
        return None, None
    lo = won_to_int(parts[0])
    hi = won_to_int(parts[1]) if len(parts) > 1 else lo
    return lo, hi


def won_to_int(s: str) -> int:
    """예: '8억 8,000' → 880000000"""
    if not s or s.strip() in ("-", ""):
        return None
    parts = s.replace("억", "").replace(",", "").split()
    o = int(parts[0]) * 100_000_000
    m = int(parts[1]) * 10_000 if len(parts) > 1 else 0
    return o + m


def parse_area_range(s: str):
    """예: '62.78㎡ ~ 75.13㎡' → (62.78, 75.13)"""
    nums = re.findall(r"([\d.]+)", s)
    if len(nums) >= 2:
        return float(nums[0]), float(nums[1])
    return None, None


def parse_complex_info(html: str) -> dict:
    soup = BeautifulSoup(html, "html.parser")
    summary = soup.find("div", id="summaryInfo")
    if not summary:
        raise ValueError("단지 요약 정보가 없습니다.")
    title = summary.select_one("h3.title").get_text(strip=True)
    features = {
        dt.get_text(strip=True): dd.get_text(strip=True)
        for dt, dd in zip(
            summary.select(".complex_feature dt"),
            summary.select(".complex_feature dd"),
        )
    }
    trade = summary.select_one(".complex_price--trade")
    recent_price = trade.select_one("dd.data").get_text(strip=True) if trade else "-"
    recent_detail = trade.select_one("dd.date").get_text(strip=True) if trade else "-"
    prices = {
        cp.select_one("dt.title")
        .get_text(strip=True): cp.select_one("dd.data")
        .get_text(strip=True)
        for cp in summary.select(".complex_price_wrap .complex_price")
    }
    return {
        "단지명": title,
        **features,
        "매매가": prices.get("매매가", "-"),
        "전세가": prices.get("전세가", "-"),
        "면적": features.get("면적", ""),
        "최근매매": recent_price,
        "상세": recent_detail,
    }


def parse_listings(html: str):
    """모든 매물 리스트(#articleListArea .item_inner) 파싱"""
    soup = BeautifulSoup(html, "html.parser")
    items = soup.select("#articleListArea .item .item_inner")
    out = []
    for i in items:
        nm = i.select_one(".item_title .text")
        tp = i.select_one(".price_line .type")
        pr = i.select_one(".price_line .price")
        ut = i.select_one(".info_area .type")
        sp = i.select_one(".info_area .spec")
        lines = i.select(".info_area .line")
        desc = lines[1].get_text(strip=True) if len(lines) > 1 else ""
        cf = i.select_one(".label_area .type-confirmed")

        trade_type = tp.get_text(strip=True) if tp else ""
        raw_price = pr.get_text(strip=True) if pr else ""
        price = None
        deposit = None
        monthly_rent = None

        if trade_type == "월세" and "/" in raw_price:
            dep_str, rent_str = raw_price.split("/", 1)
            deposit = won_to_int(dep_str.strip())
            # 월세 단위가 만원 단위로 표시되므로 10,000 곱하기
            try:
                monthly_rent = int(re.sub(r"[^\d]", "", rent_str)) * 10000
            except:
                monthly_rent = None
        else:
            # 매매/전세 둘 다 price 로 처리
            price = won_to_int(raw_price)

        out.append(
            {
                "name": nm.get_text(strip=True) if nm else "",
                "trade": trade_type,
                "price": price,
                "deposit": deposit,
                "monthly_rent": monthly_rent,
                "property_type": ut.get_text(strip=True) if ut else "",
                "spec": sp.get_text(strip=True) if sp else "",
                "desc": desc,
                "conf": cf.get_text(strip=True) if cf else "",
            }
        )
    return out


def parse_images(html: str):
    soup = BeautifulSoup(html, "html.parser")
    box = soup.select_one(".detail_box--photo")
    if not box:
        return None
    items = box.select(".detail_photo_item")
    if not items:
        return None
    out = []
    for a in items:
        style = a.get("style", "")
        m = re.search(r"url\(&quot;(.+?)&quot;\)", style) or re.search(
            r'url\("(.+?)"\)', style
        )
        url = m.group(1) if m else None
        label = a.select_one("span.label_place")
        out.append({"url": url, "label": label.get_text(strip=True) if label else ""})
    return out


def parse_schools(html: str):
    """
    detail_box--school 안의 모든 학교 정보를 뽑아 리스트로 반환
    [
      {
        "name": "...", "type": "...",
        "district": "...", "assignment_detail": "...",
        "distance": "...",
        "address": "...", "phone": "...",
        "established": "...", "office": "...",
        "teacher_count": "...", "student_count": "...",
        "homepage": "..."
      },
      ...
    ]
    """
    soup = BeautifulSoup(html, "html.parser")
    box = soup.select_one(".detail_box--school")
    if not box:
        return None

    # 학교명·타입
    h5 = box.select_one(".heading_text")
    name = h5.get_text(strip=True)
    typ = (
        h5.select_one(".school_type")["aria-label"]
        if h5.select_one(".school_type")
        else None
    )

    # 배정동·상세·거리
    town = box.select(".town_box")
    district = town[0].select_one(".town_title").get_text(strip=True) if town else None
    assignment_detail = (
        town[0].select_one(".town_detail").get_text(strip=True) if town else None
    )
    distance = (
        town[1].select_one(".town_detail").get_text(strip=True)
        if len(town) > 1
        else None
    )

    # 표 데이터
    info = {}
    for tr in box.select("table.info_table_wrap tr.info_table_item"):
        k = tr.select_one("th").get_text(strip=True)
        v = tr.select_one("td").get_text(strip=True)
        info[k] = v

    return [
        {
            "name": name,
            "type": typ,
            "district": district,
            "assignment_detail": assignment_detail,
            "distance": distance,
            "address": info.get("주소"),
            "phone": info.get("전화"),
            "established": info.get("설립"),
            "office": info.get("교육청"),
            "teacher_count": info.get("교원수"),
            "student_count": info.get("학생수"),
            "homepage": (
                box.select_one("a.link")["href"] if box.select_one("a.link") else None
            ),
        }
    ]


def search_apartment(driver, name: str):
    """이름으로 검색 후 (단지정보, 매물리스트, 이미지) 반환, 오류·타임아웃 시 (None,None,None)"""
    try:
        driver.get("https://new.land.naver.com/")
        WebDriverWait(driver, 20).until(
            lambda d: d.execute_script("return document.readyState") == "complete"
        )

        w = WebDriverWait(driver, 10)
        inp = w.until(EC.presence_of_element_located((By.ID, "land_search")))
        inp.clear()
        inp.send_keys(name, Keys.ENTER)
        time.sleep(2)
        # ENTER 후, '검색결과 없음' 또는 매물 리스트 둘 중 하나가 뜰 때까지 최대 10초 대기
        try:
            WebDriverWait(driver, 20).until(
                EC.any_of(
                    EC.presence_of_element_located(
                        (By.CSS_SELECTOR, ".item_list--search .no_data_area_inner")
                    ),
                    EC.presence_of_all_elements_located(
                        (By.CSS_SELECTOR, ".item_list--search .item_inner")
                    ),
                    EC.presence_of_element_located(
                        (By.CSS_SELECTOR, ".list_fixed .result_search")
                    ),
                    EC.presence_of_element_located((By.ID, "summaryInfo")),
                    EC.presence_of_element_located((By.ID, "articleListArea")),
                )
            )
        except TimeoutException:
            print("[DEBUG] 검색 결과 로딩 타임아웃")
            return None, None, None, None

        # '검색결과 없음' 창이 보이면 리턴
        if driver.find_elements(
            By.CSS_SELECTOR, ".item_list--search .no_data_area_inner"
        ):
            print("[DEBUG] 검색 결과 없음")
            return None, None, None, None

        # '검색결과' 헤더가 보이면
        if driver.find_elements(By.CSS_SELECTOR, ".list_fixed .result_search"):
            print("[DEBUG] 검색결과 헤더 감지")  # 디버그

            # 1) 로딩 스피너 사라질 때까지
            WebDriverWait(driver, 20).until(
                EC.invisibility_of_element_located((By.CSS_SELECTOR, ".loading"))
            )
            print("[DEBUG] .loading 스피너 사라짐")  # 디버그

            # 2) 실제 매물 항목들 로드될 때까지
            WebDriverWait(driver, 20).until(
                EC.presence_of_all_elements_located(
                    (By.CSS_SELECTOR, ".item_list--search .item_inner")
                )
            )
            print("[DEBUG] 모든 .item_inner 로드 완료")  # 디버그

            # 3) info_area가 있는 항목만 필터
            raw_items = driver.find_elements(
                By.CSS_SELECTOR, ".item_list--search .item_inner"
            )
            items = [
                el
                for el in raw_items
                if el.find_elements(By.CSS_SELECTOR, ".info_area")
            ]
            print(
                f"[DEBUG] 전체 항목 {len(raw_items)}개, 유효 항목 {len(items)}개"
            )  # 디버그

            # 4) 아파트→오피스텔 클릭
            for prefer in ("아파트", "오피스텔"):
                print(f"[DEBUG] 우선순위: {prefer}")  # 디버그
                for it in items:
                    t = it.find_element(By.CSS_SELECTOR, ".info_area .type").text
                    print(
                        f"[DEBUG] 항목 제목='{it.find_element(By.CSS_SELECTOR,'.title').text}', 타입='{t}'"
                    )  # 디버그
                    if t == prefer:
                        print(f"[DEBUG] 클릭 대상 → {t}")  # 디버그
                        try:
                            it.click()
                        except:
                            driver.execute_script("arguments[0].click();", it)
                        WebDriverWait(driver, 5).until(EC.staleness_of(it))
                        print("[DEBUG] 클릭 완료, DOM 교체 감지")  # 디버그
                        time.sleep(2)
                        break
                else:
                    print(f"[DEBUG] {prefer} 항목 없음")  # 디버그
                    continue
                break

        try:
            WebDriverWait(driver, 20).until(
                EC.any_of(
                    EC.presence_of_element_located((By.ID, "summaryInfo")),
                    EC.presence_of_element_located((By.ID, "articleListArea")),
                )
            )
        except TimeoutException:
            print("[DEBUG] summaryInfo 혹은 articleListArea 로딩 실패")
            return None, None, None, None

        # 단지정보 버튼 클릭
        try:
            btn = WebDriverWait(driver, 5).until(
                EC.element_to_be_clickable(
                    (By.CSS_SELECTOR, ".complex_detail_link .complex_link")
                )
            )
            if btn.text.strip() == "단지정보":
                btn.click()
                # 클릭 후 단지요약(#summaryInfo) 로딩될 때까지 최대 10초 대기
                WebDriverWait(driver, 10).until(
                    EC.presence_of_element_located((By.ID, "summaryInfo"))
                )
            time.sleep(2)
        except TimeoutException:
            # 단지정보 버튼이나 summaryInfo 로딩 실패 시 무시
            pass
        html = driver.page_source
        complex_info = parse_complex_info(html)
        listings = parse_listings(html)

        # 사진 탭 클릭
        try:
            tab = driver.find_element(
                By.XPATH,
                "//a[contains(@class,'tab_item') and normalize-space(.)='사진']",
            )
            tab.click()
            # ── 명시적 대기: 사진 컨테이너(.detail_box--photo)가 나타날 때까지 최대 10초
            WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, ".detail_box--photo"))
            )
            time.sleep(2)
        except (TimeoutException, NoSuchElementException):
            print("[DEBUG] 사진 탭 없음 또는 로딩 실패")
            pass
        html = driver.page_source
        images = parse_images(html)

        # 학군정보 탭 클릭
        try:
            tab = WebDriverWait(driver, 5).until(
                EC.element_to_be_clickable(
                    (
                        By.XPATH,
                        "//a[contains(@class,'tab_item') and normalize-space(.)='학군정보']",
                    )
                )
            )
            tab.click()
            # 클릭 후 .detail_box--school(학군정보 박스) 로드될 때까지 최대 10초 대기
            WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, ".detail_box--school"))
            )
            time.sleep(2)
        except (TimeoutException, NoSuchElementException):
            print("[DEBUG] 학군정보 탭 없음 또는 로딩 실패")
            pass
        html = driver.page_source
        schools = parse_schools(html)

        return complex_info, listings, images, schools
    except Exception:
        return None, None, None, None


if __name__ == "__main__":
    # 1) 파라미터 정의
    p = argparse.ArgumentParser()
    p.add_argument(
        "--chunk-size", type=int, default=0, help="한 번에 처리할 항목 개수 (0: 전체)"
    )
    p.add_argument(
        "--chunk-index", type=int, default=1, help="처리할 청크 번호 (1부터 시작)"
    )
    args = p.parse_args()

    # 2) 드라이버 초기화
    opts = Options()
    opts.add_argument("--headless")
    opts.add_argument("--disable-gpu")
    # opts.add_argument("--window-size=1200,800")
    prefs = {"profile.managed_default_content_settings.images": 2}
    opts.add_experimental_option("prefs", prefs)
    opts.add_argument("--log-level=3")
    opts.add_experimental_option("excludeSwitches", ["enable-logging"])
    driver = webdriver.Chrome(options=opts)
    wait = WebDriverWait(driver, 20)

    # 3) 입력 파일 로드
    with open("apartments.txt", "r", encoding="utf-8") as f:
        lines = [L.strip() for L in f if L.strip()]

    apt_seqs = [L.split("\t", 1)[0] for L in lines]
    raw_names = [L.split("\t", 1)[1] for L in lines]
    names = [re.sub(r"\([^)]*\)|\d+동|아파트", "", n).strip() for n in raw_names]
    total = len(apt_seqs)

    # 4) 청크 계산
    if args.chunk_size > 0:
        num_chunks = math.ceil(total / args.chunk_size)
        if not (1 <= args.chunk_index <= num_chunks):
            raise SystemExit(f"chunk-index는 1~{num_chunks} 사이여야 합니다.")
        start = (args.chunk_index - 1) * args.chunk_size
        end = min(total, args.chunk_index * args.chunk_size)
        apt_seqs = apt_seqs[start:end]
        names = names[start:end]
        print(
            f"[INFO] 전체 {total}건 중 {args.chunk_index}/{num_chunks} 구간 ({start+1}~{end}) 처리"
        )
    else:
        print(f"[INFO] 전체 {total}건 처리")

    # 5) 메인 루프 (청크 적용됨)
    with conn:
        with conn.cursor() as cur:
            for idx, (seq, name) in enumerate(zip(apt_seqs, names), start=1):
                print(f"[{idx}/{len(apt_seqs)}] '{name}' 크롤링 시작…", flush=True)
                ci, lst, imgs, schools = search_apartment(driver, name)

                # 바로 출력
                print(f"\n===== [{seq}] {name} =====")
                if ci is None:
                    print("  → 오류나 결과 없음")
                    continue
                else:
                    print("단지정보:")
                    for k, v in ci.items():
                        print(f"  {k}: {v}")

                    print("\n매물리스트:")
                    if not lst:
                        print("  정보 없음")
                    else:
                        for i, d in enumerate(lst, 1):
                            print(f"  --- 매물 {i} ---")
                            for kk, vv in d.items():
                                print(f"    {kk}: {vv}")

                    print("\n이미지:")
                    if not imgs:
                        print("  정보 없음")
                    else:
                        for i, img in enumerate(imgs, 1):
                            print(f"  --- 이미지 {i} ---")
                            print(f"    url:   {img['url']}")
                            print(f"    label: {img['label']}")

                # ─ DB 삽입 ─
                # 1) house_detail
                a_min, a_max = parse_area_range(ci.get("면적", ""))
                tp_min, tp_max = split_price_range(ci.get("매매가", ""))
                jp_min, jp_max = split_price_range(ci.get("전세가", ""))
                last_detail = ci.get("상세")
                cur.execute(
                    """
                    INSERT INTO house_detail
                      (apt_seq, area_min, area_max,
                       trade_price_min, trade_price_max,
                       jeonse_price_min, jeonse_price_max,
                       last_trade_detail)
                    VALUES (%s,%s,%s,%s,%s,%s,%s,%s)
                    ON DUPLICATE KEY UPDATE
                      area_min=VALUES(area_min),
                      area_max=VALUES(area_max),
                      trade_price_min=VALUES(trade_price_min),
                      trade_price_max=VALUES(trade_price_max),
                      jeonse_price_min=VALUES(jeonse_price_min),
                      jeonse_price_max=VALUES(jeonse_price_max),
                      last_trade_detail=VALUES(last_trade_detail)
                    """,
                    (seq, a_min, a_max, tp_min, tp_max, jp_min, jp_max, last_detail),
                )

                # 2) house_deal
                for d in lst or []:
                    m = re.search(r"(\d{2})\.(\d{2})\.(\d{2})", d.get("conf", ""))
                    confirmed_at = (
                        f"20{m.group(1)}-{m.group(2)}-{m.group(3)}" if m else None
                    )
                    cur.execute(
                        """
                        INSERT INTO house_deal
                          (apt_seq, listing_name, trade_type,
                           price, deposit, monthly_rent,
                           property_type, spec, description, confirmed_at)
                        VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
                        ON DUPLICATE KEY UPDATE
                          price=VALUES(price),
                          deposit=VALUES(deposit),
                          monthly_rent=VALUES(monthly_rent),
                          confirmed_at=VALUES(confirmed_at)
                        """,
                        (
                            seq,
                            d["name"],
                            d["trade"],
                            d["price"],
                            d["deposit"],
                            d["monthly_rent"],
                            d["property_type"],
                            d["spec"],
                            d["desc"],
                            confirmed_at,
                        ),
                    )

                # 3) house_image
                for img in imgs or []:
                    cur.execute(
                        """
                        INSERT INTO house_image
                          (apt_seq, img_path, description)
                        VALUES (%s,%s,%s)
                        ON DUPLICATE KEY UPDATE
                          img_path=VALUES(img_path),
                          description=VALUES(description)
                        """,
                        (seq, img["url"], img["label"]),
                    )

                # 4) school_detail + house_school
                for sch in schools or []:
                    cur.execute(
                        """
                        INSERT INTO school_detail
                          (school_name, school_type, address, phone,
                           established, office, teacher_count,
                           student_count, homepage)
                        VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)
                        ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id)
                        """,
                        (
                            sch["name"],
                            sch["type"],
                            sch["address"],
                            sch["phone"],
                            sch["established"],
                            sch["office"],
                            sch["teacher_count"],
                            sch["student_count"],
                            sch["homepage"],
                        ),
                    )
                    school_id = cur.lastrowid
                    cur.execute(
                        """
                        INSERT IGNORE INTO house_school
                          (apt_seq, school_id, district, assignment_detail, distance)
                        VALUES (%s,%s,%s,%s,%s)
                        """,
                        (
                            seq,
                            school_id,
                            sch["district"],
                            sch["assignment_detail"],
                            sch["distance"],
                        ),
                    )

                conn.commit()
                print(f"[DB DEBUG] [{seq}] 커밋 완료")

    driver.quit()
