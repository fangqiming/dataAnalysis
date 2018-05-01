package com.i000.stock.user.web.config;

import com.i000.stock.user.api.entity.bo.AssetInitBo;
import com.i000.stock.user.core.file.oss.OSSFileUpload;
import com.i000.stock.user.core.file.oss.OSSUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 20:04 2018/4/27
 * @Modified By:
 */
@Configuration
public class CommonConfig {
    @Bean("imageOSSFileUpload")
    @ConfigurationProperties(prefix = "component.file.system.oss.private")
    public OSSFileUpload imageOSSFileUpload(@Value("${component.file.system.oss.image.catalog}") String catalog,
                                            @Value("${component.file.system.oss.http-prefix}") String httpPrefix) {
        OSSFileUpload imageOSSFileUpload = new OSSFileUpload();
        imageOSSFileUpload.setCatalog(catalog);
        imageOSSFileUpload.setHttpPrefix(httpPrefix);
        return imageOSSFileUpload;
    }

    @Bean
    @ConfigurationProperties(prefix = "component.file.system.oss.private")
    public OSSUtil ossUtil(@Value("${component.file.system.oss.image.catalog}") String catalog,
                           @Value("${component.file.system.oss.private.read-endpoint}") String endpoint) {
        OSSUtil ossUtil = new OSSUtil();
        ossUtil.setCatalog(catalog);
        ossUtil.setEndpoint(endpoint);
        return ossUtil;
    }

    @Bean
    public AssetInitBo assetInitBo(){
        return new AssetInitBo();
    }

}
