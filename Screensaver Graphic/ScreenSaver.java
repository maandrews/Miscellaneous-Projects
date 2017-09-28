/*
 * Screensaver like program using simple lines with continuous boundary conditions
 *
 * maandrews 2017.
 */

import java.awt.Graphics;
import java.awt.Color;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.*;

public class ScreenSaver extends JFrame {
	
	/*
	 * Line class
	 * params x1,y1 starting point of line.
	 * params x2,y2 ending point of line.
	 * params c1,c2,c3 determine the color of the line. 
	 */
	private class Line{
		int x1, y1, x2, y2, c1, c2, c3;
		public Line(int x1,int y1,int x2,int y2, int c1, int c2, int c3){
			this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
			this.c1 = c1; this.c2 = c2; this.c3 = c3;
		}
	}
	
	// Number of lines visible for each sequence
	private final int QSIZE = 50;
	
	// Resolution
	private final int YRES = 1060;
	private final int XRES = 1900;
	Deque<Line> q1, q2;
	
	public ScreenSaver(){
		
		q1 = new LinkedList<Line>();
		q2 = new LinkedList<Line>();
		
		setBackground(Color.BLACK);
		setSize(XRES,YRES);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
	}
	
	public void paint(Graphics g){
		Random r = new Random();
		
		int c1 = r.nextInt(256), c2 = r.nextInt(256), c3= r.nextInt(256);
		
		int d = r.nextBoolean() ? -1 : 1;
		int nX = XRES/2 + d*r.nextInt(100);
		d = r.nextBoolean() ? -1 : 1;
		int nY = YRES/2 + d*r.nextInt(100);
		Line first = new Line(XRES/2, YRES/2, nX, nY, c1, c2, c3);
		q1.add(first);
		
		c1 = r.nextInt(256); c2 = r.nextInt(256); c3= r.nextInt(256);
		
		Line first2 = new Line(XRES/2, YRES/2, nX, nY, c1 ,c2 , c3);
		q2.add(first2);
		
		while(true){
			// First sequence
			c1 = r.nextInt(256); c2 = r.nextInt(256); c3= r.nextInt(256);
			g.setColor(new Color(c1, c2, c3));
			
			Line l = q1.peekLast();
			
			int diff = r.nextBoolean() ? -1 : 1;
			int nextX = (l.x2 + diff*r.nextInt(100));
			
			diff = r.nextBoolean() ? -1 : 1;
			int nextY = (l.y2 + diff*r.nextInt(100));
			
			if(nextX < 0 || nextX > XRES || nextY < 0 || nextY > YRES){
				int rise = nextY - l.y2;
				int run = nextX - l.x2;
				
				if(nextX < 0){nextX += XRES;}
				else if(nextX > XRES){nextX %= XRES;}
				
				if(nextY < 0){nextY += YRES;}
				else if(nextY > YRES){nextY %= YRES;}
				
				int div = ScreenSaver.gcd(Math.abs(rise), Math.abs(run));
				rise /= div; run /= div;
				
				int startX = l.x2, startY = l.y2;
				int curX = startX, curY = startY;
				for(int i = 0 ; i < div; i++){
					int endX = -1, endY = -1;
					curX += run; curY += rise;
					
					if(curX < 0){endX = 0;}
					else if(curX > XRES){endX = XRES;}
					
					if(curY < 0){endY = 0;}
					else if(curY > YRES){endY = YRES;}
					
					if(endX != -1 || endY != -1){
						curX = endX == -1 ? curX : endX;
						curY = endY == -1 ? curY : endY;
						Line section = new Line(startX, startY, curX, curY, c1, c2, c3);
						g.drawLine(section.x1, section.y1, section.x2, section.y2);
						q1.addLast(section);
						if(endX != -1){startX = endX == 0 ? XRES : 0;}
						else{startX = curX;}
						if(endY != -1){startY = endY == 0 ? YRES : 0;}
						else{startY = curY;}
						curX = startX; curY = startY;
					}
				}
				Line section = new Line(startX, startY, nextX, nextY, c1, c2, c3);
				g.drawLine(section.x1, section.y1, section.x2, section.y2);
				q1.addLast(section);
			}
			else{
				Line next = new Line(l.x2, l.y2, nextX, nextY, c1, c2, c3);
				g.drawLine(next.x1, next.y1, next.x2, next.y2);

				q1.addLast(next);
			}
			while(q1.size() >= QSIZE){
				l = q1.removeFirst();
				g.setColor(Color.BLACK);
				g.drawLine(l.x1, l.y1, l.x2, l.y2);
			}
			for(int i = 0 ; i < q1.size(); i++){
				l = q1.removeFirst();
				g.setColor(new Color(l.c1, l.c2, l.c3));
				g.drawLine(l.x1, l.y1, l.x2, l.y2);
				q1.addLast(l);
			}
			
			
			// Second sequence
			c1 = r.nextInt(256); c2 = r.nextInt(256); c3= r.nextInt(256);
			g.setColor(new Color(c1,c2,c3));
			
			l = q2.peekLast();
			
			diff = r.nextBoolean() ? -1 : 1;
			nextX = (l.x2 + diff*r.nextInt(100));
			
			diff = r.nextBoolean() ? -1 : 1;
			 nextY = (l.y2 + diff*r.nextInt(100));
			
			if(nextX < 0 || nextX > XRES || nextY < 0 || nextY > YRES){
				int rise = nextY - l.y2;
				int run = nextX - l.x2;
				
				if(nextX < 0){nextX += XRES;}
				else if(nextX > XRES){nextX %= XRES;}
				
				if(nextY < 0){nextY += YRES;}
				else if(nextY > YRES){nextY %= YRES;}
				
				int div = ScreenSaver.gcd(Math.abs(rise), Math.abs(run));
				rise /= div; run /= div;
				
				int startX = l.x2, startY = l.y2;
				int curX = startX, curY = startY;
				for(int i = 0 ; i < div; i++){
					int endX = -1, endY = -1;
					curX += run; curY += rise;
					
					if(curX < 0){endX = 0;}
					else if(curX > XRES){endX = XRES;}
					
					if(curY < 0){endY = 0;}
					else if(curY > YRES){endY = YRES;}
					
					if(endX != -1 || endY != -1){
						curX = endX == -1 ? curX : endX;
						curY = endY == -1 ? curY : endY;
						Line section = new Line(startX, startY, curX, curY, c1, c2, c3);
						g.drawLine(section.x1, section.y1, section.x2, section.y2);
						q2.addLast(section);
						if(endX != -1){startX = endX == 0 ? XRES : 0;}
						else{startX = curX;}
						if(endY != -1){startY = endY == 0 ? YRES : 0;}
						else{startY = curY;}
						curX = startX; curY = startY;
					}
				}
				Line section = new Line(startX, startY, nextX, nextY, c1, c2 ,c3);
				g.drawLine(section.x1, section.y1, section.x2, section.y2);
				q2.addLast(section);
			}
			else{
				Line next = new Line(l.x2, l.y2, nextX, nextY, c1, c2, c3);
				g.drawLine(next.x1, next.y1, next.x2, next.y2);

				q2.addLast(next);
			}
			while(q2.size() >= QSIZE){
				l = q2.removeFirst();
				g.setColor(Color.BLACK);
				g.drawLine(l.x1, l.y1, l.x2, l.y2);
			}
			for(int i = 0 ; i < q1.size(); i++){
				l = q2.removeFirst();
				g.setColor(new Color(l.c1, l.c2, l.c3));
				g.drawLine(l.x1, l.y1, l.x2, l.y2);
				q2.addLast(l);
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void main(String[] args){
		ScreenSaver s = new ScreenSaver();
	}
	
	// Finds greatest common divisor
	static int gcd(int x, int y){
		while(x!= 0 && y != 0){
			int z = y;
			y = x % y;
			x = z;	
		}
		return x+y;
	}

}

