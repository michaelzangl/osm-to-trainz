package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

import data.position.local.LatLon;

public class LatLonRectangle implements MapRectangle, Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -2460257433397238869L;
	private static final Color TRANSPARENT = new Color(0x30ff0000, true);
	private final double minlat;
	private final double maxlat;
	private final double minlon;
	private final double maxlon;

	public LatLonRectangle(double minlat, double maxlat, double minlon,
	        double maxlon) {
		if (minlat >= maxlat) {
			throw new IllegalArgumentException(
			        "Minlat neds to be smaller than maxlat.");
		}
		if (minlon >= maxlon) {
			throw new IllegalArgumentException(
			        "Minlon neds to be smaller than maxlon.");
		}
		this.minlat = minlat;
		this.maxlat = maxlat;
		this.minlon = minlon;
		this.maxlon = maxlon;
	}

	public LatLonRectangle(LatLon p1, LatLon p2) {
		this(Math.min(p1.getLat(), p2.getLat()), Math.max(p1.getLat(),
		        p2.getLat()), Math.min(p1.getLon(), p2.getLon()), Math.max(
		        p1.getLon(), p2.getLon()));
	}

	public double getMaxlat() {
		return maxlat;
	}

	public double getMaxlon() {
		return maxlon;
	}

	public double getMinlat() {
		return minlat;
	}

	public double getMinlon() {
		return minlon;
	}

	@Override
	public Coordinate getTopLeft() {
		return new Coordinate(maxlat, minlon);
	}

	@Override
	public Coordinate getBottomRight() {
		return new Coordinate(minlat, maxlon);
	}

	@Override
	public void paint(Graphics g, Point topLeft, Point bottomRight) {
		g.setColor(Color.RED);
		g.drawRect(topLeft.x, topLeft.y, bottomRight.x - topLeft.x,
		        bottomRight.y - topLeft.y);
		g.setColor(TRANSPARENT);
		g.fillRect(topLeft.x, topLeft.y, bottomRight.x - topLeft.x,
		        bottomRight.y - topLeft.y);
	}
}
