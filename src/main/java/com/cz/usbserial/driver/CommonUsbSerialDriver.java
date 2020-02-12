package com.cz.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

abstract class CommonUsbSerialDriver implements UsbSerialDriver {
    public static final int DEFAULT_READ_BUFFER_SIZE = 16384;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 16384;
    protected final UsbDeviceConnection mConnection;
    protected final UsbDevice mDevice;
    protected final Object mReadBufferLock = new Object();
    protected final Object mWriteBufferLock = new Object();
    protected byte[] mReadBuffer;
    protected byte[] mWriteBuffer;

    public CommonUsbSerialDriver(UsbDevice device, UsbDeviceConnection connection) {
        this.mDevice = device;
        this.mConnection = connection;
        this.mReadBuffer = new byte[16384];
        this.mWriteBuffer = new byte[16384];
    }

    public final UsbDevice getDevice() {
        return this.mDevice;
    }

    public final void setReadBufferSize(int bufferSize) {
        synchronized (this.mReadBufferLock) {
            if (bufferSize != this.mReadBuffer.length) {
                this.mReadBuffer = new byte[bufferSize];
            }
        }
    }

    public final void setWriteBufferSize(int bufferSize) {
        synchronized (this.mWriteBufferLock) {
            if (bufferSize != this.mWriteBuffer.length) {
                this.mWriteBuffer = new byte[bufferSize];
            }
        }
    }
}
