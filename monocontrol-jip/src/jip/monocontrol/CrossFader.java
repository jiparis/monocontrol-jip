package jip.monocontrol;

import javax.sound.midi.ShortMessage;

import org.jdom.Element;

public class CrossFader extends ControlObject implements CCListener {
	private int fadeTo;

	public CrossFader(int midiChannel, int ccValue,
			int positionX, int positionY, int sizeX, int sizeY) {
		super(midiChannel, ccValue, positionX, positionY, sizeX, sizeY);
	}

	@Override
	public int[][] getDrawMatrix() {
		if (blink) {
			return getBlinkMatrix();
		}
		int[][] matrix = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		for (int i = positionX; i < (sizeX + positionX); i++) {
			if ((value >= 127 / (sizeX - 1) * (i - positionX) && value < 127
					/ (sizeX - 1) * (i - positionX + 1)))
				matrix[i][positionY] = 1;
		}
		return matrix;
	}

	@Override
	public void buttonEvent(int x, int y, int pressed) {
		if (MonoControl.fadeOn) {
			if (pressed > 0) {
				registerForTick();
				fadeTo = (int) Math.ceil((127.0f / (sizeX - 1))
						* (x - positionX));
			} else {
				unregisterFromTick();
				setValue(fadeTo);
			}
		} else {
			setValue((int) Math.ceil((127.0f / (sizeX - 1)) * (x - positionX)));
		}
	}

	public void controllerChangeReceived(ShortMessage rc) {
	if (rc.getData1() == cc) {
		updateValue(rc.getData2());
		ViewManager.singleton.refresh();
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

	@Override
	public String getType() {
		return "crossfader";
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
