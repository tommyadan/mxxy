package com.mxxy.game.ui;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import com.mxxy.game.base.AbstactPanel;
import com.mxxy.game.base.Panel;
import com.mxxy.game.sprite.Cursor;
import com.mxxy.game.sprite.Players;
import com.mxxy.game.sprite.Sprite;
import com.mxxy.game.utils.Constant;
import com.mxxy.game.utils.MP3Player;
import com.mxxy.game.utils.SpriteFactory;
import com.mxxy.game.widget.Label;
import com.mxxy.game.widget.TileMap;
/***
 * 战争面板
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
public class BattlePanel extends AbstactPanel {
	private List<Players> ownsideTeam;
	private List<Players> hostileTeam;
	private TileMap tileMap;
	private Point point;
	private Image backgroundImage;
	private GamePanel gamePanel;
	private Random random;
	private Timer timer;
	private Image wirtImage;
	public BattlePanel(TileMap tileMap, Point viewPosition, Component functionPanel,GamePanel gamePanel) {
		this.isDraw=true;
		this.tileMap=tileMap;
		this.point=viewPosition;
		getComponent().add(functionPanel);
		this.gamePanel=gamePanel;
	}

	@Override
	public void init() {
		setGameCursor(Cursor.DEFAULT_CURSOR);
		backgroundImage=SpriteFactory.loadImage("/scene/battlebg.png");
		initTimes();
		random=new Random();
		countDown();
	}
	/**
	 * 倒计时开始 
	 */
	public void countDown(){
		secLeft=new Label(null,timerImge[3],0);
		secRight=new Label(null ,timerImge[0],0);
		secLeft.setBounds(Constant.WINDOW_WIDTH/2 -timerImge[0].getImage().getWidth(null), 27, timerImge[0].getIconWidth(), timerImge[0].getIconHeight());
		secRight.setBounds(Constant.WINDOW_WIDTH/2, 27, timerImge[0].getIconWidth(), timerImge[0].getIconHeight());
		add(secLeft,0);
		add(secRight,0);
		new Timer().schedule(new CountDown(),0,1000);
	}

	private ImageIcon timerImge[];
	private void initTimes(){
		timerImge=new ImageIcon[10];
		for (int i = 0; i < timerImge.length; i++) {
			timerImge[i]=new ImageIcon(SpriteFactory.loadImage("componentsRes/timer/"+i+".png"));
		}
		wirtImage=SpriteFactory.loadImage("componentsRes/timer/wait.png");
	}
	
	/**
	 * (初始化指令面板)
	 */
	public void startTimer(){
		//		timer=new Timer();
		//		timer.schedule(new TimerWork(), 0, 1000);
	}

	private Label secLeft,  secRight;
	private void showNumber(int number) {
		int temp = Math.abs(number);
		DecimalFormat df = new DecimalFormat("00");
		String sec = df.format(temp % 60);
		secLeft.setIcon(timerImge[sec.charAt(0) - 48]);
		secRight.setIcon(timerImge[sec.charAt(1) - 48]);
	}

	public void setTime(int seconds) {
		if(seconds>0){
			showNumber(seconds);
		}else{
			
		}
	}

	
	/**
	 * 倒计时线程
	 * @author dell
	 *
	 */
	public class CountDown extends TimerTask{
		int time = 30;
		@Override
		public void run() {
			setTime(time--);
		}
	}


	class TimerWork extends TimerTask{
		@Override
		public void run() {
//			if(second==-1){
//				runaway(ownsideTeam.get(0), true);
//			}
		}
	}

	@Override
	public void drawBitmap(Graphics2D g, long elapsedTime) {
		g.setColor(Constant.PLAYER_NAME_COLOR);
		g.clearRect(0, 0, getWidth(), getHeight());
		this.tileMap.drawBitmap(g, point.x,point.y, getWidth(), getHeight());
		if (this.backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, null);
		}
		drawNpc(g, elapsedTime);
		drawComponent(g, elapsedTime);
		drawMemory(g);
	}

	public void initPlayer(){
		for (int i = 0; i < ownsideTeam.size(); i++) {
			Players player = ownsideTeam.get(i);
			player.setLocation(530-55*i,400-45*i);
			player.setState(Players.STATE_STAND);
			player.setDirection(Sprite.DIRECTION_TOP_LEFT);
			addNPC(player);
		}
	}

	int dx = 60, dy = 40;
	//	int x0 = 340, y0 = 400;
	int x1 = 300, y1 = 150;

	/**
	 * initHostileTeam(初始化敌方团队)
	 */
	public void initHostileTeam(){
		switch (hostileTeam.size()) {
		case 1:
			x1 -= 2 * dx;
			y1 += 2 * dy;
			break;
		case 2:
			x1 -= 1.5 * dx;
			y1 += 1.5 * dy;
			break;
		case 3:
			x1 -= 1 * dx;
			y1 += 1 * dy;
			break;
		case 4:
			break;
		default:
			break;
		}
		for (int i = 0; i < hostileTeam.size(); i++) {
			Players player = hostileTeam.get(i);
			player.setLocation(x1 - dx * i, y1 + dy * i);
			player.setState(Players.STATE_STAND);
			player.setDirection(Sprite.DIRECTION_BOTTOM_RIGHT);
			addNPC(player);
		}
	}

	private void addNPC(Players player) {
		super.addNpc(player);
	}

	@Override
	public String getMusic() {
		return ("music/2003.mp3");
	}

	/**
	 * 已方团队
	 * @param team
	 */
	public void setOwnsideTeam(List<Players> team) {
		Panel dialog = UIHelp.getPanel("BattlePanelCmd");
		addComponents(dialog);
		this.ownsideTeam = team;
		initPlayer();
	}

	/**
	 * 敌方团队
	 * @param team
	 */
	public void setHostileTead(List<Players> team){
		this.hostileTeam=team;
		initHostileTeam();
	}

	public void runaway(Players player, boolean success) {
		try {
			RunawayWorker worker = new RunawayWorker(player,success, 2000);
			worker.execute();
			worker.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runaway(逃跑)
	 */
	private class RunawayWorker extends SwingWorker<Object,Object>{
		private Players player;
		private long duration;
		private boolean success;
		public RunawayWorker(Players player, boolean success, long duration) {
			super();
			this.player = player;
			this.duration = duration;
			this.success = success;
		}
		@Override
		protected Object doInBackground() throws Exception {
			int dir =  player.getDirection();
			player.setDirection(dir-2);
			player.setState(Players.STATE_WALK);
			Thread.sleep(1000);
			if(this.success) {
				MP3Player.play("music/escape_ok.mp3");
				long interval = 50;
				long t = 0;
				while(t<duration) {
					Thread.sleep(interval);
					// 计算移动量
					long elapsedTime = interval;
					int distance = (int) (2*Constant.PLAYER_RUNAWAY * elapsedTime);
					int dx = distance;  //向右下逃跑
					int dy = distance;
					if(player.getDirection() == Sprite.DIRECTION_TOP_LEFT) {//向左上逃跑
						dx = -dx;
						dy = -dy;
					}
					player.moveBy(dx, dy);
					super.publish(new Point(dx,dy));
					t += interval;
					if(playerGoBeyone(player)) {
						if(player.isShowWeapon()){
							gamePanel.quitWar();
						}
						timer.cancel();
						break;
					}
				}
			}else {				
//				UIHelp.prompt(getComponent(), "运气不济,逃跑失败 #83", 2000);
			}
			player.setState(Players.STATE_STAND);
			player.setDirection(dir);
			return null;
		}
		/**
		 * judge playerLacotion GoBeyone Screen(判断人物是否超出屏幕)
		 * @param players
		 * @return
		 */
		private boolean playerGoBeyone(Players players){
			return player.getX()<0|| player.getY()<0 || player.getX()>BattlePanel.this.getWidth()+28||player.getY() > BattlePanel.this.getHeight()+28;
		}
	}
	@Override
	public void paintImmediately(int x, int y, int w, int h) {}
}
