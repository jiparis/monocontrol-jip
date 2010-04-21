package jip.monocontrol;

import javax.sound.midi.ShortMessage;

import org.jdom.Element;

public class Fader extends ControlObject implements CCListener{
	private int fadeTo;

	public Fader(int midiChannel, int ccValue, int positionX,
			int positionY, int sizeX, int sizeY) {
		super(midiChannel, ccValue, positionX, positionY, sizeX, sizeY);
	}

	@Override
	public int[][] getDrawMatrix() {
		if (blink) {
			return getBlinkMatrix();
		}
		int[][] matrix = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		for (int i = positionY; i < (sizeY + positionY); i++) {
			if (value >= Math.round(127.0f / (sizeY - 1) * (i - positionY)))
				matrix[positionX][i] = 1;
		}
		return matrix;
	}

	@Override
	public void buttonEvent(int x, int y, int pressed) {
		if (MonoControl.fadeOn) {
			if (pressed > 0) {
				registerForTick();
				fadeTo = (int) Math
						.ceil(127.0f / (sizeY - 1) * (y - positionY));
			} else {
				unregisterFromTick();
				setValue(fadeTo);
			}
		} else {
			setValue((int) Math.ceil(127.0f / (sizeY - 1) * (y - positionY)));
		}
	}

	@Override
	public void tick() {
		if (value < fadeTo) {
			setValue(Math.min(value + MonoControl.fadeSpeed, fadeTo));
		} else if (value > fadeTo) {
			setValue(Math.max(value - MonoControl.fadeSpeed, fadeTo));
		} else {
			unregisterFromTick();
		}
		ViewManager.singleton.refresh();
	}

	public void controllerChangeReceived(ShortMessage rc) {
		if (rc.getData1() == ccValue) {
			updateValue(rc.getData2());
			ViewManager.singleton.refresh();
		}
	}
	
	@Override
	public String getType() {
		return "fader";
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
