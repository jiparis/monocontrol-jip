package jip.monocontrol;

import org.jdom.Element;

public class NoteButton extends Button {

  public NoteButton(MidiObject midi, int midiChannel, int noteValue, int positionX, int positionY, int sizeX, int sizeY) {
    super(midi, midiChannel, -1, positionX, positionY);
    this.noteValue = noteValue;
  }
 
@Override
public void setValue(int value) {
    this.value = value;
    if(value>0)
      midi.sendNoteOn(midiChannel, noteValue, value);
    else
      midi.sendNoteOff(midiChannel, noteValue);
  }
  @Override
public void buttonEvent(int x, int y, int pressed) {
    if(pressed>0)
      setValue(127);
    else
    	setValue(0);
  }
  @Override
public String getType() {
    return "noteButton";
  }
  @Override
public void setCCValue(int cc) { //setCCValue sets noteValue
    setNoteValue(cc);
  }
  public void setNoteValue(int v) {
    this.noteValue = v;
  }
  @Override
public int getCCValue() { //returns notevalue when asked for cc.. 
    return noteValue;
  }
  @Override
public int getNoteValue() {
    return noteValue;
  }

public void noteOnReceived(rwmidi.Note n) {
	super.noteOnReceived(n);
	if (n.getPitch() == noteValue){ //turn off on cued clips
		this.value = 0;
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
	return el;
}
}
