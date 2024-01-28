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
public class PreTiempoTranscurridoFilter extends ZuulFilter {
	
	private static Logger log = LoggerFactory.getLogger(PreTiempoTranscurridoFilter.class);

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
		log.info(String.format("--- PRE --- %s Request enrutado a %s", request.getMethod(), request.getRequestURI().toString()));
		// Generamos un tiempo inicial en milisegundos y se lo agregamos al "request"
		Long tiempoInicio = System.currentTimeMillis();
		// Le damos datos (el tiempo obtenido) al request 
		request.setAttribute("tiempoInicio", tiempoInicio);
		return null;
	}

	// Método para elegir cuál de los tres tipos de filtro se usará(pre, post, route)
	@Override
	public String filterType() {
		return "pre";
	}

	// Asignamos el orden como "1"
	@Override
	public int filterOrder() {
		return 1;
	}

}
