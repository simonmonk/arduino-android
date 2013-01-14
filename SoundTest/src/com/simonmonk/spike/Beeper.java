package com.simonmonk.spike;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Beeper {

	private final static int SAMPLE_RATE = 16000;

	private final static int ONE_DURATION = 32;
	private final static int ZERO_DURATION = 8;
	private final static int BIT_DURATION = 64;
	private final static int DURATION = BIT_DURATION * 32;
	private final static float f = 1000.0f;

	private short[] buffer = null;

	public void beep(int word) {
		AudioTrack at;
		int bufsizbytes = DURATION * SAMPLE_RATE / 1000;
		int bufsizsamps = bufsizbytes / 2;
		buffer = new short[bufsizsamps];
		fillbuf(word, bufsizsamps);
		try {
			at = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufsizbytes,
					AudioTrack.MODE_STATIC);
			at.setStereoVolume(1.0f, 1.0f);
			at.write(buffer, 0, bufsizsamps);
			at.play();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	void fillbuf(int word, int bufsizsamps) {
		double omega, t;
		double dt = 1.0 / SAMPLE_RATE;
		t = 0.0;
		omega = (float) (2.0 * Math.PI * f);
		for (int i = 0; i < bufsizsamps; i++) {
			if (toneRequired(t, word)) {
				buffer[i] = (short) (32000.0 * Math.sin(omega * t));
			} else {
				buffer[i] = 0;
			}
			t += dt;
		}
	}

	boolean toneRequired(double t, long word) {
		int ms = (int) (t * 1000);
		int bitIndex = ms / BIT_DURATION;
		int bit = (int) ((word >> (15 - bitIndex)) & 1);
		int msWithinBit = ms - (bitIndex * BIT_DURATION);

		if (bit == 1 && msWithinBit < ONE_DURATION) {
			return true;
		}
		if (bit == 0 && msWithinBit < ZERO_DURATION) {
			return true;
		}
		return false;
	}

}
