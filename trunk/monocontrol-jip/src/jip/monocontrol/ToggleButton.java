package jip.monocontrol;

import javax.sound.midi.ShortMessage;

import org.jdom.Element;

public class ToggleButton extends Button implements CCListener{
	public ToggleButton(int midiChannel, int ccValue,
			int positionX, int positionY, int sizeX, int sizeY) {
		super(midiChannel, ccValue, positionX, positionY);
	}

	@Override
	public void buttonEvent(int x, int y, int pressed) {
		if (pressed > 0) {
			if (value >= 64)
				setValue(0);
			else
				setValue(127);
		}
	}

	public void controllerChangeReceived(ShortMessage rc) {
		if (rc.getData1() == ccValue) {
			updateValue(rc.getData2());
			MonoControl.vm.refresh();
		}
	}

	@Override
	public String getType() {
		return "toggleButton";
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
