package jip.monocontrol;

import java.util.List;

import javax.sound.midi.ShortMessage;

import org.jdom.Attribute;
import org.jdom.Element;

public class Looper extends ControlObject {
	
	public static final int NOT_SET = -1;
	
	private Loop[] loops;
	
	private final static int OFFSET_START_CTRL = 96;

	private Boolean gateLoopChokes = true;
	private boolean muteNotes = false;

	public boolean[] stopLoopsOnNextStep;
	
	int[][] displayGrid;
	int controlCol;
	
	public static final int C3 = 60;
	public static final int F7 = 113;


	public Looper(int midiChannel, int ccValue, int positionX,
			int positionY, int sizeX, int sizeY) {
		super(midiChannel, ccValue, positionX, positionY, sizeX, sizeY);

		stopLoopsOnNextStep = new boolean[sizeX];
		
		loops = new Loop[sizeX - 1];
		for (int i = 0; i < loops.length; i++) {
			loops[i] = new Loop();
		}
		displayGrid = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		controlCol = positionX + sizeX - 1;
	}

	@Override
	public int[][] getDrawMatrix() {
		if (blink) {
			return getBlinkMatrix();
		}
		int[][] matrix = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		for (int i = 0; i < loops.length; i++) {
			if (loops[i].isPlaying())
				matrix[i + positionX][positionY + loops[i].getStep()] = 1;
			matrix[positionX + sizeX - 1][positionY + i] = loops[i].isPlaying() ? 1
					: 0;
		}

		return matrix;
	}

	@Override
	public String getType() {
		return "looper";
	}
	

	@Override
	public void buttonEvent(int x, int y, int pressed) {
		// calculate relative indexes
		int xi = x - positionX;
		int yi = y - positionY;
		if (pressed > 0) {
			press(xi, yi);
		}
		else 
			if (x < controlCol) release(xi, yi);
	}
	
	@Override
	public void noteOnReceived(ShortMessage n) {
		if (n.getData1() == Looper.F7) { // F7
			MonoControl.blinkInputLight();
			step();
		}
	}

	@Override
	public void controllerChangeReceived(ShortMessage rc){
		
	}
	
	
	// SevenUp
	
	public void release(int x, int y)
	{
		if (loops[x].isPlaying() && loops[x].getType() == Loop.MOMENTARY && loops[x].getLastTriggedStep() == y) {
			stopLoop(x);
		}
	}
	
	public void press(int x, int y)
	{
	  
		if(x == controlCol)
		{
			pressNavCol(y);
			//updateNavGrid();
		}
		else
			pressDisplay(x,y);

		
		//updateNavGrid(); // @TODO clloyd not needed, done in play and stop functions
	}
	
	private void pressNavCol(int y)
	{
		//Inverse the mode of the corresponding loop
		if (y<loops.length){
			if(loops[y].isPlaying())
			{
				loops[y].stop();
				MidiObject.sendNoteOff(midiChannel, Looper.C3+y);
			}
			else
			{
				playLoop(y, 0);
			}
		}
	}
	
	private void pressDisplay(int x, int y)
	{
			//Choke loops in the same choke group
			int curChokeGroup = loops[x].getChokeGroup();
			if(curChokeGroup > -1)
			{
				for(int i=0; i<loops.length;i++)
					if(loops[i].getChokeGroup() == curChokeGroup && i != x)
					{
						if(gateLoopChokes)
							stopLoop(i);
						else
							stopLoopsOnNextStep[i] = true;
					}
			}
			
			stopLoopsOnNextStep[x] = false;
			int loopCtrlValue = (y * 16);
			MidiObject.sendCC(midiChannel, OFFSET_START_CTRL+x, loopCtrlValue);
			playLoop(x, y);
			
			//System.out.println("Gate loops is " + gateLoopChokes);
	}
	
	

	public int getNumLoops()
	{
		return loops.length;
	}
	
	public Loop getLoop(int index)
	{
		return loops[index];
	}
	
	private void updateNavGrid()
	{
		for (int i = 0; i < loops.length; i++) {
			displayGrid[positionX + sizeX - 1][positionY + i] = loops[i]
					.isPlaying() ? 1 : 0;

		}
	}	
	
	public boolean isLoopPlaying(int loopNum)
	{
		return loops[loopNum].isPlaying();
	}
	
	public void stopLoop(int loopNum)
	{
		loops[loopNum].stop();
		updateNavGrid();
		if (loops[loopNum].getType() != Loop.HIT)
			MidiObject.sendNoteOff(midiChannel, Looper.C3+loopNum);
	}
	
	public void setLoopStopOnNextStep(int loopNum)
	{
		stopLoopsOnNextStep[loopNum] = true;
	}
	
	public void playLoop(int loopNum, int step)
	{
		loops[loopNum].setTrigger(step, true);
		loops[loopNum].setStep(step);
		loops[loopNum].setPressedRow(step);
		updateNavGrid();		
	}
		

	public void step()
	{
		//updateDisplayGrid();
		
		for(int i=0; i<loops.length; i++)
		{
			if(stopLoopsOnNextStep[i])
			{
				stopLoop(i);
				stopLoopsOnNextStep[i] = false;
			}
			
			stepOneLoop(i);
		}
		
	}
	
	

