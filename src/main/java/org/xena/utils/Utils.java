package org.xena.utils;

public final class Utils {

/*	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	public static float[] worldToScreen(float[] from, float[] to) {
		float[][] m_vMatrix = new float[4][4];
		ByteBuffer buffer = Abendigo.gameProcess.read(client.address() + Offsets.m_dwViewMatrix, 4 * 4 * 4);
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				float value = buffer.getFloat();
				m_vMatrix[row][col] = value;
			}
		}

		float w = 0.0f;

		to[0] = m_vMatrix[0][0] * from[0] + m_vMatrix[0][1] * from[1] + m_vMatrix[0][2] * from[2] + m_vMatrix[0][3];
		to[1] = m_vMatrix[1][0] * from[0] + m_vMatrix[1][1] * from[1] + m_vMatrix[1][2] * from[2] + m_vMatrix[1][3];
		w = m_vMatrix[3][0] * from[0] + m_vMatrix[3][1] * from[1] + m_vMatrix[3][2] * from[2] + m_vMatrix[3][3];

		if (w < 0.01f) {
			return to;
		}

		float invw = 1.0f / w;
		to[0] *= invw;
		to[1] *= invw;

		int width = SCREEN_SIZE.width;
		int height = SCREEN_SIZE.height;

		float x = width / 2;
		float y = height / 2;

		x += 0.5 * to[0] * width + 0.5;
		y -= 0.5 * to[1] * height + 0.5;

		to[0] = x + 0;
		to[1] = y + 0;

		return to;
	}*/

    public static int[] from(int min, int max) {
        int[] keys = new int[(max - min) + 1];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = min++;
        }
        return keys;
    }

}