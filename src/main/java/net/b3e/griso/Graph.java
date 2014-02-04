/*
 *  Copyright 2014 Christoph Böhme
 *
 *  Licensed under the Apache License, Version 2.0 the "License";
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.b3e.griso;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;

/**
 * A generic graph implementation.
 * 
 * @param <I> Type of vertex identifiers
 * @param <V> Type of vertex names
 * @param <E> Type of edge names
 *
 * @author Christoph Böhme
 */
public final class Graph<I, V, E> {

	/*
	 * From the user's perspective a graph consists of vertices
	 * and named edges which connect the vertices. Internally,
	 * the graph is described by nodes which are connected to other
	 * nodes. These connections are not named. Named edges
	 * are represented by nodes.
	 */
	private final Map<I, Node<V>> vertices = new HashMap<>();
	private final Set<Node<?>> nodes = new HashSet<>();
	
	public Set<Node<?>> getNodes() {
		return Collections.unmodifiableSet(nodes);
	}
	
	/**
	 * Adds a vertex to the graph.
	 * 
	 * @param vertexId unique identifier for addressing the vertex
	 * @param vertexName of the vertex
	 * @throws IllegalArgumentException if {@code id} exists already
	 */
	public void addVertex(final I vertexId, final V vertexName) {
		if (vertices.containsKey(vertexId)) {
			throw new IllegalArgumentException(
					"A vertex with id '" + vertexId.toString() + "' exists already");
		}
		
		final Node<V> node = new Node<>(Node.Type.VERTEX, vertexName, vertexId);
		vertices.put(vertexId, node);
		nodes.add(node);
	}
	
	/**
	 * Adds a directed edge to the graph.
	 * 
	 * @param fromVertex identifier of the vertex where the edge starts from
	 * @param toVertex identifier of the vertex at which the edge ends
	 * @param edgeName of the edge
	 * @throws IllegalArgumentException if the {@code to} or
	 *         {@code from} identifier does not exist
	 */
	public void addDirectedEdge(final I fromVertex, final I toVertex, final E edgeName) {
		final Node<V> fromNode = getVertexNode(fromVertex);
		final Node<V> toNode = getVertexNode(toVertex);
		
		final String nodeId = fromVertex.toString() + "->" + toVertex.toString();
		final Node<E> edgeNode = new Node<>(Node.Type.EDGE, edgeName, nodeId);
		nodes.add(edgeNode);
		
		fromNode.connect(edgeNode).connect(toNode);
	}
	
	/**
	 * Adds a directed unnamed edge to the graph.
	 * 
	 * @param fromVertex identifier of the vertex where the edge starts from
	 * @param toVertex identifier of the vertex at which the edge ends
	 * @throws IllegalArgumentException if the {@code to} or
	 *         {@code from} identifier does not exist
	 */
	public void addDirectedEdge(final I fromVertex, final I toVertex) {
		final Node<V> fromNode = getVertexNode(fromVertex);
		final Node<V> toNode = getVertexNode(toVertex);
				
		fromNode.connect(toNode);
	}

	/**
	 * Adds an undirected edge to the graph.
	 * 
	 * @param vertex1 identifier of the first end vertex of the edge
	 * @param vertex2 identifier of the second end vertex of the edge
	 * @param edgeName of the edge
	 * @throws IllegalArgumentException if the {@code to} or
	 *         {@code from} identifier does not exist
	 */
	public void addUndirectedEdge(final I vertex1, final I vertex2, final E edgeName) {
		final Node<V> node1 = getVertexNode(vertex1);
		final Node<V> node2 = getVertexNode(vertex2);
		
		final String nodeId = vertex1.toString() + "--" + vertex2.toString();
		final Node<E> edgeNode = new Node<>(Node.Type.EDGE, edgeName, nodeId);
		nodes.add(edgeNode);
		
		node1.connect(edgeNode).connect(node2);
		node2.connect(edgeNode).connect(node1);
	}
	
	/**
	 * Adds an undirected unnamed edge to the graph.
	 * 
	 * @param vertex1 identifier of the first end vertex of the edge
	 * @param vertex2 identifier of the second end vertex of the edge
	 * @throws IllegalArgumentException if the {@code to} or
	 *         {@code from} identifier does not exist
	 */
	public void addUndirectedEdge(final I vertex1, final I vertex2) {
		final Node<V> node1 = getVertexNode(vertex1);
		final Node<V> node2 = getVertexNode(vertex2);
				
		node1.connect(node2);
		node2.connect(node1);
	}

	/**
	 * Returns true if {@code otherGraph} is an isomorphism of this graph.
	 * 
	 * @param otherGraph which may be an isomorphism of this one
	 * @return true if otherGraph is an isomorphism
	 */
	public boolean isIsomorphism(final Graph<?, ? extends V, ? extends E> otherGraph) {
		if (this == otherGraph) {
			return true;
		}
		if (otherGraph == null) {
			return false;
		}
		if (nodes.isEmpty() && otherGraph.getNodes().isEmpty()) {
			return true;
		}
		
		if (nodes.size() == otherGraph.getNodes().size()) {
			final GraphLabeller thisLabeller = new GraphLabeller(this);
			final GraphLabeller otherLabeller = new GraphLabeller(otherGraph);
			
			while (thisLabeller.hasNext()) {
				final BiMap<Node<?>, Label> thisLabelling = thisLabeller.next();
				while (otherLabeller.hasNext()) {
					final BiMap<Node<?>, Label> otherLabelling = otherLabeller.next();
					if (compareLabellings(thisLabelling, otherLabelling)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private Node<V> getVertexNode(final I vertexId) {
		final Node<V> node = vertices.get(vertexId);
		if (node == null) {
			throw new IllegalArgumentException(
					"No vertex with id '" + vertexId.toString() + "' exists");
		}
		return node;
	}
	
	private static boolean compareLabellings(final BiMap<Node<?>, Label> thisLabelling,
			final BiMap<Node<?>, Label> otherLabelling) {
		
		if (!thisLabelling.values().equals(otherLabelling.values())) {
			return false;
		}
		
		for (final Label thisLabel : thisLabelling.values()) {
			final Node<?> thisNode = thisLabelling.inverse().get(thisLabel);
			final Node<?> otherNode = otherLabelling.inverse().get(thisLabel);
			
			if (!thisNode.getName().equals(otherNode.getName())
					|| thisNode.getType() != otherNode.getType()) {
				return false;
			}
			
			Set<Label> thisConnectedLabels = getLabels(thisNode.getConnectedTo(), thisLabelling);
			Set<Label> otherConnectedLabels = getLabels(otherNode.getConnectedTo(), otherLabelling);
			if (!thisConnectedLabels.equals(otherConnectedLabels)) {
				return false;
			}

			thisConnectedLabels = getLabels(thisNode.getConnectedFrom(), thisLabelling);
			otherConnectedLabels = getLabels(otherNode.getConnectedFrom(), otherLabelling);
			if (!thisConnectedLabels.equals(otherConnectedLabels)) {
				return false;
			}
		}
		
		return true;
	}
	
	private static Set<Label> getLabels(final Collection<Node<?>> nodes,
			final BiMap<Node<?>, Label> labelling) {
		
		final Set<Label> labels = new HashSet<>();
		for (final Node<?> node : nodes) {
			labels.add(labelling.get(node));
		}
		
		return labels;
	}

}