package com.gm.wj.config;

import com.gm.wj.filter.URLPathMatchingFilter;
import com.gm.wj.realm.WJRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfiguration {
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/api/login");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
//        Map<String, Filter> customizedFilter = new HashMap<>();  // 自定义过滤器设置 1

//        customizedFilter.put("url", getURLPathMatchingFilter()); // 自定义过滤器设置 2，命名，需在设置过滤路径前

//        filterChainDefinitionMap.put("/api/authentication", "authc"); // 防鸡贼登录
//        filterChainDefinitionMap.put("/api/menu", "authc");
//        filterChainDefinitionMap.put("/api/admin/**", "authc");
//
//        filterChainDefinitionMap.put("/api/admin/**", "url");  // 自定义过滤器设置 3，设置过滤路径

        // ========== 公开路径 ==========
        filterChainDefinitionMap.put("/api/login", "anon");
        filterChainDefinitionMap.put("/api/register", "anon");
        filterChainDefinitionMap.put("/api/logout", "anon");

// 图书相关 - 查看类（GET请求）
        filterChainDefinitionMap.put("/api/books", "anon");              // 列表
        filterChainDefinitionMap.put("/api/books/*", "anon");            // 详情
        filterChainDefinitionMap.put("/api/books/*/reviews", "anon");    // 查看评论
        filterChainDefinitionMap.put("/api/books/*/quotes", "anon");     // 查看金句
        filterChainDefinitionMap.put("/api/search", "anon");
        filterChainDefinitionMap.put("/api/categories/**", "anon");

// 静态资源
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");

// Actuator
        filterChainDefinitionMap.put("/actuator/**", "anon");

// ========== 需要认证的路径 ==========
        filterChainDefinitionMap.put("/api/authentication", "authc");
        filterChainDefinitionMap.put("/api/menu", "authc");

// 图书相关 - 操作类（POST/PUT/DELETE）
        filterChainDefinitionMap.put("POST:/api/books/reviews", "authc");   // 添加评论
        filterChainDefinitionMap.put("POST:/api/books/quotes", "authc");    // 添加金句
        filterChainDefinitionMap.put("/api/books/reviews/*/like", "anon"); // 点赞评论
        filterChainDefinitionMap.put("/api/books/quotes/*/like", "anon");  // 点赞金句

// 管理后台
        filterChainDefinitionMap.put("/api/admin/**", "authc");

//        shiroFilterFactoryBean.setFilters(customizedFilter); // 自定义过滤器设置 4，启用
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    public URLPathMatchingFilter getURLPathMatchingFilter() {
        return new URLPathMatchingFilter();
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(getWJRealm());
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        cookieRememberMeManager.setCipherKey("EVANNIGHTLY_WAOU".getBytes());
        return cookieRememberMeManager;
    }

    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setMaxAge(259200);
        return simpleCookie;
    }

    @Bean
    public WJRealm getWJRealm() {
        WJRealm wjRealm = new WJRealm();
        wjRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return wjRealm;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
