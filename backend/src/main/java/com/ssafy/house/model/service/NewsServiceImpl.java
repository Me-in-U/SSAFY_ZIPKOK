package com.ssafy.house.model.service;

import com.ssafy.house.model.dao.NewsDao;
import com.ssafy.house.model.dto.News;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsDao newsDao;

    @Override
    public List<News> getLatest(int limit, int offset) {
        return newsDao.selectLatest(limit, offset);
    }
}