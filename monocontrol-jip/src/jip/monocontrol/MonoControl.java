package jip.monocontrol;
/*
 * MONOCONTROL originally Written by Joshua Peschke in Processing, 2009
 * Ported to Java with several improvements by Jip, 2010
 */

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MonoControl{
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		
	static int monomeSizeX = 8;
	static int monomeSizeY = 8;
	static int OSCInPort = 8000;
	static int OSCOutPort = 8080;
	static boolean fadeOn = true;
	public static boolean blinking = false;
	public static boolean blinkOn = false;
	static int fadeSpeed = 10;
	static boolean gateLoopChockes = false;
	
	static ControlObject[] tickRegister = new ControlObject[128];
	
	public static int RESOLUTION = 24; // 1/8
	public static int PATTERNSTEPS;
	
	MidiObject midi;
	ViewManager vm;
	MainFrame frame;

	public MonoControl(){
		this.vm = new ViewManager();
		this.midi = new MidiObject(this);
	}
	
	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			if (lnfClassname == null)
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}
	/**
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final MonoControl mc = new MonoControl();
				mc.frame = new MainFrame(mc);
				mc.vm.setFrame(mc.frame);
				mc.frame.setDefaultCloseOperation(MainFrame.EXIT_ON_CLOSE);
				mc.frame.setTitle("jip.monocontrol");
				mc.frame.getContentPane().setPreferredSize(mc.frame.getSize());
				mc.frame.pack();
				mc.frame.setLocationRelativeTo(null);
				mc.frame.addWindowListener(new WindowListener(){

					public void windowActivated(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowClosed(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowClosing(WindowEvent e) {
						mc.frame.midi.closeOutputDevice();
						mc.frame.midi.closeInputDevice();
					}

					public void windowDeactivated(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowDeiconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowIconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void windowOpened(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
					
				});
				mc.frame.setVisible(true);
				
			}
		});
		
	}

}
