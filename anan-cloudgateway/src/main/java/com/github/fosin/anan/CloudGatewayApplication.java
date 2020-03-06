package com.github.fosin.anan;

import com.github.fosin.anan.core.banner.AnanBanner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * Spring Cloud Gateway
 * @author fosin
 * @date 2019/5/5
 */
@SpringCloudApplication
//@EnableAnanSwagger2 TODO 因为Swagger暂不支持webflux项目，所以Gateway里不能配置SwaggerConfig，也就是说Gateway无法提供自身API。
//@EnableAnanOauth2 TODO 由于Oauth2依赖Webmvc模块，这和webflux冲突
//@EnableWebSecurity
//@EnableRedisHttpSession
public class CloudGatewayApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(CloudGatewayApplication.class)
                .banner(new AnanBanner("AnAn Cloud Gateway"))
                .logStartupInfo(true)
                .run(args);
    }

}
