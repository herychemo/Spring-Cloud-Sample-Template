package com.grayraccoon.sample.authms.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef =  "testEntityManagerFactory",
        transactionManagerRef = "testTransactionManager",
        basePackages = { "com.grayraccoon.sample.authms.data.postgres.repository" }    // Repository Package
)
@Profile("default")
public class TestDataSourceConfig {

    /**
     * Creates programmatically the dataSource bean for the test DB connection.
     *
     * @return javax.sql.DataSource
     */
    @Primary
    @Bean("testDataSource")
    @ConfigurationProperties(prefix="spring.datasource.test")
    public DataSource getDataSource(){
        return DataSourceBuilder.create().build();
    }

    /**
     * Creates programmatically the entityManager bean for the test DB connection.
     *
     * @param builder
     * @param dataSource
     * @return org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
     */
    @Primary
    @Bean(name = "testEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("testDataSource") DataSource dataSource
    ) {
        Map<String, Object> props = new HashMap<>();
        Map<String, Object> jpaProperties = new HashMap<>();

        props.put("jpaDialect", org.springframework.orm.jpa.vendor.HibernateJpaDialect.class);
        props.put("jpaProperties", jpaProperties);

        return builder
                .dataSource(dataSource)
                .packages("com.grayraccoon.sample.authms.data.postgres.domain")    // Domain Package
                .persistenceUnit("test")
                .properties(props)
                .build();
    }


    /**
     * Creates programmatically the transactionManager bean for the test DB connection.
     *
     * @param entityManagerFactory
     * @return org.springframework.transaction.PlatformTransactionManager
     */
    @Primary
    @Bean(name = "testTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("testEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
