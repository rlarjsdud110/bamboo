package bamboo.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private final String HEADER_AUTHORIZATION = "X-AUTH-TOKEN";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .groupName("API 1.0.0")
                .pathMapping("")
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false);
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(
                new SecurityReference("JWT", authorizationScopes));
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", HEADER_AUTHORIZATION, "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("대나무숲 게시판")
                .description("FISA Cloud Engineering 익명 게시판 입니다.\n" +
                        "\n" +

                        "스웨거에서 외부 서비스에 대한 테스트가 어려워 구글 로그인 및 회원가입은 테스트가 어렵습니다.\n"+
                        "\n"+

                        "권한이 필요한 기능은 테스트 로그인 API로 로그인 후 받은 토큰으로 테스트가 가능합니다.\n" +
                        "이메일 : rjsdud1827@gmail.com\n"+
                        "이름 : 김건영\n"+
                        "\n"+
                        "토큰 입력 방법 : Bearer {받은 토큰}\n" +
                        "\n" +
                        "사용자 기능 : AccessToken을 Authorize 버튼에 넣어야합니다.")

                .version("1.0.0")
                .termsOfServiceUrl("")
                .license("")
                .licenseUrl("")
                .build();
    }

}
