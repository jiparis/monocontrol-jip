package jip.monocontrol;
import java.util.Vector;

import controlP5.Textfield;

public class ViewManager {
	int mode = 0; // 0 means play mode, 1 delete mode.. see buttonEvent()
	int nextObjectsCC = 0;
	int nextObjectsChannel = 0;
	String objectIdent = "";
	ControlObject selected; // stores the selected ConrtrolObject
	Monome m;
	MidiObject midi;
	View[] views;
	int activeView;
	int holdButtonX = -1; // saves coordinates of the last button beeing held -1
							// if no button is held
	int holdButtonY = -1;

	public ViewManager() {
		m = new Monome(this);
		midi = new MidiObject(this);
		views = new View[16];
		for (int i = 0; i < 16; i++) {
			views[i] = new View("v" + i);
		}
	}

	public void buttonEvent(int x, int y, int pressed) {
		if (pressed > 0) {
			if (x >= (MonoControl.monomeSizeX - 1)) { // pressed button is
														// navigation button
				activeView = MonoControl.monomeSizeY - 1 - y;
				refresh();
				return;
			}
		}
		switch (mode) {
		case -1: // edit mode
			if (pressed > 0) {
				ControlObject[] sel = getActiveView().getControlObject(x, y);
				if (sel.length > 0) {
					if (selected != null)
						selected.setBlink(false);
					MonoControl
							.setStatusLabel(sel.length
									+ " control objects belong to pressed button, first is selected.");
					selected = sel[0];
					selected.setBlink(true);
					((Textfield) MonoControl.controlP5.controller("ccEdit"))
							.setValue("" + selected.getCCValue());
					((Textfield) MonoControl.controlP5
							.controller("channelEdit")).setValue(""
							+ (selected.getChannel() + 1));
				} else {
					selected.setBlink(false);
					selected = null;
					MonoControl
							.setStatusLabel("no control objects belong to pressed button...");
				}
			}
			break;
		case 1: // delete mode
			if (pressed > 0) {
				getActiveView().deleteControlObject(x, y); // deletes the object
															// user tapped on.
				returnToPlayMode();
				refresh();
			}
			break;
		case 2: // add fader/crossfader mode
			if (pressed > 0) {
				if (holdButtonX >= 0 && holdButtonY >= 0) { // if one button is
															// already held,
															// create the fader
					addControlObject(objectIdent, activeView, nextObjectsCC,
							nextObjectsChannel, holdButtonX, holdButtonY,
							(x - holdButtonX) + 1, (y - holdButtonY) + 1);
					returnToPlayMode();
				} else {
					holdButtonX = x; // else: set one button as held
					holdButtonY = y;
				}
			} else if (holdButtonX == x && holdButtonY == y) {
				returnToPlayMode();
			}
			break;
		case 3: // add button mode
			if (pressed > 0) {
				addControlObject(objectIdent, activeView, nextObjectsCC,
						nextObjectsChannel, x, y, 1, 1);
				returnToPlayMode();
			}
			break;
		case 4: // add button matrix mode
			if (pressed > 0) {
				if (holdButtonX >= 0 && holdButtonY >= 0) { // if one button is
															// already held,
															// create the matrix
					int noteCount = nextObjectsCC;
					for (int j = holdButtonY; j <= y; j++) {
						for (int i = holdButtonX; i <= x; i++) {
							if (objectIdent.equals("noteButton")) {
								addControlObject(objectIdent, activeView,
										noteCount, nextObjectsChannel, i, j, 1,
										1);
								noteCount++;
							} else {
								int[] freeChanCC = searchFreeCC();
								addControlObject(objectIdent, activeView,
										freeChanCC[1], freeChanCC[0], i, j, 1,
										1);
							}
						}
					}
					returnToPlayMode();
				} else {
					holdButtonX = x; // else: set one button as held
					holdButtonY = y;
				}
			} else if (holdButtonX == x && holdButtonY == y) {
				returnToPlayMode();
			}
			break;
		case 5:
			if (pressed > 0) {
				if (holdButtonX >= 0 && holdButtonY >= 0) { // if one button is
															// already held,
															// create the
															// xyfader
					addControlObject("xyfader", activeView, nextObjectsCC,
							nextObjectsChannel, holdButtonX, holdButtonY,
							(x - holdButtonX) + 1, (y - holdButtonY) + 1);
					returnToPlayMode();
				} else {
					holdButtonX = x; // else: set one button as held
					holdButtonY = y;
				}
			} else if (holdButtonX == x && holdButtonY == y) {
				returnToPlayMode();
			}
			break;
		case 6: //looper
			if (pressed > 0) {
				if (holdButtonX >= 0 && holdButtonY >= 0) { // if one button is
															// already held,
															// create the
															// xyfader
					addControlObject("looper", activeView, nextObjectsCC,
							nextObjectsChannel, holdButtonX, holdButtonY,
							(x - holdButtonX) + 1, (y - holdButtonY) + 1);
					returnToPlayMode();
				} else {
					holdButtonX = x; // else: set one button as held
					holdButtonY = y;
				}
			} else if (holdButtonX == x && holdButtonY == y) {
				returnToPlayMode();
			}
			break;
		case 7: //ableton tracks
			if (pressed > 0) {
				if (holdButtonX >= 0 && holdButtonY >= 0) { // if one button is
															// already held,
															// create the
															// xyfader
					addControlObject("abletonTracks", activeView, nextObjectsCC,
							nextObjectsChannel, holdButtonX, holdButtonY,
							(x - holdButtonX) + 1, (y - holdButtonY) + 1);
					returnToPlayMode();
				} else {
					holdButtonX = x; // else: set one button as held
					holdButtonY = y;
				}
			} else if (holdButtonX == x && holdButtonY == y) {
				returnToPlayMode();
			}
			break;
		default:
			getActiveView().buttonEvent(x, y, pressed); // passes event to
														// active view
			refresh();
			break;
		}
	}