	public void stepOneLoop(int loopNum)
	{
		int pressedRow;
		int resCounter;
		int step;
		int i = loopNum;
			
			if(loops[i].isPlaying())
        	{
        		pressedRow = loops[i].getPressedRow();
        		resCounter = loops[i].getResCounter();
        		step = loops[i].getStep();
        		
        		//In buzz you have to send the controller AFTER the note is played
        		int loopCtrlValue = (loops[i].getStep() * 16);
        		
        		
        		// Only send the controller if we are changing position. This allows the sample to play smoothly and linearly.
        		if (pressedRow > -1) {
        			switch (loops[i].getType()) {
        				case Loop.HIT: // Hits we let it run to the end of the sample and don't send a noteOff on release
        					if (loops[i].getTrigger(step) == true) {
        						MidiObject.sendCC(midiChannel, OFFSET_START_CTRL+i, loopCtrlValue);
        						if(!muteNotes)
        							MidiObject.sendNoteOn(midiChannel, Looper.C3+i,pressedRow * 16  +1);
        						loops[i].setTrigger(step, false);
        					} else {
        						stopLoop(i);
        						pressedRow = -1;
        	  			}
        					break;
        				case Loop.MOMENTARY:
        				case Loop.SLICE:
        					if (resCounter == 0 || loops[i].getTrigger(step)) {
        						MidiObject.sendCC(midiChannel, OFFSET_START_CTRL+i, loopCtrlValue);
        						if(!muteNotes)
        							MidiObject.sendNoteOn(midiChannel, Looper.C3+i,pressedRow * 16  +1);
        						loops[i].setTrigger(step, false);
        					}
        					// If it's a one shot loop, then we stop after the first iteration
        	        		if (loops[i].getType() == Loop.SLICE && loops[i].isLastResInStep()) {
        	    				stopLoop(i);
        	    				pressedRow = -1;
        	    			}
        				// Don't break here, flow into SHOT	
        				case Loop.SHOT:
        					// If it's a one shot loop, then we stop after the first iteration
        	        		if (loops[i].getType() == Loop.SHOT && loops[i].isLastResStep()) {
        	    				stopLoop(i);
        	    				pressedRow = -1;
        	    			}
        	        	// Don't break flow into LOOP	
        				case Loop.LOOP:
        				case Loop.STEP:
        				default:
        					if (resCounter == 0) 
        						MidiObject.sendCC(midiChannel, OFFSET_START_CTRL+i, loopCtrlValue);
        				
        					//Send note every time looprow is 0 or at it's offset
        	        		if((resCounter == 0) && (step == 0 || pressedRow > -1))
        	        		{
        	        			if (!muteNotes) {
        	        				boolean sendNote = false;
        	        				if(loops[i].getTrigger(step) == true) { 
	        	        				loops[i].setTrigger(step, false);
	        	        				sendNote = true;
	        	        			}	
	        	        			
        	        				// We only want to retrigger when necessary to avoid additional microfades or minor timing issues.
	        	        			if (resCounter == 0 && loops[i].getIteration() > 0) {
	        	        				if (loops[i].getType() == Loop.STEP) { // Else we are stepping in Loop.STEP mode and we retrigger every step
	        	        					sendNote = true;
	        	        				} else if (step == 0) { // We only retrigger at step 0 in other modes
	        	        					sendNote = true;
		        	        				
	        	        				}
	        	        			}	
	        	        			if (sendNote)
	        	        				MidiObject.sendNoteOn(midiChannel, Looper.C3+i,pressedRow * 16  +1);
        	        			}
        	        			pressedRow = -1;
        	        				
        	        		}
        					break;
        					
        			};
        		}	
        		
        		loops[i].nextResCount();
        	}
        
		MonoControl.vm.refresh();
	}
	
	@Override
	public Element toJDOMXMLElement(Element xmlLooper)
	{		
		xmlLooper.setAttribute(new Attribute("gateLoopChokes", gateLoopChokes.toString()));
		
		Element xmlLoop;

		for(Integer i=0;i<loops.length;i++)
		{
			xmlLoop = loops[i].toJDOMXMLElement();
			xmlLoop.setAttribute(new Attribute("index", i.toString()));
			xmlLooper.addContent(xmlLoop);
		}
		
		return xmlLooper;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void loadJDOMXMLElement(Element xmlLooper)
	{	
		stopLoopsOnNextStep = new boolean[sizeX];
		
		loops = new Loop[sizeX - 1];
		for (int i = 0; i < loops.length; i++) {
			loops[i] = new Loop();
		}
		displayGrid = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		controlCol = positionX + sizeX - 1;
		List<Element> xmlLoops;
		Integer loopIndex;
		
		gateLoopChokes = xmlLooper.getAttributeValue("gateLoopChokes") == null ? gateLoopChokes : Boolean.parseBoolean(xmlLooper.getAttributeValue("gateLoopChokes"));
		
		xmlLoops = xmlLooper.getChildren();
		
		for (Element xmlLoop : xmlLoops)
		{
			loopIndex = xmlLoop.getAttributeValue("index") == null ? NOT_SET : Integer.parseInt(xmlLoop.getAttributeValue("index"));
			if (loopIndex != NOT_SET)
				loops[loopIndex].loadJDOMXMLElement(xmlLoop);		
		}
	}
	
	public void setGateLoopChokes(boolean _gateLoopChokes)
	{
		gateLoopChokes = _gateLoopChokes;
	}
	
	public boolean getGateLoopChokes()
	{
		return gateLoopChokes;
	}

	public void reset() {
		for(int i=0;i<7;i++)
			stopLoop(i);
	}

	public void setLooperMute(boolean mute) {
		muteNotes = mute;
		
	}
}
