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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeNotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for class {@link GraphLabeller}.
 * 
 * @author Christoph Böhme
 *
 */
public final class CanonicalLabellingTest {

	private static final String NAME1 = "L1";
	private static final String NAME2 = "L2";
	private static final String NAME3 = "L3";
	private static final String NAME4 = "L4";
	private static final String NAME5 = "L5";
	
	private Graph<String, String, String> graph;
	
	@Before
	public void setup() {
		graph = new Graph<>();
	}

	
	@Test
	public void shouldNotAnnounceLabellingIfGraphIsEmpty() {
		
		final GraphLabeller labeller = new GraphLabeller(graph);

		assertFalse("No labelling variants expected", labeller.hasNext());
	}
	
	@Test(expected=NoSuchElementException.class)
	public void shouldFailIfNextIsCalledWhenThereAreNoMoreLabellings() {
		graph.addVertex("1", NAME1);

		final GraphLabeller labeller = new GraphLabeller(graph);
		
		assumeNotNull(labeller.next());
		assumeFalse(labeller.hasNext());
		labeller.next();
	}
	
	@Test(expected=NoSuchElementException.class)
	public void shouldFailIfGraphIsEmpty() {
		
		final GraphLabeller labeller = new GraphLabeller(graph);

		assumeFalse(labeller.hasNext());
		labeller.next();
	}

	@Test
	public void shouldLabelGraphWithUniquelyNamedNodes() {
		graph.addVertex("1", NAME1);
		graph.addVertex("2", NAME2);
		graph.addVertex("3", NAME3);
		graph.addDirectedEdge("1", "2", NAME4);
		graph.addDirectedEdge("1", "3", NAME5);
		
		final GraphLabeller labeller = new GraphLabeller(graph);

		verifyLabellings(graph, labeller, 1);
	}

	@Test
	public void shouldLabelGraphWithAmbiguousNodesWithUniqueNeighbours() {
		graph.addVertex("1", NAME1);
		graph.addVertex("2", NAME2);
		graph.addVertex("3", NAME2);
		graph.addDirectedEdge("1", "2", NAME3);
		graph.addDirectedEdge("1", "3", NAME4);
		
		final GraphLabeller labeller = new GraphLabeller(graph);

		verifyLabellings(graph, labeller, 1);
	}
	
	@Test
	public void shouldLabelGraphWithAmbiguousNodesWithDistinctNeighbourhood() {
		graph.addVertex("1", NAME1);
		graph.addVertex("2", NAME1);
		graph.addVertex("3", NAME2);
		graph.addVertex("4", NAME3);
		graph.addVertex("5", NAME3);
		graph.addDirectedEdge("1", "2", NAME4);
		graph.addDirectedEdge("1", "3", NAME5);
		graph.addDirectedEdge("1", "4", NAME5);
		graph.addDirectedEdge("2", "5", NAME5);
		
		final GraphLabeller labeller = new GraphLabeller(graph);

		verifyLabellings(graph, labeller, 1);
	}

	@Test
	public void shouldLabelGraphWithAmbiguousNodesInALoop() {
		graph.addVertex("1", NAME1);
		graph.addVertex("2", NAME1);
		graph.addVertex("3", NAME1);
		graph.addVertex("4", NAME2);
		graph.addDirectedEdge("1", "2", NAME3);
		graph.addDirectedEdge("2", "3", NAME3);
		graph.addDirectedEdge("3", "1", NAME3);
		graph.addDirectedEdge("1", "4",NAME3);
		
		final GraphLabeller labeller = new GraphLabeller(graph);

		verifyLabellings(graph, labeller, 1);
	}
	
	@Test
	public void shouldLabelGraphWithNonResolvableAmbiguity() {
		graph.addVertex("1", NAME1);
		graph.addVertex("2", NAME2);
		graph.addVertex("3", NAME2);
		graph.addDirectedEdge("1", "2", NAME3);
		graph.addDirectedEdge("1", "3", NAME3);
		
		final GraphLabeller labeller = new GraphLabeller(graph);

		// NO CHECKSTYLE MagicNumber FOR 3 LINES:
		// The variant count is specific for the
		// graph defined this test case.
		verifyLabellings(graph, labeller, 4);
	}
	
	@Test
	public void shouldLabelGraphWithTwoNonResolvableAmbiguities() {
		graph.addVertex("1", NAME1);
		graph.addVertex("2", NAME2);
		graph.addVertex("3", NAME2);
		graph.addVertex("4", NAME2);
		graph.addDirectedEdge("1", "2", NAME3);
		graph.addDirectedEdge("1", "3", NAME3);
		graph.addDirectedEdge("1", "4", NAME3);

		final GraphLabeller labeller = new GraphLabeller(graph);

		// NO CHECKSTYLE MagicNumber FOR 3 LINES:
		// The variant count is specific for the
		// graph defined this test case.
		verifyLabellings(graph, labeller, 24);
	}

	@Test
	public void shouldLabelGraphWithNonResolvableAmbiguityInALoop() {
		graph.addVertex("1", NAME1);
		graph.addVertex("2", NAME1);
		graph.addVertex("3", NAME1);
		graph.addDirectedEdge("1", "2", NAME2);
		graph.addDirectedEdge("2", "3", NAME2);
		graph.addDirectedEdge("3", "1", NAME2);

		final GraphLabeller labeller = new GraphLabeller(graph);

		// NO CHECKSTYLE MagicNumber FOR 3 LINES:
		// The variant count is specific for the
		// graph defined this test case.
		verifyLabellings(graph, labeller, 6);
	}

	@Test
	public void shouldLabelGraphWithMultipleComponentsAndANonResolvableAmbiguity() {
		graph.addVertex("1", NAME1);
		graph.addVertex("2", NAME1);
		graph.addVertex("3", NAME2);
		graph.addVertex("4", NAME1);
		graph.addVertex("5", NAME1);
		graph.addVertex("6", NAME2);
		graph.addDirectedEdge("1", "2", NAME3);
		graph.addDirectedEdge("1", "3", NAME3);
		graph.addDirectedEdge("4", "5", NAME3);
		graph.addDirectedEdge("4", "6", NAME3);

		final GraphLabeller labeller = new GraphLabeller(graph);

		// NO CHECKSTYLE MagicNumber FOR 3 LINES:
		// The variant count is specific for the
		// graph defined this test case.
		verifyLabellings(graph, labeller, 10);
	}

	@Test
	public void shouldAllowNullAsNodeName() {
		graph.addVertex("1", null);
		graph.addVertex("2", NAME1);
		graph.addDirectedEdge("1", "2", null);

		final GraphLabeller labeller = new GraphLabeller(graph);

		verifyLabellings(graph, labeller, 1);
	}

	private static void verifyLabellings(final Graph<?, ?, ?> graph,
			final GraphLabeller labeller, final int variants) {
		
		for (int i = 0; i < variants; ++i) {
			assertTrue("Less labelling variants found than expected", labeller.hasNext());
			verifyLabelling(graph, labeller.next());
		}
		assertFalse("More labelling variants found than expected", labeller.hasNext());
	}
	
	private static void verifyLabelling(final Graph<?, ?, ?> graph,
			final Map<Node<?>, Label> labelling) {
		
		final Set<Label> labels = new HashSet<>();
		
		for (final Node<?> node : graph.getNodes()) {
			assertTrue("Node " + node.toString() + " has not been labelled",
					labelling.containsKey(node));
			assertTrue("Label " + labelling.get(node).toString() +  " is not unique",
					labels.add(labelling.get(node)));
			labelling.remove(node);
		}
		assertTrue("Supernumerous labels found", labelling.isEmpty());
	}

}
