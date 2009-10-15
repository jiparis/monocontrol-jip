package jip.monocontrol;

import javax.sound.midi.ShortMessage;

import org.jdom.Element;

public class AbletonTracks extends ControlObject 
	implements NoteListener, ClockListener {
	int controlCol;
	int[] lastNoteOn;
	PatternRecorder[] prs;
	
	int STEPS = 32;
	
	public AbletonTracks(int midiChannel, int noteValue,
			int positionX, int positionY, int sizeX, int sizeY) {
		super(midiChannel, noteValue, positionX, positionY, sizeX, sizeY);	
	    this.noteValue = noteValue;
		controlCol = positionX + sizeX - 1;
		lastNoteOn = new int[sizeX - 1];
		for (int i=0;i<lastNoteOn.length;i++) lastNoteOn[i] = -1;
		if (sizeY >= sizeX) prs = new PatternRecorder[sizeY - sizeX + 1];
		for (int i = 0; i < prs.length; i++) prs[i] = new PatternRecorder(this, STEPS);
	}

	@Override
	public void buttonEvent(int x, int y, int pressed) {
		// calculate relative indexes
		int xi = x - positionX;
		int yi = y - positionY;
		if (x < controlCol) {
			if (pressed > 0) {			
				press(xi, yi);
				for (PatternRecorder pr: prs)
					pr.buttonEvent(xi, yi);
			}
			else 
				release(xi, yi);
		}		
		else 
			if (pressed > 0) pressControl(xi, yi);
	}

	public void press (int xi, int yi){
		int onVal = noteValue + (sizeY * xi + yi);
		MidiObject.sendNoteOn(midiChannel, onVal, 127);
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
			MidiObject.sendNoteOn(midiChannel, onVal, 127);
			lastNoteOn[sizeY - yi - 1] = -1;
		}		
		else{
			PatternRecorder pr = prs[invertedyi - (sizeX - 1)];
			if (pr.getMode() == PatternRecorder.STOPPED){
				pr.setMode(PatternRecorder.CUED);
			}
			else{
				pr.setMode(PatternRecorder.STOPPED);
			}
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
		// pattern recorders
		for (int i = 0; i<prs.length; i++){
			PatternRecorder pr = prs[i];
			int mode = pr.getMode();
			if (mode == PatternRecorder.CUED || mode == PatternRecorder.RECORDING)
				matrix[positionX + sizeX - 1][sizeY - sizeX - i] = MonoControl.blinkOn ? 1:0;
			else if (mode == PatternRecorder.PLAYING)
				matrix[positionX + sizeX - 1][sizeY - sizeX - i] = 1;				
		}
		
		return matrix;
	}

	@Override
	public String getType() {
		return "abletonTracks";
	}

	public void noteOnReceived(ShortMessage n) {
		int pitch = n.getData1();
		int vel = n.getData2();
		if (pitch >= noteValue && pitch < noteValue + ((sizeX - 1) * sizeY)){
			int xi = (pitch - noteValue) / sizeY;
			int yi = pitch - (noteValue + (sizeY * xi));
			if (vel==127 || vel==1) //Play or continue. Other values (0, 64:off; 126:cue)
				lastNoteOn[xi] = yi;		
			MonoControl.vm.refresh();
		}
	}
	
	public void noteOffReceived(ShortMessage n) {
		// do nothing
	}
	
	int RESOLUTION = 12; // 1/8
	int i = 0;
	public void timingClockReceived() {	
		i++;
		if (i == RESOLUTION){
			i = 0;
			step();			
		}
	}
	
	public void start(){
		i = 0;
		for (PatternRecorder pr: prs){
			pr.start();
			pr.step();
		}
	}
	
	public void stop(){
	}
	
	// pattern recorder
	protected void step(){
		for (PatternRecorder pr: prs)
			pr.step();
	}
	
	@Override
	public void loadJDOMXMLElement(Element el) {
		RESOLUTION = Integer.parseInt(el.getAttribute("resolution").getValue());
		STEPS = Integer.parseInt(el.getAttribute("steps").getValue());
	}

	@Override
	public Element toJDOMXMLElement(Element el) {
		el.setAttribute("resolution", String.valueOf(RESOLUTION));
		el.setAttribute("steps", String.valueOf(STEPS));
		return el;
	}

	
}
