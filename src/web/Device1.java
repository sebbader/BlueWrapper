package web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.*;

import web.MqttCommunicatorToGeospatialAnalytics;

@Path("/Device1")
public class Device1 {

	static MqttClient subscribedClient;

	static String gps_lat = "0.000";
	static String gps_lng = "1.111";
	static boolean isInRegion = false;
	static String last_message = "{\"regionId\":\"unknown\", \"eventType\":\"unknown\"}";

	public Device1() {
		// TODO Subscribe
		// See MqttCommutator
		if (subscribedClient == null) startSubscribtionLCient();
	}

	public void startSubscribtionLCient() {
		//String service_name = "Geospatial Analytics-4y";
		String topic = "demo/events";
		//int qos = 0; // qos = quality of service: {0, 1, 2}
		String broker = "tcp://host:1883";
		// String broker = "tcp://localhost:1883";
		String clientId = "Device1";

		try {
			subscribedClient = new MqttClient(broker, clientId);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);

			System.out.println("Connecting to broker: " + broker);
			subscribedClient.connect(connOpts);
			System.out.println("Connected");

			subscribedClient.subscribe(topic);
			subscribedClient.setCallback(new Callback());

		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}

	}

	private class Callback implements MqttCallback {

		@Override
		public void connectionLost(Throwable cause) {
			System.out.println("Connection lost: " + cause.getMessage());

		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			// override published web content with new message
			if (topic.equals("boschdemo/events")) {
				System.out.println(message + "\n");
				last_message = message.toString();
				
				if (last_message.contains("Entry")) { 
					isInRegion = true; 
				} else if (last_message.contains("Exit")) {
					isInRegion = false; 
				}
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			System.out.println("deliveryComplete");

		}

	}

	@GET
	@Produces("text/html")
	public String describeDevice1() {
		// TODO What is the last known GPS Position?

		String device1_region = "";

		JSONObject obj = new JSONObject(last_message);
		device1_region = obj.getString("regionId");
		if (obj.getString("eventType") == "Exit") {
			isInRegion = false;
		} else if (obj.getString("eventType") == "Entry") {
			isInRegion = true;
		}

		// TODO What was the last message from Bluemix Geospatial?

		return "<!DOCTYPE html>" + "<html>" + "<body>" + "<p id=\"bereich1\">erster Absatz </p>I am groot:"
		+ " I am inside: " + device1_region + " I am at: " + gps_lat + ", " + gps_lng + "<script>"
		+ "var inhalt = document.getElementById('bereich1');" + "function changecolor () {"
		// TODO make a get to web resource --> GET
		// window.getHost/Device1/color --> var status geschrieben
		+ "inhalt.style.color = status ;" + "}" + "changecolor()" + "</script>" + "</body>" + "</html>";
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getGPSfromDevice(String coordinates) {

		JSONObject obj = new JSONObject(coordinates);

		gps_lat = obj.getString("lat");
		gps_lng = obj.getString("lng");

		String result = "Latitude: " + gps_lat + " Longitude: " + gps_lng;
		try {
			return understandGPS(coordinates);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError().build();
		}
		// System.out.println(result);

	}


	@Path("/currentgps")
	@GET
	@Produces({"text/html", "text/turtle"})
	public String returnCurrentGPS() {
		return "<> a <http://example.org/Device> ; "
				+ "<http://gm/#lng> \"" + gps_lng + "\" ; "
				+ " <http://gm/#lat> \"" + gps_lat + "\" ; "
				+ " <http://example.org/inRegion> \"" + isInRegion + "\" .";
	}


	@Path("/status")
	@GET
	@Produces("text/html")
	public String getStatus() {
		if (isInRegion) {
			return "green";
		} else {
			return "red";
		}
	}




	@Path("/ToBrocker")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response understandGPS(String body) throws URISyntaxException { // ,
		// @Context
		// HttpServletRequest
		// request)
		// throws
		// URISyntaxException
		// {
		/*
		 * JSONObject json = new JSONObject(); json.put("ID", "Device1");
		 * json.put("lat", gps_lat); json.put("lng", gps_lng);
		 * 
		 * String message = (json.toString());
		 */
		//System.out.println(body);
		try {
			MqttMessage mqttm = new MqttMessage(body.getBytes());
			subscribedClient.publish("demo/device", mqttm);
			System.out.println("Message published");
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
		// TODO send body gps to Bluemix Service (MQTT Broker)
		return Response.created(new URI("/currentgps")).build();
	}



}
