package com.mxxy.game.ui;

import java.awt.AWTException;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.mxxy.game.base.IPanelDraw;
import com.mxxy.game.config.Context;
import com.mxxy.game.utils.Constant;
import com.mxxy.game.utils.SpriteFactory;
import com.mxxy.game.utils.UIHelp;
/**
 * 游戏主窗体
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
@SuppressWarnings("serial")
public class GameFrame extends JFrame implements IWindows{
	private Dimension preferredSize;
	private Image cursorImage;
	private TrayIcon trayIcon;
	private UIHelp uihelp;
	private Image icon;
	
	@Override
	public void initContent(Context context) {
		context.setWindows(this);
		this.uihelp=new UIHelp(this);
		icon=new ImageIcon(SpriteFactory.loadImage("componentsRes/title.png")).getImage();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(icon);
		super.setTitle(Constant.getString("MainTitle"));
		setResizable(false);
		hideCursor();
		showSystemtTray();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void show() {
		super.show();
	}

	@Override
	public Container getContainers() {
		return draw.getComponent();
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(Constant.getString("MainTitle")+title);
	}
	
	private IPanelDraw draw;
	
	public void showPanel(IPanelDraw panel) {
		this.draw=panel;
		int width=panel.getClass().getSimpleName().equals("LoadingPanel")?panel.getScreenWidth():Constant.WINDOW_WIDTH;
		int height=panel.getClass().getSimpleName().equals("LoadingPanel")?panel.getScernHeight():Constant.WINDOW_HEIGHT;
		preferredSize=new Dimension(width,height);
		if(isfristApplication){
			dispose();
			setSize(preferredSize);
			setUndecorated(panel.getClass().getSimpleName().equals("LoadingPanel"));
			setLocationRelativeTo(null);
			setVisible(true);
		}
		JComponent component = panel.getComponent();
		setContentPane(component);
		component.requestFocusInWindow();
	}

	public  boolean isfristApplication=true;
	
	public void setIsfristApplication(boolean isfristApplication) {
		this.isfristApplication = isfristApplication;
	}
	@Override
	public UIHelp getUiHelp() {
		return uihelp;
	}

	@Override
	public void hideCursor() {
		cursorImage=Toolkit.getDefaultToolkit().getImage("");
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0,0),"CURSOR"));
	}

	@Override
	public void showSystemtTray() {
		if(SystemTray.isSupported()){  
			try {
				trayIcon=new TrayIcon(icon,Constant.getString("MainTitle"),createPopupMenu());
				trayIcon.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if((e.getModifiers()&InputEvent.BUTTON1_MASK)!=0){	 
							if(getExtendedState()==JFrame.ICONIFIED){  
								setVisible(true);
								setAlwaysOnTop(true); 
								setExtendedState(JFrame.NORMAL);
							}else{
								setExtendedState(JFrame.ICONIFIED);
							}
						}else if((e.getModifiers()&InputEvent.BUTTON3_MASK)!=0){  
						}
					}
				});
				trayIcon.setImageAutoSize(true);  
				SystemTray systemTray=SystemTray.getSystemTray(); 
				systemTray.add(trayIcon);   
				trayIcon.displayMessage(Constant.getString("MainTitle"), "梦想", MessageType.INFO);    
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}else{
			return;
		}
	}

	private PopupMenu createPopupMenu() {
		PopupMenu popup=new PopupMenu(); 
		MenuItem open=new MenuItem("打开");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
				setExtendedState(JFrame.NORMAL);
			}
		});
		MenuItem close=new MenuItem("关闭");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		popup.add(open);
		popup.addSeparator();
		popup.add(close);
		return popup;
	}

	@Override
	public IPanelDraw getPanel() {
		return draw;
	}
}
