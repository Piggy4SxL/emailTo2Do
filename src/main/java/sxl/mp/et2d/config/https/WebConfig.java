package sxl.mp.et2d.config.https;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.Ssl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author SxL
 *         Created on 2018/1/10.
 */
@Configuration
public class WebConfig {

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                Ssl ssl = new Ssl();
                ssl.setProtocol("TLSv1.2");
                System.out.println(ssl.getProtocol());
                ssl.setKeyStore("classpath:sxlfw.site.pfx");
                ssl.setKeyStorePassword("xilai123");
                container.setSsl(ssl);
                container.setPort(443);
            }
        };
    }
}