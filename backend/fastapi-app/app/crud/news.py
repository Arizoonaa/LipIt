import random
import requests
from bs4 import BeautifulSoup
from datetime import datetime, timezone, timedelta
from typing import List, Dict
from sqlalchemy import func, select
from sqlalchemy.ext.asyncio import AsyncSession
import logging
import time
import re
import urllib.parse

from app.model.news import News

KST = timezone(timedelta(hours=9))
logger = logging.getLogger(__name__)


# 본문 내용 크롤링 함수
async def crawl_content(url: str) -> str:
    """뉴스 URL에서 본문 내용을 크롤링합니다."""
    try:
        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        }

        logger.info(f"본문 크롤링 URL: {url}")
        response = requests.get(url, headers=headers, timeout=10)

        if response.status_code != 200:
            logger.error(f"본문 응답 오류: {response.status_code}")
            return ""

        soup = BeautifulSoup(response.text, "html.parser")
        content = ""

        # KBS 뉴스 본문 셀렉터 (최우선)
        kbs_selectors = [
            "#dic_area",  # KBS 뉴스 실제 본문 영역
            "#newsct_article",  # KBS 뉴스 기사 영역
            ".news_cont",  # KBS 뉴스 컨텐츠 영역
        ]

        # 다른 뉴스 사이트 선택자
        general_selectors = [
            "#newsEndContents",
            "#articeBody",
            ".news_end_content",
            ".news_content",
            ".article_body",
            ".article-body",
            ".news-article-contents",
        ]

        # KBS 선택자 먼저 시도
        for selector in kbs_selectors:
            content_div = soup.select_one(selector)
            if content_div:
                # 불필요한 요소 제거 (제보하기, 구독, 사진출처 등)
                for tag in content_div.select(
                    "script, style, .end_photo_org, .mask, .promotion"
                ):
                    tag.decompose()

                # <br> 태그를 줄바꿈으로 변환
                for br in content_div.find_all("br"):
                    br.replace_with("\n")

                # 텍스트 추출 후 정제
                text = content_div.get_text(separator=" ").strip()

                # 불필요한 텍스트 제거
                text = re.sub(r"■\s*제보하기.*", "", text, flags=re.DOTALL)
                text = re.sub(r"\[\s*사진\s*출처.*?\]", "", text)
                text = re.sub(r"▷\s*네이버,\s*유튜브에서.*", "", text)
                text = re.sub(r"KBS\s+뉴스를\s+구독해주세요!.*", "", text)
                text = re.sub(r"◎\s*공감언론.*", "", text)
                # 앵커 텍스트와 종합 텍스트 제거
                text = re.sub(r"\[앵커\][^\n]*\n?", "", text)
                text = re.sub(r"□\s*\(종합\)[^\n]*\n?", "", text)
                # KBS 지역 표시 제거
                text = re.sub(r"\[KBS\s+[^\]]+\]\s*", "", text)

                # 연속된 공백 제거
                text = re.sub(r"\s+", " ", text)
                content = text.strip()

                logger.info(f"KBS 선택자로 본문 추출 성공 (길이: {len(content)}자)")
                break

        # KBS 선택자로 실패하면 일반 선택자 시도
        if not content:
            for selector in general_selectors:
                content_div = soup.select_one(selector)
                if content_div:
                    # 불필요한 요소 제거
                    for tag in content_div.select(
                        ".source, .reporter, .copyright, script, style"
                    ):
                        tag.decompose()

                    content = content_div.get_text(separator=" ").strip()

                    # 불필요한 텍스트 제거
                    content = re.sub(r"■\s*제보하기.*", "", content, flags=re.DOTALL)
                    content = re.sub(r"\[\s*사진\s*출처.*?\]", "", content)

                    # 연속된 공백 제거
                    content = re.sub(r"\s+", " ", content)

                    if content:
                        logger.info(
                            f"일반 선택자로 본문 추출 성공 (길이: {len(content)}자)"
                        )
                        break

        # 위 방법들로 실패하면 직접 텍스트 노드 추출
        if not content or len(content) < 100:
            # 본문 영역에서 직접 텍스트 추출
            article_area = (
                soup.find("article")
                or soup.find("div", class_="article")
                or soup.find("div", id="contents")
            )
            if article_area:
                # <br> 태그를 줄바꿈으로 변환
                for br in article_area.find_all("br"):
                    br.replace_with("\n")

                # 텍스트 노드만 추출
                texts = []
                for text in article_area.stripped_strings:
                    if len(text.strip()) > 20:  # 의미있는 길이의 텍스트만
                        texts.append(text.strip())

                content = " ".join(texts)

                # 불필요한 텍스트 제거
                content = re.sub(r"■\s*제보하기.*", "", content, flags=re.DOTALL)
                content = re.sub(r"\[\s*사진\s*출처.*?\]", "", content)

                logger.info(f"텍스트 노드 방식으로 본문 추출 (길이: {len(content)}자)")

        # 내용이 너무 길면 잘라내기
        if len(content) > 2000:
            content = content[:2000] + "..."

        return content

    except Exception as e:
        logger.error(f"본문 크롤링 오류: {str(e)}")
        return ""


