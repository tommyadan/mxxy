package com.mxxy.game.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.mxxy.game.sprite.Sprite;
import com.mxxy.game.utils.GameColor;
import com.mxxy.game.utils.GraphicsUtils;

@SuppressWarnings("serial")
public class Label extends JLabel {
	
	private Animation animation;
	
	private long lastUpdateTime;
	
	private  Rectangle bounds;  //矩形范围   bounds;  //矩形范围  
	
	public Label(Animation anim) {
		this(null, anim!=null?new ImageIcon(anim.getImage()):null, LEFT);
		setAnim(anim);
	}
	
	public Label(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		setIgnoreRepaint(true);
		setBorder(null);
		setFont(new Font("黑体", Font.PLAIN, 13));
		setForeground(Color.WHITE);
	}
	
	public Label(Sprite s) {
		this(null, null, LEFT);
	}
	
	public void setRectangleBounds(int x,int y){
		this.bounds=new Rectangle(x, y, getWidth(), getHeight());
	}

	public boolean hit(Point p) {
		return this.bounds.contains(p);
	}
	
	public void setBorder(){
		setForeground(GameColor.decode("#F8E890"));
		setBorder(new CompoundBorder(new RoundLineBorder(Color.WHITE,1, 8, 8),new EmptyBorder(10, 10, 10, 10)));
	}
	
	public void setAnim(Animation anim) {
		this.animation = anim;
		if (anim != null) {
			this.lastUpdateTime = System.currentTimeMillis();
			this.setSize(anim.getWidth(), anim.getHeight());
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setVerticalAlignment(SwingConstants.CENTER);
		}else {
			setIcon(null);
		}
	}
	
	private Color color;
	public void setColor(Color p){
		this.color=p;
	}
	@Override
	public void paint(Graphics g) {
		GraphicsUtils.setAntialiasAll(g, true);
		if (animation != null) {
			animation.update(System.currentTimeMillis() - lastUpdateTime);
			lastUpdateTime = System.currentTimeMillis();
			setIcon(new ImageIcon(animation.getImage()));
		}
		drawLenvl(g);
//		drawToolTipText(g);
		super.paint(g);
	}
	
//	private String text;
//	public void setToolTipTexts(String text){
//		this.text=text;
//		
//	}
//	public void drawToolTipText(Graphics g){
//		g.setFont(Constant.PROMPT_FONT);
//		if(text!=null){
//			g.drawString(text, getX(), getY());
//		}
//	}
	public void drawLenvl(Graphics g){
		if(color!=null){
			g.setColor(color);
			g.fillRect(5, getHeight()-4, getWidth()-10, 2);
			g.setColor(null);
		}
	}
	@Override
	public void paintImmediately(int x, int y, int w, int h) {}
}