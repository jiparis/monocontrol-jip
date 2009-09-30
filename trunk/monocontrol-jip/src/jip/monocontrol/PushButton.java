package jip.monocontrol;

import org.jdom.Element;

import rwmidi.Note;

public class PushButton extends Button {
	public PushButton(MidiObject midi, int midiChannel, int ccValue,
			int positionX, int positionY, int sizeX, int sizeY) {
		super(midi, midiChannel, ccValue, positionX, positionY);
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
		return "pushButton";
	}

	@Override
	public void noteOnReceived(Note n) {
	}

	@Override
	public void noteOffReceived(Note n) {

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
