package test.org.tigris.mbt.conditions;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;
import org.tigris.mbt.conditions.RequirementCoverage;
import org.tigris.mbt.conditions.StopCondition;
import org.tigris.mbt.exceptions.GeneratorException;
import org.tigris.mbt.graph.Edge;
import org.tigris.mbt.graph.Graph;
import org.tigris.mbt.graph.Vertex;

import junit.framework.TestCase;

public class RequirementCoverageTest extends TestCase {

	Graph graph;
	Vertex start;
	Vertex v1;
	Vertex v2;
	Edge e0;
	Edge e1;

	protected void setUp() throws Exception {
		super.setUp();
		ModelBasedTesting.getInstance().reset();
		graph = new Graph();

		start = Util.addVertexToGraph(graph, "Start");
		v1 = Util.addVertexToGraph(graph, "V1");
		v2 = Util.addVertexToGraph(graph, "V2");

		v1.setReqTagKey("R1");
		v2.setReqTagKey("R2");

		e0 = Util.addEdgeToGraph(graph, start, v1, "E0", null, null, null);
		e1 = Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, null);

		e0.setReqTagKey("R3");
		e1.setReqTagKey("R4");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		graph.removeAllEdges();
		graph.removeAllVertices();
		start = v1 = v2 = null;
		e0 = e1 = null;
	}

	public void testConstructor() {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.setCondition(new RequirementCoverage());
	}

	public void testFulfillment() throws GeneratorException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		StopCondition condition = new RequirementCoverage();
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());

		assertEquals((double) 0 / 4, condition.getFulfilment(), 0.01);
		mbt.getNextStep();
		assertEquals((double) 2 / 4, condition.getFulfilment(), 0.01);
		mbt.getNextStep();
		assertEquals((double) 4 / 4, condition.getFulfilment(), 0.01);
	}

	public void testIsFulfilled() throws GeneratorException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		StopCondition condition = new RequirementCoverage();
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());

		assertEquals(false, condition.isFulfilled());
		mbt.getNextStep();
		assertEquals(false, condition.isFulfilled());
		mbt.getNextStep();
		assertEquals(true, condition.isFulfilled());
	}

}
