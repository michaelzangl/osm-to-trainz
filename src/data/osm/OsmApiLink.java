package data.osm;

import gui.LatLonRectangle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * This is a link to the osm api
 * 
 * @author michael
 */
public class OsmApiLink {
	private static final int MAX_RECTS_PER_REQUEST = 50;

	private URL address = null;

	ConcurrentLinkedQueue<LatLonRectangle> unprogressedTiles =
	        new ConcurrentLinkedQueue<LatLonRectangle>();

	HashSet<LatLonRectangle> requested = new HashSet<LatLonRectangle>();

	HashSet<LatLonRectangle> received = new HashSet<LatLonRectangle>();

	private OsmDatapack dataPack = OsmDatapack.createEmptyPack();;

	private boolean waitWithUnprogressed = false;

	public OsmApiLink() {
		new Thread(new Loader()).start();
		new Thread(new Loader()).start();
	}

	public synchronized URL getAddress() {
		return address;
	}

	public synchronized void setAddress(URL address)
	        throws MalformedURLException {
		if (!address.getProtocol().equals("http")) {
			throw new MalformedURLException(
			        "Currently only HTTP connections are supported.");
		}
		if (!address.getFile().endsWith("/")) {
			throw new MalformedURLException("URL should end in an /");
		}

		this.address = address;
		this.notifyAll();
	}

	private synchronized URL getAddressBlocking() {
		URL address;
		while ((address = getAddress()) == null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
		return address;
	}

	/**
	 * Sends a request to load the given area, but does not wait until it is
	 * there.
	 * 
	 * @param rectangle
	 *            The rectanlge that specifies the area.
	 */
	public synchronized void preloadArea(LatLonRectangle rectangle) {
		preloadAreaNoNotify(rectangle);
		this.notifyAll();
	}

	private void preloadAreaNoNotify(LatLonRectangle rectangle) {
		if (!requested.contains(rectangle)) {
			requested.add(rectangle);
			unprogressedTiles.offer(rectangle);
		}
	}

	public synchronized OsmDatapack loadArea(LatLonRectangle rectangle) {
		if (!requested.contains(rectangle)) {
			preloadArea(rectangle);
		}
		synchronized (received) {
			while (!received.contains(rectangle)) {
				try {
					received.wait();
				} catch (InterruptedException e) {
				}
			}
			return dataPack;
		}

	}

	public OsmDatapack loadAreas(List<LatLonRectangle> rects) {
		System.out.println("Loading " + rects.size() + " areas");
		synchronized (this) {
			waitWithUnprogressed = true;
			for (LatLonRectangle rect : rects) {
				preloadAreaNoNotify(rect);
			}
			waitWithUnprogressed = false;
			this.notifyAll();
		}
		System.out.println("Waiting for areas to come back");
		for (LatLonRectangle rect : rects) {
			loadArea(rect);
		}
		return dataPack;
	}

	private class Loader implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					System.out.println("Waiting for rectangles");
					List<LatLonRectangle> rectangles = getRectanglesToRequest();

					System.out.println("Found recangles");
					try {
						tryLoadTiles(rectangles);
						System.out.println("Loaded rectangles");
						synchronized (received) {
							for (LatLonRectangle rectangle : rectangles) {
								received.add(rectangle);
								received.notifyAll();
							}
						}
						System.out.println("Stored rectangles");
					} catch (Exception e) {
						e.printStackTrace();
						reoffer(rectangles);
						Thread.sleep(2000);
					}
				} catch (InterruptedException e) {
				}
			}
		}

		private void tryLoadTiles(List<LatLonRectangle> rectangles)
		        throws IOException {
			System.out.println("Requesting " + rectangles.size() + " areas");
			URL mapRequestUrl = getAddressBlocking();
			String script = "<osm-script timeout=\"1000\">\n";
			// do a coord query to get all sorounding rects.
			// <coord-query lat="51.25" lon="7.15" into="areas"/>
			script += "    <union into=\"basenodes\">\n";
			for (LatLonRectangle rectangle : rectangles) {
				script += "        <bbox-query" //
				        + " w=\"" + rectangle.getMinlon() + "\"" //
				        + " s=\"" + rectangle.getMinlat() + "\"" //
				        + " e=\"" + rectangle.getMaxlon() + "\"" //
				        + " n=\"" + rectangle.getMaxlat() + "\"/>\n";
			}
			script +=
			        "    </union>\n" //
			                + "    <union>\n" //
			                + "        <item />\n" //
			                + "        <recurse type=\"node-way\" from=\"basenodes\" into=\"ways\"/>\n" //
			                //+ "        <union into=\"relations\">\n" //
			                //+ "        <recurse type=\"way-relation\" from=\"ways\" into=\"all\"/>\n" //
			                //+ "        <recurse type=\"node-relation\" from=\"basenodes\"/>\n" //
			                //+ "        </union>\n" //
			                //+ "        <recurse type=\"relation-way\" from=\"relations\"/>\n" //
			                + "    </union>\n" //
			                + "    <union>\n" //
			                + "        <item/>\n" //
			                + "        <recurse type=\"way-node\"/>\n" //
			                + "    </union>\n" //
			                + "    <print order=\"quadtile\"/>\n" //
			                + "</osm-script>";

			HttpURLConnection request =
			        (HttpURLConnection) mapRequestUrl.openConnection();
			request.setRequestMethod("POST");
			request.setAllowUserInteraction(false);
			request.setDoOutput(true);
			request.setRequestProperty("Content-type", "text/xml");
			request.setRequestProperty("Content-length", script.length() + "");
			request.setInstanceFollowRedirects(true);
			request.connect();

			OutputStream out = request.getOutputStream();
			PrintWriter writer = new PrintWriter(out);
			writer.print(script);
			writer.flush();
			writer.close();

			System.out.println("sending request: " + script);
			if (request.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("Response indicates error: "
				        + request.getResponseCode() + " ("
				        + request.getResponseMessage() + ")");
			}

			InputStream in = request.getInputStream();
			
			// Scanner s = new Scanner(in);
			// String read = s.useDelimiter("\\A").next();
			// System.out.println(read);
			// in = new ByteArrayInputStream(read.getBytes());

			OsmDatapack.addFromXMLStream(in, dataPack);
			System.out.println("Received " + rectangles.size() + " areas");
		}
	}

	private synchronized void reoffer(List<LatLonRectangle> rectangles) {
		for (LatLonRectangle rectangle : rectangles) {
			unprogressedTiles.offer(rectangle);
		}
		this.notifyAll();
	}

	private List<LatLonRectangle> getRectanglesToRequest()
	        throws InterruptedException {
		List<LatLonRectangle> rectangles = new LinkedList<LatLonRectangle>();
		synchronized (this) {
			while (waitWithUnprogressed || unprogressedTiles.isEmpty()) {
				this.wait();
			}
			while (rectangles.size() < MAX_RECTS_PER_REQUEST) {
				LatLonRectangle rectangle = unprogressedTiles.poll();
				if (rectangle == null) {
					break;
				}
				rectangles.add(rectangle);
			}
		}
		return rectangles;
	}

}
