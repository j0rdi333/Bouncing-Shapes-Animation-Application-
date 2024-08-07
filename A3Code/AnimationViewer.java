/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 * YOUR UPI: jpag513
 * ==========================================================================================
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListDataListener;
import java.lang.reflect.Field;

class AnimationViewer extends JComponent implements Runnable,TreeModel {
	protected NestedShape root;
	private Thread animationThread = null; // the thread for animation
	private static int DELAY = 120; // the current animation speed
	private ShapeType currentShapeType = Shape.DEFAULT_SHAPETYPE; // the current shape type,
	private PathType currentPathType = Shape.DEFAULT_PATHTYPE; // the current path type
	private Color currentColor = Shape.DEFAULT_COLOR; // the current fill colour of a shape
	private Color currentBorderColor = Shape.DEFAULT_BORDER_COLOR;
	private int currentPanelWidth = Shape.DEFAULT_PANEL_WIDTH, currentPanelHeight = Shape.DEFAULT_PANEL_HEIGHT,currentWidth = Shape.DEFAULT_WIDTH, currentHeight = Shape.DEFAULT_HEIGHT;
	private String currentLabel = Shape.DEFAULT_LABEL;
	private ArrayList<TreeModelListener> treeModelListeners;
	protected DefaultListModel<Shape> listModel;

	public AnimationViewer() {
		start();
		treeModelListeners = new ArrayList<TreeModelListener>();
		listModel = new DefaultListModel<Shape>();
		root = new NestedShape(Shape.DEFAULT_PANEL_WIDTH,Shape.DEFAULT_PANEL_HEIGHT);
	}

	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Shape currentShape : root.getAllInnerShapes()) {
			currentShape.move();
			currentShape.draw(g);
			currentShape.drawHandles(g);
			currentShape.drawString(g);
		}
	}
	public void resetMarginSize() {
		currentPanelWidth = getWidth();
		currentPanelHeight = getHeight();
		for (Shape currentShape : root.getAllInnerShapes())
			currentShape.resetPanelSize(currentPanelWidth, currentPanelHeight);
	}


	// you don't need to make any changes after this line ______________
	public String getCurrentLabel() {return currentLabel;}
	public int getCurrentHeight() { return currentHeight; }
	public int getCurrentWidth() { return currentWidth; }
	public Color getCurrentColor() { return currentColor; }
	public Color getCurrentBorderColor() { return currentBorderColor; }
	public void setCurrentShapeType(ShapeType value) {currentShapeType = value;}
	public void setCurrentPathType(PathType value) {currentPathType = value;}
	public ShapeType getCurrentShapeType() {return currentShapeType;}
	public PathType getCurrentPathType() {return currentPathType;}
	public NestedShape getRoot(){return root;}
	public boolean isLeaf(Object node){
		return (!(node instanceof NestedShape));
	}
	public boolean isRoot(Shape selectedNode){
		return root == selectedNode;
	}
	public Object getChild(Object parent, int index){
		if (!isLeaf(parent)){
			if(index> ((NestedShape)parent).getAllInnerShapes().size() || index < 0){
				return null;
			}return ((NestedShape)parent).getAllInnerShapes().get(index);
		}return null;
	}
	public int getChildCount(Object parent){
		if(!isLeaf(parent)){
			return ((NestedShape)parent).getAllInnerShapes().size();
		}return 0;
	}
	public int getIndexOfChild(Object parent, Object child){
		if(!isLeaf(parent)){
			return ((NestedShape)parent).getAllInnerShapes().indexOf(child);
		}return -1;
	}
	public void addTreeModelListener(final TreeModelListener tml){
		treeModelListeners.add(tml);
	}
	public void removeTreeModelListener(final TreeModelListener tml){
		treeModelListeners.remove(tml);
	}
	public void valueForPathChanged(TreePath path, Object newValue){}
	public void  fireTreeNodesInserted(Object source, Object[] path,int[] childIndices,Object[] children) {
		TreeModelEvent tme = new TreeModelEvent(source, path, childIndices, children);
		for(TreeModelListener x: treeModelListeners){
			x.treeNodesInserted(tme);
		}System.out.printf("Called fireTreeNodesInserted: path=%s, childIndices=%s, children=%s\n", Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));

	}
	public void addShapeNode(NestedShape selectedNode){
		Shape s = selectedNode.createInnerShape(currentPathType, currentShapeType);
		listModel.addElement(s);
		Object[] newChild = {s};
		int[] ind = {selectedNode.indexOf(s)};
		fireTreeNodesInserted(this, selectedNode.getPath(), ind,newChild);
	}
	public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,Object[] children){
		TreeModelEvent tme = new TreeModelEvent(source, path, childIndices, children);
		for(TreeModelListener x: treeModelListeners){
			x.treeNodesRemoved(tme);
		}System.out.printf("Called fireTreeNodesRemoved: path=%s, childIndices=%s, children=%s\n", Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));
	}
	public void removeNodeFromParent(Shape selectedNode){
		NestedShape parent = selectedNode.getParent();
		int ind = parent.indexOf(selectedNode);
		parent.removeInnerShape(selectedNode);
		int[] indarr = {ind};
		Object[] children = {selectedNode};
		fireTreeNodesRemoved(this, parent.getPath(), indarr, children);

	}
	public void reload(Shape selectedNode){
		if(selectedNode instanceof NestedShape){
			listModel.clear();
			for(Shape s: ((NestedShape)selectedNode).getAllInnerShapes()){
				listModel.addElement(s);
			}
		}
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	public void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}
	public void stop() {
		if (animationThread != null) {
			animationThread = null;
		}
	}
	public void run() {
		Thread myThread = Thread.currentThread();
		while (animationThread == myThread) {
			repaint();
			pause(DELAY);
		}
	}
	private void pause(int milliseconds) {
		try {
			Thread.sleep((long) milliseconds);
		} catch (InterruptedException ie) {}
	}
}
