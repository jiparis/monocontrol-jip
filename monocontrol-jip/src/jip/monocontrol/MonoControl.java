package jip.monocontrol;


import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import processing.core.*;
import controlP5.*;
import rwmidi.*;

public class MonoControl extends PApplet {

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
	MidiInputDevice[] inputDevices;
	MidiOutputDevice[] outputDevices;
	static ControlObject[] tickRegister;
	int selectedInputNumber = -1;
	int selectedOutputNumber = -1;
	String[] midiInputDevices, midiOutputDevices;
	ScrollList inputSelect;
	ScrollList outputSelect;
	static ViewManager vm;
	static boolean abletonModeValue = false;

	@SuppressWarnings("deprecation")
	public void setup() {
		size(540, 350);
		frameRate(20);
		vm = new ViewManager(); // object that manages the diffrent views

		tickRegister = new ControlObject[128];
		// initializing user interface
		controlP5 = new ControlP5(this);
		//controlP5.setColorBackground(0xff222222);
		//controlP5.setColorForeground(0xff6060ff);
		//controlP5.setColorActive(0xff9090ff);
		controlP5.setColorBackground(0);
		controlP5.setColorForeground(0xffeeeeee);
		controlP5.setColorActive(0xffefdd66);
		controlP5.setColorLabel(0xff9090ff);
		controlP5.setColorValue(0);
		
		controlP5.tab("default").setLabel("PREFERENCES");
		controlP5.tab("default").setWidth(70);
		controlP5.tab("edit").setWidth(70);
		controlP5.tab("edit").setLabel("LAYOUT");
		headline = new Textlabel(this, "Mono Control - Jip 1.0b", 20, 30, 400, 200,
				0xff9090ff, ControlP5.standard56);
		headline.setLetterSpacing(2);

		Textlabel t = controlP5.addTextlabel("label1", "MIDI/OSC PREFERENCES:",
				20, 75);
		t.setColorValue(0);
		t.setTab("default");
		
		t = controlP5.addTextlabel("label2", "EDIT CONTROLLER LAYOUT:", 20, 75);
		t.setColorValue(0);
		t.setTab("edit");
		
		// status display
		statusLabel = controlP5
				.addTextlabel(
						"statusdisplay",
						"Started...                                                                                                ",
						20, 325); // strange, but works well... got to find
		// another way..
		statusLabel.setColorValue(0);
		statusLabel.setTab("global");
		
		// save and load buttons
		Textfield tsave = controlP5.addTextfield("saveName", width - 100,
				height - 80, 80, 20);
		tsave.setLabel("Save Layout");
		tsave.setColorActive(0xffefaa66);
		tsave.setTab("global");
		
		Textfield tload = controlP5.addTextfield("loadName", width - 100,
				height - 40, 80, 20);
		tload.setValue("test.xml");
		tload.setLabel("Load Layout");
		tload.setColorActive(0xffefaa66);
		tload.setTab("global");

		// buttons to add control objects
		controlP5.Button Button; // handle variable to work with the created
		// buttons

		Button = controlP5.addButton("exit", 1, 20, 300, 118, 20);
		Button.setLabel("Exit");
		Button.setTab("global");

		Button = controlP5.addButton("addFaderButton", 1, 275, 100, 118, 20);
		Button.setLabel("Add Fader");
		Button.setTab("edit");
		Button = controlP5.addButton("addXYFaderButton", 1, 275, 130, 118, 20);
		Button.setLabel("Add XY Fader");
		Button.setTab("edit");
		Button = controlP5.addButton("addCrossfaderButton", 1, 403, 100, 118,
				20);
		Button.setLabel("Add Crossfader");
		Button.setTab("edit");
		Button = controlP5.addButton("addLooper", 1, 403, 130, 118, 20);
		Button.setLabel("Add Looper");
		Button.setTab("edit");

		Button = controlP5.addButton("addAbletonTracks", 1, 275, 230, 118, 20);
		Button.setLabel("Add Ableton Tracks");
		Button.setTab("edit");

		Button = controlP5.addButton("addPushButtonButton", 1, 275, 200, 118,
				20);
		Button.setLabel("Add Push Button");
		Button.setTab("edit");
		Button = controlP5.addButton("addToggleButtonButton", 1, 275, 170, 118,
				20);
		Button.setLabel("Add Toggle Button");
		Button.setTab("edit");
		Button = controlP5.addButton("addNoteButtonButton", 1, 403, 170, 118,
				20);
		Button.setLabel("Add Note Button");
		Button.setTab("edit");
		Button = controlP5.addButton("addButtonMatrixButton", 1, 403, 200, 118,
				20);
		Button.setLabel("Add Button Matrix");
		Button.setTab("edit");

		// delete and edit buttons
		Button = controlP5.addButton("deleteModeButton", 1, 20, 100, 120, 20);
		Button.setId(3);
		Button.setLabel("Delete Control Object");
		Button.setTab("edit");
		Button = controlP5.addButton("editModeButton", 1, 20, 130, 120, 20);
		Button.setId(4);
		Button.setLabel("Enter Edit Mode");
		Button.setTab("edit");

		// cc and channel boxes
		Textfield tf = controlP5.addTextfield("ccEdit", 20, 170, 80, 20);
		tf.setLabel("CC / NOTE");
		tf.setColorActive(0xffefaa66);
		tf.setTab("edit");
		
		tf = controlP5.addTextfield("channelEdit", 120, 170, 80, 20);
		tf.setLabel("Channel");
		tf.setColorActive(0xffefaa66);
		tf.setTab("edit");

		// OSC In/Out Boxes
		tf = controlP5.addTextfield("OSCInChange", 20, 160, 80, 20);
		tf.setLabel("OSC In >> 8000");
		tf.setColorActive(0xffefaa66);
		tf.setTab("default");
		tf = controlP5.addTextfield("OSCOutChange", 120, 160, 80, 20);
		tf.setLabel("OSC Out >> 8080");
		tf.setColorActive(0xffefaa66);
		tf.setTab("default");

		t = controlP5.addTextlabel("label3", "INTERPOLATE FADING:", 20, 210);
		t.setColorValue(0);
		t.setTab("default");
		
		Radio r = controlP5.addRadio("fadeOnOff", 20, 225);
		r.add("On", 0);
		r.add("Off", 1);
		r.setTab("default");

		t = controlP5.addTextlabel("label4", "MONOME TYPE:", 160, 210);
		t.setColorValue(0);
		t.setTab("default");
		
		r = controlP5.addRadio("monomeType", 160, 225);
		r.add("64", 0);
		r.add("128", 1);
		r.add("256", 2);
		r.setTab("default");

		if (vm.midi.ioReady) {
			selectedInputNumber = 1;
			selectedOutputNumber = 0;
		}
		outputSelect = controlP5
				.addScrollList("outputSelect", 20, 100, 235, 40);
		outputSelect.setLabel("outputs");
		midiOutputDevices = RWMidi.getOutputDeviceNames();
		outputDevices = RWMidi.getOutputDevices();
		for (int i = 0; i < midiOutputDevices.length; i++) {
			controlP5.Button b = outputSelect.addItem("out" + i + " "
					+ midiOutputDevices[i], i);
			b.setId(100 + i);
			if (i == selectedOutputNumber)
				b.setColorBackground(0xffefaa66);
		}
		inputSelect = controlP5.addScrollList("inputSelect", 275, 100, 235, 40);
		inputSelect.setLabel("inputs");
		midiInputDevices = RWMidi.getInputDeviceNames();
		inputDevices = RWMidi.getInputDevices();
		for (int i = 0; i < midiInputDevices.length; i++) {
			controlP5.Button b = inputSelect.addItem("in" + i + " "
					+ midiInputDevices[i], i);
			b.setId(200 + i);
			if (i == selectedInputNumber)
				b.setColorBackground(0xffefaa66);
		}
		vm.returnToPlayMode();
	}

