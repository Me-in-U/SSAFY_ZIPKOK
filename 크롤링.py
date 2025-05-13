from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.keys import Keys
from bs4 import BeautifulSoup
from concurrent.futures import ThreadPoolExecutor, as_completed
from selenium.common.exceptions import TimeoutException
import time
import re
import pymysql

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


def search_apartment(name: str):
    """이름으로 검색 후 (단지정보, 매물리스트, 이미지) 반환, 오류·타임아웃 시 (None,None,None)"""
    time.sleep(1)
    opts = Options()
    opts.add_argument("--headless")  # 헤드리스 모드
    opts.add_argument("--disable-gpu")
    opts.add_argument("--window-size=400,300")  # 소형 창
    # 이미지 로딩 비활성화
    prefs = {"profile.managed_default_content_settings.images": 2}
    opts.add_experimental_option("prefs", prefs)
    opts.add_argument("--log-level=3")  # INFO 이하 로그 숨김
    opts.add_experimental_option("excludeSwitches", ["enable-logging"])
    driver = webdriver.Chrome(options=opts)
    try:
        driver.get("https://new.land.naver.com/")
        w = WebDriverWait(driver, 10)
        inp = w.until(EC.presence_of_element_located((By.ID, "land_search")))
        inp.clear()
        inp.send_keys(name, Keys.ENTER)
        time.sleep(2)
        try:
            w.until(
                EC.any_of(
                    EC.presence_of_element_located(
                        (By.CSS_SELECTOR, ".item_list--search .no_data_area_inner")
                    ),
                    EC.presence_of_element_located(
                        (By.CSS_SELECTOR, ".list_fixed .result_search")
                    ),
                    EC.presence_of_element_located((By.ID, "summaryInfo")),
                    EC.presence_of_element_located((By.ID, "articleListArea")),
                )
            )
        except TimeoutException:
            return None, None, None, None
        # 검색결과 없음
        if driver.find_elements(
            By.CSS_SELECTOR, ".item_list--search .no_data_area_inner"
        ):
            return None, None, None, None
        # 선택 단계 (아파트 우선 → 오피스텔)
        if driver.find_elements(By.CSS_SELECTOR, ".list_fixed .result_search"):
            its = driver.find_elements(By.CSS_SELECTOR, ".item_list--search .item")

            def click_kind(k):
                for it in its:
                    try:
                        t = it.find_element(By.CSS_SELECTOR, ".info_area .type").text
                    except:
                        t = ""
                    if t == k:
                        try:
                            it.click()
                        except:
                            driver.execute_script("arguments[0].click();", it)
                        return True
                return False

            click_kind("아파트") or click_kind("오피스텔")
            time.sleep(1)

        # 단지정보 버튼 클릭
        try:
            btn = driver.find_element(
                By.CSS_SELECTOR, ".complex_detail_link .complex_link"
            )
            if btn.text.strip() == "단지정보":
                btn.click()
                time.sleep(1)
        except:
            # 클릭 실패해도 무시
            pass

        # 사진 탭 클릭
        try:
            tab = driver.find_element(
                By.XPATH,
                "//a[contains(@class,'tab_item') and normalize-space(.)='사진']",
            )
            tab.click()
            time.sleep(1)
        except:
            # 사진 탭이 없으면 images=None 처리
            pass

        html = driver.page_source
        complex_info = parse_complex_info(html)
        listings = parse_listings(html)
        images = parse_images(html)
        # 학군정보 탭 클릭
        try:
            tab = driver.find_element(
                By.XPATH,
                "//a[contains(@class,'tab_item') and normalize-space(.)='학군정보']",
            )
            tab.click()
            time.sleep(1)
        except:
            # 없으면 무시
            pass
        html_sch = driver.page_source
        schools = parse_schools(html_sch)

        return complex_info, listings, images, schools
    except Exception:
        return None, None, None, None
    finally:
        driver.quit()


