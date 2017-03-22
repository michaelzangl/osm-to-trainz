package data.osm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException;

import data.osm.OsmRelation.OsmRoledNode;
import data.osm.OsmRelation.OsmRoledWay;
import data.position.local.LatLon;
import data.position.local.LatLonObject;

/**
 * This is a osm data pack that holds node and way data.
 * 
 * @author michael
 */
public class OsmDatapack {
	private Hashtable<Integer, OsmNode> nodes =
	        new Hashtable<Integer, OsmNode>();
	private Hashtable<Integer, OsmWay> ways = new Hashtable<Integer, OsmWay>();
	private Hashtable<Integer, OsmRelation> relations =
	        new Hashtable<Integer, OsmRelation>();

	int currentId = -1;

	private LatLon center = null;

	private OsmDatapack() {
	}

	private synchronized OsmNode addNode(Integer id, OsmNode node) {
		if (getNode(id) != null) {
			return getNode(id);
		} else {
			nodes.put(id, node);
			return node;
		}
	}

	public synchronized void addNode(OsmNode node) {
		nodes.put(currentId, node);
		currentId--;
	}

	private OsmNode getNode(Integer id) {
		return nodes.get(id);
	}

	private synchronized void addWay(Integer id, OsmWay way) {
		ways.put(id, way);
		for (OsmNode node : way) {
			node.addWay(way);
		}
	}

	public OsmWay getWay(Integer id) {
		return ways.get(id);
	}

	public void addRelation(Integer id, OsmRelation osmRelation) {
		relations.put(id, osmRelation);
		for (OsmRoledNode node : osmRelation.getNodes()) {
			node.getNode().addRelation(osmRelation);
		}
		for (OsmRoledWay way : osmRelation.getWays()) {
			way.getWay().addRelation(osmRelation);
		}
	}

	public Iterable<OsmWay> getWays() {
		return ways.values();
	}

	public Iterable<OsmNode> getNodes() {
		return nodes.values();
	}

	public Iterable<OsmRelation> getRelations() {
		return relations.values();
	}

	public static OsmDatapack readFromXMLFile(File file)
	        throws FileNotFoundException, IOException {
		return readFromXMLStream(new FileInputStream(file));
	}

	public static OsmDatapack readFromXMLStream(InputStream in)
	        throws IOException {
		OsmDatapack pack = createEmptyPack();
		addFromXMLStream(in, pack);
		return pack;
	}

	public static void addFromXMLStream(InputStream in, OsmDatapack pack)
	        throws IOException {
		XMLReader xr;
		try {
			xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(new OsmSaxHandler(pack));
			xr.parse(new InputSource(in));
		} catch (SAXException e) {
			throw new IOException("Sax error occurred", e);
		} catch (NumberFormatException e) {
			throw new IOException("A number was malformed", e);
		} catch (MalformedByteSequenceException e) {
			throw new IOException("The charset in the input stream is wrong.",
			        e);
		}
	}

	private static class OsmSaxHandler extends DefaultHandler {
		private final OsmDatapack pack;

		// reused
		private final LinkedList<OsmNode> currentWayNodes =
		        new LinkedList<OsmNode>();
		// reused
		private Properties tags = new Properties();
		// reused
		private final LinkedList<OsmRoledWay> currentRelationWays =
		        new LinkedList<OsmRoledWay>();
		// reused
		private final LinkedList<OsmRoledNode> currentRelationNodes =
		        new LinkedList<OsmRoledNode>();

		private Integer currentWayOrRelationId;

		private String currentNodeLat;

		private String currentNodeLon;

		private String currentNodeId;

		OsmSaxHandler(OsmDatapack pack) {
			this.pack = pack;
		}

		@Override
		public void endElement(String ui, String localName, String qName)
		        throws SAXException {
			String name = qName;

			if (name.equals("way")) {
				wayEnded();
			} else if (name.equals("node")) {
				nodeEnded();
			} else if (name.equals("relation")) {
				relationEnded();
			}
		}

