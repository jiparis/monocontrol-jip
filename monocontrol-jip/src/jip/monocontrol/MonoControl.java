package jip.monocontrol;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.sound.midi.MidiDevice.Info;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import processing.core.PApplet;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Radio;
import controlP5.ScrollList;
import controlP5.Textfield;
import controlP5.Textlabel;

public class MonoControl extends PApplet{

	/*
	 * MONOCONTROL Written by Joshua Peschke, 2009
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = -3709326205447254431L;
	static long time1, time2;
	static int monomeSizeX = 8;
	static int monomeSizeY = 8;
	static int OSCInPort = 8000;
	static int OSCOutPort = 8080;
	static boolean fadeOn = true;
	static boolean blinkOn = false;
	static int fadeSpeed = 10;
	static ControlP5 controlP5;
	Textlabel headline;
	static Textlabel statusLabel;
	static boolean gateLoopChockes = false;
	
	static ControlObject[] tickRegister = new ControlObject[128];
	int selectedInputNumber = -1;
	int selectedOutputNumber = -1;
	List<Info> midiInputDevices, midiOutputDevices;

	static boolean abletonModeValue = false;
	public static int RESOLUTION = 24; // 1/8
	public static int PATTERNSTEPS;
	MidiObject midi;

	@SuppressWarnings("deprecation")
	public void setup() {

	}

	public void draw() {

		for (int i = 0; i < tickRegister.length; i++) { // send ticks to
			// registered
			// ControlObjects
			if (tickRegister[i] != null)
				tickRegister[i].tick();
		}
		if (frameCount % 2 == 0) {
			blinkOn = !blinkOn;
			//vm.refresh();
		}
	}


	public void deleteModeButton() {
		//vm.setMode(1, "");
	//	setStatusLabel("Delete Mode: press a button on your monome to delete a control object");
	}

	public void editModeButton() {
//		if (vm.getMode() != -1) {
//			vm.setMode(-1);
//			controlP5.controller("editModeButton").setLabel("Exit Edit Mode");
//			controlP5.controller("editModeButton").setColorBackground(
//					0xffefaa66);
//			//setStatusLabel("Edit Mode: Press a button to select a control object for editing");
//		} else {
//			controlP5.controller("editModeButton").setLabel("Enter Edit Mode");
//			controlP5.controller("editModeButton").setColorBackground(
//					0xff222222);
//			vm.returnToPlayMode();
//		}
	}



	// light up selected input/output


	static public void blinkInputLight() {
		time1 = System.currentTimeMillis();
	}

	static public void blinkOutputLight() {
		time2 = System.currentTimeMillis();
	}





	/*
	 * Monome Class for MidiStep by Joshua Peschke manages leds and button
	 * presses
	 * 
	 * IMPORTANT NOTE: The MonoControl coordinate system is mirrored on the
	 * x-axis.
	 */

	public static void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#d4d0c8",
				"jip.monocontrol.MonoControl" });
	}

}
