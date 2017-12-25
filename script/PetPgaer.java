package panel;


import java.awt.event.ActionEvent;

import com.mxxy.game.base.Panel;
import com.mxxy.game.event.PanelEvent;
import com.mxxy.game.handler.AbstractPanelHandler;
/**
 * PetPgaer (宠物)
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
final public class PetPgaer extends AbstractPanelHandler{

	@Override
	public void init(PanelEvent evt) {
		super.init(evt);
	}
	
	public void openPet(ActionEvent e){
		Panel openHeadPet = uihelp.getPanel("SummonPager");
		showOrHide(openHeadPet);
	}
	
	@Override
	protected void initView() {
	}
}
