package ar.edu.dds.libros;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class AppLibros {

	public static EntityManagerFactory entityManagerFactory;

	public static void main(String[] args) throws Exception {
		
		Map<String, String> env = System.getenv();
		for (String string : env.keySet()) {
			System.out.println(string + ": " + env.get(string));
		}
		
		entityManagerFactory =  createEntityManagerFactory();
		
		Integer port = Integer.parseInt(System.getProperty("PORT", "8080"));

		Javalin app = Javalin.create().start(port);
		
		LibrosController controller = new LibrosController(entityManagerFactory); 
		
		app.get("/libros", controller::listLibros);
		app.post("/libros", controller::addLibro);
		
	}
	
	
	
	public static EntityManagerFactory createEntityManagerFactory() {
		// https://stackoverflow.com/questions/8836834/read-environment-variables-in-persistence-xml-file
		Map<String, String> env = System.getenv();
		Map<String, Object> configOverrides = new HashMap<String, Object>();

		String[] keys = new String[] { "javax.persistence.jdbc.url", "javax.persistence.jdbc.user",
				"javax.persistence.jdbc.password", "javax.persistence.jdbc.driver", "hibernate.hbm2ddl.auto",
				"hibernate.connection.pool_size", "hibernate.show_sql" };

		for (String key : keys) {
			if (env.containsKey(key)) {

				String value = env.get(key);
				configOverrides.put(key, value);

			}
		}

		return Persistence.createEntityManagerFactory("db", configOverrides);

	}




}
