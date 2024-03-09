package com.mmm.springbootinit.config;

import org.springframework.context.annotation.Profile;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.config
 * @Project：mmmbi-backend
 * @name：Knife4jConfig
 * @Date：2024/3/9 14:04
 * @Filename：Knife4jConfig
 */
//@Configuration
//@EnableSwagger2
//@Profile({"dev", "prod"})   //版本控制访问
public class Knife4jConfig {
//    @Bean
//    public Docket defaultApi2() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                // 指定 Controller 扫描包路径
//                .apis(RequestHandlerSelectors.basePackage("com.shier.shierbi.controller"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//    /**
//     * 自定义接口文档信息
//     * @return
//     */
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                // 接口文档的标题
//                .title("智能BI系统")
//                // 接口文档的描述信息
//                .description("用户只需要导入最原始的数据集，输入想要进行分析的目标（比如帮我分析一下网站的增长趋势)，就能利用AI自动生成一个符合要求的图表以及分析结论。此外，还会有图表管理、异步生成等功能。")
//                // 提供服务的是谁？可以填写你自己的地址因为是你自己提供的服务
//                .termsOfServiceUrl("https://github.com/kongshier")
//                .contact(new Contact("Shier", "https://blog.csdn.net/qq_56098191", "2927527234@qq.com"))
//                // 版本
//                .version("1.0")
//                // 构建
//                .build();
//    }
}
