package solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;

import constraint.AllDiffConstraint;
import constraint.BinaryConstraint;
import constraint.UnaryConstraint;
import datastructs.Edge;
import datastructs.Tupel;
import datastructs.Vertex;

public class AC3_LA {

	private UndirectedGraph<Vertex, Edge<Vertex>> constraintNetz = null;
	private Vertex assumptionVertex;
	private int assumptionValue;
	private Set<Edge<Vertex>> constSet;

	public AC3_LA(UndirectedGraph<Vertex, Edge<Vertex>> g, Set<Edge<Vertex>> constSet) {
		constraintNetz = g;
		this.constSet = constSet;
	}

	public boolean ac3_la_procedure(Vertex assumptionVertex, int assumptionValue) {
		int cv = assumptionVertex.getId();
		this.assumptionVertex = assumptionVertex;
		this.assumptionValue = assumptionValue;
		
//		Set<Integer> s = new HashSet<Integer>();
//		s.add(assumptionValue);
//		assumptionVertex.setDomain(s);

		boolean consistent = true;
		Set<Edge<Vertex>> q = new HashSet<Edge<Vertex>>();
//		List<Edge<Vertex>> neighborsOfCv = new ArrayList<Edge<Vertex>>(constraintNetz.edgesOf(assumptionVertex));

		List<Edge<Vertex>> neighborsOfCv = rightNeighborList(constraintNetz.edgesOf(assumptionVertex), assumptionVertex);
		/**
		 * Menge Q erzeugen Hierbei muss unterschieden werden, an welchem Ende
		 * der Kante der Knoten vK ist
		 */
		for (Edge<Vertex> item : neighborsOfCv) {
			if (/*((Vertex) item.getV2()).getId() == cv && */((Vertex) item.getV1()).getId() > cv)
				q.add(item);
//			else if (((Vertex) item.getV1()).getId() == cv && ((Vertex) item.getV2()).getId() > cv) {
//				q.add(new Edge<Vertex>(((Vertex) item.getV2()), ((Vertex) item.getV1()), item.getConstraintList()));
//			}
		}
		Vertex vK = null;

		while (!q.isEmpty() && consistent) {
//			 System.out.println("Q: " + q.toString());
			
//			Edge<Vertex> arc = chooseArc(q);
			 
			Edge<Vertex> arc = q.iterator().next();
			q.remove(arc);
			
			System.out.println("gewahlte arc: " + arc);

			if (revise(arc)) {
				vK = ((Vertex) arc.getV1());
				int k = Integer.valueOf(vK.getId());
				int m = Integer.valueOf(((Vertex) arc.getV2()).getId());
				int i = -1;
				List<Edge<Vertex>> neighborsOfK = rightNeighborList(constraintNetz.edgesOf(vK), vK);

				System.out.println("neighbors: " + neighborsOfK);

				for (Edge<Vertex> item : neighborsOfK) {
					i = ((Vertex) item.getV1()).getId();
					if (i != k && i != m && i > cv) {
						System.out.println(item);
						
//						q.add(item);
						consistent = !vK.getDomain().isEmpty();
					}
				}
			}
		}

		return consistent;
	}

//	private Edge<Vertex>chooseArc(Set<Edge<Vertex>> q){
//		Edge<Vertex> arc = null;
//		
//		for(Edge<Vertex> item : q){
////			System.out.println("arc: " + item);
//			if(constSet.contains(item)){
//				arc = item;
//				break;
//			}
//		}
//		if(arc == null)
//			arc = q.iterator().next();
//
////		q.remove(arc);
//		return arc;
//	}
	
	private List<Edge<Vertex>> rightNeighborList(Set<Edge<Vertex>> set, Vertex vk) {
		List<Edge<Vertex>> newList = new ArrayList<Edge<Vertex>>();

		for (Edge<Vertex> item : set) {
			if (item.getV1().equals(vk))
				newList.add(new Edge<Vertex>(item.getV2(), item.getV1(), item.getConstraintList()));
			else
				newList.add(item);
		}

		return newList;
	}

	private boolean revise(Edge<Vertex> arc) {
		boolean delete = false;
		Set<Integer> delSet = new HashSet<Integer>();
		Set<Integer> domY = new HashSet<Integer>();

		Set<Integer> domX = null;

		// Unaere Constraints der Knoten pruefen
		List<UnaryConstraint> constraintListU = (List<UnaryConstraint>) ((Vertex) arc.getV1()).getUnaryConstraintList();
		if (constraintListU.isEmpty())
			domX = new HashSet<Integer>(((Vertex) arc.getV1()).getDomain());
		else {
			domX = new HashSet<Integer>();
			for (UnaryConstraint constraint : constraintListU) {
				domX.addAll(constraint.operationUnary(((Vertex) arc.getV1()).getId()));
				delete = true;
			}
		}

		// Ist der Annahmeknoten der Nachbar des zu beschraenkenden Knoten,
		// muss der Annahmewert des Annahmeknoten verwendet werden
		if (((Vertex) arc.getV2()).equals(assumptionVertex))
			domY.add(assumptionValue);
		else
			domY = ((Vertex) arc.getV2()).getDomain();

		List<BinaryConstraint> constraintListB = (List<BinaryConstraint>) arc.getConstraintList();
		// Im Vorfeld AllDifferent Constraint abarbeiten
		for (BinaryConstraint constraint : constraintListB) {
			if (constraint instanceof AllDiffConstraint) {
				if (domX.removeAll(constraint.operationBinaryAllDiff(domX, domY)))
					delete = true;
			}
		}

		for (int x : domX) {
			for (BinaryConstraint constraint : constraintListB) {
				if (!(constraint instanceof AllDiffConstraint)) {
					List<Tupel> crossProduct = generateCrossProduct(x, domY);

					// Iteration ueber die Menge Dj erfolgt in
					// checkConstraints
					if (checkTupelWithConstraints(crossProduct, arc.getConstraintList())) {
						delete = true;
						delSet.add(x);
					}

				}
			}

		}

		// Zu loeschende Menge erzeugen und die Domaene des jeweiligen Knoten
		// anpassen
		Set<Integer> newSet = new HashSet<Integer>(domX);
		newSet.removeAll(delSet);
		((Vertex) arc.getV1()).setDomain(newSet);

		return delete;
	}

	private List<Tupel> generateCrossProduct(Integer x, Set<Integer> valueSet) {
		List<Tupel> crossProduct = new ArrayList<Tupel>();

		for (Integer y : valueSet) {
			crossProduct.add(new Tupel(x, y));
		}

		return crossProduct;
	}

	private boolean checkTupelWithConstraints(List<Tupel> crossProduct, List<BinaryConstraint> constraintList) {
		int counter = 0;

		for (Tupel item : crossProduct) {
			for (BinaryConstraint constraint : constraintList) {
				if (!constraint.operationBinary(item.getX(), item.getY()))
					counter++;
			}
		}

		return counter == crossProduct.size() ? true : false;
	}

	public UndirectedGraph<Vertex, Edge<Vertex>> getConstraintNetz() {
		return constraintNetz;
	}

	public void setConstraintNetz(UndirectedGraph<Vertex, Edge<Vertex>> constraintNetz) {
		this.constraintNetz = constraintNetz;
	}

}
