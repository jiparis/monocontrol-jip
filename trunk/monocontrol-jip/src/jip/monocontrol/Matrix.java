package jip.monocontrol;
public class Matrix {
	public static int[][] melt(int x, int y, int[][] a, int[][] b) {
		int[][] result = new int[x][y];
		if (a.length == x && a[0].length == y && b.length == x
				&& b[0].length == y) {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					if (a[i][j] > 0 || b[i][j] > 0)
						result[i][j] = 1;
				}
			}
		}
		return result;
	}
}
