package com.formacionbdi.springboot.app.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/*
 * Registrmos como un bean en el contenedo de spring
 * Heredamos de la clase abstracta "ZuulFilter", para ser registrado como un "filtro de zuul"
 * Al heredar de esta clase, estamos obligados a implementar sus métodos.
 */
@Component
public class PostTiempoTranscurridoFilter extends ZuulFilter {
	
	private static Logger log = LoggerFactory.getLogger(PostTiempoTranscurridoFilter.class);

	/*
	 * Método que permite hacer la validación. Y decidir si el filtro se ejecuta o no.
	 * Por ejemplo, validar la existencia de un parámetro en la ruta, o una autenticación de usuario.
	 * Ya que solo si el método retorna "true" se ejecutará el filtro.
	 * Lo dejaremos en "true" para que siempre se ejecute.
	 */
	@Override
	public boolean shouldFilter() {
		return true;
	}

	// En este método se resuleve lo lógica de nuestro filtro
	// Debemos el objeto "HttpRequest", que representa un request, para pasarle datos.
	@Override
	public Object run() throws ZuulException {
		
		// Obtnemos el contexto del request
		RequestContext context = RequestContext.getCurrentContext();
		// Obtenemos el request por medio del contexto
		HttpServletRequest request = context.getRequest();
		// Impresión en el terminal
		log.info("Entrando a post");
		// Obtenemos el tiempo de incio desde el "request"
		Long tiempoInicio = (Long) request.getAttribute("tiempoInicio");
		// Asignamos un nuevo tiempo, que representará el tiempo final
		Long tiempoFinal = System.currentTimeMillis();
		// Calculamos la diferencia de ambos tiempos
		Long tiempoTranscurrido = tiempoFinal - tiempoInicio;
		// Imprimimos el tiempo transcurrido transformado en segundos
		log.info(String.format("--- POST --- Tiempo transcurrido en segundos: %s", tiempoTranscurrido.doubleValue() /1000.00));
		// Imprimimos el tiempo transcurrido transformado en milisegundos
		log.info(String.format("--- POST --- Tiempo transcurrido en milisegundos: %s", tiempoTranscurrido));		
		return null;
	}

	// Método para elegir cuál de los tres tipos de filtro se usará(pre, post, route)
	@Override
	public String filterType() {
		return "post";
	}

	// Asignamos el orden como "1"
	@Override
	public int filterOrder() {
		return 1;
	}

}
