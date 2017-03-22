package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

import conversion.datachange.geometry.GridPart;
import data.position.local.LatLon;
import data.position.local.LocalPoint;

public class SelectTilesStep implements ContentStep {

	private static final Color RED = Color.RED;

	private JPanel content;

	private MouseAction currentAction = MouseAction.ADD;

	private JTextField originLatField;

	private JTextField originLonField;

	private final ConversionSettings converterData;

	private JMapViewer mapView;

	private JToggleButton selectorigin;

	private JRadioButton addRadio;

	private JRadioButton removeRadio;

	public SelectTilesStep(ConversionSettings converterData) {
		this.converterData = converterData;
	}

	@Override
	public String getTitle() {
		return "Wähle zu nutzende Gridteile";
	}

	@Override
	public JComponent getContent() {
		if (content == null) {
			content = generateContent();
		}
		mapView.setDisplayPositionByLatLon(converterData.getOrigin().getLat(), converterData.getOrigin().getLon(), 15);
		loadOrigin();
		setActionType(MouseAction.SET_ORIGIN);
		reloadMapTilesList();
		return content;
	}

	private void reloadMapTilesList() {
		List<MapRectangle> rects = new ArrayList<MapRectangle>();
		for (GridPart block : converterData.getLandscape().getConvertedBlocks()) {
			LatLon g1 =
			        converterData.getConverter().toGlobal(
			                new LocalPoint(block.getMinX(), block.getMinY()));
			LatLon g2 =
			        converterData.getConverter().toGlobal(
			                new LocalPoint(block.getMaxX(), block.getMaxY()));
			rects.add(new LatLonRectangle(g1, g2));
		}
		mapView.setMapRectangleList(rects);
		mapView.repaint();
	}

	private void loadOrigin() {
		LatLon pos = converterData.getOrigin();
		originLatField.setText(pos.getLat() + "");
		originLonField.setText(pos.getLon() + "");
		setMapOriginMarker(pos.getLat(), pos.getLon());
	}

	private void setMapOriginMarker(double lat, double lon) {
		MapMarker point = new MapMarkerDot(lat, lon);
		mapView.setMapMarkerList(Collections.singletonList(point));
	}

	private void storeOrigin() {
		double lat = Double.NaN;
		double lon = Double.NaN;
		try {
			originLatField.setForeground(Color.BLACK);
			lat = Double.parseDouble(originLatField.getText());
			originLatField.setBackground(Color.WHITE);
			if (lat > 90 || lat < -90) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			originLatField.setBackground(RED);
		}
		try {
			originLonField.setForeground(Color.BLACK);
			lon = Double.parseDouble(originLonField.getText());
			originLonField.setBackground(Color.WHITE);
			if (lon > 180 || lon < -180) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			originLonField.setBackground(RED);
		}

		if (lat != Double.NaN && lon != Double.NaN) {
			converterData.setOrigin(new LatLon(lat, lon));
			setMapOriginMarker(lat, lon);
		}
	}

	private JPanel generateContent() {
		JPanel content = new JPanel(new BorderLayout());

		content.add(generateOriginSelector(), BorderLayout.NORTH);

		JPanel innerContent = new JPanel(new BorderLayout());
		content.add(innerContent);
		innerContent.add(generateActionSelector(), BorderLayout.NORTH);

		mapView = new JMapViewer();
		mapView.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					preformActionOn(mapView.getPosition(e.getPoint()));
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					preformActionOn(mapView.getPosition(e.getPoint()));
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
					preformActionOn(mapView.getPosition(e.getPoint()));
				}
			}
		});
		innerContent.add(mapView);

		return content;
	}

	private JPanel generateActionSelector() {
		addRadio = new JRadioButton("Segmente Hinzufügen");
		addRadio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setActionType(MouseAction.ADD);
			}
		});
		removeRadio = new JRadioButton("Segmente Entfernen");
		removeRadio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setActionType(MouseAction.REMOVE);
			}
		});

		JPanel panel = new JPanel();
		panel.add(addRadio);
		panel.add(removeRadio);
		return panel;
	}

	private Component generateOriginSelector() {
		JPanel panel = new JPanel();

		panel.add(new JLabel("lat: "));
		originLatField = new JTextField();
		originLatField.setColumns(10);
		panel.add(originLatField);

		panel.add(new JLabel("lon: "));
		originLonField = new JTextField();
		originLonField.setColumns(10);
		panel.add(originLonField);

		selectorigin = new JToggleButton("Wählen");
		selectorigin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setActionType(MouseAction.SET_ORIGIN);
			}
		});
		panel.add(selectorigin);
		return panel;
	}

	/**
	 * The action that is done by the mouse when clicking on the map.
	 * 
	 * @author michael
	 */
	private enum MouseAction {
		ADD, REMOVE, SET_ORIGIN;
	}

	private void setActionType(MouseAction action) {
		this.currentAction = action;
		addRadio.setSelected(action == MouseAction.ADD);
		removeRadio.setSelected(action == MouseAction.REMOVE);
		selectorigin.setSelected(action == MouseAction.SET_ORIGIN);
	}

	private void preformActionOn(Coordinate coords) {
		if (currentAction == MouseAction.SET_ORIGIN) {
			originLatField.setText(coords.getLat() + "");
			originLonField.setText(coords.getLon() + "");
			storeOrigin();
		} else {
			LocalPoint point =
			        converterData.getConverter().toLocal(coords.getLat(),
			                coords.getLon());
			GridPart part = GridPart.getUnder(point);
			if (currentAction == MouseAction.ADD) {
				converterData.getLandscape().activateGridPart(part);
			} else if (currentAction == MouseAction.REMOVE) {
				converterData.getLandscape().deactivateGridPart(part);
			}
			reloadMapTilesList();
		}
	}
}
