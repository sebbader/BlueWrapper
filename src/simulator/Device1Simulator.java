package simulator;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Device1Simulator {

	List<Double[]> tour = new ArrayList<Double[]>();

	public Device1Simulator() {
		// TODO Auto-generated constructor stub
		
		
		//Stuttgart-Feuerbach
		
		/*tour.add(new Double[] { 48.819228, 9.175351 });
		tour.add(new Double[] { 48.817709, 9.176209 });
		tour.add(new Double[] { 48.816685, 9.176810 });
		tour.add(new Double[] { 48.815710, 9.177432 });
		tour.add(new Double[] { 48.816070, 9.175919 });
		tour.add(new Double[] { 48.817031, 9.173773 });
		tour.add(new Double[] { 48.817956, 9.174588 });
		tour.add(new Double[] { 48.818620, 9.173569 });
		tour.add(new Double[] { 48.819704, 9.174685 });
		//tour.add(new Double[] { 40.819704, 9.174685 });
		
		//Stuttgart48.742211,9.206802
		tour.add(new Double[] { 48.742211, 9.206802 });
		tour.add(new Double[] { 48.742211, 9.206802 });
		tour.add(new Double[] { 48.742211, 9.206802 });
		tour.add(new Double[] { 48.742211, 9.206802 });
		tour.add(new Double[] { 48.742211, 9.206802 });
		tour.add(new Double[] { 48.742211, 9.206802 });
		tour.add(new Double[] { 48.742211, 9.206802 });
		tour.add(new Double[] { 48.742211, 9.206802 });
		tour.add(new Double[] { 48.742211, 9.206802 });
		tour.add(new Double[] { 48.742211, 9.206802 });
		*/
		
		// in Stuttgart, außerhalb des Ziels
		tour.add(new Double[] { 49.0, 9.0 });
		tour.add(new Double[] { 50.0, 9.0 });
		tour.add(new Double[] { 50.0, 10.0 });
		tour.add(new Double[] { 51.0, 10.0 });
		tour.add(new Double[] { 51.0, 11.0 });
		tour.add(new Double[] { 50.0, 11.0 });
		tour.add(new Double[] { 50.0, 10.0 });
		
		// in Stuttgart, im Zielbereich
		tour.add(new Double[] { 48.78275, 9.16805 });
		tour.add(new Double[] { 48.78295, 9.16729 });
		tour.add(new Double[] { 48.78306, 9.16685 });
		tour.add(new Double[] { 48.78298, 9.16656 });
		tour.add(new Double[] { 48.78286, 9.16606 });
		tour.add(new Double[] { 48.78284, 9.16599 });
		
		// Zieladresse
		tour.add(new Double[] { 48.7827, 9.16552 });
		tour.add(new Double[] { 48.7827, 9.16552 });
		tour.add(new Double[] { 48.7827, 9.16552 });
		
		// in Stuttgart, im noch Zielbereich
		tour.add(new Double[] { 48.78284, 9.16599 });
		tour.add(new Double[] { 48.78286, 9.16606 });
		
		tour.add(new Double[] { 48.78308, 9.16628 });
		tour.add(new Double[] { 48.78327, 9.16585 });
		tour.add(new Double[] { 48.78352, 9.16485 });
		tour.add(new Double[] { 48.78375, 9.16404 });
		tour.add(new Double[] { 48.78402, 9.16286 });
		tour.add(new Double[] { 48.78426, 9.16192 });
	}

	public static void main(String[] args) {

		Device1Simulator simulator = new Device1Simulator();
		/*
		 * int i = 1;
		 * 
		 * String coordinates = " {\"lat\":\" " + i + " \",\"lng\":\" " + i +
		 * " \", \"ID\":\"Device1\"}"; JSONObject obj = new
		 * JSONObject(coordinates); System.out.println(obj.toString());
		 * System.out.println(obj.getString("lat"));
		 */
		simulator.startTour();

	}

	private void startTour() {
		// TODO Send HTTP POSTS every 5 sec with a GPS dummy value from a
		// formerly specidied list.


		Timer timer = new Timer();

		TimerTask timerTask = new TimerTask() {
			int counter = 0;

			@Override
			public void run() {
				// Pop next GPS
				try {
					String coordinates = " {" + "\"lat\":\"" + tour.get(counter)[0] + "\",\"lng\":\""
							+ tour.get(counter)[1] + "\"" + ",\"ID\":\"Device1\"}";

					System.out.println("Technichian at: \"lat\":\"" + tour.get(counter)[0] + "\",\"lng\":\""
							+ tour.get(counter)[1] + "\"" );
					System.out.println("Send coordinates via POST");
					
					
					HttpURLConnection con = (HttpURLConnection) (new URL("http://localhost:9080/BluemixGeospatialWrapper/Device1").openConnection());

					// add request header
					con.setRequestMethod("POST");
					con.setRequestProperty("Content-Type", "application/json");
					//con.setRequestProperty("Accept", "application/json");
					
					
					// Send put request
					con.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					String body = coordinates;
					

					wr.writeBytes(body);
					wr.flush();
					wr.close();

					con.connect();
					

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
					
					/*
					 * System.out.println("Output from Server .... \n"); String
					 * output = response.getEntity(String.class);
					 * System.out.println(output);
					 */
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (counter < tour.size() - 1) {
					counter++;
				} else {
					counter = 0;
				}
			}
		};
		timer.schedule(timerTask, 3000, 3000);
	}
}