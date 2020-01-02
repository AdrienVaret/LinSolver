package utils;

import graphs.Node;
import solver.BenzenoidsSolver.Direction;

public class NodeComputer {

	//n2 à droite de n1
	public static boolean isAtRight(Node n1, Node n2) {
		return (n2.getX() == n1.getX() + 2 && n2.getY() == n1.getY());
	}
	
	public static boolean isAtDownRight(Node n1, Node n2) {
		return (n2.getX() == n1.getX() + 1 && n2.getY() == n1.getY() + 2);
	}
	
	public static boolean isAtDownLeft(Node n1, Node n2) {
		return (n2.getX() == n1.getX() - 1 && n2.getY() == n1.getY() + 2);
	}
	
	public static boolean isAtLeft(Node n1, Node n2) {
		return (n2.getX() == n1.getX() - 2 && n2.getY() == n1.getY());
	}
	
	public static boolean isAtHighLeft(Node n1, Node n2) {
		return (n2.getX() == n1.getX() - 1 && n2.getY() == n1.getY() - 2);
	}
	
	public static boolean isAtHighRight(Node n1, Node n2) {
		return (n2.getX() == n1.getX() + 1 && n2.getY() == n1.getY() - 2);
	}
	
	public static Node computeInternalNode(Direction direction, Node u, Node v, Node w, Node x) {
		
		/**
		 * Cas 1
		 */
		if (isAtRight(u,v)) {
			
			if (isAtHighLeft(v, w)) {
				if (direction == Direction.HORAIRE)
					return x;
				else 
					return null;
			}
			
			else if (isAtHighRight(v, w)) {
				if (direction == Direction.HORAIRE) {
					if (isAtHighLeft(v, x))
						return null;
					else 
						return x;
				}
				
				else {
					if (!isAtHighLeft(v, x))
						return null;
					else 
						return x;
				}
			}
			
			else if (isAtRight(v, w)) {
				
				if (direction == Direction.HORAIRE) {
					if (isAtDownLeft(v, x) || isAtDownRight(v,x))
						return x;
					else 
						return null;
				}
				
				else {
					if (!(isAtDownLeft(v, x) || isAtDownRight(v,x)))
						return x;
					else 
						return null;
				}
			}
			
			else if (isAtDownRight(v,w)) {
				
				if (direction == Direction.HORAIRE) {
					if (isAtDownLeft(v, x))
						return x;
					else 
						return null;
				} else {
					if (!isAtDownLeft(v, x))
						return x;
					else 
						return null;
				}
			}
			
			else if (isAtDownLeft(v,w)) {
				if (direction == Direction.HORAIRE)
					return null;
				else 
					return x;
			}
		}
		
		
		
		
		/**
		 * Cas 2
		 */
		else if (isAtDownRight(u, v)) {
			
			if (isAtHighRight(v, w)) {
				if (direction == Direction.HORAIRE)
					return x;
				else return null;
			}
			
			if (isAtRight(v, w)) {
				if (direction == Direction.HORAIRE) {
					if (!isAtHighRight(v, x))
						return x;
					else 
						return null;
				}
				
				else {
					if (isAtHighRight(v, x))
						return x;
					else 
						return null;
				}
					
			}
			
			if (isAtDownRight(v, w)) {
				if (direction == Direction.HORAIRE) {
					if (!(isAtHighRight(v, x) || isAtRight(v,x)))
						return x;
					else 
						return null;
				}
				
				else {
					if ((isAtHighRight(v, x) || isAtRight(v,x)))
						return x;
					else 
						return null;
				}
			}
			
			if (isAtDownLeft(v, w)) {
				if (direction == Direction.HORAIRE) {
					if (isAtLeft(v,x))
						return x;
					else 
						return null;
				} 
				
				else {
					if (!isAtLeft(v,x))
						return x;
					else 
						return null;
				}
			}
			
			if (isAtLeft(v, w)) {
				if (direction == Direction.HORAIRE)
					return null;
				else 
					return x;
			}
		}
		
		
		/**
		 * Cas 3 
		 */
		if (isAtDownLeft(u, v)) {
			
			if (isAtRight(v, w)) {
				if (direction == Direction.HORAIRE)
					return x;
				else 
					return null;
			}
			
			else if (isAtDownRight(v, w)) {
				if (direction == Direction.HORAIRE) {
					if (!isAtRight(v,x))
						return x;
					else 
						return null;
				}
				
				else {
					if (isAtRight(v,x))
						return x;
					else 
						return null;
				}
			}
			
			else if (isAtDownLeft(v, w)) {
				
				if (direction == Direction.HORAIRE) {
					if (!(isAtRight(v,x) || isAtDownRight(v,x))) {
						return x;
					}
					
					else
						return null;
				}
				
				else {
					if ((isAtRight(v,x) || isAtDownRight(v,x))) {
						return x;
					}
					
					else
						return null;
				}
			}
			
			else if (isAtLeft(v, w)) {
				if (direction == Direction.HORAIRE) {
					if (isAtHighLeft(v,x)) {
						return x;
					}
					else
						return null;
				}
				
				else {
					if (!isAtHighLeft(v,x)) {
						return x;
					}
					else
						return null;
				}
			}
			
			else if (isAtHighLeft(v,w)) {
				if (direction == Direction.HORAIRE)
					return null;
				else
					return x;
			}
		}
		
		
		
		
		/**
		 * Cas 4
		 */
		if (isAtLeft(u,v)) {
			
			if (isAtDownRight(v,w)) {
				if (direction == Direction.HORAIRE)
					return x;
				else 
					return null;
			}
			
			else if (isAtDownLeft(v,w)) {
				if (direction == Direction.HORAIRE) {
					if (!isAtDownRight(v,x))
						return x;
					else 
						return null;
				}
				
				else {
					if (isAtDownRight(v,x))
						return x;
					else 
						return null;
				}
			}
			
			else if (isAtLeft(v,w)) {
				if (direction == Direction.HORAIRE) {
					if ((isAtHighLeft(v,x) || isAtHighRight(v,x)))
						return x;
					else 
						return null;
				}
				
				else {
					if (!(isAtHighLeft(v,x) || isAtHighRight(v,x)))
						return x;
					else 
						return null;
				}
			}
			
			else if (isAtHighLeft(v,w)) {
				if (direction == Direction.HORAIRE) {
					if (isAtHighRight(v,x))
						return x;
					else 
						return null;
				}
				
				else {
					if (!isAtHighRight(v,x))
						return x;
					else 
						return null;
				}		
			}
			
			else if (isAtHighRight(v,w)) {
				if (direction == Direction.HORAIRE)
					return null;
				else
					return x;
			}
		}
		
		
		
		/**
		 * Cas 5
		 */
		if (isAtHighLeft(u,v)) {
			
			if (isAtDownLeft(v,w)) {
				if (direction == Direction.HORAIRE)
					return x;
				else 
					return null;
			}
			
			else if (isAtLeft(v,w)) {
				if (direction == Direction.HORAIRE) {
					if (!isAtDownLeft(v,x))
						return x;
					else return null;
				}
				
				else {
					if (isAtDownLeft(v,x))
						return x;
					else 
						return null;
				}
			}
			
			else if (isAtHighLeft(v,w)) {
				if (direction == Direction.HORAIRE) {
					if ((isAtHighRight(v,x) || isAtRight(v,x)))
						return x;
					else return null;
				}
				
				else {
					if (!(isAtHighRight(v,x) || isAtRight(v,x)))
						return x;
					else return null;
				}
			}
			
			else if (isAtHighRight(v,w)) {
				if (direction == Direction.HORAIRE) {
					if (isAtRight(v,x))
						return x;
					else
						return null;
				}
				
				else {
					if (!isAtRight(v,x))
						return x;
					else
						return null;
				}
			}
			
			else if (isAtRight(v,w)) {
				if (direction == Direction.HORAIRE)
					return null;
				else
					return x;
			}
		}
		
		
		
		/**
		 * Cas 6
		 */
		if (isAtHighRight(u,v)) {
			
			if (isAtLeft(v,w)) {
				if (direction == Direction.HORAIRE)
					return x;
				else 
					return null;
			}
			
			else if (isAtHighLeft(v,w)) {
				
				if (direction == Direction.HORAIRE) {
					if (!isAtLeft(v,x))
						return x;
					else
						return null;
				}
				
				else {
					if (isAtLeft(v,x))
						return x;
					else
						return null;
				}
			}
			
			else if (isAtHighRight(v,w)) {
				
				if (direction == Direction.HORAIRE) {
					if ((isAtRight(v,x) || isAtDownRight(v,x)))
						return x;
					else 
						return null;
				}
				
				else {
					if (!(isAtRight(v,x) || isAtDownRight(v,x)))
						return x;
					else 
						return null;
				}
			}
			
			else if (isAtRight(v,w)) {
				if (direction == Direction.HORAIRE) {
					if (isAtDownRight(v,x))
						return x;
					else 
						return null;
				}
				
				else {
					if (!isAtDownRight(v,x))
						return x;
					else 
						return null;
				}
			}
			
			else if (isAtDownRight(v,w)) {
				if (direction == Direction.HORAIRE)
					return null;
				else
					return x;
			}
		}
		
		
		return null;
	}
}
