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
import java.util.LinkedList;

/**
 * A node of the internal graph representation. It may
 * represent a vertex or a named edge of the user's
 * graph.
 * 
 * @param <N> Type of edge or vertex name
 * 
 * @author christoph
 */
final class Node<N> {
	
	private final Type type;
	private final N name;
	private final Object nodeId;
	
	private final Collection<Node<?>> connectedTo = new LinkedList<>();
	private final Collection<Node<?>> connectedFrom = new LinkedList<>();

	/**
	 * Type of node
	 */
	enum Type { VERTEX, EDGE };
	
	public Node(final Type type, final N name) {
		this(type, name, null);
	}
	
	public <I> Node(final Type type, final N name, final I nodeId) {
		this.type = type;
		this.name = name;
		this.nodeId = nodeId;
	}
	
	public N getName() {
		return name;
	}

	public Type getType() {
		return type;
	}
	
	public Collection<Node<?>> getConnectedTo() {
		return Collections.unmodifiableCollection(connectedTo);
	}

	public Collection<Node<?>> getConnectedFrom() {
		return Collections.unmodifiableCollection(connectedFrom);
	}

	/**
	 * Connects this node to another node.
	 * 
	 * @param toNode the node to which this node is connected
	 * @return toNode to enable method chaining.
	 */
	public Node<?> connect(final Node<?> toNode) {
		connectedTo.add(toNode);
		toNode.connectedFrom.add(this);
		return toNode;
	}

	@Override
	public String toString() {
		final String prefix;
		if (type == Type.VERTEX) {
			prefix = "V";
		} else {
			prefix = "E";
		}
		if (nodeId == null) {
			return prefix + ":__no_id/" + name.toString();
		} else {
			return prefix + ":" + nodeId.toString() + "/" + name.toString();
		}
	}
	
}