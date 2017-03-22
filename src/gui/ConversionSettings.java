package gui;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import conversion.landscape.Landscape;
import data.osm.OsmApiLink;
import data.position.local.GlobalToLocalConverter;
import data.position.local.LatLon;

public class ConversionSettings implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = 4218446435034104891L;

	//
	private LatLonRectangle bounds = new LatLonRectangle(50, 51, 9, 10);

	private LatLon origin = new LatLon(48.98742700601184, 8.382568359375);
	
	private Landscape landscape = new Landscape();
	private GlobalToLocalConverter converter = new GlobalToLocalConverter(origin);
	
	private OsmApiLink osmApi = new OsmApiLink();
	
	private List<File> srtmPaths = new LinkedList<File>();
	
	private String outdir = "/media/4ACB-6526/trainz/";
	
	public ConversionSettings() {
		srtmPaths.add(new File("/home/michael/Downloads/hgt"));
		try {
	        osmApi.setAddress(new URL("http://www.overpass-api.de/api/interpreter/"));
        } catch (MalformedURLException e) {
	        System.err.println("Default url not accepted");
        }
	}
	
	public LatLonRectangle getBounds() {
	    return bounds;
    }
	
	public void setBounds(LatLonRectangle bounds) {
		if (bounds ==null) {
			throw new NullPointerException();
		}
	    this.bounds = bounds;
    }
	
	public LatLon getOrigin() {
	    return origin;
    }
	public void setOrigin(LatLon origin) {
	    this.origin = origin;
	    converter = new GlobalToLocalConverter(origin);
    }

	public Landscape getLandscape() {
	    return landscape;
    }
	
	public GlobalToLocalConverter getConverter() {
	    return converter;
    }

	public OsmApiLink getOsmApi() {
	    return osmApi;
    }

	public List<File> getSrtmPaths() {
	    return srtmPaths;
    }

	public void setSrtmPaths(List<File> srtmPaths) {
	    this.srtmPaths = srtmPaths;
    }
	
	public String getOutdir() {
	    return outdir;
    }
	
	public void setOutdir(String outdir) {
	    this.outdir = outdir;
    }
}
