package jip.monocontrol;

import org.jdom.Element;

public abstract class ControlObject {
	public MidiObject midi;
	protected boolean blink;
	protected long blinkTimer;
	protected int midiChannel, ccValue, noteValue, positionX, positionY, sizeX,
			sizeY, value, tickID;

	
	public ControlObject(MidiObject midi, int midiChannel, int ccValue,
			int positionX, int positionY, int sizeX, int sizeY) {
		this.ccValue = ccValue;
		this.positionX = positionX;
		this.positionY = positionY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		value = 0;
		tickID = -1;
		this.midiChannel = midiChannel;
		this.midi = midi;
		midi.plug(this, midiChannel); // registers midi event callbacks for
										// objects
		blink = true;
		blinkTimer = System.currentTimeMillis() + 1000;
	}

	public void controllerChangeReceived(rwmidi.Controller rc) {
		if (rc.getCC() == ccValue) {
			MonoControl.blinkInputLight();
			updateValue(rc.getValue());
			MonoControl.vm.refresh();
		}
	}

	public void noteOnReceived(rwmidi.Note n) {
		if (n.getPitch() == noteValue) {
			MonoControl.blinkInputLight();
			updateValue(120);
			MonoControl.vm.refresh();
		}
	}

	public void noteOffReceived(rwmidi.Note n) {
		if (n.getPitch() == noteValue) {
			MonoControl.blinkInputLight();
			updateValue(0);
			MonoControl.vm.refresh();
		}
	}

	public boolean buttonIsElement(int x, int y) {
		if (x >= positionX && x < (positionX + sizeX)) {
			if (y >= positionY && y < (positionY + sizeY)) {
				return true;
			}
		}
		return false;
	}

	public void setValue(int value) {
		this.value = value;
		midi.sendCC(midiChannel, ccValue, value);
	}

	public void updateValue(int value) {
		this.value = value;
	}

	public void setCCValue(int cc) {
		this.ccValue = cc;
	}

	public void setChannel(int channel) {
		this.midiChannel = channel;
	}

	public int getNoteValue() { // return -1 for none note objects, override in
								// noteButton
		return -1;
	}

	public int getCCValue() {
		return ccValue;
	}

	public int getChannel() {
		return midiChannel;
	}

	public int getPosX() {
		return positionX;
	}

	public int getPosY() {
		return positionY;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	protected void registerForTick() {
		if (tickID == -1) {
			for (int i = 0; i < MonoControl.tickRegister.length; i++) {
				if (MonoControl.tickRegister[i] == null) {
					MonoControl.tickRegister[i] = this;
					tickID = i;
					break;
				}
			}
		}
	}

	protected void unregisterFromTick() {
		if (tickID != -1) {
			MonoControl.tickRegister[tickID] = null;
			tickID = -1;
		}
	}

	protected int[][] getBlinkMatrix() {
		if (blinkTimer > 0 && blinkTimer < System.currentTimeMillis()) {
			blink = false;
			blinkTimer = -1;
		}
		int[][] matrix = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		if (MonoControl.blinkOn) {
			for (int i = positionX; i < (sizeX + positionX); i++) {
				for (int j = positionY; j < (sizeY + positionY); j++) {
					matrix[i][j] = 1;
				}
			}
		}
		return matrix;
	}

	public void setBlink(boolean on) {
		blink = on;
	}

	public void setBlink(int time) {
		blink = true;
		blinkTimer = System.currentTimeMillis() + time;
	}

	public void tick() {
	}

	public abstract int[][] getDrawMatrix();

	public abstract void buttonEvent(int x, int y, int pressed);

	public abstract String getType();
	
	public abstract void loadJDOMXMLElement(Element el);

	public abstract Element toJDOMXMLElement(Element el);
	
}
