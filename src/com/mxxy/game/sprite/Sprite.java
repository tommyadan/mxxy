package com.mxxy.game.sprite;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.mxxy.game.widget.AbstractCanvas;
import com.mxxy.game.widget.Animation;
/**
 * 抽象精灵
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
public class Sprite extends AbstractCanvas{

	public static final int DIRECTION_BOTTOM_RIGHT = 0;  /**右下*/
	public static final int DIRECTION_BOTTOM_LEFT = 1;/**左下*/
	public static final int DIRECTION_TOP_LEFT = 2;/**左上*/
	public static final int DIRECTION_TOP_RIGHT = 3;/**右上*/
	public static final int DIRECTION_BOTTOM = 4;/**下*/
	public static final int DIRECTION_LEFT = 5;/**左*/
	public static final int DIRECTION_TOP = 6;/**上*/
	public static final int DIRECTION_RIGHT = 7;/**右*/
	
	
	private static double k1 = Math.tan(0.3926990816987241D);/**三角正切*/
	private static double k2 = 3.0D * k1;
	private Vector<Animation> animations;  //动画集合
	private boolean autoPlay = true;  //自动循环
	private int centerX;
	private int centerY;
	private List<Integer> colorations;  //颜色
	private Animation currAnimation;  
	private int direction;  //时间
	private String resId;
	private int repeat = -1;

	/**
	 * 判断方向
	 * @param src
	 * @param mouse  鼠标
	 * @return  方向
	 */
	public static int computeDirection(Point src, Point mouse){
		int direction = 0;
		double dy = mouse.y - src.y;
		double dx = mouse.x - src.x;
		if (dx == 0.0D)
			return dy >= 0.0D ? DIRECTION_BOTTOM : DIRECTION_TOP;
			if (dy == 0.0D) {
				return dx >= 0.0D ? DIRECTION_RIGHT : DIRECTION_LEFT;
			}
			double k = Math.abs(dy / dx);
			if (k >= k2) {
				if (dy > 0.0D)
					direction = DIRECTION_BOTTOM;
				else
					direction = DIRECTION_TOP;
			} else if (k <= k1) {
				if (dx > 0.0D)
					direction = DIRECTION_RIGHT;
				else
					direction = DIRECTION_LEFT;
			} else if (dy > 0.0D) {
				if (dx > 0.0D)
					direction = DIRECTION_BOTTOM_RIGHT;
				else
					direction = DIRECTION_BOTTOM_LEFT;
			}
			else if (dx > 0.0D)
				direction = DIRECTION_TOP_RIGHT;
			else {
				direction = DIRECTION_TOP_LEFT;
			}
			return direction;
	}

	public Sprite(int width, int height, int centerX, int centerY){
		this.colorations = new ArrayList<Integer>(3);
		this.colorations.add(Integer.valueOf(0));
		this.colorations.add(Integer.valueOf(0));
		this.colorations.add(Integer.valueOf(0));
		this.animations = new Vector<Animation>();
		setWidth(width);
		setHeight(height);
		this.centerX = centerX;
		this.centerY = centerY;
	}

	/**
	 * 构造精灵对象
	 * @param s
	 */
	@SuppressWarnings("unchecked")
	public Sprite(Sprite s) {
		this(s.getWidth(), s.getHeight(), s.getCenterX(), s.getCenterY());
		this.animations = (Vector<Animation>) s.animations.clone();   
		this.colorations = new ArrayList<Integer>(s.colorations);
		this.autoPlay = s.autoPlay;
		this.resId = s.resId;
	}

	public void addAnimation(Animation anim) {
		anim.reset();  //先重置在添加
		this.animations.add(anim);
	}

	public void clearAnimations() {
		this.animations.clear();
	}


	@Override
	public boolean contains(int x, int y) {
		try {
			BufferedImage bi = (BufferedImage)this.currAnimation.getImage();
			if (this.animations.size() > 1) {
				return bi.getRGB(x + this.centerX, y + this.centerY) != 0; 
			}
			return bi.getRGB(x, y) != 0;
		} catch (Exception localException) {
		}
		return false;
	}


	public void dispose() {
		for (Animation anim : this.animations) {
			anim.dispose();
		}
		this.animations.clear();
		this.currAnimation = null;
	}


	@Override
	protected void draw(Graphics2D g, int x, int y, int width, int height){
		x -= this.currAnimation.getCenterX();
		y -= this.currAnimation.getCenterY();
		g.drawImage(this.currAnimation.getImage(), x, y, x + width, y + height, 0, 0, width, height, null);
	}


	public Animation getAnimation(int index) {
		return (Animation)this.animations.get(index);
	}


	public int getAnimationCount() {
		return this.animations.size();
	}

	public int getCenterX() {
		return this.centerX;
	}

	public int getCenterY() {
		return this.centerY;
	}

	//颜色
	public int getColoration(int part) {
		return ((Integer)this.colorations.get(part)).intValue();
	}


	public Animation getCurrAnimation() {
		if (this.currAnimation == null) {
			setDirection(this.direction);
		}
		return this.currAnimation;
	}

	public int getDirection() {
		return this.direction;
	}

	public Image getImage() {
		return this.currAnimation.getImage();
	}

	public int getRepeat() {
		return this.currAnimation.getRepeat();
	}

	public String getResId() {
		return this.resId;
	}

	public boolean isAutoPlay() {
		return this.autoPlay;
	}
	//重置动画
	public void reset() {
		this.direction = 0;
		this.currAnimation = ((Animation)this.animations.get(0));
		resetFrames();
	}

	//重置帧
	public synchronized void resetFrames() {
		this.currAnimation.setIndex(0);
	}

	public void setAutoPlay(boolean autoPlay) {
		this.autoPlay = autoPlay;
		setRepeat(autoPlay ? -1 : 1);
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public void setColoration(int part, int coloration) {
		this.colorations.set(part, Integer.valueOf(coloration));
	}

	public void setColorations(int[] colorations) {
		for (int i = 0; i < colorations.length; i++)
			this.colorations.set(i, Integer.valueOf(colorations[i]));
	}

	/**
	 * 设置方向
	 * @param index
	 */
	public synchronized void setDirection(int index){
		index %= this.animations.size();
		this.direction = index;
		this.currAnimation = ((Animation)this.animations.get(this.direction));
		this.currAnimation.setRepeat(this.repeat);
	}

	public void setRepeat(int repeat) {
		this.currAnimation.setRepeat(repeat);
		this.repeat = repeat;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public void update(long elapsedTime){
		this.currAnimation.update(elapsedTime);
	}

	public int[] getColorations() {
		int[] colorations = new int[this.colorations.size()];
		for (int i = 0; i < colorations.length; i++) {
			colorations[i] = ((Integer)this.colorations.get(i)).intValue();
		}
		return colorations;
	}
}