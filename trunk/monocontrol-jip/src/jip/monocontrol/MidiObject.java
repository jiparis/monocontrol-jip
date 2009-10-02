package jip.monocontrol;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiDevice.Info;

public class MidiObject implements Receiver{
	public static Logger logger = Logger.getLogger(MidiObject.class.getName());
	public static Info[] infos;
	MidiDevice inputDevice, outputDevice;
	private static Receiver outputReceiver;
	private static Transmitter inputTransmitter;
	private static HashSet<ControlObject>[] plugs = new HashSet[16];
	
	public MidiObject() {
		infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < 16; i++){
			plugs[i] = new HashSet<ControlObject>();
		}
	}

	public void setOutputDevice(Info info) {
		closeOutputDevice();
		try {
			outputDevice = MidiSystem.getMidiDevice(info);
			outputDevice.open();
			outputReceiver = outputDevice.getReceiver();
			
			logger.log(Level.INFO, info.getName() + " open");
		} catch (MidiUnavailableException e) {
			logger.log(Level.SEVERE, "Error opening output device", e);
		}
	}
	
	public void closeOutputDevice(){
		if (outputReceiver != null){
			outputReceiver.close();
		}
		if (this.outputDevice != null && this.outputDevice.isOpen()){
			outputDevice.close();
		}
	}

	public void setInputDevice(Info info) {
		closeInputDevice();
		try {
			inputDevice = MidiSystem.getMidiDevice(info);
			inputDevice.open();
			inputTransmitter = inputDevice.getTransmitter();
			inputTransmitter.setReceiver(this);
			logger.log(Level.INFO, info.getName() + " open");
		} catch (MidiUnavailableException e) {
			logger.log(Level.SEVERE, "Error opening input device", e);
		}
	}
	
	public void closeInputDevice(){
		if (this.inputTransmitter != null){
			inputTransmitter.close();
		}
		if (this.inputDevice != null && this.inputDevice.isOpen()){
			inputDevice.close();
		}
	}

	public static List<Info> getAvailibleInputs() {
		List<Info> res = new Vector<Info>();
		for (int i = 0; i < infos.length; i++){
			MidiDevice dev;
			try {
				dev = MidiSystem.getMidiDevice(infos[i]);
				if (dev.getMaxTransmitters() != 0) {
					res.add(infos[i]);
				}
			} catch (MidiUnavailableException e) {
				logger.log(Level.SEVERE, "Error getting midi input infos", e);
			}			
			
		}
		
		return res;
	}

	public static List<Info> getAvailibleOutputs() {
		List<Info> res = new Vector<Info>();
		for (int i = 0; i < infos.length; i++){
			MidiDevice dev;
			try {
				dev = MidiSystem.getMidiDevice(infos[i]);
				if (dev.getMaxReceivers() != 0) {
					res.add(infos[i]);
				}
			} catch (MidiUnavailableException e) {
				logger.log(Level.SEVERE, "Error getting output device infos", e);
			}			
			
		}
		
		return res;
	}

	public static void sendCC(int channel, int ccValue, int value) {
		if (outputReceiver != null) {
			ShortMessage msg = new ShortMessage();
			try {
				msg.setMessage(ShortMessage.CONTROL_CHANGE, channel, ccValue, value);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
			outputReceiver.send(msg, -1);
			
			MonoControl.blinkOutputLight();
		}
	}

	public static void sendNoteOn(int channel, int noteValue, int velValue) {
		if (outputReceiver != null) {
			ShortMessage msg = new ShortMessage();

			try {
				msg.setMessage(ShortMessage.NOTE_ON, channel, noteValue, velValue);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
			outputReceiver.send(msg, -1);
			
			MonoControl.blinkOutputLight();
		}
	}

	public static void sendNoteOff(int channel, int noteValue) {
		if (outputReceiver != null) {
			ShortMessage msg = new ShortMessage();
		
			try {
				msg.setMessage(ShortMessage.NOTE_OFF, channel, noteValue);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
			outputReceiver.send(msg, -1);
			
			MonoControl.blinkOutputLight();
		}
	}

	// plugging
	public static void plug(int channel, ControlObject obj){
		plugs[channel].add(obj);
	}
	
	public static void unplug(int channel, ControlObject obj){
		plugs[channel].remove(obj);
	}
	
	// Receiver
	public void close() {
		// TODO Auto-generated method stub
		
	}

	public void send(MidiMessage msg, long time) {
		if (msg instanceof ShortMessage){
			ShortMessage sm = (ShortMessage) msg;
			int cmd = sm.getCommand();
			int channel = sm.getChannel();
			Set<ControlObject> objects = plugs[channel];
			switch(cmd){
			case ShortMessage.NOTE_ON:
				for (ControlObject obj: objects){
					obj.noteOnReceived(sm);
				}
				break;
			case ShortMessage.NOTE_OFF:
				for (ControlObject obj: objects){
					obj.noteOffReceived(sm);
				}
				break;
			case ShortMessage.CONTROL_CHANGE:
				for (ControlObject obj: objects){
					obj.controllerChangeReceived(sm);
				}
				break;			
			case 0xF0: // Sysex for clock (ch 8) & reset (ch 12) events
				for (ControlObject obj: objects){
					obj.sysexReceived(sm);
				}
				break;
			default:
				System.out.println(sm.getCommand());
			}
		}
		//logger.log(Level.INFO, msg.toString());		
	}

}
