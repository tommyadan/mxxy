package com.mxxy.game.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

import com.mxxy.game.utils.Constant;
import com.mxxy.game.utils.GameColor;
import com.mxxy.game.utils.GraphicsUtils;
import com.mxxy.game.utils.SpriteFactory;
import com.mxxy.game.was.Toolkit;

/**
 * 主页报幕
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
@SuppressWarnings("serial")
public class Announcer extends JPanel implements Runnable{
	
	private int y=300;
	
	private int stringHeight;  //文字高度

	private String str;
	
	private Image image,notice;
	
	public void paint(Graphics g) {
		Graphics2D graphics2d = (Graphics2D) g.create();  
		GraphicsUtils.setAlpha(graphics2d, 0.8f);
		graphics2d.fillRect(0, 0, getWidth(), getHeight());  
		GraphicsUtils.setAntialias(g, true);
		g.setColor(GameColor.white);
		Font font = new Font("黑体", Font.PLAIN, 15);  
		g.setFont(font);  
		FontMetrics fm = g.getFontMetrics();  
		String[] split = str.split("H");
		for (int i = 0; i < split.length; i++) {
			String[] split2 = str.split("H");
			stringHeight=(fm.charWidth(1)+20)*i;
			g.drawString(split2[i], 150, y+(fm.charWidth(1)+15)*i);  
		}
		g.drawImage(notice, Constant.WINDOW_WIDTH/2-notice.getWidth(null)/2, y+(stringHeight-180), null);
		g.drawImage(image, 150, y+(stringHeight+38), null);
	}
	@Override
	public void run() {
		while(y>-stringHeight&&flage){  
			y = y - 1;  
			repaint();  
			Toolkit.sleep(80);  
		}  			
	}
	@Override
	public void paintImmediately(int x, int y, int w, int h) {}
	
	public Announcer() {
		super(null);
		str=Constant.getString("Producer");
		setSize(Constant.WINDOW_WIDTH,Constant.WINDOW_HEIGHT);
		image=SpriteFactory.loadImage("componentsRes/Logo_WY.png");
		notice=SpriteFactory.loadImage("componentsRes/notice.png");
	}
	private boolean flage=true;
	public void setStop(boolean b) {
		this.flage=b;
	}
}