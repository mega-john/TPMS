package com.cz.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
//import android.support.v4.view.MotionEventCompat;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ch34xSerialDriver implements UsbSerialDriver {
    private static final String TAG = Ch34xSerialDriver.class.getSimpleName();
    private final UsbDevice mDevice;
    private final UsbSerialPort mPort;// = new Ch340SerialPort(this.mDevice, 0);

    public Ch34xSerialDriver(UsbDevice device) {
        this.mDevice = device;
        this.mPort = new Ch340SerialPort(this.mDevice, 0);
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_QINHENG), new int[]{29987});
        return supportedDevices;
    }

    public UsbDevice getDevice() {
        return this.mDevice;
    }

    public List<UsbSerialPort> getPorts() {
        return Collections.singletonList(this.mPort);
    }

    public class Ch340SerialPort extends CommonUsbSerialPort {
        private static final int USB_TIMEOUT_MILLIS = 5000;
        private final int DEFAULT_BAUD_RATE = 9600;
        private boolean dtr = false;
        private UsbEndpoint mReadEndpoint;
        private UsbEndpoint mWriteEndpoint;
        private boolean rts = false;

        public Ch340SerialPort(UsbDevice device, int portNumber) {
            super(device, portNumber);
        }

        public /* bridge */ /* synthetic */ int getPortNumber() {
            return super.getPortNumber();
        }

        public /* bridge */ /* synthetic */ String getSerial() {
            return super.getSerial();
        }

        public /* bridge */ /* synthetic */ String toString() {
            return super.toString();
        }

        public UsbSerialDriver getDriver() {
            return Ch34xSerialDriver.this;
        }

        public void open(UsbDeviceConnection connection) throws IOException {
            if (this.mConnection != null) {
                throw new IOException("Already opened.");
            }
            this.mConnection = connection;
            boolean opened = false;
            int i = 0;
            while (i < this.mDevice.getInterfaceCount()) {
                try {
                    if (this.mConnection.claimInterface(this.mDevice.getInterface(i), true)) {
                        Log.d(Ch34xSerialDriver.TAG, "claimInterface " + i + " SUCCESS");
                    } else {
                        Log.d(Ch34xSerialDriver.TAG, "claimInterface " + i + " FAIL");
                    }
                    i++;
                } finally {
                    if (!opened) {
                        try {
                            close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
            UsbInterface dataIface = this.mDevice.getInterface(this.mDevice.getInterfaceCount() - 1);
            for (int i2 = 0; i2 < dataIface.getEndpointCount(); i2++) {
                UsbEndpoint ep = dataIface.getEndpoint(i2);
                if (ep.getType() == 2) {
                    if (ep.getDirection() == 128) {
                        this.mReadEndpoint = ep;
                    } else {
                        this.mWriteEndpoint = ep;
                    }
                }
            }
            initialize();
            setBaudRate(9600);
            opened = true;
        }

        public void close() throws IOException {
            if (this.mConnection == null) {
                throw new IOException("Already closed");
            }
            try {
                this.mConnection.close();
            } finally {
                this.mConnection = null;
            }
        }

        public int read(byte[] dest, int timeoutMillis) throws IOException {
            synchronized (this.mReadBufferLock) {
                int numBytesRead = this.mConnection.bulkTransfer(this.mReadEndpoint, this.mReadBuffer, Math.min(dest.length, this.mReadBuffer.length), timeoutMillis);
                if (numBytesRead < 0) {
                    return 0;
                }
                System.arraycopy(this.mReadBuffer, 0, dest, 0, numBytesRead);
                return numBytesRead;
            }
        }

        public int write(byte[] src, int timeoutMillis) throws IOException {
            int writeLength;
            byte[] writeBuffer;
            int amtWritten;
            int offset = 0;
            while (offset < src.length) {
                synchronized (this.mWriteBufferLock) {
                    writeLength = Math.min(src.length - offset, this.mWriteBuffer.length);
                    if (offset == 0) {
                        writeBuffer = src;
                    } else {
                        System.arraycopy(src, offset, this.mWriteBuffer, 0, writeLength);
                        writeBuffer = this.mWriteBuffer;
                    }
                    amtWritten = this.mConnection.bulkTransfer(this.mWriteEndpoint, writeBuffer, writeLength, timeoutMillis);
                }
                if (amtWritten <= 0) {
                    throw new IOException("Error writing " + writeLength + " bytes at offset " + offset + " length=" + src.length);
                }
                Log.d(Ch34xSerialDriver.TAG, "Wrote amt=" + amtWritten + " attempted=" + writeLength);
                offset += amtWritten;
            }
            return offset;
        }

        private int controlOut(int request, int value, int index) {
            return this.mConnection.controlTransfer(65, request, value, index, (byte[]) null, 0, 5000);
        }

        private int controlIn(int request, int value, int index, byte[] buffer) {
            return this.mConnection.controlTransfer(FtdiSerialDriver.FtdiSerialPort.FTDI_DEVICE_IN_REQTYPE, request, value, index, buffer, buffer.length, 5000);
        }

        private void checkState(String msg, int request, int value, int[] expected) throws IOException {
            int current;
            byte[] buffer = new byte[expected.length];
            int ret = controlIn(request, value, 0, buffer);
            if (ret < 0) {
                throw new IOException("Faild send cmd [" + msg + "]");
            } else if (ret != expected.length) {
                throw new IOException("Expected " + expected.length + " bytes, but get " + ret + " [" + msg + "]");
            } else {
                int i = 0;
                while (i < expected.length) {
                    if (expected[i] == -1 || expected[i] == (current = buffer[i] & 255)) {
                        i++;
                    } else {
                        throw new IOException("Expected 0x" + Integer.toHexString(expected[i]) + " bytes, but get 0x" + Integer.toHexString(current) + " [" + msg + "]");
                    }
                }
            }
        }

        private void writeHandshakeByte() throws IOException {
            int i;
            if (this.dtr) {
                i = 32;
            } else {
                i = 0;
            }
            if (controlOut(164, (i | (this.rts ? 64 : 0)) ^ -1, 0) < 0) {
                throw new IOException("Faild to set handshake byte");
            }
        }

        private void initialize() throws IOException {
            int[] iArr = new int[2];
            iArr[0] = -1;
            checkState("init #1", 95, 0, iArr);
            if (controlOut(161, 0, 0) < 0) {
                throw new IOException("init failed! #2");
            }
            setBaudRate(9600);
            int[] iArr2 = new int[2];
            iArr2[0] = -1;
            checkState("init #4", 149, 9496, iArr2);
            if (controlOut(154, 9496, 80) < 0) {
                throw new IOException("init failed! #5");
            }
            checkState("init #6", 149, 1798, new int[]{255, 238});
            if (controlOut(161, 20511, 55562) < 0) {
                throw new IOException("init failed! #7");
            }
            setBaudRate(9600);
            writeHandshakeByte();
            checkState("init #10", 149, 1798, new int[]{-1, 238});
        }

        private void setBaudRate(int baudRate) throws IOException {
            int[] baud = {2400, 55553, 56, 4800, 25602, 31, 9600, 45570, 19, 19200, 55554, 13, 38400, 25603, 10, 115200, 52227, 8};
            int i = 0;
            while (i < baud.length / 3) {
                if (baud[i * 3] != baudRate) {
                    i++;
                } else if (controlOut(154, 4882, baud[(i * 3) + 1]) < 0) {
                    throw new IOException("Error setting baud rate. #1");
                } else if (controlOut(154, 3884, baud[(i * 3) + 2]) < 0) {
                    throw new IOException("Error setting baud rate. #1");
                } else {
                    return;
                }
            }
            throw new IOException("Baud rate " + baudRate + " currently not supported");
        }

        public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
            setBaudRate(baudRate);
        }

        public boolean getCD() throws IOException {
            return false;
        }

        public boolean getCTS() throws IOException {
            return false;
        }

        public boolean getDSR() throws IOException {
            return false;
        }

        public boolean getDTR() throws IOException {
            return this.dtr;
        }

        public void setDTR(boolean value) throws IOException {
            this.dtr = value;
            writeHandshakeByte();
        }

        public boolean getRI() throws IOException {
            return false;
        }

        public boolean getRTS() throws IOException {
            return this.rts;
        }

        public void setRTS(boolean value) throws IOException {
            this.rts = value;
            writeHandshakeByte();
        }

        public boolean purgeHwBuffers(boolean purgeReadBuffers, boolean purgeWriteBuffers) throws IOException {
            return true;
        }
    }
}
