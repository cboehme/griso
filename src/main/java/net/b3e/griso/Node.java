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
 * @author Christoph Böhme
 */
class Node<N> {

	private final N name;

	private final Collection<Node<?>> connectedTo = new LinkedList<>();
	private final Collection<Node<?>> connectedFrom = new LinkedList<>();

	public Node(final N name) {
		this.name = name;
	}

	public boolean hasName() {
		return name != null;
	}

	public N getName() {
		return name;
	}

	public Collection<Node<?>> getConnectedTo() {
		return Collections.unmodifiableCollection(connectedTo);
	}

	public Collection<Node<?>> getConnectedFrom() {
		return Collections.unmodifiableCollection(connectedFrom);
	}

	/**
	 * Checks whether two nodes are equivalent. Relations between
	 * nodes are distinguished into <i>equality</i> and
	 * <i>equivalence</i>. Two nodes are considered to be equal only
	 * if they refer to the same instance. This can be checked for
	 * using the {@code equals} method. The {@code isEquivalent}
	 * method checks whether two nodes are equivalent meaning that
	 * two nodes are logically corresponding.
	 *
	 * The reason for introducing an additional method for checking for
	 * equivalence is that {@code GraphLabeller} has different
	 * requirements on the equality of nodes than the {@code Graph}
	 * class.
	 *
	 * @param other node to check for equivalence
	 * @return true of {@code this} and {@code other} are equivalent.
	 */
	public boolean isEquivalent(final Node<?> other) {
		if (this.getClass() != other.getClass()) {
			return false;
		}
		if (name == null) {
			return other.name == null;
		}
		return name.equals(other.name);
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

	/**
	 * Can be implemented by derived classes to control how edges
	 * are shown in the Graph.toString output.
	 *
	 * @param builder to which the output is appended
	 */
	protected void printEdges(final StringBuilder builder) {
		// Default implementation does nothing
	}

}
