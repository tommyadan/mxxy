package com.mxxy.game.ui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mxxy.game.astar.AStar;
import com.mxxy.game.base.AbstactPanel;
import com.mxxy.game.config.MapConfig;
import com.mxxy.game.event.PlayerEvent;
import com.mxxy.game.event.PlayerListenerAdapter;
import com.mxxy.game.handler.GamePanelController;
import com.mxxy.game.listener.ISetOnListener;
import com.mxxy.game.resources.DefaultTileMapProvider;
import com.mxxy.game.resources.JumpTrigger;
import com.mxxy.game.resources.SceneNpc;
import com.mxxy.game.resources.SceneTeleporter;
import com.mxxy.game.sprite.Cursor;
import com.mxxy.game.sprite.Players;
import com.mxxy.game.sprite.Sprite;
import com.mxxy.game.utils.Constant;
import com.mxxy.game.utils.SearchUtils;
import com.mxxy.game.was.Toolkit;
import com.mxxy.game.widget.SpriteImage;
import com.mxxy.game.widget.TileMap;
/**
 * 游戏面板
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
@SuppressWarnings("serial")
public class GamePanel extends AbstactPanel implements ISetOnListener<GamePanelController>{
	private TileMap map;
	private int sceneHeight;
	private int sceneWidth;
	private byte[] maskdata;  
	private String SceneId;  
	private String SceneName;
	private AStar searcher;
	private List<Point> path;
	private ScenePlayerHandler scenePlayerHandler;
	private ArrayList<JumpTrigger> jumpTriggers;
	private Random random;
	@Override
	public void init() {
		setScreenSize(806, 600);
		setGameCursor(Cursor.DEFAULT_CURSOR);  //设置鼠标样式
	}
	public void initGameDate(){
		searcher = new AStar();
		setMap(getMap(context.getScene()));
		scenePlayerHandler=new ScenePlayerHandler();
		setPlayer(context.getPlayer());
		random=new Random();
		new MovementThread().start();
	}

	/**
	 * 事件处理
	 */
	@Override
	public void setListener(GamePanelController event) {
		this.addMouseMotionListener(event);
		this.addMouseListener(event);
	}

	@Override
	public void drawBitmap(Graphics2D g, long elapsedTime) {
		drawMap(g);
		drawJumpScene(g,elapsedTime);
		drawClick(g,elapsedTime);
		drawNpc(g, elapsedTime);
		drawPlayers(g, elapsedTime);
		drawMemory(g);
	}

	/**
	 * 加载地图掩码
	 * @param filename
	 * @return
	 */
	private byte[] loadMask(String filename){
		System.out.println("map : " + this.map.getWidth() + "*" + this.map.getHeight() + ", scene: " + this.sceneWidth + "*" + 
				this.sceneHeight + ", msk: " + filename);
		byte[] maskdata = new byte[(this.sceneWidth * this.sceneHeight)+300];
		try {
			InputStream in = new FileInputStream(filename);
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			int pos = 0;
			String str;
			while ((str = reader.readLine()) != null){
				int len = str.length();
				for (int i = 0; i < len; i++)
					maskdata[(pos++)] = (byte)(str.charAt(i) - '0');
			}
		}
		catch (UnsupportedEncodingException e) {
			System.out.println("加载地图掩码失败！filename=" + filename);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("加载地图掩码失败！filename=" + filename);
			e.printStackTrace();
		}
		return maskdata;
	}

	/**
	 * 绘制地图场景
	 * @param g
	 */
	private void drawMap(Graphics2D g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setBackground(Color.black);
		if (this.map != null) {
			int viewX = getViewportX();  
			int viewY = getViewportY();
			this.map.drawBitmap(g, viewX,viewY, getWidth(), getHeight());
		}
	}

	/**
	 * 绘制鼠标点击效果
	 * @param g
	 * @param elapsedTime
	 */
	private void drawClick(Graphics2D g, long elapsedTime) {
		Cursor gameCursor = getGameCursor();
		SpriteImage effectSprite = (gameCursor == null) ? null : gameCursor.getEffect();
		if (effectSprite != null) {
			effectSprite.update(elapsedTime);
			Point p = this.sceneToView(gameCursor.getClickPosition());
			effectSprite.drawBitmap(g, p.x + gameCursor.getOffsetX(), p.y + gameCursor.getOffsetY());
		}
	}

	/**
	 * 绘制跳转场景
	 * @param g
	 */
	public void drawJumpScene(Graphics2D g,long elapsedTime){
		if(jumpTriggers==null){
			return;
		}
		for (int i = 0; i < jumpTriggers.size(); i++) {
			JumpTrigger t =jumpTriggers.get(i);
			Sprite s = t.getSprite();
			s.update(elapsedTime);
			Point p = t.getLocation();
			p = sceneToView(p);
			s.drawBitmap(g, p.x, p.y - s.getHeight() / 2 + s.getCenterY());
		}
	}

	public void setMap(TileMap map) {
		if(map==null){
			return;
		}
		this.map = map;
		MapConfig mapConfig=map.getConfig();  //获取到配置对象
		setSceneId(mapConfig.getId());    //设置场景ID
		setSceneName(mapConfig.getName());//设置场景Name
		setMaxWidth(map.getWidth());
		setMaxHeight(map.getHeight());
		this.sceneHeight=map.getHeight()/20;
		this.sceneWidth=map.getWidth()/20;
		this.maskdata=loadMask(mapConfig.getPath().replace(".map", ".msk"));
		this.searcher.init(this.sceneWidth, this.sceneHeight, this.maskdata);
		jumpTriggers=new ArrayList<JumpTrigger>();
		//场景跳转点
		List<SceneTeleporter> findJump = dataStore.findJump(mapConfig.getId());
		for (int i = 0; findJump!=null&&i < findJump.size(); i++) {
			JumpTrigger jumpTrigger = new JumpTrigger(findJump.get(i));
			jumpTriggers.add(jumpTrigger);
		}
		clearNpc();
		//添加NPC
		//更具场景ID获取到所有的NPC实例
		List<SceneNpc> SceneNpc = dataStore.findSceneNpc(mapConfig.getId());
		for (int i = 0;SceneNpc!=null&& i < SceneNpc.size(); i++) {
			System.out.println(SceneNpc.get(i).toString());
			Players npc = dataStore.createPlayer(SceneNpc.get(i));
			Point p = sceneToLocal(npc.getSceneLocation());
			npc.setLocation(p.x, p.y);
			super.addNpc(npc);
		}
	}

	/**
	 * 获取当前地图场景
	 * @param id
	 * @return
	 */
	public TileMap getMap(String id) { 
		DefaultTileMapProvider tileMapProvider = new DefaultTileMapProvider();
		TileMap m = tileMapProvider.getResource(id);
		m.setAlpha(1.0F);
		return m;
	}

	/**
	 * 跳转场景
	 * @param id
	 * @param x
	 * @param y
	 * TODO
	 */
	public void changeScene(String sceneId,int x,int y ){
		getPlayer().stop(true);
		if(sceneId == null || sceneId=="null") {
			throw new IllegalArgumentException("跳转场景失败，sceneId不能为空！");
		}
	}

	/**
	 * 鼠标点击效果 
	 * @param p   坐标点
	 */
	public void click(Point p) {
		final SpriteImage effectSprite = this.getGameCursor().getEffect();  //获取到水波纹
		effectSprite.setVisible(true);  //显示水波纹
		Point sp = this.viewToScene(p);
		Point vp = this.sceneToView(sp);
		p.translate(-vp.x, -vp.y);
		this.getGameCursor().setClick(sp.x, sp.y);
		this.getGameCursor().setOffset(p.x, p.y);
		if (effectSprite != null) {
			effectSprite.setRepeat(2);
			new Thread() {
				public void run() {
					while (effectSprite.isVisible()) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						if (effectSprite.getSprite().getRepeat() == 0) {
							effectSprite.setVisible(false);
							break;
						}
					}
				};
			}.start();
		}
	}

	public Point viewToScene(Point p) {
		return localToScene(viewToLocal(p));
	}

	public Point sceneToView(Point p) {
		return this.localToView(this.sceneToLocal(p));
	}

	/**
	 * 场景坐标
	 * @param p
	 * @return
	 */
	public Point sceneToLocal(Point p) {
		return new Point(p.x * 20,  map.getHeight() - p.y *20);
	}

	public Point localToScene(Point p) {
		return new Point(p.x / 20, (map.getHeight() - p.y) / 20);
	}

	/**
	 * 设置人物
	 */
	public void setPlayer(Players player) {
		Players player0 = getPlayer();
		if (player0 != null) {
			player0.stop(false);
			player0.removePlayerListener(scenePlayerHandler);
		}
		player.stop(false);
		super.setPlayer(player); 
		if (player != null) {
			player.addPlayerListener(scenePlayerHandler);
			setPlayerSceneLocation(player.getSceneLocation());  
			player.setSearcher(searcher);
		}
	}

	/**
	 * 设置人物在场景的坐标
	 * @param p
	 */
	private void setPlayerSceneLocation(Point p) {
		if (this.map == null) {
			return;
		}
		Point vp = sceneToLocal(p);
		this.setViewPosition(vp.x- 430, vp.y - 300);
		this.setPlayerLocation(vp);
		this.revisePlayerSceneLocation();
	}

	/**
	 * 设置Player坐标
	 * @param p
	 */
	public void setPlayerLocation(Point p) {
		this.revisePlayer(p);
		this.getPlayer().setLocation(p.x, p.y);
	}

	/**
	 * 判断是否超越屏幕宽高   
	 */
	private void revisePlayer(Point p) {
		int canvasWidth = getWidth();
		int canvasHeight = getHeight();
		int viewX = getViewportX();
		int viewY = getViewportY();
		if (p.x > viewX + canvasWidth) {
			p.x = viewX + canvasWidth;
		}
		if (p.y > viewY + canvasHeight) {
			p.y = viewY + canvasHeight;
		}
		if (p.x < viewX) {
			p.x = viewX;
		}
		if (p.y < viewY) {
			p.y = viewY;
		}
	}

	@Override
	public String getMusic() {
		return ("music/"+context.getScene()+".mp3");
	}
	/**
	 * 同步人物和场景
	 */
	public void synchronizedPlayAndScene(Point point){
		synchronized (UPDATE_LOCK) {
			Point p = getPlayerLocation();
			p = this.localToView(p);
			int dx = point.x;
			int dy = point.y;
			Point vp = getViewPosition();
			if (dx < 0) {
				if (p.x < 300) {
					vp.x += dx;
				}
			}
			else if (p.x > 340) {
				vp.x += dx;
			}

			if (dy < 0) {
				if (p.y < 220) {
					vp.y += dy;
				}
			}
			else if (p.y > 260) {
				vp.y += dy;
			}
			setViewPosition(vp.x, vp.y);
		}
	}

	/**
	 * 事件响应
	 * @author ZAB
	 *
	 */
	private final class ScenePlayerHandler extends PlayerListenerAdapter{

		@Override
		public void move(Players player, Point increment) {

			revisePlayerSceneLocation();
			syncSceneAndPlayer(increment);
			Point p = getPlayerSceneLocation();  //获取到人物坐标
			//TODO  触发场景跳转
			for (int i = 0; jumpTriggers!=null && i < jumpTriggers.size(); i++) {
				JumpTrigger jumpTrigger = jumpTriggers.get(i);
				if (jumpTrigger.hit(p)) {
					//TODO
					return;
				}
			}

			if(getSceneId().equals(Constant.SCENE_DHW)||getSceneId().equals(Constant.SCENE_CAC)){  //判断是东海湾才能遇到怪物
				long nowtime = System.currentTimeMillis();
				Long lastPatrolTime = (Long) Constant.props.get(Constant.LAST_PATROL_TIME);
				if (lastPatrolTime!=null && nowtime - lastPatrolTime > 10000L) {
					Random rand = new Random();
					if (rand.nextInt(100) < 5) {
//						enterTheWar();
					}
				}
			}
		}
		@Override
		public void walk(PlayerEvent evt) {
			Point target = evt.getTarget(); //获取到点击的坐标点
			walkTo(target.x, target.y);
		}
	}

	/**
	 * 移动到
	 * @param x  鼠标点击的x,y
	 * @param y
	 */
	public void walkTo(int x, int y) {
		System.out.println(x+":::"+y);
		if ((x <= 0) || (y <= 0) || (x > this.sceneWidth) || (y > this.sceneHeight)) return;
		this.path = findPath(x, y);
		if (this.path != null) {
			getPlayer().setPath(this.path);
			getPlayer().move();
		} else {

		}
	}

	/**
	 * 寻找路径
	 * @param x
	 * @param y
	 * @return
	 */
	public List<Point> findPath(int x, int y) {
		Point source = getPlayerSceneLocation();
		Point target = new Point(x, y);

		List<Point> path = SearchUtils.getLinePath(source.x,source.y,target.x,target.y);
		for (int i = path.size() - 1; i >= 0; i--) {
			Point p = path.get(i);
			if (pass(p.x, p.y)) {
				target = p;
				break;
			}
			path.remove(i);
		}
		// 如果直线上全部点可通行，则返回
		boolean passed = true;
		for (int i = 0; i < path.size(); i++) {
			Point p = path.get(i);
			if (!pass(p.x, p.y)) {
				passed = false;
				break;
			}
		}
		if (passed)
			return path;
		// 否则计算两点间的路径  Astar
		path = searcher.findPath(source.x, source.y, target.x, target.y);
		return path;
	}

	/**
	 * 判断某点是否可以通行
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean pass(int x, int y) {
		return searcher.pass(x, y);
	}

	/**
	 * 同步场景  人物移动  地图移动
	 * @param increment
	 */
	public void syncSceneAndPlayer(Point increment) {
		synchronized (UPDATE_LOCK) {
			Point p = getPlayerLocation();
			p = localToView(p);
			int dx = increment.x;
			int dy = increment.y;
			Point vp = getViewPosition();
			if (dx < 0) {
				if (p.x < 300) {
					vp.x += dx;
				}
			}else if (p.x > 340) {
				vp.x += dx;
			}
			if (dy < 0) {
				if (p.y < 220) {
					vp.y += dy;
				}
			}else if (p.y > 260) {
				vp.y += dy;
			}
			setViewPosition(vp.x, vp.y);
		}
	}

	/**
	 * 修改人物在场景上的坐标
	 */
	public void revisePlayerSceneLocation() {
		Point p = getPlayer().getLocation();
		p = this.localToScene(p);
		getPlayer().setSceneLocation(p);
	}

	/**
	 * 获取到人物的坐标点
	 * @return
	 */
	private Point getPlayerLocation() {
		return getPlayer().getLocation();
	}

	/**
	 * 获取场景中的坐标
	 * @return
	 */
	public Point getPlayerSceneLocation() {
		return this.getPlayer().getSceneLocation();
	}

	/**
	 * 修改人物移动动作 并修改坐标
	 * @param elapsedTime
	 */
	public void updateMovements(long elapsedTime) {
		Players p = getPlayer();
		if (p != null) {
			p.updateMovement(elapsedTime);
		}
	}

	@Override
	public void removeListener(GamePanelController event) {
		this.removeMouseMotionListener(event);
		this.removeMouseListener(event);
	}


	private BattlePanel battlePanel;
	/**
	 * 进入游戏战场
	 */
	public void enterTheWar(){
		if(battlePanel==null){
			this.stopDraw();
			Players players = getPlayer();
			players.setShowMount(false);
			players.removeAllListeners();
			players.stop(true);  //停止移动
			TileMap map = getMap(context.getScene());
			Point viewPosition = getViewPosition();
			List<Players> ownsideTeam = new ArrayList<Players>();//己方阵容
			List<Players> hostileTeam = new ArrayList<Players>();//敌方阵容
			int elfCount = random.nextInt(3)+3;
			for(int i=0;i<elfCount;i++) {		
				hostileTeam.add(createElf(context.getScene()));
			}
			ownsideTeam.add(players);
			ownsideTeam.add(dataStore.createElf("5004", "超级神虎",100));
			Component[] components = getComponents();
			battlePanel=new BattlePanel(map,viewPosition,components[0],this);
			battlePanel.setConfigManager(getConfigManager());
			battlePanel.setUIhelp(UIHelp);
			battlePanel.setOpaque(false);
			battlePanel.setOwnsideTeam(ownsideTeam);
			battlePanel.setHostileTead(hostileTeam);
			battlePanel.setBounds(0, 0, Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
			battlePanel.playMusic();
			getComponent().add(battlePanel);
		}
	}

	/**
	 * 退出游戏战场
	 */
	public void quitWar() {

	}
	/**
	 * 根据场景创建对应的怪物
	 * @param id
	 * @return
	 */
	public Players createElf(String id){
		int elflevel = Math.max(0,10 +random.nextInt(4)-2);
		int elfIndex = random.nextInt(Constant.SCENE_DHW_ELFS.length); 
		Players p=null;
		switch (context.getScene()) {
		case Constant.SCENE_DHW:
			p=dataStore.createElf(Constant.SCENE_DHW_ELFS[elfIndex], Constant.DHW_ELFNAMES[elfIndex],elflevel);
			break;
		case Constant.SCENE_CAC:
			p=dataStore.createElf(Constant.XXT_ELFS[elfIndex], Constant.XXT_ELFNAMES[elfIndex],elflevel);
			break;
		}
		return p;
	}

	public String getSceneId() {
		return SceneId;
	}
	public String getSceneName() {
		return SceneName;
	}

	public void setSceneId(String string) {
		SceneId = string;
	}

	public void setSceneName(String sceneName) {
		SceneName = sceneName;
	}

	public final class MovementThread extends Thread {
		private long lastTime;
		{
			this.setName("movementThread");
		}
		public void run() {
			while (true) {
				synchronized (MOVEMENT_LOCK) {
					long currTime = System.currentTimeMillis();
					if (lastTime == 0)
						lastTime = currTime;
					long elapsedTime = currTime - lastTime;
					// update movement
					updateMovements(elapsedTime);
					lastTime = currTime;
				}
				Toolkit.sleep(40);
			}
		}
	}
	@Override
	public void paintImmediately(int arg0, int arg1, int arg2, int arg3) {}
}
