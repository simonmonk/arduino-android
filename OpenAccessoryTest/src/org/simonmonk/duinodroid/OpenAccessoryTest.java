/**
 * code from the book Arduino + Android Projects for the Evil Genius
 * <br>Copyright 2011 Simon Monk
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

package org.simonmonk.duinodroid;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class OpenAccessoryTest extends Activity implements Runnable {

  private EditText mByteField;
  private EditText mResponseField;
  private Button mSendButton;

  private static final String ACTION_USB = "com.simonmonk.action.USB_ACTION";
  private PendingIntent mPermissionIntent;
  private boolean mPermissionRequestPending;

  private UsbManager mUsbManager;
  private UsbAccessory mAccessory;
  private ParcelFileDescriptor mFileDescriptor;
  private FileInputStream mInputStream;
  private FileOutputStream mOutputStream;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    mByteField = (EditText) findViewById(R.id.messagebyte);
    mResponseField = (EditText) findViewById(R.id.arduinoresponse);
    mSendButton = (Button) findViewById(R.id.sendButton);
    mSendButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        sendMessageToArduino();
      }
    });
    setupAccessory();
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    if (mAccessory != null) {
      return mAccessory;
    } else {
      return super.onRetainNonConfigurationInstance();
    }
  }

  @Override
  public void onResume() {
    log("Resuming");
    super.onResume();

    if (mInputStream != null && mOutputStream != null) {
      log("Resuming: streams were not null");
    } else {
      log("Resuming: streams were null");
      establishPermissionsAndOpenAccessory();
    }
  }

  private void establishPermissionsAndOpenAccessory() {
    UsbAccessory[] accessories = mUsbManager.getAccessoryList();
    UsbAccessory accessory = (accessories == null ? null : accessories[0]);
    if (accessory != null) {
      if (mUsbManager.hasPermission(accessory)) {
        openAccessory(accessory);
      } else {
        log("In establishPermissionsAndOpenAccessory: accessory didnt have permission");
        synchronized (mUsbReceiver) {
          if (!mPermissionRequestPending) {
            mUsbManager.requestPermission(accessory, mPermissionIntent);
            mPermissionRequestPending = true;
          }
        }
      }
    } else {
      log("onResume:mAccessory is null");
    }
  }

  @Override
  public void onPause() {
    log("Pausing");
    super.onPause();
  }

  @Override
  public void onDestroy() {
    log("Destroying");
    unregisterReceiver(mUsbReceiver);
    super.onDestroy();
  }

  Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      ValueMsg t = (ValueMsg) msg.obj;
      log("Arduino sent: " + t.getFlag() + " " + t.getReading());
    }
  };

  private void setupAccessory() {
    log("In setupAccessory");
    mUsbManager = UsbManager.getInstance(this);
    mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
        ACTION_USB), 0);
    IntentFilter filter = new IntentFilter(ACTION_USB);
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
    registerReceiver(mUsbReceiver, filter);
    if (getLastNonConfigurationInstance() != null) {
      mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
      openAccessory(mAccessory);
    }
  }

  private void openAccessory(UsbAccessory accessory) {
    log("In openAccessory");
    mFileDescriptor = mUsbManager.openAccessory(accessory);
    if (mFileDescriptor != null) {
      mAccessory = accessory;
      FileDescriptor fd = mFileDescriptor.getFileDescriptor();
      mInputStream = new FileInputStream(fd);
      mOutputStream = new FileOutputStream(fd);
      Thread thread = new Thread(null, this, "OpenAccessoryTest");
      thread.start();
      alert("openAccessory: Accessory openned");
      log("Attached");
    } else {
      log("openAccessory: accessory open failed");
    }
  }

  private void closeAccessory() {
    log("In closeAccessory");
    try {
      if (mFileDescriptor != null) {
        mFileDescriptor.close();
      }
    } catch (IOException e) {
    } finally {
      mFileDescriptor = null;
      mAccessory = null;
      mInputStream = null; // srm
      mOutputStream = null; // srm
    }
  }

  private int composeInt(byte hi, byte lo) {
    int val = (int) hi & 0xff;
    val *= 256;
    val += (int) lo & 0xff;
    return val;
  }

  public void run() {
    int ret = 0;
    byte[] buffer = new byte[16384];
    int i;

    while (true) { // keep reading messages forever. There are probably lots
      // of messages in the buffer, each 4 bytes
      try {
        ret = mInputStream.read(buffer);
      } catch (IOException e) {
        break;
      }

      i = 0;
      while (i < ret) {
        int len = ret - i;
        if (len >= 2) {
          Message m = Message.obtain(mHandler);
          int value = composeInt(buffer[i], buffer[i + 1]);
          m.obj = new ValueMsg('a', value);
          mHandler.sendMessage(m);
        }
        i += 2;
      }

    }
  }

  public void sendMessageToArduino() {
    String valueStr = mByteField.getText().toString();
    byte val;
    try {
      val = Byte.parseByte(valueStr);
      log("Sending to Arduino: " + val);
      sendCommand(val);
    } catch (NumberFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      alert("The Byte should be a number between 0 and 255");
    }

  }

  public void sendCommand(byte value) {
    byte[] buffer = new byte[1];
    buffer[0] = (byte) value;
    if (mOutputStream != null) {
      try {
        mOutputStream.write(buffer);
      } catch (IOException e) {
        log("Send failed: " + e.getMessage());
      }
    } else {
      log("Send failed: mOutStream was null");
    }
  }

  private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
        UsbAccessory accessory = UsbManager.getAccessory(intent);
        if (accessory != null && accessory.equals(mAccessory)) {
          log("Detached");
          closeAccessory();
        }
      }
    }
  };

  private void log(String string) {
    String contents = mResponseField.getText().toString();
    mResponseField.setText(string + "\n" + contents);
  }

  public void alert(String message) {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle("Alert");
    alertDialog.setMessage(message);
    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        return;
      }
    });
    alertDialog.show();
  }
}