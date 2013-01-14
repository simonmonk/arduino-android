/*
 * TODO prüfen, ob wir diese Datei auf GPLv3 upgraden können
 * 
 * org.hermit.android.io: Android utilities for accessing peripherals.
 * 
 * These classes provide some basic utilities for accessing the audio
 * interface, at present.
 *
 * <br>Copyright 2009 Ian Cameron Smith
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

//package org.hermit.android.io;
package org.cbase.blinkendroid.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * A class which reads audio input from the mic in a background thread and
 * passes it to the caller when ready.
 * 
 * <p>
 * To use this class, your application must have permission RECORD_AUDIO.
 */
public class AudioReader implements Runnable {

    // ******************************************************************** //
    // Class Data.
    // ******************************************************************** //
    // Debugging tag.
    @SuppressWarnings("unused")
    private static final String TAG = "WindMeter";

    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //

    /** Our audio input device. */
    private AudioRecord audioInput;
    /** Our audio input buffer. */
    private short[][] inputBuffer = null;
    /** The index of the current item in the buffer.*/
    private int inputBufferWhich = 0;
    /** The index of the next item to go in.*/
    private int inputBufferIndex = 0;
    /** Size of the block to read each time. */
    private int inputBlockSize = 0;
    /** Time in ms to sleep between blocks, to meter the supply rate.*/
    private long sleepTime = 0;
    /** Listener for input. */
    private Listener inputListener = null;
    /** Flag whether the thread should be running. */
    private boolean running = false;
    /** The thread, if any, which is currently reading. Null if not running. */
    private Thread readerThread = null;

    // ******************************************************************** //
    // Public Classes.
    // ******************************************************************** //

    /**
     * Listener for audio reads.
     */
    public static abstract class Listener {
        /**
         * An audio read has completed.
         * 
         * @param buffer
         *            Buffer containing the data.
         */
        public abstract void onReadComplete(short[] buffer);
    }

    // ******************************************************************** //
    // Constructor.
    // ******************************************************************** //

    /**
     * Create an AudioReader instance.
     */
    public AudioReader() {
        // audioManager = (AudioManager)
        // app.getSystemService(Context.AUDIO_SERVICE);
    }

    // ******************************************************************** //
    // Run Control.
    // ******************************************************************** //

    /**
     * Start this reader.
     * 
     * @param rate
     *            The audio sampling rate, in samples / sec.
     * @param block
     *            Number of samples of input to read at a time. This is
     *            different from the system audio buffer size.
     * @param listener
     *            Listener to be notified on each completed read.
     */
    public void startReader(int rate, int block, Listener listener) {
        Log.i(TAG, "Reader: Start Thread");
        synchronized (this) {
            // Calculate the required I/O buffer size.
            int audioBuf = AudioRecord.getMinBufferSize(rate,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT) * 2;

            // Set up the audio input.
            audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, rate,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, audioBuf);

            inputBlockSize = block;
            sleepTime = (long) (1000f / ((float) rate / (float) block));
            inputBuffer = new short[2][inputBlockSize];
            inputBufferWhich = 0;
            inputBufferIndex = 0;
            inputListener = listener;
            running = true;
            readerThread = new Thread(this, "Audio Reader");
            readerThread.start();
        }
    }

    /**
     * Start this reader.
     */
    public void stopReader() {
        Log.i(TAG, "Reader: Signal Stop");
        synchronized (this) {
            running = false;
        }
        try {
            if (readerThread != null)
                readerThread.join();
        } catch (InterruptedException e) {
            ;
        }
        readerThread = null;

        // Kill the audio input.
        synchronized (this) {
            if (audioInput != null) {
                audioInput.release();
                audioInput = null;
            }
        }

        Log.i(TAG, "Reader: Thread Stopped");
    }

    // ******************************************************************** //
    // Main Loop.
    // ******************************************************************** //

    /**
     * Main loop of the audio reader. This runs in our own thread.
     * 
     * <p>
     * <b>Note:</b> this method is public because the Thread API requires it.
     * Outside classes must not call this.
     */
    public void run() {
        short[] buffer;
        int index, readSize;

        try {
            Log.i(TAG, "Reader: Get state");
            int astate = audioInput.getState();
            Log.i(TAG, "Reader: Start Recording in " + astate);
            audioInput.startRecording();
            Log.i(TAG, "Reader: Start Recording (" + astate + " -> "
                    + audioInput.getState() + ")");
            while (running) {
                long stime = System.currentTimeMillis();

                if (!running)
                    break;

                readSize = inputBlockSize;
                int space = inputBlockSize - inputBufferIndex;
                if (readSize > space)
                    readSize = space;
                buffer = inputBuffer[inputBufferWhich];
                index = inputBufferIndex;

                synchronized (buffer) {
                    int nread = audioInput.read(buffer, index, readSize);

                    boolean done = false;
                    if (!running)
                        break;

                    if (nread < 0) {
                        Log.e(TAG, "Audio read failed: error " + nread);
                        running = false;
                        break;
                    }
                    int end = inputBufferIndex + nread;
                    if (end >= inputBlockSize) {
                        inputBufferWhich = (inputBufferWhich + 1) % 2;
                        inputBufferIndex = 0;
                        done = true;
                    } else
                        inputBufferIndex = end;

                    if (done) {
                        readDone(buffer);

                        // Because our block size is way smaller than the audio
                        // buffer, we get blocks in bursts, which messes up
                        // the audio analyzer. We don't want to be forced to
                        // wait until the analysis is done, because if
                        // the analysis is slow, lag will build up. Instead
                        // wait, but with a timeout which lets us keep the
                        // input serviced.
                        long etime = System.currentTimeMillis();
                        long sleep = sleepTime - (etime - stime);
                        if (sleep < 5)
                            sleep = 5;
                        try {
                            buffer.wait(sleep);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        } finally {
            Log.i(TAG, "Reader: Stop Recording");
            if (audioInput.getState() == AudioRecord.RECORDSTATE_RECORDING)
                audioInput.stop();
        }
    }

    /**
     * Notify the client that a read has completed.
     * 
     * @param buffer
     *            Buffer containing the data.
     */
    private void readDone(short[] buffer) {
        inputListener.onReadComplete(buffer);
    }
}