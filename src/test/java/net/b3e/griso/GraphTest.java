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

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for class {@link Graph}.
 * 
 * @author Christoph Böhme
 *
 */
public final class GraphTest {

	private static final String ISOMORPHISM_EXPECTED = "Graphs should be isomorph";
	private static final String NO_ISOMORPHISM_EXPECTED = "Graphs should not be isomorph";
	
	private static final String NAME1 = "L1";
	private static final String NAME2 = "L2";
	private static final String NAME3 = "L3";
	private static final String NAME4 = "L4";
	private static final String NAME5 = "L5";

	private  Graph<String, String, String> graph1;
	private  Graph<String, String, String> graph2;
	
	@Before
	public void setup() {
		graph1 = new Graph<>();
		graph2 = new Graph<>();
	}
	
	@Test
	public void shouldClassifyAGraphIsomorphToItself() {
		assertTrue("A graph should be isomorph to itself", graph1.isIsomorphism(graph1));
	}
	
	@Test
	public void shouldClassifyNullAsNotEvenIsomorphToAnEmptyGraph() {
		assertFalse("A graph should not be isomorph to null", graph1.isIsomorphism(null));
	}
	
	@Test
	public void shouldClassifyEmptyGraphsAsIsomorph() {
		assertTrue(ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertTrue(ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}
	
	@Test
	public void shouldClassifyAnEmptyGraphAsNotIsomorphToANoneEmptyGraph() {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addDirectedEdge("1", "2");
		
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
		
	}
	
	@Test
	public void shouldClassifyIsomorphUniquelyNamedGraphsCorrectly() {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME3);
		graph1.addDirectedEdge("1", "2", NAME4);
		graph1.addDirectedEdge("1", "3", NAME5);

		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME3);
		graph2.addDirectedEdge("a", "b", NAME4);
		graph2.addDirectedEdge("a", "c", NAME5);
		
		assertTrue(ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertTrue(ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}
	
	@Test
	public void shouldClassifyNoneIsomorphUniquelyNamedGraphsCorrectly() {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME3);
		graph1.addDirectedEdge("1", "2", NAME4);
		graph1.addDirectedEdge("1", "3", NAME5);
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME3);
		graph2.addDirectedEdge("a", "b", NAME4);
		graph2.addDirectedEdge("b", "c", NAME5);
		
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}
	
	@Test
	public void shouldClassifyIsomorphAmbiguouslyNamedGraphsCorrectly() {
		createAmbiguouslyNamedGraph(graph1);
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME1);
		graph2.addVertex("c", NAME2);
		graph2.addVertex("d", NAME3);
		graph2.addVertex("e", NAME3);
		graph2.addDirectedEdge("a", "b", NAME4);
		graph2.addDirectedEdge("a", "c", NAME5);
		graph2.addDirectedEdge("a", "d", NAME5);
		graph2.addDirectedEdge("b", "e", NAME5);

		assertTrue(ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertTrue(ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}
	
	@Test
	public void shouldClassifyNoneIsomorphAmbiguouslyNamedGraphsCorrectly() {
		createAmbiguouslyNamedGraph(graph1);
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME1);
		graph2.addVertex("c", NAME2);
		graph2.addVertex("d", NAME3);
		graph2.addVertex("e", NAME3);
		graph2.addDirectedEdge("a", "b", NAME4);
		graph2.addDirectedEdge("a", "c", NAME5);
		graph2.addDirectedEdge("a", "d", NAME5);
		graph2.addDirectedEdge("b", "e", NAME4);

		assertFalse(NO_ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}

	@Test
	public void shouldClassifyIsomorphGraphsWithMultipleLabellingsCorrectly() {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME2);
		graph1.addDirectedEdge("1", "2", NAME3);
		graph1.addDirectedEdge("1", "3", NAME3);
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME2);
		graph2.addDirectedEdge("a", "b", NAME3);
		graph2.addDirectedEdge("a", "c", NAME3);
		
