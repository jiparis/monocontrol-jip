package jip.monocontrol;
abstract class Button extends ControlObject {
	public Button(MidiObject midi, int midiChannel, int ccValue, int positionX,
			int positionY) {
		super(midi, midiChannel, ccValue, positionX, positionY, 1, 1);
	}

	@Override
	public int[][] getDrawMatrix() {
		if (blink) {
			return getBlinkMatrix();
		}
		int[][] matrix = new int[MonoControl.monomeSizeX][MonoControl.monomeSizeY];
		if (value > 0)
			matrix[positionX][positionY] = 1;
		return matrix;
	}
}
