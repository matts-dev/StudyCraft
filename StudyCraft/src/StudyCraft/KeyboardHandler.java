package StudyCraft;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;

public class KeyboardHandler implements KeyListener {
	private boolean altPressed = false;
	private boolean altReady = true;
	private boolean controlPressed = false;
	private JButton openFileButton = null;
	private GUI gui = null;

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ALT && altReady) {
			altPressed = !altPressed;
			altReady = false;
			e.consume();
			return;
		}

		if (keyCode == KeyEvent.VK_CONTROL) {
			controlPressed = true;
			e.consume();
			return;
		}
		

		if (keyCode == KeyEvent.VK_O && altPressed && openFileButton != null) {
			// poll action on button
			openFileButton.doClick();
			altPressed = false;
			e.consume();
			return;
		}

		if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE && getGui() != null) {
			gui.quizGUI();
			e.consume();
			return;
		}

		if (controlPressed) {
			// place control related key maps in this if statement
			if(keyCode == KeyEvent.VK_O){
				// poll action on button
				openFileButton.doClick();
				// controlPressed = false; // doesn't need to release ctrlPressed state
				e.consume();
				return;	
			}
			e.consume();
			return;
		}
		// System.out.println("control pressed at press: " + controlPressed);

		// This will clear previous pressed key
		e.consume();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ALT) {
			altReady = true;
		}
		if (keyCode == KeyEvent.VK_CONTROL) {
			controlPressed = false;
		}
		// System.out.println("control pressed at released: " + controlPressed);

		e.consume();

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		e.consume();

	}

	public void setOpenFileButton(JButton openFileButton) {
		this.openFileButton = openFileButton;
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}



}
