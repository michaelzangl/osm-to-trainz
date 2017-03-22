package conversion;

import java.util.Collection;

import conversion.datachange.geometry.GridPart;
import conversion.landscape.Landscape;
import conversion.objects.ObjectList;
import data.height.HeightDataProvider;
import data.height.LocalHeightDataProvider;
import data.osm.OsmDatapack;
import data.position.local.GlobalToLocalConverter;
import export.tracks.TrackNetwork;

/**
 * this class holds all important conversion data to be passed on between the
 * classes.
 * 
 * @author michael
 */
public class ConversionData {
	private final OsmDatapack data;
	private final GlobalToLocalConverter converter;
	private final Landscape landscape;
	private final LocalHeightDataProvider heightProvider;
	private final TrackNetwork network = new TrackNetwork();
	private final ObjectList objects = new ObjectList();

	public ConversionData(OsmDatapack data, GlobalToLocalConverter converter,
	        HeightDataProvider heightProvider, Collection<GridPart> gridparts) {
		this.data = data;
		this.converter = converter;
		this.heightProvider =
		        new LocalHeightDataProvider(converter, heightProvider);
		landscape = Landscape.generateFormOsmData(data, converter, gridparts);
	}

	public OsmDatapack getOsmData() {
		return data;
	}

	public GlobalToLocalConverter getConverter() {
		return converter;
	}

	/**
	 * Gets the landscpae.
	 * <p>
	 * TODO: sync it with osm data.
	 * 
	 * @return
	 */
	public Landscape getLandscape() {
		return landscape;
	}

	public LocalHeightDataProvider getHeightProvider() {
		return heightProvider;
	}

	public TrackNetwork getNetwork() {
	    return network;
    }
	
	public ObjectList getObjects() {
	    return objects;
    }
}
