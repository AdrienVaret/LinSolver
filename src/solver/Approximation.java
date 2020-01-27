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
import graphs.UndirGraph;
import parser.GraphParser;

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
	
	public static void displayTime() {
		System.out.println("computeCycles() : " + computeCyclesTime + " ms.");
	}
	
	public static void main(String[] args) {
		String path = "/Users/adrien/CLionProjects/ConjugatedCycles/molecules/coronnoids/2_crowns.graph_coord";
		String pathNoCoords = "/Users/adrien/CLionProjects/ConjugatedCycles/molecules/coronnoids/2_crowns.graph";
		
		UndirGraph molecule = GraphParser.parseUndirectedGraph(path, pathNoCoords);
		
		List<ArrayList<Integer>> cycles = computeCycles(molecule);
		
		int [][] test = substractCycle(molecule, cycles.get(0));
		
		System.out.println("----------");
		
		for (int i = 0 ; i < test.length ; i++) {
			for (int j = (i+1) ; j < test.length ; j++) {
				if (test[i][j] == 1)
					System.out.println(i + " " + j);
			}
		}
		
		displayTime();
	}
}
