package jip.monocontrol;

import org.jdom.Element;

public class AbletonTracks extends ControlObject {
	int controlCol;
	int[] lastNoteOn;
	public AbletonTracks(MidiObject midi, int midiChannel, int noteValue,
			int positionX, int positionY, int sizeX, int sizeY) {
		super(midi, midiChannel, noteValue, positionX, positionY, sizeX, sizeY);	
	    this.noteValue = noteValue;
		controlCol = positionX + sizeX - 1;
		lastNoteOn = new int[sizeX - 1];
		for (int i=0;i<lastNoteOn.length;i++) lastNoteOn[i] = -1;
	}

	@Override
	public void buttonEvent(int x, int y, int pressed) {
		// calculate relative indexes
		int xi = x - positionX;
		int yi = y - positionY;
		if (x < controlCol) {
			if (pressed > 0) 
				press(xi, yi);
			else 
				release(xi, yi);
		}		
		else 
			if (pressed > 0) pressControl(xi, yi);
	}

	public void press (int xi, int yi){
		int onVal = noteValue + (sizeY * xi + yi);
		midi.sendNoteOn(midiChannel, onVal, 127);
		lastNoteOn[xi] = yi;
	}
	
	public void release (int xi, int yi){
		//midi.sendNoteOff(midiChannel, noteValue + (sizeX * xi + yi));
	}
	
	public void pressControl(int xi, int yi){		
		//if (lastNoteOn[yi] > -1){
		int invertedyi = sizeY - yi - 1;
		if (invertedyi < sizeX - 1){
			int onVal = noteValue + (sizeY * (sizeX - 1)) + invertedyi;
			midi.sendNoteOn(midiChannel, onVal, 127);
			lastNoteOn[sizeY - yi - 1] = -1;
		}		
	}

	@Override
	public int[][] getDrawMatrix() {
		if (blink) {
			return getBlinkMatrix();
		}
		int[][] matrix = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		for (int i=0; i<sizeX - 1; i++){
			if (lastNoteOn[i]>-1) {
				matrix[i + positionX][lastNoteOn[i]] = 1;
				matrix[positionX + sizeX-1][sizeY - i - 1] = 1;
			}
			
		}
		
		return matrix;
	}

	@Override
	public String getType() {
		return "abletonTracks";
	}

	@Override
	public void noteOnReceived(rwmidi.Note n) {
		super.noteOnReceived(n);
		int pitch = n.getPitch();
		int vel = n.getVelocity();
		if (pitch >= noteValue && pitch < noteValue + ((sizeX - 1) * sizeY)){
			int xi = (pitch - noteValue) / sizeY;
			int yi = pitch - (noteValue + (sizeY * xi));
			if (vel==127 || vel==1) //Play or continue. Other values (0, 64:off; 126:cue)
				lastNoteOn[xi] = yi;		
			MonoControl.vm.refresh();
		}
	}
	
	@Override
	public void controllerChangeReceived(rwmidi.Controller rc) {		
	}
	
	
	
	@Override
	public void loadJDOMXMLElement(Element el) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element toJDOMXMLElement(Element el) {
		// TODO Auto-generated method stub
		return el;
	}

}