	public void abletonMode(boolean theValue) {
		MonoControl.abletonModeValue = theValue;
		setStatusLabel("Ableton mode: " + theValue);
	}

	public void draw() {
		background(0xffdddddd);
		monitorMidi();
		headline.draw(this);
		for (int i = 0; i < tickRegister.length; i++) { // send ticks to
			// registered
			// ControlObjects
			if (tickRegister[i] != null)
				tickRegister[i].tick();
		}
		if (frameCount % 7 == 0) {
			blinkOn = !blinkOn;
			vm.refresh();
		}
	}

	public void addFaderButton() {
		setStatusLabel("Add Fader: Hold a button, then press a button above the button you hold");
		vm.setMode(2, "fader");
	}

	public void addCrossfaderButton() {
		setStatusLabel("Add Crossfader: Hold a button, the aress a button on the right of the one you hold");
		vm.setMode(2, "crossfader");
	}

	public void addToggleButtonButton() {
		setStatusLabel("Add NoteButton: Press a button to create a ToggleButton");
		vm.setMode(3, "toggleButton");
	}

	public void addPushButtonButton() {
		setStatusLabel("Add PushButton: Press a button to create a PushButton");
		vm.setMode(3, "pushButton");
	}

	public void addNoteButtonButton() {
		setStatusLabel("Add NoteButton: Press a button to create a NoteButton");
		vm.setMode(3, "noteButton");
	}

