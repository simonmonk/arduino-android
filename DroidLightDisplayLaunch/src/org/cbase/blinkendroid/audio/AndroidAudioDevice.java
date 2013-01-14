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
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AndroidAudioDevice
{
   AudioTrack track;
   short[] buffer = new short[1024];
 
   public AndroidAudioDevice( )
   {
      int minSize =AudioTrack.getMinBufferSize( 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT );        
      track = new AudioTrack( AudioManager.STREAM_MUSIC, 8000, 
                                        AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 
                                        minSize, AudioTrack.MODE_STREAM);
      track.play();        
   }       
 

   public void writeSamples(float[] samples) 
   {    
      fillBuffer( samples );
      track.write( buffer, 0, samples.length );
//      Log.d("sound", "write");
   }
 
   private void fillBuffer( float[] samples )
   {
      if( buffer.length < samples.length )
         buffer = new short[samples.length];
 
      for( int i = 0; i < samples.length; i++ )
         buffer[i] = (short)(samples[i] * Short.MAX_VALUE);;
   }            
}