# KBS 네이버 카테고리 매핑 정보를 가져옵니다
async def get_kbs_category_mapping(headers: dict) -> Dict[str, str]:
    """네이버 KBS 페이지에서 카테고리 ID와 이름 매핑을 가져옵니다."""
    try:
        url = "https://media.naver.com/press/056"
        response = requests.get(url, headers=headers, timeout=5)

        if response.status_code != 200:
            return {}

        soup = BeautifulSoup(response.text, "html.parser")
        category_map = {}

        # 카테고리 메뉴 항목 찾기
        category_items = soup.select("li.Nlist_item._LNB_ITEM")

        for item in category_items:
            link = item.find("a")
            if link and "href" in link.attrs:
                href = link["href"]
                # sid 파라미터 추출 (카테고리 ID)
                if "sid=" in href:
                    sid = href.split("sid=")[1].split("#")[0]
                    # 카테고리 이름 추출
                    span = link.find("span", class_="Nitem_link_menu")
                    if span and span.text:
                        category_name = span.text.strip()
                        category_map[sid] = category_name
                        logger.info(f"카테고리 매핑: {sid} -> {category_name}")

        return category_map

    except Exception as e:
        logger.error(f"카테고리 매핑 추출 실패: {str(e)}")
        return {}


# 네이버 뉴스 URL에서 카테고리 추출
async def extract_category_from_url(
    url: str, category_map: Dict[str, str], headers: dict
) -> str:
    """URL에서 카테고리 정보를 추출합니다."""
    try:
        # URL에서 sid 파라미터 추출
        parsed_url = urllib.parse.urlparse(url)
        query_params = urllib.parse.parse_qs(parsed_url.query)

        # 1. 네이버 미디어 URL인 경우 sid 직접 추출
        if "media.naver.com" in url and "sid" in query_params:
            sid = query_params["sid"][0]
            if sid in category_map:
                logger.info(f"URL에서 직접 카테고리 추출: {category_map[sid]}")
                return category_map[sid]

        # 2. 네이버 뉴스인 경우 페이지 분석
        if "news.naver.com" in url:
            response = requests.get(url, headers=headers, timeout=5)
            if response.status_code == 200:
                soup = BeautifulSoup(response.text, "html.parser")

                # 2-1. 활성화된 메뉴에서 찾기
                active_menu = soup.select_one("li.Nlist_item._LNB_ITEM.is_active")
                if active_menu:
                    link = active_menu.find("a")
                    if link and "href" in link.attrs:
                        href = link["href"]
                        if "sid=" in href:
                            sid = href.split("sid=")[1].split("#")[0]
                            if sid in category_map:
                                logger.info(
                                    f"활성 메뉴에서 카테고리 추출: {category_map[sid]}"
                                )
                                return category_map[sid]

                # 2-2. 기사 내 카테고리 표시에서 찾기
                category_span = soup.select_one(".press_category")
                if category_span and category_span.text:
                    category_text = category_span.text.strip()
                    # 매핑 테이블에서 일치하는 카테고리 찾기
                    for sid, name in category_map.items():
                        if name == category_text:
                            logger.info(f"카테고리 표시에서 추출: {name}")
                            return name

        # 3. 제목 키워드 기반 추정
        return "일반"  # 기본값

    except Exception as e:
        logger.error(f"URL에서 카테고리 추출 실패: {str(e)}")
        return "일반"