	public void addButtonMatrixButton() {
		setStatusLabel("Add ButtonMatrix: Hold a button, then press a button up and rigth from the one you hold");
		vm.setMode(4);
	}

	public void addXYFaderButton() {
		setStatusLabel("Add XYFader: Hold a button, then press a button up and rigth from the one you hold");
		vm.setMode(5);
	}

	public void addLooper() {
		setStatusLabel("Add Looper: Hold a button, then press a button up and rigth from the one you hold");
		vm.setMode(6);
	}

	public void addAbletonTracks() {
		setStatusLabel("Add Ableton Tracks: Hold a button, then press a button up and rigth from the one you hold");
		vm.setMode(7);
	}

	public void deleteModeButton() {
		vm.setMode(1, "");
		setStatusLabel("Delete Mode: press a button on your monome to delete a control object");
	}

	public void editModeButton() {
		if (vm.getMode() != -1) {
			vm.setMode(-1);
			controlP5.controller("editModeButton").setLabel("Exit Edit Mode");
			controlP5.controller("editModeButton").setColorBackground(
					0xffefaa66);
			setStatusLabel("Edit Mode: Press a button to select a control object for editing");
		} else {
			controlP5.controller("editModeButton").setLabel("Enter Edit Mode");
			controlP5.controller("editModeButton").setColorBackground(
					0xff222222);
			vm.returnToPlayMode();
		}
	}

	public void fadeOnOff(int r) {
		fadeOn = (r == 0);
	}

	public void monomeType(int type) {
		if (type == 0) {
			monomeSizeX = 8;
			monomeSizeY = 8;
		} else if (type == 1) {
			monomeSizeX = 16;
			monomeSizeY = 8;
		} else if (type == 2) {
			monomeSizeX = 16;
			monomeSizeY = 16;
		}
	}

	public void ccEdit(String cc) {
		int ccval = PApplet.parseInt(cc);
		if (ccval < 0 || ccval > 127)
			setStatusLabel("Invalid CC Number!");
		else {
			if (vm.selected != null && vm.getMode() == -1)
				vm.selected.setCCValue(ccval);
			else {
				vm.nextObjectsCC = ccval;
				setStatusLabel("Next object created will have CC: "
						+ vm.nextObjectsCC + " Channel: "
						+ vm.nextObjectsChannel);
			}
		}
	}

	public void channelEdit(String c) {
		int cval = PApplet.parseInt(c) - 1;
		if (cval < 0 || cval > 16)
			setStatusLabel("Invalid Channel Number!");
		else {
			if (vm.selected != null && vm.getMode() == -1)
				vm.selected.setChannel(cval);
			else {
				vm.nextObjectsChannel = cval;
				setStatusLabel("Next object created will have CC: "
						+ vm.nextObjectsCC + " Channel: "
						+ (vm.nextObjectsChannel + 1));
			}
		}
	}

	public void OSCOutChange(String s) {
		OSCOutPort = PApplet.parseInt(s);
		vm.m = new Monome(vm);
		((Textfield) controlP5.controller("OSCOutChange"))
				.setLabel("OSC Out >> " + s);
	}

	public void OSCInChange(String s) {
		OSCInPort = PApplet.parseInt(s);
		vm.m = new Monome(vm);
		((Textfield) controlP5.controller("OSCInChange")).setLabel("OSC In >> "
				+ s);
	}

	public void saveName(String filename) {
		println("Saving Layout...");
		exportVMLayout(vm, filename);
	}

	public void loadName(String filename) {
		println("Loading Layout: " + filename);
		importVMLayout(filename);
	}

	// function to save and reload the current layout after midi input has
	// changed
	public void reloadLayout() {
		exportVMLayout(vm, "temp.xml");
		importVMLayout("temp.xml");
	}

	// light up selected input/output
	public void controlEvent(ControlEvent theEvent) {
		if (theEvent.controller().id() > 99 && theEvent.controller().id() < 200) {
			setMidiOut((int) theEvent.controller().value());
		} else if (theEvent.controller().id() > 199
				&& theEvent.controller().id() < 300) {
			setMidiIn((int) theEvent.controller().value());
			reloadLayout();
		}
	}

