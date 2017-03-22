package export.xml;

import java.io.File;
import java.util.List;

import data.position.local.LocalPoint;

import export.objects.ObjectLink;
import export.objects.XMLObject;
import export.tracks.TrackNetwork;
import export.trainz.map.MapGrid;
import export.trainz.map.MapGridPart;

public class XMLMapExporter {

	private final MapGrid grid;
	private final TrackNetwork network;
	private final List<ObjectLink> objects;

	public XMLMapExporter(MapGrid grid, TrackNetwork network,
	        List<ObjectLink> objects) {
		this.grid = grid;
		this.network = network;
		this.objects = objects;
	}

	private String getXMLString() {
		StringBuilder builder = new StringBuilder();
		for (MapGridPart part : grid.getParts()) {
			// TODO: ?
			builder.append("<groundgrid spacing=\"10\" minx=\"");
			builder.append(part.getMinWorldX());
			builder.append("\" miny=\"");
			builder.append(part.getMinWorldY());
			builder.append("\" maxx=\"");
			builder.append(part.getMaxWorldX());
			builder.append("\" maxy=\"");
			builder.append(part.getMaxWorldY());
			builder.append("\">");
			builder.append("</groundgrid>");
		}

		for (ObjectLink link : objects) {
			LocalPoint position = link.getPosition();
			builder.append("<object x=\"" + position.getX() + "\" y=\""
			        + position.getY() + "\" height=\"" + position.getHeight()
			        + "\" rotation=\"" + link.getRotation() + "\"");
			if (link.getData() instanceof XMLObject) {
				XMLObject xmlObject = (XMLObject) link.getData();
				// TODO: xml encode
				builder.append(" xmlfile=\"" + xmlObject.getName() + "\"");
			}
			builder.append(">");
			
			// TODO: tags
			builder.append("</object>");

		}

		return builder.toString();
	}

	public void exportToDir(File dir) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("can only export to a directory");
		}
	}
}
