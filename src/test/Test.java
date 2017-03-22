package test;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.xml.sax.SAXException;

import conversion.ConversionData;
import conversion.datachange.LandscapeAdder;
import conversion.datachange.LandscapeDisplacer;
import conversion.datachange.TreeAdder;
import conversion.datachange.geometry.GridPart;
import conversion.datachange.geometry.Polygon;
import conversion.datachange.geometry.SimplePolygon;
import conversion.datachange.geometry.SimpleRectangle;
import conversion.landscape.LandscapePolygon;
import conversion.landscape.LandscapeTextureProvider;
import conversion.objects.OsmObjectConverter;
import conversion.tracks.OsmTrackConverter;
import conversion.tracks.height.rules.DesireBetweenNodes;
import conversion.tracks.height.rules.HeightRule;
import data.height.LocalHeightDataProvider;
import data.height.SRTMImporter;
import data.osm.OsmDatapack;
import data.osm.OsmNode;
import data.osm.OsmWay;
import data.position.local.GlobalToLocalConverter;
import data.position.local.LatLon;
import data.position.local.LocalPoint;
import export.ConfigFile;
import export.ground.TextureLink;
import export.objects.ObjectLink;
import export.tracks.Subnetwork;
import export.tracks.TrackConnection;
import export.tracks.TrackNetwork;
import export.tracks.TrackNode;
import export.trainz.general.Kuid;
import export.trainz.map.GridFile;
import export.trainz.map.MapGrid;
import export.trainz.objects.ObjectFile;
import export.trainz.tracks.TrackFile;

public class Test {
	private static final String OSMFILE = "/home/michael/Downloads/map(2).osm";

	private static final String OUTDIR = "/media/2F35-CAAF/trainz/";

	private JLabel heightIndicator;

	private LocalHeightDataProvider heightProvider;

	private DatapackPanel content;

	private final LandscapeTextureProvider textureProvider;

	private final ConversionData data;

	private final TrackNetwork network;

	public static void main(String[] args) throws FileNotFoundException,
	        IOException, SAXException {
		new Test();
	}

	public Test() throws FileNotFoundException, IOException {
		this(OsmDatapack.readFromXMLFile(new File(OSMFILE)));
	}

	public Test(OsmDatapack osmData) {
		this(loadOsmData(osmData));
	}

	private static ConversionData loadOsmData(OsmDatapack osmData) {
		GlobalToLocalConverter converter =
		        new GlobalToLocalConverter(osmData.getCenter());
		HashSet<GridPart> gridparts = new HashSet<GridPart>();
		for (OsmNode node : osmData.getNodes()) {
			LocalPoint local = converter.toLocal(node);
			gridparts.add(GridPart.getUnder(local));
		}
		ConversionData data =
		        new ConversionData(osmData, converter, new SRTMImporter(),
		                gridparts);
		System.out.println("Adding landscapes");
		new LandscapeAdder(data).addLandscapes();
		new TreeAdder(data).addTrees();
		data.getHeightProvider().addDisplacer(
		        new LandscapeDisplacer(data.getLandscape()));
		return data;
	}

	public Test(ConversionData data) {
		this(data, generateNetwork(data));
	}