# KBS 네이버 인기 뉴스 랭킹 크롤링
async def crawl_news() -> List[dict]:
    """네이버 KBS 인기 뉴스 랭킹을 크롤링합니다."""
    try:
        logger.info("네이버 KBS 인기 뉴스 랭킹 크롤링 시작")
        url = "https://media.naver.com/press/056/ranking?type=popular"

        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        }

        # 카테고리 매핑 정보 가져오기
        category_map = await get_kbs_category_mapping(headers)
        if not category_map:
            logger.warning("카테고리 매핑 정보를 가져오지 못했습니다")
            # 기본 매핑 정보 설정
            category_map = {
                "100": "정치",
                "101": "경제",
                "102": "사회",
                "103": "생활",
                "104": "세계",
                "105": "IT",
                "106": "연예",
                "107": "스포츠",
                "108": "오피니언",
            }

        logger.info(f"크롤링 URL: {url}")
        response = requests.get(url, headers=headers, timeout=10)

        if response.status_code != 200:
            logger.error(f"응답 오류: {response.status_code}")
            return []

        logger.info(
            f"응답 코드: {response.status_code}, 응답 길이: {len(response.text)}"
        )

        soup = BeautifulSoup(response.text, "html.parser")
        news_items = []

        # 뉴스 항목 찾기
        items = soup.select(".press_ranking_list li")
        logger.info(f"발견된 뉴스 항목: {len(items)}개")

        for item in items:
            try:
                # 제목 찾기 (strong 태그)
                title_elem = item.find("strong")
                if not title_elem:
                    continue

                title = title_elem.text.strip()

                # 링크 찾기 (a 태그)
                link = None
                for parent in title_elem.parents:
                    if parent.name == "a":
                        link = parent
                        break

                if link and "href" in link.attrs:
                    news_url = link["href"]

                    # URL에서 카테고리 추출
                    category = await extract_category_from_url(
                        news_url, category_map, headers
                    )
                    logger.info(f"뉴스 [{title[:20]}...] 카테고리: {category}")

                    # 정치 카테고리 제외
                    if category == "정치":
                        logger.info(f"정치 카테고리 뉴스 제외: {title[:30]}...")
                        continue

                    # 중복 확인
                    is_duplicate = False
                    for existing in news_items:
                        if existing["title"] == title:
                            is_duplicate = True
                            break

                    if not is_duplicate:
                        # 본문 내용 크롤링
                        content_text = await crawl_content(news_url)

                        news_items.append(
                            {
                                "title": title,
                                "url": news_url,
                                "category": category,
                                "content": content_text,
                            }
                        )

                        logger.info(f"뉴스 발견 ({category}): {title[:30]}...")

                        # 요청 간 약간의 딜레이
                        time.sleep(0.5)

                    if len(news_items) >= 20:
                        break
            except Exception as e:
                logger.error(f"항목 처리 오류: {str(e)}")
                continue

        logger.info(f"크롤링 완료: {len(news_items)}개 뉴스")
        return news_items[:20]

    except Exception as e:
        logger.error(f"크롤링 오류: {str(e)}")
        return []


# 뉴스 크롤링 및 DB 저장
async def update_news_db(db: AsyncSession) -> int:
    """뉴스를 크롤링하여 DB에 저장하고 저장된 개수를 반환합니다."""
    try:
        # 뉴스 크롤링
        news_items = await crawl_news()
        saved_count = 0

        if not news_items:
            logger.error("크롤링된 뉴스가 없습니다.")
            return 0

        # 타임스탬프 생성 (URL 고유성 보장)
        timestamp = int(datetime.now().timestamp())

        # DB에 저장
        for item in news_items:
            try:
                # URL에 타임스탬프 추가 (중복 방지)
                if "?" in item["url"]:
                    item["url"] = f"{item['url']}&t={timestamp}"
                else:
                    item["url"] = f"{item['url']}?t={timestamp}"

                # 새 뉴스 추가
                news = News(
                    title=item["title"],
                    url=item["url"],
                    content=item["content"],
                    category=item["category"],
                )
                db.add(news)
                saved_count += 1
                logger.info(f"새 뉴스 저장: {item['title'][:30]}...")
            except Exception as e:
                logger.error(f"저장 오류: {str(e)}")
                continue

        # 커밋
        await db.commit()
        logger.info(f"총 {saved_count}개 뉴스 저장 완료")
        return saved_count
    except Exception as e:
        logger.error(f"뉴스 업데이트 오류: {str(e)}")
        await db.rollback()
        return 0


