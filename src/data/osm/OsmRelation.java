package data.osm;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

public class OsmRelation implements Propertyable {
	LinkedList<OsmRoledNode> nodes = new LinkedList<OsmRoledNode>();
	LinkedList<OsmRoledWay> ways = new LinkedList<OsmRoledWay>();
	Properties properties = new Properties();

	// currently unsupported: parent relations

	public OsmRelation(LinkedList<OsmRoledNode> nodes,
	        LinkedList<OsmRoledWay> ways, Properties tags) {
		this.nodes.addAll(nodes);
		this.ways.addAll(ways);
		properties.putAll(tags);
	}

	public Collection<OsmRoledNode> getNodes() {
		return nodes;
	}

	public Collection<OsmRoledWay> getWays() {
		return ways;
	}

	@Override
	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	@Override
	public Set<String> getPropertyKeys() {
		return properties.stringPropertyNames();
	}

	/**
	 * This is a node that is a member of a relation
	 * 
	 * @author michael
	 */
	public static class OsmRoledNode {
		private final String role;
		private final OsmNode node;

		public OsmRoledNode(String role, OsmNode node) {
			this.role = role;
			this.node = node;
		}

		public String getRole() {
			return role;
		}

		public OsmNode getNode() {
			return node;
		}
	}

	/**
	 * This is a way that is a member of a relation
	 * 
	 * @author michael
	 */
	public static class OsmRoledWay {
		private final String role;
		private final OsmWay way;

		public OsmRoledWay(String role, OsmWay way) {
			this.role = role;
			this.way = way;
		}

		public String getRole() {
			return role;
		}

		public OsmWay getWay() {
			return way;
		}
	}
}
