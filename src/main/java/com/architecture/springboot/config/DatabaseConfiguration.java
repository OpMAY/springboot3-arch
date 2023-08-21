package com.architecture.springboot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Log4j2
@Configuration
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class DatabaseConfiguration {
    private final ApplicationContext applicationContext;

    /**
     * DataBase Information Settings with Properties
     */

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    /**
     * JPA SETTINGS
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPersistenceUnitName("jpa-mysql");
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        return entityManagerFactory;
    }

    /**
     *  SQLSessionFactory Registration for mybatis
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        log.info("sqlSessionFactory init");
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(applicationContext.getResources("classpath:sqls/*.xml"));
        bean.setTypeAliasesPackage("com/architecture/springboot/model/dto");
        log.info("sqlSessionFactory initialized");
        return bean.getObject();
    }

    /**
     * SQLSessionTemplate For Mybatis
     * **/
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        log.info("sqlSessionTemplate init");
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * Transactional Settings - Chained
     * **/
    @Bean
    public PlatformTransactionManager transactionManager() {
        // MyBatis Transactional
        log.info("Mybatis transactional init");
        DataSourceTransactionManager mybatisDataSourceTransactionManager = new DataSourceTransactionManager();
        mybatisDataSourceTransactionManager.setDataSource(dataSource());
        log.info("Mybatis transactional initialized");
        // JPA Transactional
        log.info("jpa transactional init");
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        log.info("jpa transactional initialized");
        return new ChainedTransactionManager(jpaTransactionManager, mybatisDataSourceTransactionManager);
    }
}
