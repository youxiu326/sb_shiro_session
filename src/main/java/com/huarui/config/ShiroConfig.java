package com.huarui.config;

import com.huarui.entity.Operator;
import com.huarui.entity.Resource;
import com.huarui.service.ResourceService;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import javax.servlet.ServletRequest;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lihui on 2019/1/29.
 * 实现session共享
 * shiro配置类
 */
@Configuration
public class ShiroConfig {

    @Value("${shiro.redis.host}")
    public String host;

    @Value("${shiro.redis.timeout}")
    public int timeout;

    @Autowired
    private ResourceService resourceService;

    //shiro共享session 参考博客: https://www.cnblogs.com/LUA123/p/9337963.html

    /*
    过滤器
    anon:所有url都都可以匿名访问
    authc: 需要认证才能进行访问
    user:配置记住我或认证通过可以访问
    */
    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //拦截器.
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/static*//**", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/loginAction", "anon");
        filterChainDefinitionMap.put("/register", "anon");
        //配置退出 过滤器,其中的具体的退出代码shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");

        //<!-- 过滤链定义，从上向下顺序执行，一般将*//**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        List<Resource> resources = resourceService.findAllByAutho();
        for(Resource re : resources){
            filterChainDefinitionMap.put(re.getUrl(), String.format("perms[%s]", re.getUrl()));
        }
        filterChainDefinitionMap.put("/**", "authc");
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.html"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 自定义 认证 授权
     */
    @Bean
    public MyShiroRealm myShiroRealm(){
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        return myShiroRealm;
    }

    /**
     * 4. 配置LifecycleBeanPostProcessor，可以来自动的调用配置在Spring IOC容器中 Shiro Bean 的生命周期方法
     * //TODO 该方法指定为static 否则无法初始化@Value
     * 参考博客: https://blog.csdn.net/wuxuyang_7788/article/details/70141812
     * @return
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 5. 启用IOC容器中使用Shiro的注解，但是必须配置第四步才可以使用
     *
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    /**
     * 密码校验器
     */
    @Bean
    public CredentialsMatcher myCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("MD5");//MD5加密
        credentialsMatcher.setHashIterations(2);//加密两次
        return credentialsMatcher;
    }

    /**
     * 配置SecurityManager
     *
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());  // 设置realm
        securityManager.setSessionManager(sessionManager());    // 设置sessionManager
//        securityManager.setCacheManager(redisCacheManager()); // 配置缓存的话，退出登录的时候crazycake会报错，要求放在session里面的实体类必须有个id标识
        return securityManager;
    }

    /**
     * redis 管理器
     * @return
     */
    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();     // crazycake 实现
        redisManager.setHost(host);
        redisManager.setTimeout(timeout);
        return redisManager;
    }

    /**
     * 缓存管理器
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager();   // crazycake 实现
        cacheManager.setRedisManager(redisManager());
        return cacheManager;
    }

    /**
     * session dao 持久到redis
     * @return
     */
    @Bean
    public RedisSessionDAO sessionDAO() {
        RedisSessionDAO sessionDAO = new RedisSessionDAO(); // crazycake 实现
        sessionDAO.setRedisManager(redisManager());
        sessionDAO.setSessionIdGenerator(new JavaUuidSessionIdGenerator()); //  Session ID 生成器
        return sessionDAO;
    }

    @Bean
    public SimpleCookie cookie() {
        SimpleCookie cookie = new SimpleCookie("SHAREJSESSIONID"); //  cookie的name,对应的默认是 JSESSIONID
        cookie.setHttpOnly(true);
        cookie.setPath("/");        //  path为 / 用于多个系统共享JSESSIONID
        return cookie;
    }

    /**
     * session 默认管理器
     * @return
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        //解决doReadSession多次访问
        DefaultWebSessionManager sessionManager = new CustomDefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(timeout);    // 设置session超时
        sessionManager.setDeleteInvalidSessions(true);      // 删除无效session
        sessionManager.setSessionIdCookie(cookie());        // 设置JSESSIONID
        sessionManager.setSessionDAO(sessionDAO());         // 设置sessionDAO
        return sessionManager;
    }

    /**
     * 配置RedisTemplate，充当数据库服务
     * @return
     */
    @Bean
    public RedisTemplate<String, Operator> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Operator> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Operator>(Operator.class));
        return redisTemplate;
    }

    /**
     * 解决doReadSession多次访问问题
     *
     */
    public static class CustomDefaultWebSessionManager extends DefaultWebSessionManager{
        @Override
        protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
            Serializable sessionId = getSessionId(sessionKey);
            if(sessionId == null){
                return super.retrieveSession(sessionKey);
            }
            ServletRequest request = WebUtils.getRequest(sessionKey);
            Object sessionObj = request.getAttribute(sessionId.toString());
            if (sessionObj != null) {
                return (Session) sessionObj;
            }
            Session s = super.retrieveSession(sessionKey);
            if (request != null && null != sessionId) {
                request.setAttribute(sessionId.toString(), s);
            }
            return s;
        }
    }

}
