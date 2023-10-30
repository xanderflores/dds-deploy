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

		String[] keys = new String[] { 
					      "hibernate__hbm2ddl__auto",
				"hibernate__connection__pool_size", 
					      "hibernate__show_sql" };

		for (String key : keys) {
               
		
			if (key.equals("DATABASE_URL")) {
                    
				// https://devcenter.heroku.com/articles/connecting-heroku-postgres#connecting-in-java
				String value = env.get(key);
				URI dbUri = new URI(value);
				String username = dbUri.getUserInfo().split(":")[0];
				String password = dbUri.getUserInfo().split(":")[1];
				//javax.persistence.jdbc.url=jdbc:postgresql://localhost/dblibros
				value = "jdbc:mysql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();// + "?sslmode=require";
				configOverrides.put("javax.persistence.jdbc.url", value);
				configOverrides.put("javax.persistence.jdbc.user", username);
				configOverrides.put("javax.persistence.jdbc.password", password);
				//  configOverrides.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
			}
			// no se pueden poner variables de entorno con "." en la key
			String key2 = key.replace("__",".");
			if (env.containsKey(key)) {
				String value = env.get(key);
				configOverrides.put(key, value);
			}
		}
		return Persistence.createEntityManagerFactory("db", configOverrides);
	}
}
