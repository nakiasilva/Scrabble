package client.agent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * 
 */

/**
 * @author nitishmathur
 *
 */
public class buttonProp extends JButton implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageIcon pic;
	private String icon;
	
	public buttonProp(){
		
		this.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e){
try {
	
	System.out.println("action list "+e.getSource());
	
	
	
		String input = JOptionPane.showInputDialog("Enter the character.");
		icon = input.toUpperCase()+".jpg";
		System.out.println(icon);
		pic = new ImageIcon(""+icon);
		setIcon(pic);
		System.out.println(getIcon());

		findWord();
}
catch(NullPointerException excp) {
	JOptionPane.showMessageDialog(null, "Character not entered");
}
		
	}
	
	private void findWord() {
		//System.out.println(buttons[i]);
		String horizontalStr = "";
		String verticalStr = "";
		
		int buttonId = Client_GUI.buttons.indexOf(this);
	
		int updateButtId;
		System.out.println("Button Id:"+ buttonId);
		int leftWall = buttonId % 20;
		int rightWall = 20 - (buttonId % 20) - 1 ;
		int upWall = buttonId / 20;
		int downWall = 20 - (buttonId / 20) - 1;
		int horizontalI = 0;
		int horizontalJ = 0;
		int verticalI = 0;
		int verticalJ = 0;
		
		
		//----------------------------Check word is valid Horizontally----------------------------
		if (buttonId == 0 )
		{
			updateButtId = buttonId;
		}
		else
		{
			updateButtId = buttonId - 1;
		}
		System.out.println(Client_GUI.buttons.get(updateButtId).getIcon() == null);
		
		while ( leftWall > 0 && (Client_GUI.buttons.get(updateButtId).getIcon() != null))
		{

			leftWall = leftWall - 1;
			updateButtId = updateButtId - 1;
		}
		
		if (buttonId == Client_GUI.buttons.size() - 1)
		{
			updateButtId = buttonId;
		}
		else
		{
			updateButtId = buttonId + 1;
		}
		while (rightWall > 0 && (Client_GUI.buttons.get(updateButtId).getIcon() != null))
		{
			
			rightWall = rightWall - 1;
			updateButtId = updateButtId + 1;
		}
		
		if (leftWall != buttonId % 20)
		{
			horizontalI = buttonId % 20 - leftWall;
		}
		if (rightWall != 20 - (buttonId % 20) - 1)
		{
			horizontalJ = (20 - (buttonId % 20) - 1) - rightWall;
		}
		
		
		System.out.println("I:"+horizontalI);
		System.out.println("J:"+horizontalJ);
		for (int i = buttonId - horizontalI; i <= (buttonId + horizontalJ); i++ )
		{
			String path[] = Client_GUI.buttons.get(i).getIcon().toString().split("/");
			String file = path[path.length - 1];
			horizontalStr = horizontalStr + file.split("")[0]; 
			
		}
		
		//-------------------------------Check word is valid Vertically------------------------------
		
		System.out.println("Check up: "+ upWall);
		System.out.println("Check down: " + downWall);
		
		if (upWall > 0 )
		{
			updateButtId = buttonId - 20;
		}
		else
		{
			updateButtId = buttonId;
		}
		
		while ( upWall > 0 && (Client_GUI.buttons.get(updateButtId).getIcon() != null))
		{

			upWall = upWall - 1;
			updateButtId = updateButtId - 20;
		}
		
		if (downWall == 0)
		{
			updateButtId = buttonId;
		}
		else
		{
			updateButtId = buttonId + 20;
		}
		
		while (downWall > 0 && (Client_GUI.buttons.get(updateButtId).getIcon() != null))
		{
			
			downWall = downWall - 1;
			updateButtId = updateButtId + 20;
		}
		
		if (upWall != buttonId / 20)
		{
			verticalI = buttonId / 20 - upWall;
		}
		if (downWall != 20 - (buttonId / 20) - 1)
		{
			verticalJ = (20 - (buttonId / 20) - 1) - downWall;
		}
		
		System.out.println("I:"+verticalI);
		System.out.println("J:"+verticalJ);
		for (int i = buttonId - (20*verticalI); i <= (buttonId + (20*verticalJ)); i = i + 20 )
		{
			String path[] = Client_GUI.buttons.get(i).getIcon().toString().split("/");
			String file = path[path.length - 1];
			verticalStr = verticalStr + file.split("")[0]; 
			
		}
		
		String[] words = {horizontalStr,verticalStr,"Not Applicable"};
		Object value = JOptionPane.showInputDialog(null, "Which is the correct word?", "Selection", JOptionPane.DEFAULT_OPTION, null, words, "Not Applicable");
		System.out.println("option selected is: " + value.toString());

		if (value != null )
		{
			if (!value.toString().equals("Not Applicable"))
			{
				Client_GUI.score += value.toString().length();
				JOptionPane.showMessageDialog(null, "Your Score is: " + Client_GUI.score);
				Client_GUI.buttons.get(buttonId).setEnabled(false);
			}
			else 
			{
				JOptionPane.showMessageDialog(null, "Your Score is: " + Client_GUI.score);
				//To Disable a button
				Client_GUI.buttons.get(buttonId).setEnabled(false);
			}
		}
		
	}

}
