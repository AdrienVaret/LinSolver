package solver;

import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;
import java.util.ArrayList;
import java.util.List;

import org.chocosolver.graphsolver.GraphModel;
import org.chocosolver.graphsolver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;

import graphs.Node;
import graphs.UndirGraph;
import parser.GraphParser;
import utils.Couple;
import utils.EdgeSet;
import utils.Interval;

import org.chocosolver.solver.search.strategy.strategy.*;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;

public class Approximation {

	private static long computeCyclesTime;
	
	public static List<ArrayList<Integer>> computeCycles(UndirGraph molecule){
		
		long begin = System.currentTimeMillis();
		
		ArrayList<ArrayList<Integer>> cycles = new ArrayList<ArrayList<Integer>>();
		
		int [] firstVertices = new int [molecule.getNbEdges()];
		int [] secondVertices = new int [molecule.getNbEdges()];
		
		for (int n = 1 ; n <= 4 ; n++) {
			
			int size = 4 * n + 2;
			
			GraphModel model = new GraphModel("Cycles");
			
			UndirectedGraph GLB = new UndirectedGraph(model, molecule.getNbNodes(), SetType.BITSET, false);
	        UndirectedGraph GUB = new UndirectedGraph(model, molecule.getNbNodes(), SetType.BITSET, false); 
	        
	        for (int i = 0 ; i < molecule.getNbNodes() ; i++) {
	        	GUB.addNode(i);
	        	
	        	for (int j = (i+1) ; j < molecule.getNbNodes() ; j++) {
	        		if (molecule.getAdjacencyMatrix()[i][j] == 1) {
	        			GUB.addEdge(i, j);
	        		}
	        	}
	        }
	        
	        UndirectedGraphVar g = model.graphVar("g", GLB, GUB);
	        
	        BoolVar[] boolEdges = new BoolVar[molecule.getNbEdges()];
	        
	        int index = 0;
	        for (int i = 0 ; i < molecule.getNbNodes() ; i++) {
	        	for (int j = (i+1) ; j < molecule.getNbNodes() ; j++) {
	        		
	        		if (molecule.getAdjacencyMatrix()[i][j] == 1) {
	        			boolEdges[index] = model.boolVar("(" + i + "--" + j + ")");
	                    model.edgeChanneling(g, boolEdges[index], i, j).post();
	                    firstVertices[index] = i;
	                    secondVertices[index] = j;
	                    index ++;
	        		}
	        	}
	        }
	        
	        model.cycle(g).post();
	        model.arithm(model.nbNodes(g), "=", size).post();
	        
	        model.getSolver().setSearch(new IntStrategy(boolEdges, new FirstFail(model), new IntDomainMin()));
			Solver solver = model.getSolver();
			
			while(solver.solve()) {
				Solution solution = new Solution(model);
				solution.record();
				
				ArrayList<Integer> cycle = new ArrayList<Integer>();
				
				for (int i = 0 ; i < boolEdges.length ; i++) {
					if (solution.getIntVal(boolEdges[i]) == 1) {
						cycle.add(firstVertices[i]);
						cycle.add(secondVertices[i]);
					}
				}
				
				cycles.add(cycle);
			}
		}
		
		long end = System.currentTimeMillis();
		computeCyclesTime = end - begin;
		
		return cycles;
	}
	
	public static EdgeSet computeStraightEdges(UndirGraph molecule, ArrayList<Integer> cycle) {
		
		List<Node> firstVertices = new ArrayList<Node>();
		List<Node> secondVertices = new ArrayList<Node>();
		
		for (int i = 0 ; i < cycle.size() - 1 ; i += 2) {
			int uIndex = cycle.get(i);
			int vIndex = cycle.get(i + 1);
			
			Node u = molecule.getNodesRefs()[uIndex];
			Node v = molecule.getNodesRefs()[vIndex];
			
			if (u.getX() == v.getX()) {
				firstVertices.add(u);
				secondVertices.add(v);
			}
		}
		
		return new EdgeSet(firstVertices, secondVertices);
	}
	
