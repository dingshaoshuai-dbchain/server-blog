package cloud.dbchain.server.blog;

import cloud.dbchain.server.blog.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/blog/publish")
                .addPathPatterns("/blog/discuss")
                .addPathPatterns("/user/saveRecoverWord")
                .addPathPatterns("/user/resetPasswordFromOld");
    }
}
