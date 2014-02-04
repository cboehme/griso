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
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * An iterator that returns all canonical labellings of a graph.
 * If a graph has nodes whose label cannot be defined by their
 * name or their neighbourhood, the class has to randomly assign
 * a label to a node. Because of this randomisation more than
 * one canonical labelling may exist for a graph.
 * 
 * @author Christoph Böhme
 *
 */
public final class GraphLabeller implements Iterator<Map<Node<?>, Label>> {
	
	private static final int CONNECTION_TO = 31;
	private static final int CONNECTION_FROM = 43;

	private static final Node<?> REWIND_MARKER
			= new Node<String>(Node.Type.VERTEX, "REWIND_MARKER");
	
	private final Label.Factory labelFactory = new Label.Factory();
		
	private final Collection<Node<?>> nodes;
	private final int graphDiameter;

	private final Map<Node<?>, Label> labelling = new HashMap<>();
	
	private final Deque<Multimap<Label, Node<?>>> labelGroupStack = new LinkedList<>();
	private final Deque<Node<?>> alternatives = new LinkedList<>();

	private Multimap<Label, Node<?>> labelGroups = HashMultimap.create();
	
	GraphLabeller(final Graph<?, ?, ?> graph) {
		nodes = new LinkedList<Node<?>>(graph.getNodes());
		graphDiameter = estimateGraphDiameter() + 1;
		
		assignNodesToLabelGroups();
	}
	
	@Override
	public BiMap<Node<?>, Label> next() {
		if (labelling.isEmpty()) {
			// If nodeLabels is empty, this is the first
			// invocation of next
			if (nodes.isEmpty()) {
				throw new NoSuchElementException();
			}
		} else {
			selectNextAlternative();
		}
			
		while (!createLabelling()) {
			collectAlternatives();
			selectNextAlternative();
		}
		
		buildNodeLabelsMap();
		return HashBiMap.create(labelling);
	}
	
	@Override
	public boolean hasNext() {
		for (final Node<?> node : alternatives) {
			if (node != REWIND_MARKER) {  // NOPMD: references to REWIND_MARKER are used as markers
				return true;
			}
		}
		return labelling.isEmpty() && !nodes.isEmpty();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private boolean createLabelling() {
		for (int i=0; i < graphDiameter; ++i) {
			collectAmbiguousNodes();
			if (!relabelAmbiguousNodes()) {
				break;
			}
		}
		collectAmbiguousNodes();
		
		return nodes.isEmpty();
	}
	
	private void assignNodesToLabelGroups() {
		for (final Node<?> node : nodes) {
			final Label classId = labelFactory.create(node.getName().hashCode());
			labelGroups.put(classId, node);
		}
	}
	
	private void collectAmbiguousNodes() {
		nodes.clear();
		for (final Collection<Node<?>> nodesInClass : labelGroups.asMap().values()) {
			if (nodesInClass.size() > 1) {
				nodes.addAll(nodesInClass);
			}
		}
	}
	
	private boolean relabelAmbiguousNodes() {
		buildNodeLabelsMap();
		boolean modified = false;
		for (final Node<?> node : nodes) {
			final Label oldLabel = labelling.get(node);
			final Label newLabel = computeLabel(node);
			if (!oldLabel.equals(newLabel)) {
				labelGroups.remove(oldLabel, node);
				labelGroups.put(newLabel, node);
				modified = true;
			}
		}
		return modified;
	}
	
	private void buildNodeLabelsMap() {
		labelling.clear();
		for (final Entry<Label, Node<?>> entry : labelGroups.entries()) {
			labelling.put(entry.getValue(), entry.getKey());
		}
	}
	
	private Label computeLabel(final Node<?> node) {
		int value = node.getName().hashCode();
		for (final Node<?> connectedNode : node.getConnectedTo()) {
			value += CONNECTION_TO * labelling.get(connectedNode).hashCode();
		}
		for (final Node<?> connectedNode : node.getConnectedFrom()) {
			value += CONNECTION_FROM * labelling.get(connectedNode).hashCode();
		}
		return labelFactory.create(value);
	}

	private void collectAlternatives() {
		labelGroupStack.push(labelGroups);
		alternatives.push(REWIND_MARKER);
		for (final Collection<Node<?>> nodesInClass : labelGroups.asMap().values()) {
			if (nodesInClass.size() > 1) {
				for(final Node<?> node : nodesInClass) {
					alternatives.push(node);
				}
			}
		}
	}
	
	private void selectNextAlternative() {
		Node<?> node = alternatives.pop();
		while (node == REWIND_MARKER) {  // NOPMD: references to REWIND_MARKER are used as markers
			labelGroupStack.pop();
			node = alternatives.pop();
		}
		labelGroups = HashMultimap.create(labelGroupStack.peek());
		buildNodeLabelsMap();
		final Label oldLabel = labelling.get(node);
		final Label newLabel = labelFactory.create();
		labelGroups.remove(oldLabel, node);
		labelGroups.put(newLabel, node);
	}

	private int estimateGraphDiameter() {
		// A rough estimate is enough if the graph only has
		// a few nodes:
		return nodes.size() -1 ;
	}
	
}