	public void returnToPlayMode() {
		mode = 0;
		if (selected != null)
			selected.setBlink(false);
		selected = null;
		holdButtonX = holdButtonY = -1;
		int[] freeChanCC = searchFreeCC();
		nextObjectsChannel = freeChanCC[0];
		nextObjectsCC = freeChanCC[1];
		// setStatusLabel("Next object created will have CC: "+vm.nextObjectsCC+" Channel: "
		// +vm.nextObjectsChannel);
	}

	public void refresh() {
		int[][] matrix = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		// Navigation
		matrix[MonoControl.monomeSizeX - 1][MonoControl.monomeSizeY - 1
				- activeView] = 1;
		// ActiveView
		matrix = Matrix.melt(MonoControl.monomeSizeX, MonoControl.monomeSizeY,
				matrix, getActiveView().getDrawMatrix());

		m.refresh(matrix);
	}

	public void refresh(int[][] matrix) {
		// Navigation
		matrix[MonoControl.monomeSizeX - 1][MonoControl.monomeSizeY - 1
				- activeView] = 1;
		// ActiveView
		matrix = Matrix.melt(MonoControl.monomeSizeX, MonoControl.monomeSizeY,
				matrix, getActiveView().getDrawMatrix());

		m.refresh(matrix);
	}

	public View getActiveView() {
		return views[activeView];
	}

	public ControlObject[] getControlObjectsByCC(int ccValue) { // returns a
																// list of
																// control
																// objects with
																// the
																// particular cc
																// value
		Vector<ControlObject> retC = new Vector<ControlObject>();
		for (int i = 0; i < views.length; i++) {
			for (int j = 0; j < views[i].objects.size(); j++) {
				if (views[i].objects.get(j).getCCValue() == ccValue)
					retC.add(views[i].objects.get(j));
			}
		}
		return (ControlObject[]) retC.toArray();
	}

