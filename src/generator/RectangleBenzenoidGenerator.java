package generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;

public class RectangleBenzenoidGenerator {

	private static BufferedWriter writer;
	private static List<String> hexagons;
	private static List<String> edges;
	
	private static int [][][] M;
	
	public static boolean edgesEquals(String edge1, String edge2) {
		String [] sEdge1 = edge1.split(" ");
		String [] sEdge2 = edge2.split(" ");
		
		int u1 = Integer.parseInt(sEdge1[1]);
		int v1 = Integer.parseInt(sEdge1[2]);
		int u2 = Integer.parseInt(sEdge2[1]);
		int v2 = Integer.parseInt(sEdge2[2]);
		
		return ((u1 == u2 && v1 == v2) || (u1 == v2 && v1 == u2));
	}
	
	public static void writeFirstHexagon() throws IOException {
		
		edges.add("e 1 2 \n");
		edges.add("e 2 3 \n");
		edges.add("e 3 4 \n");
		edges.add("e 4 5 \n");
		edges.add("e 5 6 \n");
		edges.add("e 6 1 \n");
		
		hexagons.add("h 1 2 3 4 5 6 \n");
	
		M[0][0][0] = 1;
		M[0][0][1] = 2;
		M[0][0][2] = 3;
		M[0][0][3] = 4;
		M[0][0][4] = 5;
		M[0][0][5] = 6;
	}
	
