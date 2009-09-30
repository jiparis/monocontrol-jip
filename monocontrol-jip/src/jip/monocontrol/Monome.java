package jip.monocontrol;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

public class Monome {
	private OscP5 oscP5;
	private NetAddress location;
	private ViewManager c;
	private int monomeNumber;
	public int sizeX, sizeY;

	public Monome(ViewManager c) {
		oscP5 = new OscP5(this, MonoControl.OSCInPort);
		location = new NetAddress("127.0.0.1", MonoControl.OSCOutPort);
		oscP5.plug(this, "buttonEvent", "/monocontrol/press");
		oscP5.plug(this, "prefixReceived", "/sys/prefix");
		oscP5.plug(this, "typeReceived", "/sys/type");
		OscMessage m = new OscMessage("/sys/report");
		oscP5.send(m, location);
		this.c = c;
	}

	public void refresh(int[][] matrix) {
		OscMessage m;
		int data;
		for (int i = 0; i < MonoControl.monomeSizeX; i++) {
			m = new OscMessage("/monocontrol/led_col");
			m.add(i);
			data = 0;
			for (int j = 0; j < 8; j++) {
				//if (matrix[i][j] == 1)
				if (matrix[i][MonoControl.monomeSizeY - 1 - j] == 1) // mirrored
																		// on
																		// the
																		// x-axis
					data += Math.pow(2, j);
			}
			m.add(data);
			if (MonoControl.monomeSizeY > 8) {
				int data2 = 0;
				for (int j = 8; j < 16; j++) {
					//if (matrix[i][j] == 1)
					if (matrix[i][MonoControl.monomeSizeY - 1 - j] == 1) // mirrored
																			// on
																			// the
																			// x-axis
						data2 += Math.pow(2, j - 8);
				}
				m.add(data2);
			}
			oscP5.send(m, location);
		}
	}

	public void buttonEvent(int x, int y, int pressed) {
		//c.buttonEvent(x, y, pressed);
		c.buttonEvent(x, MonoControl.monomeSizeY - 1 - y, pressed); // mirrored
																	// on the
																	// x-axis
	}

	public void prefixReceived(int nr, String prefix) {
		if (prefix == "/monocontrol") {
			monomeNumber = nr;
		}
	}

	public void typeReceived(int nr, String type) {
		if (nr == monomeNumber) {
			if (type == "128") {
				MonoControl.monomeSizeX = 16;
				MonoControl.monomeSizeY = 8;
			} else if (type == "256") {
				MonoControl.monomeSizeX = 16;
				MonoControl.monomeSizeY = 16;
			} else {
				MonoControl.monomeSizeX = 8;
				MonoControl.monomeSizeY = 8;
			}
			System.out.println("Monome number " + nr + " is Type: " + type);
		}
	}

	public void led(int x, int y, int on) {
		OscMessage m = new OscMessage("/monocontrol/led");
		m.add(x);
		m.add(y);
		m.add(on);
		oscP5.send(m, location);
	}

	public void led_col(int col, int data) {
		OscMessage m = new OscMessage("/monocontrol/led_col");
		m.add(col);
		m.add(data);
		oscP5.send(m, location);
	}

	public void led_row(int row, int data) {
		OscMessage m = new OscMessage("/monocontrol/led_row");
		m.add(row);
		m.add(data);
		oscP5.send(m, location);
	}

	public void clear() {
		OscMessage m2 = new OscMessage("/monocontrol/clear");
		oscP5.send(m2, location);
	}

}