	public static List<Interval> computeIntervals(UndirGraph molecule, ArrayList<Integer> cycle, EdgeSet edges){
		
		List<Interval> intervals = new ArrayList<Interval>();
		
		/*
		 * Computing "straight edges"
		 */
	/*	
		List<Node> firstVertices = new ArrayList<Node>();
		List<Node> secondVertices = new ArrayList<Node>();
		
		for (int i = 0 ; i < cycle.size() - 1 ; i += 2) {
			int uIndex = cycle.get(i);
			int vIndex = cycle.get(i + 1);
			
			Node u = molecule.getNodesRefs()[uIndex];
			Node v = molecule.getNodesRefs()[vIndex];
			
			if (u.getX() == v.getX()) {
				firstVertices.add(u);
				secondVertices.add(v);
			}
		}
	*/	
		/*
		 * Creating intervals
		 */
		
		int [] edgesOK = new int [edges.size()];
		
		for (int i = 0 ; i < edges.size() ; i ++) {
			if (edgesOK[i] == 0) {
				edgesOK[i] = 1;
				Node u1 = edges.getFirstVertices().get(i);
				Node v1 = edges.getSecondVertices().get(i);
				
				int y1 = Math.min(u1.getY(), v1.getY());
				int y2 = Math.max(u1.getY(), v1.getY());
				
				List<Integer> sameLineNodes = new ArrayList<Integer>();
				
				for (int j = (i+1) ; j < edges.size() ; j++) {
					if (edgesOK[j] == 0) {
						Node u2 = edges.getFirstVertices().get(j);
						Node v2 = edges.getSecondVertices().get(j);
						
						int y3 = Math.min(u2.getY(), v2.getY());
						int y4 = Math.max(u2.getY(), v2.getY());
						
						if (y1 == y3 && y2 == y4) {
							edgesOK[j] = 1;
							sameLineNodes.add(j);
						}
					}
				}
				
				if (sameLineNodes.size() == 1) {
					intervals.add(new Interval(edges.getFirstVertices().get(i), edges.getSecondVertices().get(i), 
								edges.getFirstVertices().get(sameLineNodes.get(0)), edges.getFirstVertices().get(sameLineNodes.get(0))));
				}
				
				else {
					
					int minIndex1 = i;
					int minIndex2 = -1;
					
					int minX1 = edges.getFirstVertices().get(i).getX();
					int minX2 = Integer.MAX_VALUE;
					
					for (Integer j : sameLineNodes) {
						int x = edges.getFirstVertices().get(j).getX();
						
						if (x < minX1) {
							minX2 = minX1;
							minX1 = x;
							minIndex2 = minIndex1;
							minIndex1 = j;
						}
						
						else if (x < minX2 && x > minX1) {
							minX2 = x;
							minIndex2 = j;
						}
					}
					
					Interval interval1 = new Interval(edges.getFirstVertices().get(minIndex1), edges.getSecondVertices().get(minIndex1), 
							                          edges.getFirstVertices().get(minIndex2), edges.getSecondVertices().get(minIndex2));
					
					intervals.add(interval1);
					
					int index1 = -1, index2 = -1;
					
					for (Integer j : sameLineNodes) {
						if (j != minIndex1 && j != minIndex2) {
							if (index1 == -1)
								index1 = j;
							else
								index2 = j;
						}
					}
					
					Interval interval2 = new Interval(edges.getFirstVertices().get(index1), edges.getSecondVertices().get(index1), 
							                          edges.getFirstVertices().get(index2), edges.getSecondVertices().get(index2));
					
					intervals.add(interval2);
					
				}
			}
		}
		
		return intervals;
	}
	
	public static int [][] substractCycle(UndirGraph molecule, ArrayList<Integer> cycle){
		
		int [][] newGraph = new int [molecule.getNbNodes()][molecule.getNbNodes()];
		int [] vertices = new int [molecule.getNbNodes()];
		
		for (Integer u : cycle)
			vertices[u] = 1;
		
		for (int u = 0 ; u < molecule.getNbNodes() ; u++) {
			if (vertices[u] == 0) {
				for (int v = (u+1) ; v < molecule.getNbNodes() ; v++) {
					if (vertices[v] == 0) {
						newGraph[u][v] = molecule.getAdjacencyMatrix()[u][v];
						newGraph[v][u] = molecule.getAdjacencyMatrix()[v][u];
					}
				}
			}
		}
		
		return newGraph;
	}
	
