package jip.monocontrol;

import javax.sound.midi.ShortMessage;

import org.jdom.Element;

public class NoteButton extends Button implements NoteListener {

	public NoteButton(int midiChannel, int noteValue, int positionX,
			int positionY, int sizeX, int sizeY) {
		super(midiChannel, -1, positionX, positionY);
		this.noteValue = noteValue;
	}

	@Override
	public void setValue(int value) {
		this.value = value;
		if (value > 0)
			MidiObject.sendNoteOn(midiChannel, noteValue, value);
		else
			MidiObject.sendNoteOff(midiChannel, noteValue);
	}

	@Override
	public void buttonEvent(int x, int y, int pressed) {
		if (pressed > 0)
			setValue(127);
		else
			setValue(0);
	}

	@Override
	public String getType() {
		return "noteButton";
	}

	public void setNoteValue(int v) {
		this.noteValue = v;
	}

	@Override
	public int getCCValue() { // returns notevalue when asked for cc..
		return noteValue;
	}

	@Override
	public int getNoteValue() {
		return noteValue;
	}

	public void noteOnReceived(ShortMessage n) {
		if (n.getData1() == noteValue) {
			MonoControl.blinkInputLight();
			updateValue(n.getData2());
			ViewManager.singleton.refresh();
		}
	}
	
	public void noteOffReceived(ShortMessage n) {
		if (n.getData1() == noteValue) {
			MonoControl.blinkInputLight();
			updateValue(0);
			ViewManager.singleton.refresh();
		}
	}

	@Override
	public void loadJDOMXMLElement(Element el) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element toJDOMXMLElement(Element el) {
		return el;
	}
}
