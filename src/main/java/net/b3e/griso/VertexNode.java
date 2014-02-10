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

/**
 *
 * @param <N> Type of vertex name
 *
 * @author Christoph Böhme
 *
 */
final class VertexNode<N> extends Node<N> {

	private final Object nodeId;

	public <I> VertexNode(final N name, final I nodeId) {
		super(name);
		this.nodeId = nodeId;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append('(');
		builder.append(nodeId.toString());
		builder.append(", \"");
		if (hasName()) {
			builder.append(getName().toString());
		}
		builder.append("\")");

		return builder.toString();
	}

	protected void printConnections(final StringBuilder builder) {
		builder.append(toString());
		builder.append('\n');
		for (final Node<?> node : getConnectedTo()) {
			node.printEdges(builder);
		}
	}

	@Override
	protected void printEdges(final StringBuilder builder) {
		builder.append("\t----> ");
		builder.append(toString());
		builder.append('\n');
	}

}
