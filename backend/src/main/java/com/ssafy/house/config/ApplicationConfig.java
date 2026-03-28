package com.ssafy.house.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ssafy.house.interceptor.PerformanceInterceptor;
import com.ssafy.house.model.dto.Comment;
import com.ssafy.house.model.dto.HouseDeal;
import com.ssafy.house.model.dto.HouseDealDone;
import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.dto.HouseInfoFull;
import com.ssafy.house.model.dto.HouseInfoSimple;
import com.ssafy.house.model.dto.HouseRecommend;
import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.dto.News;
import com.ssafy.house.model.dto.Post;
import com.ssafy.house.model.dto.SchoolInfo;

@Configuration
@MapperScan(
        basePackages = "com.ssafy.house.model.dao",
        sqlSessionTemplateRef = "sqlSessionTemplate")
public class ApplicationConfig implements WebMvcConfigurer {
    @Autowired
    private PerformanceInterceptor performanceMonitorInterceptor;

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ApplicationContext applicationContext)
            throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setVfs(SpringBootVFS.class);
        factoryBean.setTypeAliases(new Class<?>[] {
                Comment.class,
                HouseDeal.class,
                HouseDealDone.class,
                HouseInfo.class,
                HouseInfoFull.class,
                HouseInfoSimple.class,
                HouseRecommend.class,
                Member.class,
                News.class,
                Post.class,
                SchoolInfo.class
        });
        factoryBean.setMapperLocations(applicationContext.getResources("classpath*:mappers/**/*.xml"));

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        factoryBean.setConfiguration(configuration);

        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @SuppressWarnings("null")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(performanceMonitorInterceptor)
                .addPathPatterns("/member/**");
    }
}
