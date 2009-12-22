package org.tigris.mbt.conditions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tigris.mbt.graph.Vertex;
import org.tigris.mbt.machines.ExtendedFiniteStateMachine;
import org.tigris.mbt.machines.FiniteStateMachine;

public class ReachedVertex extends StopCondition {

	private ArrayList<Vertex> allVertices;
	private Vertex endVertex;
	private int[] proximity;
	private int maxDistance;
	private String stateName;
	private String subState;

	public boolean isFulfilled() {
		return getFulfilment() >= 0.99999;
	}

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		if (this.endVertex == null)
			this.endVertex = machine.findState(stateName);
		if (this.endVertex == null)
			throw new RuntimeException("State '" + stateName + "' not found in model");
		this.proximity = getFloydWarshall();
		this.maxDistance = max(this.proximity);
	}

	public ReachedVertex(Vertex endState) {
		this.endVertex = endState;
		this.subState = "";
	}

	public ReachedVertex(String vertexName) {

		String[] state = vertexName.split("/", 2);
		this.stateName = state[0];
		this.subState = (state.length > 1 ? state[1] : "");
	}

	public double getFulfilment() {
		int distance = proximity[allVertices.indexOf(getMachine().getCurrentState())];
		if (getMachine() instanceof ExtendedFiniteStateMachine) {
			String currentState = getMachine().getCurrentStateName();
			String currentSubState = "";
			if (currentState.contains("/")) {
				currentSubState = currentState.split("/", 2)[1];
				Pattern actionPattern = Pattern.compile(this.subState);
				Matcher actionMatcher = actionPattern.matcher(currentSubState);
				if (actionMatcher.find()) {
					return 1;
				}
			}
			return 0;
		}

		return ((double) 1) - ((double) distance / (double) maxDistance);
	}

	private int max(int[] t) {
		int maximum = t[0];
		for (int i = 1; i < t.length; i++) {
			if (t[i] > maximum) {
				maximum = t[i];
			}
		}
		return maximum;
	}

	private int[][] getFloydWarshallMatrix() {
		allVertices = new ArrayList<Vertex>(getMachine().getAllStates());
		int n = allVertices.size();
		int[][] retur = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				int x = 99999;
				if (i == j) {
					x = 0;
				} else if (getMachine().getModel().isPredecessor(allVertices.get(i), allVertices.get(j))) {
					x = 1;
				}
				retur[i][j] = x;
			}
		return retur;
	}

	private int[] getFloydWarshall() {
		int path[][] = getFloydWarshallMatrix();
		int n = path.length;
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++) {
					path[i][j] = Math.min(path[i][j], path[i][k] + path[k][j]);
				}
		}
		int startIndex = allVertices.indexOf(endVertex);
		if (startIndex >= 0)
			return path[startIndex];
		throw new RuntimeException("vertex no longer in Graph!");
	}

	public String toString() {
		return "VERTEX='" + endVertex + "'";
	}

}