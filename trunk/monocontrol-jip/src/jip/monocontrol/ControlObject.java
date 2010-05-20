package jip.monocontrol;

import org.jdom.Element;

public abstract class ControlObject {
	protected boolean blink;
	protected long blinkTimer;
	protected int midiChannel, cc, positionX, positionY, sizeX,
			sizeY, value, tickID;

	
	public ControlObject(int midiChannel, int cc,
			int positionX, int positionY, int sizeX, int sizeY) {
		this.midiChannel = midiChannel;
		this.cc = cc;
		this.positionX = positionX;
		this.positionY = positionY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		value = 0;
		tickID = -1;
										// objects
		blink = false;
		blinkTimer = System.currentTimeMillis() + 1000;
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
		MidiObject.sendCC(midiChannel, cc, value);
	}

	public void updateValue(int value) {
		this.value = value;
	}

	public void setCC(int cc) {
		this.cc = cc;
	}

	public void setChannel(int channel) {
		this.midiChannel = channel;
	}

	public int getCC() {
		return cc;
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
		int[][] matrix = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		
		for (int i = positionX; i < (sizeX + positionX); i++) {
			for (int j = positionY; j < (sizeY + positionY); j++) {
				matrix[i][j] = 1;
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