# 기상청 날씨 크롤링
async def crawl_weather() -> List[dict]:
    """기상청 단기예보 크롤링"""
    try:
        logger.info("기상청 단기예보 크롤링 시작")
        url = "https://www.weather.go.kr/w/weather/forecast/short-term.do"

        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        }

        response = requests.get(url, headers=headers, timeout=10)

        if response.status_code != 200:
            logger.error(f"날씨 응답 코드 오류: {response.status_code}")
            return []

        logger.info(
            f"날씨 응답 코드: {response.status_code}, 응답 길이: {len(response.text)}"
        )

        soup = BeautifulSoup(response.text, "html.parser")
        weather_items = []

        # 제목 (h4 태그) 찾기
        title_elem = soup.select_one("div.cmp-view-header h4")
        if title_elem:
            title = title_elem.text.strip()
            logger.info(f"날씨 제목: {title}")

            # 내용 (summary 클래스) 찾기
            summary_elem = soup.select_one("p.summary")
            content = ""

            if summary_elem:
                # 모든 span 요소 내용 합치기
                for span in summary_elem.find_all("span"):
                    content += span.text.strip() + " "

                content = content.strip()
                logger.info(f"날씨 내용: {content[:100]}...")

                # 결과 항목 추가
                weather_items.append(
                    {"title": title, "content": content, "url": url, "category": "날씨"}
                )
            else:
                logger.error("summary 태그를 찾을 수 없습니다.")
        else:
            logger.error("h4 태그를 찾을 수 없습니다.")

        logger.info(f"날씨 크롤링 완료: {len(weather_items)}개 항목")
        return weather_items

    except Exception as e:
        logger.error(f"날씨 크롤링 오류: {str(e)}")
        return []


# 날씨 크롤링 및 DB 저장
async def update_weather_db(db: AsyncSession) -> int:
    """날씨를 크롤링하여 DB에 저장하고 저장된 개수를 반환합니다."""
    try:
        # 날씨 크롤링
        weather_items = await crawl_weather()
        saved_count = 0

        if not weather_items:
            logger.error("크롤링된 날씨 정보가 없습니다.")
            return 0

        # 타임스탬프 생성 (URL 고유성 보장)
        timestamp = int(datetime.now().timestamp())

        # DB에 저장
        for item in weather_items:
            try:
                # URL에 타임스탬프 추가 (중복 방지)
                if "?" in item["url"]:
                    item["url"] = f"{item['url']}&t={timestamp}"
                else:
                    item["url"] = f"{item['url']}?t={timestamp}"

                # 새 날씨 정보 추가
                news = News(
                    title=item["title"],
                    url=item["url"],
                    content=item["content"],
                    category=item["category"],
                )
                db.add(news)
                saved_count += 1
                logger.info(f"새 날씨 정보 저장: {item['title']}")
            except Exception as e:
                logger.error(f"날씨 저장 오류: {str(e)}")
                continue

        # 커밋
        await db.commit()
        logger.info(f"총 {saved_count}개 날씨 정보 저장 완료")
        return saved_count
    except Exception as e:
        logger.error(f"날씨 업데이트 오류: {str(e)}")
        await db.rollback()
        return 0


async def get_random_news(session: AsyncSession, category: str = None) -> News:
    """
    뉴스 테이블에서 랜덤 뉴스 하나를 가져온다.
    - category가 주어지면 해당 카테고리 내에서 선택
    - category가 없으면 전체에서 선택
    """
    query = select(func.min(News.news_id), func.max(News.news_id))
    if category:
        query = query.where(News.category == category)

    result = await session.execute(query)
    min_id, max_id = result.fetchone()

    if min_id is None or max_id is None:
        return None  # 해당 조건에 맞는 뉴스 없음

    for _ in range(5):  # 최대 5번 시도
        random_id = random.randint(min_id, max_id)
        news_query = select(News).where(News.news_id == random_id)
        if category:
            news_query = news_query.where(News.category == category)

        news_result = await session.execute(news_query)
        news = news_result.scalars().first()
        if news:
            return news

    return None  # 조건에 맞는 뉴스가 없거나 시도 실패
