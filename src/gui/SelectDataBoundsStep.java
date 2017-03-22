package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

public class SelectDataBoundsStep implements ContentStep {

	private JPanel content = null;
	private JMapViewer mapViewer;

	private DataBoundRectangle rectangle;

	// current data position.
	private double datastartlat;
	private double dataendlat;
	private double datastartlon;
	private double dataendlon;
	private final ConversionSettings conversionData;

	public SelectDataBoundsStep(ConversionSettings conversionData) {
		this.conversionData = conversionData;
	}

	@Override
	public String getTitle() {
		return "Datenbereich auswählen";
	}

	@Override
	public JComponent getContent() {
		if (content == null) {
			content = generateContent();
		}
		loadDataPosition();

		return content;
	}

	/**
	 * Reloads the data
	 */
	private void loadDataPosition() {
		LatLonRectangle rect = conversionData.getBounds();
		datastartlat = rect.getMaxlat();
		datastartlon = rect.getMaxlon();
		dataendlat = rect.getMinlat();
		dataendlon = rect.getMinlat();
	}

	/**
	 * Saves the data area
	 */
	private void storeDataPosition() {
		if (datastartlat == dataendlat) {
			datastartlat += .001;
		}
		if (datastartlon == dataendlon) {
			datastartlon += .001;
		}
		double minlat = Math.min(datastartlat, dataendlat);
		double maxlat = Math.max(datastartlat, dataendlat);
		double minlon = Math.min(datastartlon, dataendlon);
		double maxlon = Math.max(datastartlon, dataendlon);
		conversionData.setBounds(new LatLonRectangle(minlat, maxlat, minlon,
		        maxlon));
	}

	private JPanel generateContent() {
		JPanel content = new JPanel();

		mapViewer = new JMapViewer();
		rectangle = new DataBoundRectangle();
		mapViewer.addMapRectangle(rectangle);
		content.add(mapViewer);

		mapViewer.addMouseListener(new MapMouseListener());
		return content;
	}

	private class MapMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Coordinate coord = mapViewer.getPosition(e.getPoint());
				setDataAreaStart(coord.getLat(), coord.getLon());
				setDataAreaEnd(coord.getLat(), coord.getLon());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Coordinate coord = mapViewer.getPosition(e.getPoint());
				setDataAreaEnd(coord.getLat(), coord.getLon());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Coordinate coord = mapViewer.getPosition(e.getPoint());
				setDataAreaEnd(coord.getLat(), coord.getLon());
				storeDataPosition();
			}
		}
	};

	private class DataBoundRectangle implements MapRectangle {
		@Override
		public Coordinate getTopLeft() {
			return new Coordinate(Math.max(datastartlat, dataendlat), Math.min(
			        datastartlon, dataendlon));
		}

		@Override
		public Coordinate getBottomRight() {
			return new Coordinate(Math.min(datastartlat, dataendlat), Math.max(
			        datastartlon, dataendlon));
		}

		@Override
		public void paint(Graphics g, Point topLeft, Point bottomRight) {
			g.setColor(Color.RED);
			g.drawRect(topLeft.x, topLeft.y, bottomRight.x - topLeft.x,
			        bottomRight.y - topLeft.y);
		}
	};

	public void setDataAreaStart(double lat, double lon) {
		datastartlat = lat;
		datastartlon = lon;
		mapViewer.repaint();
	}

	public void setDataAreaEnd(double lat, double lon) {
		dataendlat = lat;
		dataendlon = lon;
		mapViewer.repaint();
	}
}
