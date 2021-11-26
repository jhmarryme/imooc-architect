package com.jhmarryme.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * description: swagger配置
 * @author Jiahao Wang
 * @date 2021/2/21 22:12
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

//    http://localhost:7777/swagger-ui.html     原路径
//    http://localhost:7777/doc.html     ui路径

    /**
     * 配置swagger2核心配置 docket
     *      http://localhost:7777/swagger-ui.html     原路径
     *      http://localhost:7777/doc.html     ui路径
     * <br/>
     * @return springfox.documentation.spring.web.plugins.Docket
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    .apis(RequestHandlerSelectors
                            .basePackage("com.jhmarryme.web.controller"))
                    .paths(PathSelectors.any())
                    .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("天天吃货 电商平台接口api")
                .contact(new Contact("jhmarryme",
                        "https://www.jhmarryme.top",
                        "jhmarryme@gmail.com"))
                .description("专为天天吃货提供的api文档")
                .version("1.0.1")   // 文档版本号
                .termsOfServiceUrl("https://www.jhmarryme.top")
                .build();
    }

}