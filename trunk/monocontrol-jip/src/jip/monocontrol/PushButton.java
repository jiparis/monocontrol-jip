package jip.monocontrol;

import javax.sound.midi.ShortMessage;

import org.jdom.Element;

public class PushButton extends Button {
	public PushButton(int midiChannel, int ccValue,
			int positionX, int positionY, int sizeX, int sizeY) {
		super(midiChannel, ccValue, positionX, positionY);
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
	public void noteOnReceived(ShortMessage n) {
	}

	@Override
	public void noteOffReceived(ShortMessage n) {

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
