package src.main.java;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.gson.Gson;

@Path("/cities")
public class WebComunicator {
	
	//Doesn't working
	@GET
	@Produces("application/json")
	public String getAll() {
		Gson g = new Gson();
		return g.toJson("Jean");
	}
}