	void setMidiOut(int value) {
		if (value != -1)
			selectedOutputNumber = value;
		vm.midi.setOutputDevice(outputDevices[selectedOutputNumber]);

		for (int i = 0; i < midiOutputDevices.length; i++) {
			if (i == selectedOutputNumber)
				controlP5.controller("out" + i + " " + midiOutputDevices[i])
						.setColorBackground(0xffefaa66);
			else
				controlP5.controller("out" + i + " " + midiOutputDevices[i])
						.setColorBackground(0xff222222);
		}
	}

	void setMidiIn(int value) {
		if (value != -1)
			selectedInputNumber = value;
		vm.midi.setInputDevice(inputDevices[selectedInputNumber]);

		for (int i = 0; i < midiInputDevices.length; i++) {
			if (i == selectedInputNumber)
				controlP5.controller("in" + i + " " + midiInputDevices[i])
						.setColorBackground(0xffefaa66);
			else
				controlP5.controller("in" + i + " " + midiInputDevices[i])
						.setColorBackground(0xff222222);
		}
	}

	// lights up in/out squares
	public void monitorMidi() {
		if (time1 + 100 >= millis())
			fill(0xff9090ff);
		else
			fill(0xff222222);
		rect(width - 50, 20, 30, 30);
		if (time2 + 100 >= millis())
			fill(0xffa0ffff);
		else
			fill(0xff222222);
		rect(width - 100, 20, 30, 30);
	}

	static public void blinkInputLight() {
		time1 = System.currentTimeMillis();
	}

	static public void blinkOutputLight() {
		time2 = System.currentTimeMillis();
	}

	static public void setStatusLabel(String s) {
		statusLabel.setValue(s);
	}

	public void exportVMLayout(ViewManager vm, String filename) {
		if (filename == "" || filename == " ")
			filename = "Untitled.xml";
		Element layout = new Element("MonoControlLayout");
		Element object;
		Element view;
		for (int i = 0; i < vm.views.length; i++) {
			view = new Element("view");
			view.setAttribute("viewID", String.valueOf(i));
			for (int j = 0; j < vm.views[i].objects.size(); j++) {
				ControlObject co = vm.views[i].objects.get(j);
				object = new Element(co.getType());
				object.setAttribute("channel", String.valueOf(co.getChannel()));
				object.setAttribute("sizey", String.valueOf(co.getSizeY()));
				object.setAttribute("sizex", String.valueOf(co.getSizeX()));
				object.setAttribute("posy", String.valueOf(co.getPosY()));
				object.setAttribute("posx", String.valueOf(co.getPosX()));
				object.setAttribute("cc", String.valueOf(co.getCCValue()));
				object = co.toJDOMXMLElement(object);

				view.addContent(object);
			}
			layout.addContent(view);
		}
		org.jdom.output.XMLOutputter fmt = new XMLOutputter();
		try {
			FileWriter fileWriter = new FileWriter(filename);
			fmt.output(layout, fileWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void importVMLayout(String filename) {

		try {
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(new File(filename));
			importVMLayout(doc);
		} catch (Exception e) {
			e.printStackTrace();
			MonoControl.setStatusLabel(filename + ": File not found!");
		}

	}

	@SuppressWarnings("unchecked")
	public void importVMLayout(Document layout) {

		vm.clearViews();
		vm.midi.input.closeMidi();
		vm.midi.output.closeMidi();
		setMidiIn(-1);
		setMidiOut(-1);

		int posx, posy, sizex, sizey, channel, cc;
		if (layout.getRootElement().getName().equals("MonoControlLayout")) {
			List<Element> itr = layout.getRootElement().getChildren();

			for (Element view : itr) {
				int viewID = Integer.parseInt(view.getAttributeValue("viewID"));
				for (Element object : (List<Element>) view.getChildren()) {
					channel = Integer.parseInt(object
							.getAttributeValue("channel"));
					sizey = Integer.parseInt(object.getAttributeValue("sizey"));
					sizex = Integer.parseInt(object.getAttributeValue("sizex"));
					posy = Integer.parseInt(object.getAttributeValue("posy"));
					posx = Integer.parseInt(object.getAttributeValue("posx"));
					cc = Integer.parseInt(object.getAttributeValue("cc"));

					ControlObject co = vm.addControlObject(object.getName(),
							viewID, cc, channel, posx, posy, sizex, sizey);
					co.loadJDOMXMLElement(object); // load specific xml data and
													// initialize object
				}
			}

		}
	}

	public void exit() {
		vm.midi.input.closeMidi();
		vm.midi.output.closeMidi();
		super.exit();
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
