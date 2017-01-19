package web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * sample demo to show how to publish a HelloWorld message to the eclipse MQTT
 * sandbox server
 * 
 * @author sba
 * @source https://www.eclipse.org/paho/clients/java/
 *
 */
public class MqttCommunicatorToGeospatialAnalytics {

	private static final String host = "streams-broker.eu-gb.bluemix.net";
	private static final int port = 443;
	private static final String service_instance = "a9263b31-a719-43e3-9db7-3733e2efac6a";
	private static final String service_binding = "21c7d637-8807-447a-aee3-5c37fbd13046";
	private static final String user = "57709025-4ca7-4ce9-8336-49ac4bf332bb";
	private static final String password = "42edd1f3-05ce-4607-a06c-10dcf9c12fee";

	public static void main(String[] args) {

		MqttCommunicatorToGeospatialAnalytics app = new MqttCommunicatorToGeospatialAnalytics();

		// app.stopBluemixGeoSpatialService();
		// app.startBluemixGeoSpatialService();
		// app.restartBluemixGeoSpatialService();
		// app.stopBluemixGeoSpatialService();

		// app.startPublisherMqttClient();
		app.startSubscribtionLCient();

	}

	private static void subscribeToBluemixGeospatialAnalytics() {
		// TODO Auto-generated method stub

	}

	public void startBluemixGeoSpatialService() throws IOException {
		callBluemixGeoSpatialService("start", null);
	}

	public void stopBluemixGeoSpatialService() throws IOException {
		callBluemixGeoSpatialService("stop", null);
	}

	public void restartBluemixGeoSpatialService() throws IOException {
		callBluemixGeoSpatialService("restart", null);
	}


	public void addRegionToBluemixGeoSpatialService(String name, String gps_lat, String gps_lng) throws IOException {
		JSONArray regions = new JSONArray();

		JSONObject new_region = new JSONObject();
		new_region.put("region_type", "regular");
		new_region.put("name", name);
		new_region.put("notifyOnEntry", "true");
		new_region.put("notifyOnExit", "true");
		new_region.put("center_latitude", gps_lat);
		new_region.put("center_longitude", gps_lng);
		new_region.put("number_of_sides", "10");
		new_region.put("distance_to_vertices", "500");

		regions.put(new_region);

		callBluemixGeoSpatialService("addRegion", regions);
	}


	private void callBluemixGeoSpatialService(String method, JSONArray regions) throws IOException {
		String bluemix_service_url = "https://" + host + ":" + port + "/jax-rs/geo/" + method + "/"
				+ "service_instances/" + service_instance + "/service_bindings/" + service_binding;
		// https://streams-broker.eu-gb.bluemix.net:443/jax-rs/geo/start/service_instances/a9263b31-a719-43e3-9db7-3733e2efac6a/service_bindings/21c7d637-8807-447a-aee3-5c37fbd13046
		// https://streams-broker.eu-gb.bluemix.net:443/jax-rs/geo/start/service_instances/a9263b31-a719-43e3-9db7-3733e2efac6a/service_bindings/21c7d637-8807-447a-aee3-5c37fbd13046
		HttpURLConnection con = (HttpURLConnection) (new URL(bluemix_service_url).openConnection());

		// add reuqest header
		con.setRequestMethod("PUT");
		// con.setRequestProperty("Link", "<http://www.w3.org/ns/ldp#Resource>;
		// rel=\"type\"");
		// con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		// con.setRequestProperty("If-Match", ldp_response.geteTag());
		String userpass = user + ":" + password;
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
		con.setRequestProperty("Authorization", basicAuth);

		// Send put request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		String body = "";

		if (regions == null) {
			// do a start/stop/restart

			JSONObject json = new JSONObject();
			//json.put("mqtt_uid", "iamuser");
			//json.put("mqtt_pw", "thepass");
			json.put("mqtt_uri", "aifbkos.aifb.uni-karlsruhe.de:1883");
			json.put("mqtt_input_topics", "boschdemo/device");
			json.put("mqtt_notify_topic", "boschdemo/events");
			json.put("device_id_attr_name", "ID");
			json.put("latitude_attr_name", "lat");
			json.put("longitude_attr_name", "lng");
			body = (json.toString());
		} else {
			// add a region
			body = ("{\"regions\":" + regions.toString() + "}");
		}

		wr.writeBytes(body);
		wr.flush();
		wr.close();

		con.connect();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'START' request to URL : " + bluemix_service_url);
		// System.out.println("Header : " + con.getRequestProperties());
		System.out.println("Body : " + body);
		System.out.println("Response Code : " + responseCode);

		Map<String, List<String>> headerFields = con.getHeaderFields();
		if (!headerFields.isEmpty()) {
			headerFields.forEach((header, value) -> System.out.println(header + ": " + value));
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		}

	}

	public void startPublisherMqttClient() {
		String service_name = "Geospatial Analytics-4y";
		String topic = "boschdemo/test";
		String content = "Message from MqttPublishSample";
		int qos = 2; // qos = quality of service: {0, 1, 2}
		String broker = "tcp://aifbkos.aifb.uni-karlsruhe.de:1883";
		String clientId = "JavaSampl12345";

		try {
			MqttClient sampleClient = new MqttClient(broker, clientId);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);

			System.out.println("Connecting to broker: " + broker);
			sampleClient.connect(connOpts);
			System.out.println("Connected");

			System.out.println("Publishing message: " + content);
			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(qos);
			sampleClient.publish(topic, message);
			System.out.println("Message published");

			sampleClient.disconnect();
			System.out.println("Disconnected");
			System.exit(0);
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}

	}

	public void startSubscribtionLCient() {
		String service_name = "Geospatial Analytics-4y";
		String topic = "boschdemo";
		int qos = 0; // qos = quality of service: {0, 1, 2}
		// String broker = "tcp://aifbkos.aifb.uni-karlsruhe.de:1883";
		String broker = "tcp://localhost:1883";
		String clientId = "ListenerNo1";

		try {
			MqttClient sampleClient = new MqttClient(broker, clientId);
			//MqttConnectOptions connOpts = new MqttConnectOptions();
			//connOpts.setCleanSession(true); 
			System.out.println("Connecting to broker: "+broker);
			//sampleClient.connect(connOpts);

			sampleClient.connect();
			System.out.println("Connected"); 
			sampleClient.subscribe(topic);
			sampleClient.setCallback(new Callback());

			while (true) {
				continue;
			}

			// sampleClient.disconnect();
			// System.out.println("Disconnected");
			// System.exit(0);
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
			System.out.println("Connection lost");

		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			// override published web content with new message
			System.out.println(message + "\n");

		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			System.out.println("deliveryComplete");

		}

	}
}
