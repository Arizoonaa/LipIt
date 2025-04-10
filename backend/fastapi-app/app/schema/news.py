from pydantic import BaseModel, HttpUrl
from typing import List, Optional


class NewsItem(BaseModel):
    title: str
    source: str
    url: HttpUrl
    published_at: str
    thumbnail_url: Optional[HttpUrl] = None
    summary: Optional[str] = None


class NewsListResponse(BaseModel):
    news: List[NewsItem]
    updated_at: str


class NewsCrawlRequest(BaseModel):
    count: int = 20