	public ControlObject[] getControlObjectsByNote(int noteValue) {
		Vector<ControlObject> retC = new Vector<ControlObject>();
		for (int i = 0; i < MonoControl.monomeSizeY; i++) {
			for (int j = 0; j < views[i].objects.size(); j++) {
				if (views[i].objects.get(j).getNoteValue() == noteValue) // returns
																			// -1
																			// for
																			// none
																			// note
																			// buttons
					retC.add(views[i].objects.get(j));
			}
		}
		return (ControlObject[]) retC.toArray();
	}

	public ControlObject addControlObject(String type, int viewID, int cc, int channel,
			int posx, int posy, int sizex, int sizey) {
		
		ControlObject co = null;
		if (viewID < 0 || cc < 0 || cc > 127 || channel < 0 || channel > 15
				|| posx < 0 || posy < 0 || sizex < 0 || sizey < 0) {
			MonoControl
					.setStatusLabel("Invalid Parameters. Could not create ControlObject: "
							+ type);
		} else {
			if (type.equals("fader")) {
				if (sizex == 1)
					co = new Fader(midi, channel, cc,
							posx, posy, sizex, sizey);
			} else if (type.equals("crossfader")) {
				if (sizey == 1)
					co = new CrossFader(midi,
							channel, cc, posx, posy, sizex, sizey);
			} else if (type.equals("pushButton"))
				co = new PushButton(midi, channel,
						cc, posx, posy, sizex, sizey);
			else if (type.equals("toggleButton"))
				co = new ToggleButton(midi, channel,
						cc, posx, posy, sizex, sizey);
			else if (type.equals("noteButton"))
				co = new NoteButton(midi, channel,
						cc, posx, posy, sizex, sizey);
			else if (type.equals("xyfader"))
				co = new XYFader(midi, channel, cc,
						posx, posy, sizex, sizey);
			else if (type.equals("looper"))
				co = new Looper(midi, channel, cc,
						posx, posy, sizex, sizey);
			else if (type.equals("abletonTracks"))
				co = new AbletonTracks(midi, channel, cc,
						posx, posy, sizex, sizey);
			else
				System.out.println("Invalid object type: " + type);
			if (co!=null)
				views[viewID].addControlObject(co);
			
			refresh();
		}
		return co;
	}

	public void clearViews() {		
		views = new View[16];
		for (int i = 0; i < 16; i++) {
			views[i] = new View("v" + i);
		}
	}

	public void setMode(int mode, String objectIdent) {
		if (this.mode == -1 && mode != -1) { // make sure edit mode returns to
												// normal when quitting
			MonoControl.controlP5.controller("editModeButton").setLabel(
					"Enter Edit Mode");
			MonoControl.controlP5.controller("editModeButton")
					.setColorBackground(0xff222222);
		}
		this.mode = mode;
		this.objectIdent = objectIdent;
	}

	public void setMode(int mode) {
		if (this.mode == -1 && mode != -1) { // make sure edit mode returns to
												// normal when quitting
			MonoControl.controlP5.controller("editModeButton").setLabel(
					"Enter Edit Mode");
			MonoControl.controlP5.controller("editModeButton")
					.setColorBackground(0xff222222);
		}
		if (mode == 4 && this.mode != 3) {
			MonoControl
					.setStatusLabel("Click on a specific button type first..");
		} else
			this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	public int[] searchFreeCC() { // returns the loest free cc number
		int[] ChanCC = { -1, -1 };
		boolean free = true;
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 128; y++) {
				free = true;
				for (int i = 0; i < MonoControl.monomeSizeY; i++) {
					for (int j = 0; j < views[i].objects.size(); j++) {
						if (views[i].objects.get(j).getCCValue() == y
								&& views[i].objects.get(j).getChannel() == x) {
							free = false;
							break;
						}
					}
					if (!free)
						break;
				}
				if (free) {
					ChanCC[0] = x;
					ChanCC[1] = y;
					break;
				}
			}
			if (free)
				break;
		}
		return ChanCC;
	}

}
