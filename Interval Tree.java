package structures;

import java.util.*;

/**
 * Encapsulates an interval tree.
 * 
 * @author mitraakash
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		Sorter.sortIntervals(intervalsLeft, 'l');
		Sorter.sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = Sorter.getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		// COMPLETE THIS METHOD
		Queue<IntervalTreeNode> treeQ = new Queue<IntervalTreeNode>();
		
		for (int x : endPoints){
			IntervalTreeNode temp = new IntervalTreeNode((float)x,(float)x,(float)x);
			temp.leftIntervals = new ArrayList<Interval>();
			temp.rightIntervals = new ArrayList<Interval>();
			treeQ.enqueue(temp);
		}
		
		//System.out.println("endpoint leaves: ");
		for (int i=0; i<treeQ.size; i++){
			IntervalTreeNode x = treeQ.dequeue();
			//System.out.println(x.splitValue);
			treeQ.enqueue(x);
		}
		int sizeQ = treeQ.size;
		IntervalTreeNode toReturn = null;
		
		while (sizeQ > 0){
			if (sizeQ == 1){
				toReturn = treeQ.dequeue();
				return toReturn;
			}
			else {
				int temps = sizeQ;
				while (temps > 1){
					IntervalTreeNode T1 = treeQ.dequeue();
					IntervalTreeNode T2 = treeQ.dequeue();
					float v1 = T1.maxSplitValue;
					float v2 = T2.minSplitValue;
					float x = (v1+v2)/(2);
					IntervalTreeNode N = new IntervalTreeNode(x, T1.minSplitValue, T2.maxSplitValue);
					N.leftIntervals = new ArrayList<Interval>();
					N.rightIntervals = new ArrayList<Interval>();
					N.leftChild = T1;
					N.rightChild = T2;
					treeQ.enqueue(N);
					temps = temps-2;
				}
				if (temps == 1){
					IntervalTreeNode temporary = treeQ.dequeue();
					treeQ.enqueue(temporary);
				}
				sizeQ = treeQ.size;
			}
		}
		toReturn = treeQ.dequeue();
		return toReturn;
	}
		
	private void mapHelp(Interval x, IntervalTreeNode theRoot, boolean isLeft){
		if (x.contains(theRoot.splitValue)){
			if (isLeft){
				theRoot.leftIntervals.add(x);
			}
			else {
				theRoot.rightIntervals.add(x);
			}
			return;
		}
		
		if (theRoot.splitValue < x.leftEndPoint){
			mapHelp(x, theRoot.rightChild, isLeft);
		}
		else {
			mapHelp(x, theRoot.leftChild, isLeft);
		}
		
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
//		root.leftIntervals = new ArrayList<Interval>();
//		root.rightIntervals = new ArrayList<Interval>();
		
		//left
		for (Interval x : leftSortedIntervals){
			mapHelp(x, root, true);
		}
		for (Interval x : rightSortedIntervals){
			mapHelp(x, root, false);
		}
		return;
	}
		
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		// COMPLETE THIS METHOD
		return helpFind(root, q);
	}
	
	private ArrayList<Interval> helpFind(IntervalTreeNode R, Interval q){
		ArrayList<Interval> resultant = new ArrayList<Interval>();

		if (R == null){
		//	System.out.println("Null resultant: " + resultant);
			return resultant;
		}
		
		float splitVal = R.splitValue;
		ArrayList<Interval> rlist = R.rightIntervals;
		ArrayList<Interval> llist = R.leftIntervals;
		if(rlist == null) System.out.println("Node's right is null");
		if(llist == null) System.out.println("Node's left is null");
		IntervalTreeNode lsub = R.leftChild;
		IntervalTreeNode rsub = R.rightChild;
				
		if (q.contains(splitVal)){
			for (Interval x : llist){
				resultant.add(x);
			}
		//	System.out.println("Going to both. Split val: " + splitVal + " resultant: " + resultant);
		//	System.out.println("Right first!");
			resultant.addAll(helpFind(rsub, q));
		//	System.out.println("Left first!");
			resultant.addAll(helpFind(lsub, q));
		//	System.out.println("Done with both.");
		}
		
		else if (splitVal < q.leftEndPoint){
		//	System.out.println("Going to right. Split val: " + splitVal + " resultant: " + resultant);
			int i = rlist.size()-1;
			while (i >= 0 && (rlist.get(i).intersects(q))){
				resultant.add(rlist.get(i));
				i = i-1;
			}
			
			resultant.addAll(helpFind(rsub, q));
		}
		
		else if (splitVal > q.rightEndPoint){
			//System.out.println("Going to left. Split val: " + splitVal + " resultant: " + resultant);
			int i = 0;
			while ((i < llist.size()) && (llist.get(i).intersects(q))){
				resultant.add(llist.get(i));						
				i = i+1;
			}
			resultant.addAll(helpFind(lsub, q));
		}
	//	System.out.println("Resultant at bottom: " + resultant);
		return resultant;
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
}