if __name__ == "__main__":
    # TEST = 25
    with open("apartments.txt", "r", encoding="utf-8") as f:
        lines = [L.strip() for L in f if L.strip()]
    # lines = lines[:TEST]
    apt_seqs = [L.split("\t", 1)[0] for L in lines]
    raw_names = [L.split("\t", 1)[1] for L in lines]
    names = [
        re.sub(r"\d+동", "", n.replace("(", " ").replace(")", " ")).strip()
        for n in raw_names
    ]
    total = len(apt_seqs)
    # apt_seqs = ["11110-101"]
    # names = ["종로구 평창동 크래스빌"]

    results = {}
    future_to_meta = {}

    with ThreadPoolExecutor(max_workers=25) as ex:
        for idx, (seq, name) in enumerate(zip(apt_seqs, names), start=1):
            print(f"[{idx}/{total}] '{name}' 크롤링 시작…", flush=True)
            fut = ex.submit(search_apartment, name)
            future_to_meta[fut] = seq
            time.sleep(0.5)

        # 작업이 끝날 때까지 기다린 후 결과 수집
        for fut in as_completed(future_to_meta):
            seq = future_to_meta[fut]
            ci, lst, imgs, schools = fut.result()
            results[seq] = {
                "complex": ci,
                "list": lst,
                "images": imgs,
                "schools": schools,
            }

    # 3) 정보 출력
    for seq in apt_seqs:
        info = results.get(seq, {})
        name = info.get("name")
        ci = info.get("complex")
        lst = info.get("list") or []
        imgs = info.get("images") or []
        schools = info.get("schools") or []

        # print(f"\n===== [{seq}] {name} =====")
        # if ci is None:
        # print("  → 오류나 결과 없음으로 건너뜀")
        # continue

        # 단지정보
        # print("단지정보:")
        # for k, v in ci.items():
        #     print(f"  {k}: {v}")

        # 매물리스트
        # print("\n매물리스트:")
        # if not lst:
        # print("  정보 없음")
        # else:
        #     for idx, d in enumerate(lst, 1):
        #         print(f"  --- 매물 {idx} ---")
        #         for kk, vv in d.items():
        #             print(f"    {kk}: {vv}")
        # 이미지
        # print("\n이미지:")
        # if not imgs:
        #     print("  정보 없음")
        # else:
        #     for idx, img in enumerate(imgs, 1):
        #         print(f"  --- 이미지 {idx} ---")
        #         print(f"    url: {img['url']}")
        #         print(f"    label: {img['label']}")

    # ─ DB 삽입 ─────────────────────────
    with conn:
        with conn.cursor() as cur:
            for seq, info in results.items():
                ci = info["complex"]
                if ci is None:
                    continue
                lst = info["list"] or []

                # 면적 최소·최대
                a_min, a_max = parse_area_range(ci.get("면적", ""))

                # 매매가·전세가 최소·최대
                tp_min, tp_max = split_price_range(ci.get("매매가", ""))
                jp_min, jp_max = split_price_range(ci.get("전세가", ""))

                # 거래 상세
                last_detail = ci.get("상세")

                # house_detail 업데이트
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
                    (
                        seq,
                        a_min,
                        a_max,
                        tp_min,
                        tp_max,
                        jp_min,
                        jp_max,
                        last_detail,
                    ),
                )

                # house_deal 업데이트
                for d in lst:
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
                # ─ 이미지 테이블 업데이트 ─────────────────────────
                imgs = info.get("images") or []
                for img in imgs:
                    cur.execute(
                        """
                        INSERT INTO house_image
                        (apt_seq, img_path, description)
                        VALUES (%s, %s, %s)
                        ON DUPLICATE KEY UPDATE
                        img_path=VALUES(img_path),
                        description=VALUES(description)
                        """,
                        (seq, img["url"], img["label"]),
                    )
                # ─ 학군정보 저장 ────────────────────────────────
                schools = info.get("schools") or []
                for sch in schools:
                    # 1) school_detail upsert
                    cur.execute(
                        """
                        INSERT INTO school_detail
                        (school_name, school_type, address, phone,
                        established, office, teacher_count,
                        student_count, homepage)
                        VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)
                        ON DUPLICATE KEY UPDATE
                        id=LAST_INSERT_ID(id)
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

                    # 2) house_school 매핑 테이블에 관계 저장
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
