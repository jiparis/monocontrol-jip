package jip.monocontrol;
import java.util.Vector;

public class View {
	private String name;
	public Vector<ControlObject> objects;

	public View(String name) {
		this.name = name;
		objects = new Vector<ControlObject>();
	}

	public void clear() {
		objects.clear();
	}

	public void addControlObject(ControlObject co) {
		objects.add(co);
	}

	public void deleteControlObject(int x, int y) {
		for (ControlObject co : objects) {
			if (co.buttonIsElement(x, y))
				objects.remove(co);
		}
	}

	public ControlObject[] getControlObject(int x, int y) {
		Vector<ControlObject> h1 = new Vector<ControlObject>();
		for (ControlObject co : objects) {
			if (co.buttonIsElement(x, y))
				h1.add(co);
		}
		return (ControlObject[]) h1.toArray();
	}

	public int[][] getDrawMatrix() {
		int[][] result = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		for (ControlObject co : objects) {
			result = Matrix.melt(MonoControl.monomeSizeX,
					MonoControl.monomeSizeY, result, co.getDrawMatrix());
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public void buttonEvent(int x, int y, int pressed) {
		for (ControlObject co : objects) {
			if (co.buttonIsElement(x, y))
				co.buttonEvent(x, y, pressed);
		}
	}

}
