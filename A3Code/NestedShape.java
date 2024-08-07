/*
 *	===============================================================================
 *	NestedShape.java : A shape that has other shapes within it.
 *  YOUR UPI: jpag513
 * 
 *	=============================================================================== */
import java.awt.*;
import java.awt.Color;
import java.util.*;


class NestedShape extends RectangleShape{
    private ArrayList<Shape> innerShapes = new ArrayList<Shape>(); ;
    public Shape createInnerShape(PathType pt, ShapeType st){
        Shape innerShape = null;
        if (st == ShapeType.RECTANGLE){
            innerShape = new RectangleShape(0,0,this.width/5,this.height/5,this.width,this.height, this.color, this.borderColor, pt);
        }if (st == ShapeType.OVAL){
            innerShape = new OvalShape(0,0,this.width/5,this.height/5,this.width,this.height, this.color, this.borderColor, pt);
        }if (st == ShapeType.NESTED){
            innerShape = new NestedShape(0,0,this.width/5,this.height/5,this.width,this.height, this.color, this.borderColor, pt);
        }innerShape.setParent(this);
        innerShapes.add(innerShape);
        return innerShape;
    }
    public NestedShape(){
        super();
        createInnerShape(PathType.BOUNCING, ShapeType.RECTANGLE);
    }
    public NestedShape(int x, int y, int w, int h, int mw, int mh, Color c, Color bc, PathType pt){
        super(x, y, w, h, mw, mh, c, bc, pt);
        
        createInnerShape(PathType.BOUNCING, ShapeType.RECTANGLE);
    }
    public NestedShape(int w, int h){
        super(0, 0, w, h, Shape.DEFAULT_PANEL_WIDTH , Shape.DEFAULT_PANEL_HEIGHT, Shape.DEFAULT_COLOR, Shape.DEFAULT_BORDER_COLOR, PathType.BOUNCING);
        
    }
    public Shape getInnerShapeAt(int index){
        return innerShapes.get(index);
    }
    public int getSize(){return innerShapes.size();}
    public void draw(Graphics g){
        g.setColor(Color.BLACK);
        g.drawRect(this.x,this.y,this.width,this.height);
        g.translate(x,y);
        for (int i=0; i<innerShapes.size();i++){
                innerShapes.get(i).draw(g);
                innerShapes.get(i).drawHandles(g);
                innerShapes.get(i).drawString(g);
        }
            g.translate(-x, -y);
        
        
    }
    public void move(){
        super.move();
        for (int i = 0; i< innerShapes.size();i++){
            innerShapes.get(i).move();
        }
    }
    public int indexOf(Shape s){
        return innerShapes.indexOf(s);
    }
    public void addInnerShape(Shape s){
        s.setParent(this);
        innerShapes.add(s);
        
    }
    public void removeInnerShape(Shape s){
        s.setParent(null);
        innerShapes.remove(s);
        
    }
    public void removeInnerShapeAt(int index){
        if (index >= 0 && index < innerShapes.size()) {
            Shape removedShape = innerShapes.get(index);
            removedShape.setParent(null);
            innerShapes.remove(index);
        }
    }
    public ArrayList<Shape> getAllInnerShapes(){
        return innerShapes;
    }

}