		assertTrue(ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertTrue(ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}
	
	@Test
	public void shouldClassifyNoneIsomorphGraphsWithMultipleLabellingsCorrectly() {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME2);
		graph1.addDirectedEdge("1", "2", NAME3);
		graph1.addDirectedEdge("1", "3", NAME3);
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME2);
		graph2.addDirectedEdge("b", "a", NAME3);
		graph2.addDirectedEdge("c", "a", NAME3);
		
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}

	@Test
	public void shouldClassifyIsomorphGraphsWithUndirectedEdgesCorrectly()  {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME2);
		graph1.addUndirectedEdge("1", "2");
		graph1.addUndirectedEdge("1", "3");
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME2);
		graph2.addUndirectedEdge("a", "b");
		graph2.addUndirectedEdge("c", "a");
		
		assertTrue(ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertTrue(ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}

	@Test
	public void shouldClassifyNoneIsomorphGraphsWithUndirectedEdgesCorrectly()  {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME2);
		graph1.addUndirectedEdge("1", "2");
		graph1.addUndirectedEdge("1", "3");
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME2);
		graph2.addUndirectedEdge("a", "b");
		graph2.addUndirectedEdge("c", "b");
		
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}
	
	@Test
	public void shouldClassifyIsomorphGraphsWithUndirectedNamedEdgesCorrectly()  {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME2);
		graph1.addUndirectedEdge("1", "2", NAME3);
		graph1.addUndirectedEdge("1", "3", NAME3);
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME2);
		graph2.addUndirectedEdge("a", "b", NAME3);
		graph2.addUndirectedEdge("c", "a", NAME3);
		
		assertTrue(ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertTrue(ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}


	@Test
	public void shouldClassifyNoneIsomorphGraphsWithUndirectedNamedEdgesCorrectly()  {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME2);
		graph1.addUndirectedEdge("1", "2", NAME3);
		graph1.addUndirectedEdge("1", "3", NAME3);
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME2);
		graph2.addUndirectedEdge("a", "b", NAME3);
		graph2.addUndirectedEdge("c", "b", NAME3);
		
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}

	@Test
	public void shouldClassifyIsomorphGraphsWithDirectedEdgesCorrectly() {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME3);
		graph1.addDirectedEdge("1", "2");
		graph1.addDirectedEdge("1", "3");
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME3);
		graph2.addDirectedEdge("a", "b");
		graph2.addDirectedEdge("a", "c");
		
		assertTrue(ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertTrue(ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}
	
	@Test
	public void shouldClassifyNoneIsomorphGraphsWithDirectedEdgesCorrectly() {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addVertex("3", NAME3);
		graph1.addDirectedEdge("1", "2");
		graph1.addDirectedEdge("1", "3");
		
		graph2.addVertex("a", NAME1);
		graph2.addVertex("b", NAME2);
		graph2.addVertex("c", NAME3);
		graph2.addDirectedEdge("a", "b");
		graph2.addDirectedEdge("c", "a");
		
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}
	
	@Test
	public void shouldDistinguishBetweenEdgesAndVerticesWithTheSameName() {
		graph1.addVertex("1", NAME1);
		graph1.addVertex("2", NAME2);
		graph1.addDirectedEdge("1", "2", NAME3);
		
		graph2.addVertex("1", NAME1);
		graph2.addVertex("2", NAME2);
		graph2.addVertex("3", NAME3);
		graph2.addDirectedEdge("1", "3");
		graph2.addDirectedEdge("3", "2");
		
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph1.isIsomorphism(graph2));
		assertFalse(NO_ISOMORPHISM_EXPECTED, graph2.isIsomorphism(graph1));
	}
	
	private static void createAmbiguouslyNamedGraph(final Graph<String, String, String> graph) {
		graph.addVertex("1", NAME1);
		graph.addVertex("2", NAME1);
		graph.addVertex("3", NAME2);
		graph.addVertex("4", NAME3);
		graph.addVertex("5", NAME3);
		graph.addDirectedEdge("1", "2", NAME4);
		graph.addDirectedEdge("1", "3", NAME5);
		graph.addDirectedEdge("1", "4", NAME5);
		graph.addDirectedEdge("2", "5", NAME5);
	}
	
}