		@Override
		public void startElement(String uri, String localName, String qName,
		        Attributes atts) throws SAXException {
			String name = qName;

			if (name.equals("way")) {
				wayStarted(atts);
			} else if (name.equals("node")) {
				nodeStarted(atts);
			} else if (name.equals("relation")) {
				relationStarted(atts);
			} else if (name.equals("tag")) {
				tagRead(atts);
			} else if (name.equals("member")) {
				memberRead(atts);
			} else if (name.equals("nd")) {
				nodeReferenceRead(atts);
			} else if (name.equals("bounds")) {
				boundsRead(atts);
			}
		}

		private void boundsRead(Attributes atts) {
			String latval1 = atts.getValue("minlat");
			String lonval1 = atts.getValue("minlon");
			String latval2 = atts.getValue("maxlat");
			String lonval2 = atts.getValue("maxlon");
			if (latval1 != null && lonval1 != null && latval2 != null
			        && lonval2 != null) {
				double lat =
				        (Double.parseDouble(latval1) + Double
				                .parseDouble(latval2)) / 2;
				double lon =
				        (Double.parseDouble(lonval1) + Double
				                .parseDouble(lonval2)) / 2;
				pack.center = new LatLon((float) lat, (float) lon);
			}
		}

		private void wayStarted(Attributes attributes) {
			this.currentWayNodes.clear();
			tags.clear();
			loadIdByAttributes(attributes);
		}

		private void loadIdByAttributes(Attributes attributes) {
			String wayVal = attributes.getValue("id");
			if (currentWayOrRelationId != null) {
				currentWayOrRelationId = Integer.parseInt(wayVal);
			} else {
				currentWayOrRelationId = 0;
			}
		}

		private void wayEnded() {
			if (currentWayNodes.size() >= 2) {
				OsmWay way = new OsmWay(currentWayNodes, tags);
				pack.addWay(currentWayOrRelationId, way);
			}
		}

		private void relationStarted(Attributes attributes) {
			this.currentRelationNodes.clear();
			this.currentRelationWays.clear();
			tags.clear();
			loadIdByAttributes(attributes);
		}

		private void relationEnded() {
			pack.addRelation(currentWayOrRelationId, new OsmRelation(
			        currentRelationNodes, currentRelationWays, tags));

		}

		private void tagRead(Attributes attributes) {
			String key = attributes.getValue("k");
			String value = attributes.getValue("v");
			tags.put(key, value);
		}

		private void nodeReferenceRead(Attributes attributes) {
			String nodeid = attributes.getValue("ref");
			OsmNode node =
			        pack.getNode(Integer.valueOf(Integer.parseInt(nodeid)));
			if (node != null) {
				currentWayNodes.add(node);
			}
		}

		private void memberRead(Attributes attributes) {
			int id = Integer.parseInt(attributes.getValue("ref"));
			String role = attributes.getValue("role");
			if (role == null) {
				role = "";
			}
			if ("node".equals(attributes.getValue("type"))) {
				OsmNode node = pack.getNode(Integer.valueOf(id));
				currentRelationNodes.add(new OsmRoledNode(role, node));
			} else if ("way".equals(attributes.getValue("type"))) {
				OsmWay way = pack.getWay(Integer.valueOf(id));
				currentRelationWays.add(new OsmRoledWay(role, way));
			}
		}

		private void nodeStarted(Attributes attributes) {
			this.currentNodeLat = attributes.getValue("lat");
			this.currentNodeLon = attributes.getValue("lon");
			this.currentNodeId = attributes.getValue("id");
			tags.clear();
		}

		private void nodeEnded() {
			if (currentNodeLat != null && currentNodeLon != null) {
				double lat = Double.parseDouble(currentNodeLat);
				double lon = Double.parseDouble(currentNodeLon);
				int id = Integer.parseInt(currentNodeId);
				pack.addNode(id, new OsmNode(lat, lon, this.tags));
			} else {
				System.out.println("Node end for unstarted node.");
			}
		}

	}

	public LatLon getCenter() {
		if (center == null) {
			center = computeCenter();
		}
		return center;
	}

	private LatLon computeCenter() {
		double latsum = 0;
		double lonsum = 0;
		int count = 0;
		for (LatLonObject p : getNodes()) {
			latsum += p.getLat();
			lonsum += p.getLon();
			count++;
		}
		return new LatLon(latsum / count, lonsum / count);
	}

	public static OsmDatapack createEmptyPack() {
		return new OsmDatapack();
	}

}
