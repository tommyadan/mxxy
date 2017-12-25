package com.mxxy.game.config;

import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;
/**
 * 任务配置对象   用于后期的 存档和读档
 * @author ZAB
 */
@SuppressWarnings("serial")
public class PlayerVO  implements Serializable{
	
	public static final String STATE_STAND = "stand";
	
	public static final String STATE_WALK = "walk";
	
	
	public String id;
	/**
	 * 人物文件
	 */
	public String character;
	/**
	 * 人物名字
	 */
	public String name;
	
	/**
	 * 人物染色
	 */
	public int []	colorations;
	/**
	 * 人物状态
	 */
	public String state = STATE_STAND;
	/**
	 * 方向
	 */
	public int direction;
	/**
	 * 坐标
	 */
	public Point sceneLocation;
	
	/**
	 * 武器ID
	 */
	public String weaponIndex;
	
	/**
	 * 称谓
	 */
	public String describe;
	
	
	public String sceneId;
	
	public void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}
	public String getSceneId() {
		return sceneId;
	}
	
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
	public String getDescribe() {
		return describe;
	}
	
	public String getWeaponIndex() {
		return weaponIndex;
	}
	
	public void setWeaponIndex(String string) {
		this.weaponIndex = string;
	}

	public String getCharacter() {
		return character;
	}
	
	public void setCharacter(String character) {
		this.character = character;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int[] getColorations() {
		return colorations;
	}
	
	public void setColorations(int[] colorations) {
		this.colorations = colorations;
	}
	
	public void setSceneLocation(Point sceneLocation) {
		this.sceneLocation = sceneLocation;
	}
	
	public Point getSceneLocation() {
		return sceneLocation;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return state;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "PlayerVO [id=" + id + ", character=" + character + ", name="
				+ name + ", colorations=" + Arrays.toString(colorations)
				+ ", state=" + state + ", direction=" + direction
				+ ", sceneLocation=" + sceneLocation + ", weaponIndex="
				+ weaponIndex + ", describe=" + describe + "]";
	}
}