	public static void generateRectangleBenzenoid(int width, int height, String outputFileName) throws IOException {
		
		writer = new BufferedWriter(new FileWriter(new File(outputFileName)));
		hexagons = new ArrayList<String>();
		edges = new ArrayList<String>();
		
		M = new int [height][width][6];
		
		writeFirstHexagon();
		
		int index = 7;
		int state = 0;
		
		for (int line = 0 ; line < height ; line ++) {
			if (line == 0) {		
				for (int column = 1 ; column < width ; column ++) {
					
					int v1 = index;
					int v2 = index + 1;
					int v3 = index + 2;
					int v4 = index + 3;
					int v5 = (M[line][column - 1][2]);
					int v6 = (M[line][column - 1][1]);
					
					edges.add("e " + v1 + " " + v2 + " \n");
					edges.add("e " + v2 + " " + v3 + " \n");
					edges.add("e " + v3 + " " + v4 + " \n");
					edges.add("e " + v4 + " " + v5 + " \n");
					edges.add("e " + v5 + " " + v6 + " \n");
					edges.add("e " + v6 + " " + v1 + " \n");
					
					hexagons.add("h " + v1 + " " + v2 + " " + v3 + " " + v4 + " " + v5 + " " + v6 + " \n");
					
					M[line][column][0] = v1;
					M[line][column][1] = v2;
					M[line][column][2] = v3;
					M[line][column][3] = v4;
					M[line][column][4] = v5;
					M[line][column][5] = v6;
					
					index += 4;
				}
			}
			
			else {
				if (state == 1) {
					for (int column = 0 ; column < width ; column ++) {
						
						if (column == 0) {
							int v1 = M[line - 1][0][4];
							int v2 = M[line - 1][0][3];
							int v3 = index;
							int v4 = index + 1;
							int v5 = index + 2;
							int v6 = index + 3;
							
							edges.add("e " + v1 + " " + v2 + " \n");
							edges.add("e " + v2 + " " + v3 + " \n");
							edges.add("e " + v3 + " " + v4 + " \n");
							edges.add("e " + v4 + " " + v5 + " \n");
							edges.add("e " + v5 + " " + v6 + " \n");
							edges.add("e " + v6 + " " + v1 + " \n");
							
							hexagons.add("h " + v1 + " " + v2 + " " + v3 + " " + v4 + " " + v5 + " " + v6 + " \n");
							
							M[line][column][0] = v1;
							M[line][column][1] = v2;
							M[line][column][2] = v3;
							M[line][column][3] = v4;
							M[line][column][4] = v5;
							M[line][column][5] = v6;
							
							index += 4;
						}
						
						else {
							
							int v1 = M[line - 1][column][4];
							int v2 = M[line - 1][column][3];
							int v3 = index;
							int v4 = index + 1;
							int v5 = M[line][column - 1][2];
							int v6 = M[line][column - 1][1];
							
							edges.add("e " + v1 + " " + v2 + " \n");
							edges.add("e " + v2 + " " + v3 + " \n");
							edges.add("e " + v3 + " " + v4 + " \n");
							edges.add("e " + v4 + " " + v5 + " \n");
							edges.add("e " + v5 + " " + v6 + " \n");
							edges.add("e " + v6 + " " + v1 + " \n");
							
							hexagons.add("h " + v1 + " " + v2 + " " + v3 + " " + v4 + " " + v5 + " " + v6 + " \n");
							
							M[line][column][0] = v1;
							M[line][column][1] = v2;
							M[line][column][2] = v3;
							M[line][column][3] = v4;
							M[line][column][4] = v5;
							M[line][column][5] = v6;
							
							index += 2;
						}
					}
				}
				
				else {
					
					for (int column = 0 ; column < width ; column ++) {
						
						if (column == width - 1) {
							
							if (column == 0) {
								
								int v1 = M[line - 1][column][2];
								int v2 = index;
								int v3 = index + 1;
								int v4 = index + 2;
								int v5 = index + 3;
								int v6 = M[line - 1][column][3];
								
								edges.add("e " + v1 + " " + v2 + " \n");
								edges.add("e " + v2 + " " + v3 + " \n");
								edges.add("e " + v3 + " " + v4 + " \n");
								edges.add("e " + v4 + " " + v5 + " \n");
								edges.add("e " + v5 + " " + v6 + " \n");
								edges.add("e " + v6 + " " + v1 + " \n");
							
								hexagons.add("h " + v1 + " " + v2 + " " + v3 + " " + v4 + " " + v5 + " " + v6 + " \n");
								
								M[line][column][0] = v1;
								M[line][column][1] = v2;
								M[line][column][2] = v3;
								M[line][column][3] = v4;
								M[line][column][4] = v5;
								M[line][column][5] = v6;
								
								index += 4;
							}
							
							else {
							
								int v1 = M[line - 1][column][2];
								int v2 = index;
								int v3 = index + 1;
								int v4 = index + 2;
								int v5 = M[line][column - 1][2];
								int v6 = M[line][column - 1][1];
							
								edges.add("e " + v1 + " " + v2 + " \n");
								edges.add("e " + v2 + " " + v3 + " \n");
								edges.add("e " + v3 + " " + v4 + " \n");
								edges.add("e " + v4 + " " + v5 + " \n");
								edges.add("e " + v5 + " " + v6 + " \n");
								edges.add("e " + v6 + " " + v1 + " \n");
							
								hexagons.add("h " + v1 + " " + v2 + " " + v3 + " " + v4 + " " + v5 + " " + v6 + " \n");
								
								M[line][column][0] = v1;
								M[line][column][1] = v2;
								M[line][column][2] = v3;
								M[line][column][3] = v4;
								M[line][column][4] = v5;
								M[line][column][5] = v6;
								
								index += 3;
							}
						}	
						
						else {
							int v1 = M[line - 1][column][2];
							int v2 = M[line - 1][column + 1][3];
							int v3 = index;
							int v4 = index + 1;
							int v5;
							
							if (column == 0)
								v5 = index + 2;
							else
								v5 = M[line][column - 1][2];
							
							int v6 = M[line - 1][column][3];
							
							edges.add("e " + v1 + " " + v2 + " \n");
							edges.add("e " + v2 + " " + v3 + " \n");
							edges.add("e " + v3 + " " + v4 + " \n");
							edges.add("e " + v4 + " " + v5 + " \n");
							edges.add("e " + v5 + " " + v6 + " \n");
							edges.add("e " + v6 + " " + v1 + " \n");
							
							hexagons.add("h " + v1 + " " + v2 + " " + v3 + " " + v4 + " " + v5 + " " + v6 + " \n");
							
							M[line][column][0] = v1;
							M[line][column][1] = v2;
							M[line][column][2] = v3;
							M[line][column][3] = v4;
							M[line][column][4] = v5;
							M[line][column][5] = v6;
							
							if (column == 0)
								index += 3;
							else
								index += 2;
						}
					}
				}
			}
			
			state = 1 - state;
		}
		
		for (int i = 0 ; i < edges.size() ; i++) {
			String edge1 = edges.get(i);
			for (int j = (i+1) ; j < edges.size() ; j++) {
				String edge2 = edges.get(j);
				if (edgesEquals(edge1, edge2)) {
					edges.remove(j);
					j --;
				}
			}
		}
		
		writer.write("p DIMACS " + (index-1) + " " + edges.size() + " " + (width * height) + " \n");
		
		for(String edge : edges)
			writer.write(edge);
		
		for (String hexagon : hexagons)
			writer.write(hexagon);
	
		writer.close();
	} 
	
	public static void main(String[] args) throws IOException {
		
		for (int nbLines = 1 ; nbLines <= 10 ; nbLines ++) {
			for (int nbColumns = 1 ; nbColumns <= 20 ; nbColumns ++) {
				
				if (nbLines == 3 && nbColumns == 1)
					System.out.print("");
				
				File directory = new File("/Users/adrien/Documents/molecules/rectangles/" + nbLines + "_lines");
				directory.mkdir();
				
				String outputFileName = "/Users/adrien/Documents/molecules/rectangles/" +
										  nbLines + "_lines/rectangle_" + nbLines + "_" + nbColumns + ".graph";
				
				generateRectangleBenzenoid(nbColumns, nbLines, outputFileName);
			}
		}
		
	}
}