	public Test(ConversionData data, TrackNetwork network) {
		this.data = data;
		this.network = network;
		heightProvider = data.getHeightProvider();
		content = new DatapackPanel();

		textureProvider = new LandscapeTextureProvider(data.getLandscape());

		JPanel root = new JPanel(new BorderLayout());
		root.add(content, BorderLayout.CENTER);
		heightIndicator = new JLabel("-");

		root.add(heightIndicator, BorderLayout.SOUTH);
		content.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				LocalPoint local =
				        new LocalPoint(e.getY() - content.dy, e.getX()
				                - content.dx);
				LatLon global = Test.this.data.getConverter().toGlobal(local);
				heightIndicator.setText("lat=" + global.getLat() + ", lon="
				        + global.getLon() + ", height="
				        + heightProvider.getHeight(local));
			}
		});

		JFrame frame = new JFrame("Visual");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(root);
		frame.pack();
		frame.setSize(500, 500);
		frame.setVisible(true);

		frame.setFocusable(true);
		content.setFocusable(true);
		content.requestFocusInWindow();
		content.requestFocus();
		content.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					content.dy += 300;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					content.dy -= 300;
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					content.dx -= 300;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					content.dx += 300;
				} else if (e.getKeyCode() == KeyEvent.VK_W) {
					// writeOutput();
				}
				content.repaint();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		// TODO Auto-generated constructor stub
	}

	private class DatapackPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4159210648265848248L;
		
		int dx = 100;
		int dy = 100;

		private DatapackPanel() {
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(new Color(0x15b01a));
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.translate(dx, dy);
			SimpleRectangle bounds =
			        new SimpleRectangle(-dy, -dx, -dy + getHeight(), -dx
			                + getWidth());

			// drawBackgroundHeights(g2d);

			drawWays(g2d);

			// drawNodes(h, g2d);

			drawDebugTracks(g2d, bounds);

			drawDebugObjects(g2d, bounds);

			// drawTestPolys(h, g2d);

			g2d.translate(-dx, -dy);
		}

		private void drawDebugObjects(Graphics2D g2d, SimpleRectangle bounds) {
			g2d.setColor(new Color(0x06470c));

			for (ObjectLink object : data.getObjects().getObjectsIn(bounds)) {
				int x = (int) object.getPosition().getY();
				int y = (int) object.getPosition().getX();
				g2d.fillRect(x - 1, y - 1, 3, 3);
			}
		}

		private void drawTestPolys(Graphics2D g2d) {
			g2d.setColor(Color.DARK_GRAY);
			g2d.setStroke(new BasicStroke(3));

			System.out.println("There area "
			        + data.getLandscape()
			                .getPolygonsOnBlock(new GridPart(0, 0)) + " polys");
			for (LandscapePolygon poly : data.getLandscape()
			        .getPolygonsOnBlock(new GridPart(0, 0))) {
				Polygon poly2 = poly.getPolygon();
				if (poly2 instanceof SimplePolygon) {
					Double path = new Path2D.Double();
					boolean first = true;
					for (LocalPoint point : (SimplePolygon) poly2) {
						if (first) {
							path.moveTo(point.getY(), point.getX());
							first = false;
						} else {
							path.lineTo(point.getY(), point.getX());
						}
					}
					path.closePath();
					g2d.draw(path);
				}
			}
		}

		private void drawDebugTracks(Graphics2D g2d, SimpleRectangle bounds) {
			g2d.setStroke(new BasicStroke(3));

			System.out.println("Found " + network.getConnections().size()
			        + " connections");
			for (TrackConnection connection : network.getConnectionsIn(bounds)) {
				g2d.setColor(getConnectionColor(connection));
				g2d.drawLine((int) connection.getStart().getPosition().getY(),
				        (int) connection.getStart().getPosition().getX(),
				        (int) connection.getEnd().getPosition().getY(),
				        (int) connection.getEnd().getPosition().getX());
			}

			g2d.setStroke(new BasicStroke(1));

			for (TrackNode node : network.getNodes()) {
				int x = (int) node.getPosition().getY();
				int y = (int) node.getPosition().getX();

				g2d.setColor(Color.ORANGE);
				for (HeightRule rule : node.getHeightRules()) {
					visualizeHeightRule(g2d, x, y, rule);
				}

				g2d.setColor(Color.WHITE);
				g2d.drawRect(x - 1, y - 1, 3, 3);
				g2d.drawString(
				        Math.round(node.getPosition().getHeight()) + "m",
				        x + 6, y + 3);
			}

		}

		private void visualizeHeightRule(Graphics2D g2d, int x, int y,
		        HeightRule rule) {
			if (rule instanceof DesireBetweenNodes) {
				LocalPoint p1 =
				        ((DesireBetweenNodes) rule).getNode1().getPosition();
				LocalPoint p2 =
				        ((DesireBetweenNodes) rule).getNode2().getPosition();

				g2d.drawLine((int) p1.getY(), (int) p1.getX(), x, y);
				g2d.drawLine((int) p2.getY(), (int) p2.getX(), x, y);
			}
		}

		private Color getConnectionColor(TrackConnection connection) {
			Subnetwork subnetwork = connection.getTracktype().getSubnetwork();
			if (subnetwork == Subnetwork.RAIL) {
				return connection.isStraight() ? new Color(0x653700)
				        : new Color(0x9c6d57);
			} else if (subnetwork == Subnetwork.WATERWAY) {
				return connection.isStraight() ? new Color(0x030aa7)
				        : new Color(0x0343df);
			} else if (subnetwork == Subnetwork.CONTACT_WIRE) {
				return new Color(0x0, true);
			} else {
				return connection.isStraight() ? Color.BLACK : Color.GRAY;
			}
		}

		private void drawBackgroundHeights(Graphics2D g2d) {
			for (int x = -dx; x < getWidth() - dx; x++) {
				for (int y = -dy; y < getHeight() - dy; y++) {
					LocalPoint point = new LocalPoint(y, x);
					TextureLink texture = textureProvider.getTextureAt(point);
					g2d.setColor(texture.getColor());
					g2d.drawRect(x, y, 1, 1);
				}
			}
		}

		private void drawWays(Graphics2D g2d) {
			for (OsmWay way : data.getOsmData().getWays()) {
				if (way.getProperty("natural") != null) {
					g2d.setColor(Color.GREEN);
				} else if (way.getProperty("landuse") != null) {
					g2d.setColor(Color.GRAY);
				} else if (way.getProperty("railway") != null) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.RED);
				}

				LocalPoint old = null;
				for (OsmNode node : way) {
					LocalPoint local =
					        data.getConverter().toLocal(node.getLat(),
					                node.getLon());
					if (old != null) {
						g2d.draw(new Line2D.Double(old.getY(), old.getX(),
						        local.getY(), local.getX()));
					}
					old = local;
				}
			}
		}

		private void drawNodes(Graphics2D g2d) {
			for (OsmNode node : data.getOsmData().getNodes()) {
				if (node.getProperty("natural") != null) {
					g2d.setColor(Color.GREEN);
				} else if (node.getProperty("landuse") != null) {
					g2d.setColor(Color.GRAY);
				} else if (node.getProperty("railway") != null) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.RED);
				}

				LocalPoint local =
				        data.getConverter().toLocal(node.getLat(),
				                node.getLon());
				g2d.fillRect((int) local.getY() - 1, (int) local.getX() - 1, 3,
				        3);
			}
		}
	}

	private static TrackNetwork generateNetwork(ConversionData data) {
		TrackNetwork network = data.getNetwork();
		(new OsmTrackConverter(data)).addToNetwork();
		network.cleanUp();
		network.straightenLongLines();

		return network;
	}
}
