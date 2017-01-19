package web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

@Path("geospatialanalytics")
public class GeospatialConfigurator {


	@GET
	@Produces("text/html")
	public Response getInfos() {
		return Response.ok("all right").build();
	}


	@Path("/StartAnalytics")
	@PUT
	public Response startService () throws URISyntaxException {

		MqttCommunicatorToGeospatialAnalytics communicator = new MqttCommunicatorToGeospatialAnalytics();

		try {
			communicator.startBluemixGeoSpatialService();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(200).entity("Service started").build();

	}


	@Path("/addregion")
	@Consumes("application/rdf+xml")
	@PUT
	public Response addRegion (String input) throws URISyntaxException {

		MqttCommunicatorToGeospatialAnalytics communicator = new MqttCommunicatorToGeospatialAnalytics();

		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("", "http://aifb.kit.edu/step");
		model.read(new ByteArrayInputStream(input.getBytes()), null, "RDF/XML");

		StmtIterator iter = model.listStatements(null, model.createProperty("http://gm/#lat"), (Resource) null);
		String gps_lat = iter.next().getLiteral().getString();
		
		iter = model.listStatements(null, model.createProperty("http://gm/#lng"), (Resource) null);
		String gps_lng = iter.next().getLiteral().getString();
		
		iter = model.listStatements(null, model.createProperty("http://example.org/hasCity"), (Resource) null);
		String name = iter.next().getLiteral().getString();

		try {
			communicator.addRegionToBluemixGeoSpatialService(name, gps_lat, gps_lng);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(200).build();

	}
}
