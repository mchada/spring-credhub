package org.springframework.credhub.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration.ClientFactoryWrapper;
import org.springframework.credhub.autoconfig.security.CredHubCredentialsDetails;
import org.springframework.credhub.configuration.OAuth2CredHubTemplateFactory;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link CredHubTemplate} with
 * OAuth2 credentials if spring-security-oauth2 and OAuth2 properties are provided.
 * 
 * @author Daniel Lavoie
 */
@Configuration
@AutoConfigureBefore(CredHubTemplateAutoConfiguration.class)
@ConditionalOnProperty("spring.credhub.oauth2.client-id")
@ConditionalOnClass(name = "org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails")
public class CredHubOAuth2TemplateAutoConfiguration {
	private final OAuth2CredHubTemplateFactory credHubTemplateFactory = new OAuth2CredHubTemplateFactory();

	/**
	 * Bean that holds OAuth2 credential information for CredHub.
	 *
	 * @return the {@link ClientCredentialsResourceDetails} bean
	 */
	@Bean
	@CredHubCredentialsDetails
	@ConfigurationProperties("spring.credhub.oauth2")
	public ClientCredentialsResourceDetails credHubCredentialsDetails() {
		return new ClientCredentialsResourceDetails();
	}

	/**
	 * Preconfigured {@link OAuth2RestTemplate} with OAuth2 credentials for CredHub.
	 *
	 * @param credHubProperties {@link CredHubProperties} for CredHub
	 * @param credHubCredentialsDetails OAuth2 credentials for use with the {@link OAuth2RestTemplate}
	 * @param clientFactoryWrapper a {@link ClientFactoryWrapper} to customize CredHub http requests
	 *
	 * @return the {@link CredHubOperations} bean
	 */
	@Bean
	public CredHubOperations credHubTemplate(
			CredHubProperties credHubProperties,
			@CredHubCredentialsDetails ClientCredentialsResourceDetails credHubCredentialsDetails,
			ClientFactoryWrapper clientFactoryWrapper) {
		return credHubTemplateFactory.credHubTemplate(credHubCredentialsDetails,
				credHubProperties,
				clientFactoryWrapper.getClientHttpRequestFactory());
	}
}
