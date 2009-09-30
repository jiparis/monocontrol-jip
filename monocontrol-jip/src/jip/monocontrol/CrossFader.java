package jip.monocontrol;

import org.jdom.Element;

import rwmidi.Note;

public class CrossFader extends ControlObject {
	private int fadeTo;

	public CrossFader(MidiObject midi, int midiChannel, int ccValue,
			int positionX, int positionY, int sizeX, int sizeY) {
		super(midi, midiChannel, ccValue, positionX, positionY, sizeX, sizeY);
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

	@Override
	public void noteOnReceived(Note n){
	}
	
	@Override
	public void noteOffReceived(Note n){
		
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
		MonoControl.vm.refresh();
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
