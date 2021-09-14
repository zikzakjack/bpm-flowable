package zikzakjack;

import org.flowable.engine.cfg.HttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

	@Bean
	public HttpClientConfig httpClientConfig() {
		HttpClientConfig httpClientConfig = new HttpClientConfig();
		httpClientConfig.setConnectTimeout(5000);
		httpClientConfig.setSocketTimeout(5000);
		httpClientConfig.setConnectionRequestTimeout(5000);
		httpClientConfig.setRequestRetryLimit(5);
		httpClientConfig.setDisableCertVerify(true);
		return httpClientConfig;
	}

}
