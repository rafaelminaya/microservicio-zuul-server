package com.formacionbdi.springboot.app.zuul.oauth;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
/*
 * @RefreshScope
 * Anotación que permite actualizar los componentes del contenedor de spring en tiempo real, sin reiniciar la aplicación.
 * Es decir, aquellas clases anotadas con @Component y sus derivados (@Service, @RestController, etc).
 * Esto también aplica a los atributos inyectados con @Value y @Autowired.
 * Este proecedimiento se realizará mediante un endpoint de Spring Actuator
 * Requiere instalar esta dependencia en el "pom.xml"
 */
@RefreshScope
/*
 * Implementaremos 2 métodos 
 * Uno para proteger nuestras rutas(endpoints) 
 * Y otro para configurar el token de la misma estructura/configuracion que el "servidor de autorización"
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Value("${config.security.oauth.jwt.key}")
	private String jwtKey;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		// configuración del token store
		resources.tokenStore(this.tokenStore());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
        // protegemos nuestras rutas

        http.authorizeRequests(requests -> requests.antMatchers("/api/security/oauth/**").permitAll() // asignamos nuestras rutas públicas
                .antMatchers(HttpMethod.GET, "/api/productos/listar", "/api/items/listar", "/api/usuarios/usuarios").permitAll()
                .antMatchers(HttpMethod.GET, "/api/productos/ver/{id}", "/api/items/ver/{id}/cantidad/{cantidad}", "/api/usuarios/usuarios/{id}").hasAnyRole("ADMIN", "USER")
                // 1° Opción
                .antMatchers("/api/productos/**", "/api/items/**", "/api/usuarios/**").hasRole("ADMIN")
                // 2° Opción
                //.antMatchers(HttpMethod.POST, "/api/productos/crear", "/api/items/crear", "/api/usuarios/crear").hasRole("ADMIN")
                //.antMatchers(HttpMethod.PUT, "/api/productos/editar/{id}", "/api/items/editar/{id}", "/api/usuarios/usuarios/{id}").hasRole("ADMIN")
                //.antMatchers(HttpMethod.DELETE, "/api/productos/eliminar/{id}", "/api/items/eliminar/{id}", "/api/usuarios/eliminar/{id}").hasRole("ADMIN")
                // configuracón para indicar que cualquier otra ruta requiera autenticación.
                .anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(this.CorsConfigurationSource())); // Configuración del CORS, personalizándolo en el argumento.        	
	}
	
	// Configuración personalizada del CORS
	@Bean
	CorsConfigurationSource CorsConfigurationSource() {
		// Instancia para el CORS
		CorsConfiguration corsConfig = new CorsConfiguration();
		// Configuraciones qué rutas, métodos y headers permitiremos de nuestros orígenes(aplicaciones clientes)
		// 1° Opción
		//corsConfig.addAllowedOrigin("*");
		// 2° Opción
		corsConfig.setAllowedOrigins(Arrays.asList("*"));
		corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		corsConfig.setAllowCredentials(true); // permite ingreso de credenciales
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-type"));
		
		// Pasamos esta configuración del  corsConfig a todas nuestras rutas(endpoints)
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig); // con "/**" estamos enviando esta configuración hacia todas nuestras rutas
		
		return source;
	}
	 
	// Método que configura un filtro de CORS pero de forma global para todo el sistema de microservicios
	@Bean
	FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(this.CorsConfigurationSource()));
		// Le damos una prioridad alta
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;		
	}
	

	// Método que crea un nuevo token de estándar JWT, utilizando lo almacenado en el "JwtAccessTokenConverter"
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(this.jwtAccessTokenConverter());
	}

	// Método que devuelve un token del estándar JWT personalizando algunos datos, en este caso el "secret" de su firma
	@Bean
	JwtAccessTokenConverter jwtAccessTokenConverter() {
		
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
	
		// Asignamos el "codigo secreto" para la firma del JWT y lo codificamos a "Base64".
		accessTokenConverter.setSigningKey(Base64.getEncoder().encodeToString(this.jwtKey.getBytes()));
		return accessTokenConverter;
		
	}

}
