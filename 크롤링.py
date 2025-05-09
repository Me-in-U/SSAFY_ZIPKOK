from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.keys import Keys
from bs4 import BeautifulSoup
import time
from concurrent.futures import ThreadPoolExecutor, as_completed


def parse_first_listing(html: str) -> dict:
    """#articleListArea .item 중 첫 번째를 파싱"""
    soup = BeautifulSoup(html, "html.parser")
    item = soup.select_one("#articleListArea .item")
    if not item:
        raise ValueError("매물 리스트가 없습니다.")
    inner = item.select_one(".item_inner")

    return {
        "매물명": inner.select_one(".item_title .text").get_text(strip=True),
        "거래종류": inner.select_one(".price_line .type").get_text(strip=True),
        "가격": inner.select_one(".price_line .price").get_text(strip=True),
        "유형": inner.select_one(".info_area .type").get_text(strip=True),
        "스펙": inner.select_one(".info_area .spec").get_text(strip=True),
        "설명": inner.select(".info_area .line")[1].get_text(strip=True),
        "확인일": inner.select_one(".label_area .type-confirmed").get_text(strip=True),
    }


def parse_complex_info(html: str) -> dict:
    """id=summaryInfo 단지 요약 정보를 파싱"""
    soup = BeautifulSoup(html, "html.parser")
    summary = soup.find("div", id="summaryInfo")
    if not summary:
        raise ValueError("단지 요약 정보가 없습니다.")

    title = summary.find("h3", class_="title").get_text(strip=True)
    features = {
        dt.get_text(strip=True): dd.get_text(strip=True)
        for dt, dd in zip(
            summary.select(".complex_feature dt"), summary.select(".complex_feature dd")
        )
    }

    trade = summary.select_one(".complex_price--trade")
    if trade:
        recent_price = trade.select_one(".data").get_text(strip=True)
        recent_detail = trade.select_one(".date").get_text(strip=True)
    else:
        recent_price = recent_detail = "-"

    prices = {
        cp.select_one("dt.title")
        .get_text(strip=True): cp.select_one("dd.data")
        .get_text(strip=True)
        for cp in summary.select(".complex_price_wrap .complex_price")
    }

    return {
        "단지명": title,
        **features,
        "최근 매매 실거래가": recent_price,
        "거래 상세": recent_detail,
        **prices,
    }


def parse_detail_page(html: str) -> dict:
    """
    단지/매물의 상세 정보 화면(리스트 없이, summaryInfo 아래에
    상세 탭들만 있는 경우)을 파싱.
    여기서는 예시로 summaryInfo + 주요 상세 정보 탭(예: 시세 탭)을 가져옵니다.
    """
    soup = BeautifulSoup(html, "html.parser")

    # 1) summaryInfo 먼저
    base = parse_complex_info(html)

    # 2) 그 아래 '시세/실거래가' 탭 데이터를 가져오기 (예시)
    detail = {}
    # 예를 들어, '시세/실거래가' 차트 바로 아래 테이블이 .complex_price_wrap 대신 .complex_price--trade-list 라면:
    trade_rows = soup.select(".complex_price--trade-list dl")
    for row in trade_rows:
        label = row.select_one("dt").get_text(strip=True)
        val = row.select_one("dd").get_text(strip=True)
        detail[f"실거래가_{label}"] = val

    # 3) 원하는 추가 필드가 있으면 여기에 더 파싱 로직을...
    # detail["추가필드명"] = ...

    return {**base, **detail}


def search_apartment(name: str) -> dict:
    chrome_opts = Options()
    # chrome_opts.add_argument("--headless")
    driver = webdriver.Chrome(options=chrome_opts)
    try:
        driver.get("https://new.land.naver.com/")
        wait = WebDriverWait(driver, 10)

        # 검색 입력
        inp = wait.until(EC.presence_of_element_located((By.ID, "land_search")))
        inp.clear()
        inp.send_keys(name)
        time.sleep(1)
        inp.send_keys(Keys.ENTER)
        time.sleep(1)  # JS 로딩 대기

        html = driver.page_source

        # 1) 리스트 있는지 시도
        try:
            return parse_first_listing(html)
        except ValueError:
            pass

        # 2) 단지 요약이 있는지 시도
        try:
            return parse_complex_info(html)
        except ValueError:
            pass

        # 3) 그 외 디테일-only 화면
        return parse_detail_page(html)

    finally:
        driver.quit()


if __name__ == "__main__":
    names = [
        "평창동 형우럭스빌(33-1)",
        "숭인동 동문A",
        "옥인동 세종아파트",
        "누상동 청호그린빌",
        "사직동 사직아파트",
        "명륜1가 방산아파트",
        "숭인동 블루빌",
        "교북동 경희궁자이(4단지)",
        "무악동 경희궁롯데캐슬",
        "효제동 이지마루종로",
        "신당동 신당푸르지오",
        "후암동 신후암",
        "신당동 도현블랑빌",
        "한남동 한남하우스",
        "원효로3가 금성아파트",
    ]
    results = {}

    # 최대 5개 스레드로 동시에 검색
    with ThreadPoolExecutor(max_workers=20) as executor:
        future_to_name = {executor.submit(search_apartment, n): n for n in names}

        for fut in as_completed(future_to_name):
            n = future_to_name[fut]
            try:
                results[n] = fut.result()
            except ValueError:
                results[n] = None
            except Exception as e:
                results[n] = f"오류 발생: {e}"

    # 모든 검색이 끝난 뒤 한 번에 출력
    for n in names:
        print(f"\n===== {n} =====")
        info = results[n]
        if isinstance(info, dict):
            for k, v in info.items():
                print(f"{k}: {v}")
        elif info is None:
            print("정보가 없습니다")
        else:
            print(info)
