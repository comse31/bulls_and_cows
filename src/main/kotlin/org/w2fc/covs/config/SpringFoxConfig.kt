package org.w2fc.covs.config

import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import springfox.documentation.swagger2.annotations.EnableSwagger2


@EnableSwagger2
@PropertySource("classpath:application.properties")
@Configuration
class SwaggerConfig {
    @Bean
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient()
    }
}