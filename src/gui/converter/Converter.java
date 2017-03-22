package gui.converter;

import gui.ConversionSettings;
import gui.LatLonRectangle;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import test.Test;
import conversion.ConversionData;
import conversion.datachange.LandscapeAdder;
import conversion.datachange.LandscapeDisplacer;
import conversion.datachange.TreeAdder;
import conversion.datachange.geometry.GridPart;
import conversion.landscape.LandscapeTextureProvider;
import conversion.objects.OsmObjectConverter;
import conversion.tracks.OsmTrackConverter;
import conversion.tracks.height.HeightRuleAdder;
import conversion.tracks.height.TrackHeightComputer;
import data.height.SRTMImporter;
import data.osm.OsmApiLink;
import data.osm.OsmDatapack;
import data.osm.OsmNode;
import data.osm.OsmWay;
import data.position.local.GlobalToLocalConverter;
import data.position.local.LatLon;
import data.position.local.LocalPoint;
import export.ConfigFile;
import export.tracks.TrackNetwork;
import export.trainz.general.Kuid;
import export.trainz.map.GridFile;
import export.trainz.map.MapGrid;
import export.trainz.objects.ObjectFile;
import export.trainz.tracks.TrackFile;

public class Converter {

	private ConversionStep currentStep;

	private List<ConversionStep> steps;

	private final ConversionSettings settings;

	private boolean started = false;

	private boolean stop = false;

	public Converter(ConversionSettings data) {
		this.settings = data;
	}

	public void startConversion() {
		synchronized (this) {
			if (started) {
				throw new IllegalStateException();
			}
			started = true;
			stop = false;
		}

		OsmApiLink link = settings.getOsmApi();

		// Download OSM data
		List<LatLonRectangle> rects = new LinkedList<LatLonRectangle>();
		for (GridPart block : settings.getLandscape().getConvertedBlocks()) {
			LocalPoint l1 = new LocalPoint(block.getMinX(), block.getMinY());
			LatLon g1 = settings.getConverter().toGlobal(l1);

			LocalPoint l2 = new LocalPoint(block.getMaxX(), block.getMaxY());
			LatLon g2 = settings.getConverter().toGlobal(l2);

			rects.add(new LatLonRectangle(g1, g2));
		}
		OsmDatapack osm = link.loadAreas(rects);

		// load height provider
		SRTMImporter heightProvider = new SRTMImporter();
		heightProvider.setLookupPaths(settings.getSrtmPaths());

		// init converter
		GlobalToLocalConverter converter =
		        new GlobalToLocalConverter(settings.getOrigin());
		ConversionData data =
		        new ConversionData(osm, converter, heightProvider, settings
		                .getLandscape().getConvertedBlocks());

		System.out.println("found "
		        + ((Collection<OsmNode>) osm.getNodes()).size() + " nodes and "
		        + ((Collection<OsmWay>) osm.getWays()).size() + " ways");

		System.out.println("Adding landscapes");
		new LandscapeAdder(data).addLandscapes();
		System.out.println("Adding trees");
		new TreeAdder(data).addTrees();
		data.getHeightProvider().addDisplacer(
		        new LandscapeDisplacer(data.getLandscape()));

		// train network
		System.out.println("Generating network");
		generateNetwork(data);

		new Test(data);
		// generate main data structures
		MapGrid grid = new MapGrid();

		// heights and fill grid
		System.out.println("Generating heights");
		for (GridPart gp : data.getLandscape().getConvertedBlocks()) {
			// TODO: realy use blocks here...
			grid.assertPartIsUnder(gp.getMinX(), gp.getMinY());
		}
		grid.loadHeightsForm(data.getHeightProvider());

		// textures
		LandscapeTextureProvider textureProvider =
		        new LandscapeTextureProvider(data.getLandscape());
		grid.loadTexturesFrom(textureProvider);

		System.out.println("Converting objects");
		new OsmObjectConverter(data).doConversion(new Random());
		ObjectFile objects =
		        ObjectFile.constructByList(data.getObjects().getObjects());

		System.out.println("Generating file data");
		TrackFile tracks = TrackFile.constructByNetwork(data.getNetwork());
		GridFile gridFile = GridFile.constructByGrid(grid);
		ConfigFile config =
		        new ConfigFile(
		                new Kuid(-2, 2000 + new Random().nextInt(10000)),
		                "OSM Test export");
		config.addKuids(objects.getUsedKuids());
		config.addKuids(grid.getUsedKuids());
		config.addKuids(data.getNetwork().getUsedKuids());

		String dirname = settings.getOutdir();
		try {
			System.out.println("Writing files");
			gridFile.writeToFile(new File(dirname, "mapfile.gnd"));
			tracks.writeToFile(new File(dirname, "mapfile.trk"));
			objects.writeToFile(new File(dirname, "mapfile.obs"));
			config.writeToFile(new File(dirname, "config.txt"));
			System.out.println("Finished");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private TrackNetwork generateNetwork(ConversionData data) {
		OsmTrackConverter tc = new OsmTrackConverter(data);
		tc.addToNetwork();
		System.out.println("Network cleanup");
		TrackNetwork network = data.getNetwork();
		network.cleanUp();
		network.straightenLongLines();

		System.out.println("Adding height rules");
		new HeightRuleAdder(network).addRules(data.getHeightProvider());
		System.out.println("Computing height");
		new TrackHeightComputer().optimizeNetworkHeights(network);
		return network;
	}

	public synchronized void stop() {
		stop = true;
		// TODO How to stop.
	}

}
