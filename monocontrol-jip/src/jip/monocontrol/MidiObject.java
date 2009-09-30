package jip.monocontrol;
import rwmidi.MidiInput;
import rwmidi.MidiInputDevice;
import rwmidi.MidiOutput;
import rwmidi.MidiOutputDevice;
import rwmidi.RWMidi;

public class MidiObject {
	public boolean ioReady = false;
	MidiOutput output;
	MidiInput input;
	private ViewManager vm;

	public MidiObject(ViewManager vm) {
		this.vm = vm;
		System.out.println(RWMidi.getOutputDeviceNames());
		System.out.println(RWMidi.getInputDeviceNames());
		if (RWMidi.getOutputDevices().length > 0
				&& RWMidi.getInputDevices().length > 1) {
			output = RWMidi.getOutputDevices()[0].createOutput();
			input = RWMidi.getInputDevices()[1].createInput();
			ioReady = true;
		}
	}

	public void setOutputDevice(MidiOutputDevice device) {
		this.output = device.createOutput();
	}

	public void setInputDevice(MidiInputDevice device) {
		if (input != null)
			this.input.closeMidi();
		this.input = device.createInput();
	}

	public MidiInputDevice[] getAvailibleInputs() {
		return RWMidi.getInputDevices();
	}

	public MidiOutputDevice[] getAvailibleOutputs() {
		return RWMidi.getOutputDevices();
	}

	public void controllerChangeReceived(rwmidi.Controller rc) {
		MonoControl.blinkInputLight();
		vm.refresh();
	}

	public void sysexReceived(rwmidi.SysexMessage e) {
		MonoControl.blinkInputLight();
		System.out.print(e.toString());
	}

	public void sendCC(int channel, int ccValue, int value) {
		if (output != null) {
			int check = output.sendController(channel, ccValue, value);
			if (check == 0)
				System.out.println("sendController(" + channel + ", " + ccValue
						+ ", " + value + "); -> FAILED");
			else
				MonoControl.blinkOutputLight();
		}
	}

	public void sendNoteOn(int channel, int noteValue, int velValue) {
		if (output != null) {
			int check = output.sendNoteOn(channel, noteValue, velValue);
			if (check == 0)
				System.out.println("sendNoteOn(" + channel + ", " + noteValue
						+ ", " + velValue + "); -> FAILED");
			else
				MonoControl.blinkOutputLight();
		}
	}

	public void sendNoteOff(int channel, int noteValue) {
		if (output != null) {
			int check = output.sendNoteOff(channel, noteValue, 0);
			if (check == 0)
				System.out.println("sendNoteOff(" + channel + ", " + noteValue
						+ ", 0); -> FAILED");
			else
				MonoControl.blinkOutputLight();
		}
	}

	public void plug(Object o, int channel) {
		input.plug(o, channel);
	}

}