	public static Couple<Integer> getCycleIndex(UndirGraph molecule, ArrayList<Integer> cycle) {
		
		int n = cycle.size() - 4;
		
		if (n == 1)
			return new Couple<Integer>(0, -1);
		
		else if (n == 2)
			return new Couple<Integer>(1, -1);
		
		else {
			EdgeSet edges = computeStraightEdges(molecule, cycle);
			
			if (edges.size() == 2) {
				
				if (n == 3)
					return new Couple<Integer>(2, 1);
				
				if (n == 4)
					return new Couple<Integer>(5, 1);
			}
			
			
			else if (edges.size() == 4) {
				
				if (n == 3) {
					
					List<Interval> intervals = computeIntervals(molecule, cycle, edges);
					boolean condition = false;
					
					for (Interval interval : intervals) {
						if (interval.getSize() == 2) {
							condition = true;
							break;
						}
					}
					
					if (condition)
						return new Couple<Integer>(3, 1);
					
					else
						return new Couple<Integer>(4, 1);
				
				}
				
				else if (n == 4) {
					
					List<Interval> intervals = computeIntervals(molecule, cycle, edges);
					boolean condition = false;
					
					for (Interval interval : intervals) {
						if (interval.getSize() == 2) {
							condition = true;
							break;
						}
					}
					
					if (condition)
						return new Couple<Integer>(6, 1);
					
					else
						return new Couple<Integer>(9, 1);
				}
			}
			
			
			else if (edges.size() == 6) {
				
				List<Interval> intervals = computeIntervals(molecule, cycle, edges);
				
				List<Interval> intervalsSize2 = new ArrayList<Interval>();
				List<Interval> intervalsSize4 = new ArrayList<Interval>();
				List<Interval> intervalsSize6 = new ArrayList<Interval>();
				
				for (Interval interval : intervals) {
					if (interval.getSize() == 2) intervalsSize2.add(interval);
					if (interval.getSize() == 4) intervalsSize4.add(interval);
					if (interval.getSize() == 6) intervalsSize6.add(interval);
				}
				
				if (intervalsSize2.size() == 2 && intervalsSize4.size() == 1 && intervalsSize6.size() == 0) {
					
					Interval i2_1 = intervalsSize2.get(0);
					Interval i2_2 = intervalsSize2.get(1);
					Interval i4 = intervalsSize4.get(0);
					
					
					if (i2_1.getX1() == i2_2.getX1() && i2_1.getX2() == i2_2.getX2()) {
						
						if (i4.getX1() < i2_1.getX1() && i4.getX2() > i2_1.getX2())
							return new Couple<Integer>(4, 2);
						
						if ((i4.getX1() < i2_1.getX1() && i4.getX2() < i2_1.getX2()) ||
							(i4.getX1() > i2_1.getX1() && i4.getX2() > i2_1.getX2()))
							return new Couple<Integer>(7, -1);
							
					}
					
					else if (i2_1.getX1() != i2_2.getX1() && i2_1.getX1() != i2_2.getX2()) {
						
						if (i2_1.getY1() == i2_2.getY1() && i2_1.getY2() == i2_2.getY2())
							return new Couple<Integer>(11, 1);
						
						if ((i2_1.getX1() == i4.getX1() || i2_2.getX1() == i4.getX1()) ||
							(i2_1.getX2() == i4.getX2() || i2_2.getX2() == i4.getX2()))
							return new Couple<Integer>(11, 2);
						
						if ((i4.getY1() > Math.min(i2_1.getY1(), i2_2.getY1()) && i4.getY1() < Math.max(i2_1.getY1(), i2_2.getY1())) &&
							(i4.getY2() > Math.min(i2_1.getY2(), i2_2.getY2()) && i4.getY2() < Math.max(i2_1.getY2(), i2_2.getY2())))
							return new Couple<Integer>(13, 1);
						
						else 
							return new Couple<Integer>(6, 2);
					}
				}
				
				if (intervalsSize2.size() == 1 && intervalsSize4.size() == 1 && intervalsSize6.size() == 1) {
					return new Couple<Integer>(10, 1);
				}
				
				if (intervalsSize2.size() == 0 && intervalsSize4.size() == 3 && intervalsSize6.size() == 0) {
					return new Couple<Integer>(10, 2);
				}
				
				if (intervalsSize2.size() == 2 && intervalsSize4.size() == 0 && intervalsSize6.size() == 1) {
					return new Couple<Integer>(8, 2);
				}
				
				if (intervalsSize2.size() == 1 && intervalsSize4.size() == 2 && intervalsSize6.size() == 0) {
					
					Interval i2 = intervalsSize2.get(0);
					Interval i4_1 = intervalsSize4.get(0);
					Interval i4_2 = intervalsSize4.get(1);
					
					if ((i2.getY1() > Math.min(i4_1.getY1(), i4_2.getY1()) && i2.getY1() < Math.max(i4_1.getY1(), i4_2.getY1())) &&
						(i2.getY2() > Math.min(i4_1.getY2(), i4_2.getY2()) && i2.getY2() < Math.max(i4_1.getY2(), i4_2.getY2()))) 
						return new Couple<Integer>(14, 1);
					
					
					else if ((i4_1.getX1() == i2.getX1() || i4_1.getX2() == i2.getX2()) ||
						(i4_2.getX1() == i2.getX1() || i4_2.getX2() == i2.getX2()))
						return new Couple<Integer>(8, 1);
					
					else
						return new Couple<Integer>(9, 2);
						
				}
				
				if (intervalsSize2.size() == 0 && intervalsSize4.size() == 2 && intervalsSize6.size() == 1)
					return new Couple<Integer>(12, -1);
				
				if (intervalsSize2.size() == 3 && intervalsSize4.size() == 0 && intervalsSize6.size() == 0) {
					
					Interval i2_1 = intervalsSize2.get(0);
					Interval i2_2 = intervalsSize2.get(1);
					Interval i2_3 = intervalsSize2.get(2);
					
					if ((i2_1.getX1() == i2_2.getX1() && i2_1.getX2() == i2_2.getX2()) || 
						(i2_1.getX1() == i2_3.getX1() && i2_1.getX2() == i2_3.getX2()) ||
						(i2_2.getX1() == i2_3.getX1() && i2_3.getX2() == i2_3.getX2())) 
						return new Couple<Integer>(3, 2);
					
					else
						return new Couple<Integer>(2, 2);
				}
					
			}
			
			else if (edges.size() == 8) {
				
				List<Interval> intervals = computeIntervals(molecule, cycle, edges);
				
				Interval i2_1 = intervals.get(0);
				Interval i2_2 = intervals.get(1);
				Interval i2_3 = intervals.get(2);
				Interval i2_4 = intervals.get(3);
				
				if ((i2_1.getX1() == i2_2.getX1() && i2_1.getX2() == i2_2.getX2()) || 
					(i2_1.getX1() == i2_3.getX1() && i2_1.getX2() == i2_3.getX2()) ||
					(i2_1.getX1() == i2_4.getX1() && i2_1.getX2() == i2_4.getX2()) ||
					(i2_2.getX1() == i2_3.getX1() && i2_2.getX2() == i2_3.getX2()) ||
					(i2_2.getX1() == i2_4.getX1() && i2_2.getX2() == i2_4.getX2()) ||
					(i2_3.getX1() == i2_4.getX1() && i2_3.getX2() == i2_4.getX2()))
					return new Couple<Integer>(13, 2);
				
				else
					return new Couple<Integer>(5, 2);
			}
		}
		
		return null;
	}
	
	public static void displayTime() {
		System.out.println("computeCycles() : " + computeCyclesTime + " ms.");
	}
	
	public static void main(String[] args) {
		String path = "/Users/adrien/CLionProjects/ConjugatedCycles/molecules/coronnoids/2_crowns.graph_coord";
		String pathNoCoords = "/Users/adrien/CLionProjects/ConjugatedCycles/molecules/coronnoids/2_crowns.graph";
		
		UndirGraph molecule = GraphParser.parseUndirectedGraph(path, pathNoCoords);
		
		List<ArrayList<Integer>> cycles = computeCycles(molecule);
	
		System.out.println(cycles.size() + " cycles");
		
		/*
		int i = 0;
		for (ArrayList<Integer> cycle : cycles) {
			if (i == 40)
				System.out.print("");
			EdgeSet edges = computeStraightEdges(molecule, cycle);
			List<Interval> intervals = computeIntervals(molecule, cycle, edges);
			System.out.print("");
			i++;
		}
		*/
		displayTime();
	}
}
