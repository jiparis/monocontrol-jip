package jip.monocontrol;

import javax.sound.midi.ShortMessage;

import org.jdom.Element;

public class XYFader extends ControlObject {
	private int ccX, ccY, valueX, valueY, currentX, currentY, fadeToX, fadeToY;

	public XYFader(int midiChannel, int ccValue,
			int positionX, int positionY, int sizeX, int sizeY) {
		super(midiChannel, ccValue, positionX, positionY, sizeX, sizeY);
		ccX = ccValue;
		ccY = ccValue + 1;
		currentY = currentX = 0;
	}

	@Override
	public int[][] getDrawMatrix() {
		if (blink) {
			return getBlinkMatrix();
		}
		int[][] matrix = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		for (int i = positionX; i < (sizeX + positionX); i++) {
			for (int j = positionY; j < (sizeY + positionY); j++) {
				if ((valueX >= 127 / (sizeX - 1) * (i - positionX) && valueX < 127
						/ (sizeX - 1) * (i - positionX + 1))
						|| (valueY >= 127 / (sizeY - 1) * (j - positionY) && valueY < 127
								/ (sizeY - 1) * (j - positionY + 1)))
					matrix[i][j] = 1;
			}
		}
		return matrix;
	}

	@Override
	public void buttonEvent(int x, int y, int pressed) {
		if (MonoControl.fadeOn) {
			if (pressed > 0) {
				fadeToX = (int) Math.ceil(127.0f / (sizeX - 1)
						* (x - positionX));
				fadeToY = (int) Math.ceil(127.0f / (sizeY - 1)
						* (y - positionY));
				registerForTick();
			} else {
				unregisterFromTick();
				if (x != currentX)
					setValueX(fadeToX);
				if (y != currentY)
					setValueY(fadeToY);
				currentX = x;
				currentY = y;
			}
		} else {
			if (pressed > 0) {
				if (x != currentX) {
					setValueX((int) Math.ceil(127.0f / (sizeX - 1)
							* (x - positionX)));
					currentX = x;
				}
				if (y != currentY) {
					setValueY((int) Math.ceil(127.0f / (sizeY - 1)
							* (y - positionY)));
					currentY = y;
				}
			}
		}
	}

	@Override
	public void tick() {
		if (valueX < fadeToX) {
			setValueX(Math.min(valueX + MonoControl.fadeSpeed, fadeToX));
		} else if (valueX > fadeToX) {
			setValueX(Math.max(valueX - MonoControl.fadeSpeed, fadeToX));
		}
		if (valueY < fadeToY) {
			setValueY(Math.min(valueY + MonoControl.fadeSpeed, fadeToY));
		} else if (valueY > fadeToY) {
			setValueY(Math.max(valueY - MonoControl.fadeSpeed, fadeToY));
		}
		if (valueX == fadeToX && valueY == fadeToY) {
			unregisterFromTick();
		}
		MonoControl.vm.refresh();
	}

	@Override
	public void controllerChangeReceived(ShortMessage rc) {
		if (rc.getData1() == ccX) {
			MonoControl.blinkInputLight();
			updateValueX(rc.getData2());
			MonoControl.vm.refresh();
		} else if (rc.getData1() == ccY) {
			MonoControl.blinkInputLight();
			updateValueY(rc.getData2());
			MonoControl.vm.refresh();
		}
	}

	public void setValueX(int value) {
		valueX = value;
		MidiObject.sendCC(midiChannel, ccX, value);
	}

	public void setValueY(int value) {
		valueY = value;
		MidiObject.sendCC(midiChannel, ccY, value);
	}

	@Override
	public void noteOnReceived(ShortMessage n) {
	}

	@Override
	public void noteOffReceived(ShortMessage n) {

	}

	@Override
	public void updateValue(int value) {
	}

	public void updateValueX(int value) {
		valueX = value;
	}

	public void updateValueY(int value) {
		valueY = value;
	}

	@Override
	public String getType() {
		return "xyfader";
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
