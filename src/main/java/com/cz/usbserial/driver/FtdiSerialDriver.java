package com.cz.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FtdiSerialDriver implements UsbSerialDriver {
    private final UsbDevice mDevice;
    private final UsbSerialPort mPort;// = new FtdiSerialPort(this.mDevice, 0);

    public FtdiSerialDriver(UsbDevice device) {

        this.mDevice = device;
        this.mPort = new FtdiSerialPort(this.mDevice, 0);
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_FTDI), new int[]{UsbId.FTDI_FT232R, UsbId.FTDI_FT231X});
        return supportedDevices;
    }

    public UsbDevice getDevice() {
        return this.mDevice;
    }

    public List<UsbSerialPort> getPorts() {
        return Collections.singletonList(this.mPort);
    }

    private enum DeviceType {
        TYPE_BM,
        TYPE_AM,
        TYPE_2232C,
        TYPE_R,
        TYPE_2232H,
        TYPE_4232H
    }

    public class FtdiSerialPort extends CommonUsbSerialPort {
        public static final int FTDI_DEVICE_IN_REQTYPE = 192;
        public static final int FTDI_DEVICE_OUT_REQTYPE = 64;
        public static final int USB_ENDPOINT_IN = 128;
        public static final int USB_ENDPOINT_OUT = 0;
        public static final int USB_READ_TIMEOUT_MILLIS = 5000;
        public static final int USB_RECIP_DEVICE = 0;
        public static final int USB_RECIP_ENDPOINT = 2;
        public static final int USB_RECIP_INTERFACE = 1;
        public static final int USB_RECIP_OTHER = 3;
        public static final int USB_TYPE_CLASS = 0;
        public static final int USB_TYPE_RESERVED = 0;
        public static final int USB_TYPE_STANDARD = 0;
        public static final int USB_TYPE_VENDOR = 0;
        private static final boolean ENABLE_ASYNC_READS = false;
        private static final int MODEM_STATUS_HEADER_LENGTH = 2;
        private static final int SIO_MODEM_CTRL_REQUEST = 1;
        private static final int SIO_RESET_PURGE_RX = 1;
        private static final int SIO_RESET_PURGE_TX = 2;
        private static final int SIO_RESET_REQUEST = 0;
        private static final int SIO_RESET_SIO = 0;
        private static final int SIO_SET_BAUD_RATE_REQUEST = 3;
        private static final int SIO_SET_DATA_REQUEST = 4;
        private static final int SIO_SET_FLOW_CTRL_REQUEST = 2;
        private final String TAG = FtdiSerialDriver.class.getSimpleName();
        private int mInterface = 0;
        private int mMaxPacketSize = 64;
        private DeviceType mType;

        public FtdiSerialPort(UsbDevice device, int portNumber) {
            super(device, portNumber);
        }

        public UsbSerialDriver getDriver() {
            return FtdiSerialDriver.this;
        }

        private final int filterStatusBytes(byte[] src, byte[] dest, int totalBytesRead, int maxPacketSize) {
            int count;
            int packetsCount = (totalBytesRead / maxPacketSize) + (totalBytesRead % maxPacketSize == 0 ? 0 : 1);
            for (int packetIdx = 0; packetIdx < packetsCount; packetIdx++) {
                if (packetIdx == packetsCount - 1) {
                    count = (totalBytesRead % maxPacketSize) - 2;
                } else {
                    count = maxPacketSize - 2;
                }
                if (count > 0) {
                    System.arraycopy(src, (packetIdx * maxPacketSize) + 2, dest, (maxPacketSize - 2) * packetIdx, count);
                }
            }
            return totalBytesRead - (packetsCount * 2);
        }

        public void reset() throws IOException {
            int result = getConnection().controlTransfer(FTDI_DEVICE_OUT_REQTYPE, 0, 0, 0, (byte[]) null, 0, USB_READ_TIMEOUT_MILLIS);
            if (result != 0) {
                throw new IOException("Reset failed: result=" + result);
            }
            this.mType = DeviceType.TYPE_R;
        }

        public void open(UsbDeviceConnection connection) throws IOException {
            if (getConnection() != null) {
                throw new IOException("Already open");
            }
            setConnection(connection);
            boolean opened = false;
            int i = 0;
            while (i < this.mDevice.getInterfaceCount()) {
                try {
                    if (connection.claimInterface(this.mDevice.getInterface(i), true)) {
                        Log.d(this.TAG, "claimInterface " + i + " SUCCESS");
                        i++;
                    } else {
                        throw new IOException("Error claiming interface " + i);
                    }
                } finally {
                    if (!opened) {
                        close();
                        setConnection(null);
                    }
                }
            }
            reset();
            opened = true;
        }

        public void close() throws IOException {
            if (getConnection() == null) {
                throw new IOException("Already closed");
            }
            try {
                getConnection().close();
            } finally {
                setConnection(null);
            }
        }

        public int read(byte[] dest, int timeoutMillis) throws IOException {
            int filterStatusBytes;
            UsbEndpoint endpoint = this.mDevice.getInterface(0).getEndpoint(0);
            synchronized (this.mReadBufferLock) {
                int totalBytesRead = getConnection().bulkTransfer(endpoint, this.mReadBuffer, Math.min(dest.length, this.mReadBuffer.length), timeoutMillis);
                if (totalBytesRead < 2) {
                    throw new IOException("Expected at least 2 bytes");
                }
                filterStatusBytes = filterStatusBytes(this.mReadBuffer, dest, totalBytesRead, endpoint.getMaxPacketSize());
            }
            return filterStatusBytes;
        }

        public int write(byte[] src, int timeoutMillis) throws IOException {
            int writeLength;
            byte[] writeBuffer;
            int amtWritten;
            UsbEndpoint endpoint = this.mDevice.getInterface(0).getEndpoint(1);
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
                    amtWritten = getConnection().bulkTransfer(endpoint, writeBuffer, writeLength, timeoutMillis);
                }
                if (amtWritten <= 0) {
                    throw new IOException("Error writing " + writeLength + " bytes at offset " + offset + " length=" + src.length);
                }
                offset += amtWritten;
            }
            return offset;
        }

        private int setBaudRate(int baudRate) throws IOException {
            long[] vals = convertBaudrate(baudRate);
            long actualBaudrate = vals[0];
            long index = vals[1];
            int result = getConnection().controlTransfer(FTDI_DEVICE_OUT_REQTYPE, 3, (int) vals[2], (int) index, (byte[]) null, 0, USB_READ_TIMEOUT_MILLIS);
            if (result == 0) {
                return (int) actualBaudrate;
            }
            throw new IOException("Setting baudrate failed: result=" + result);
        }

        public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
            int config;
            int config2;
            setBaudRate(baudRate);
            int config3 = dataBits;
            switch (parity) {
                case 0:
                    config = config3 | 0;
                    break;
                case 1:
                    config = config3 | 256;
                    break;
                case 2:
                    config = config3 | 512;
                    break;
                case 3:
                    config = config3 | 768;
                    break;
                case 4:
                    config = config3 | 1024;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown parity value: " + parity);
            }
            switch (stopBits) {
                case 1:
                    config2 = config | 0;
                    break;
                case 2:
                    config2 = config | 4096;
                    break;
                case 3:
                    config2 = config | 2048;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown stopBits value: " + stopBits);
            }
            int result = getConnection().controlTransfer(FTDI_DEVICE_OUT_REQTYPE, 4, config2, 0, (byte[]) null, 0, USB_READ_TIMEOUT_MILLIS);
            if (result != 0) {
                throw new IOException("Setting parameters failed: result=" + result);
            }
        }

        private long[] convertBaudrate(int baudrate) {
            int baudDiff;
            long index;
            int divisor = 24000000 / baudrate;
            int bestDivisor = 0;
            int bestBaud = 0;
            int bestBaudDiff = 0;
            int[] fracCode = new int[8];
            fracCode[1] = 3;
            fracCode[2] = 2;
            fracCode[3] = 4;
            fracCode[4] = 1;
            fracCode[5] = 5;
            fracCode[6] = 6;
            fracCode[7] = 7;
            for (int i = 0; i < 2; i++) {
                int tryDivisor = divisor + i;
                if (tryDivisor <= 8) {
                    tryDivisor = 8;
                } else if (this.mType != DeviceType.TYPE_AM && tryDivisor < 12) {
                    tryDivisor = 12;
                } else if (divisor < 16) {
                    tryDivisor = 16;
                } else if (this.mType != DeviceType.TYPE_AM && tryDivisor > 131071) {
                    tryDivisor = 131071;
                }
                int baudEstimate = (24000000 + (tryDivisor / 2)) / tryDivisor;
                if (baudEstimate < baudrate) {
                    baudDiff = baudrate - baudEstimate;
                } else {
                    baudDiff = baudEstimate - baudrate;
                }
                if (i == 0 || baudDiff < bestBaudDiff) {
                    bestDivisor = tryDivisor;
                    bestBaud = baudEstimate;
                    bestBaudDiff = baudDiff;
                    if (baudDiff == 0) {
                        break;
                    }
                }
            }
            long encodedDivisor = (long) ((bestDivisor >> 3) | (fracCode[bestDivisor & 7] << 14));
            if (encodedDivisor == 1) {
                encodedDivisor = 0;
            } else if (encodedDivisor == 16385) {
                encodedDivisor = 1;
            }
            long value = encodedDivisor & 65535;
            if (this.mType == DeviceType.TYPE_2232C || this.mType == DeviceType.TYPE_2232H || this.mType == DeviceType.TYPE_4232H) {
                index = ((encodedDivisor >> 8) & 65535 & 65280) | 0;
            } else {
                index = (encodedDivisor >> 16) & 65535;
            }
            return new long[]{(long) bestBaud, index, value};
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
            return false;
        }

        public void setDTR(boolean value) throws IOException {
        }

        public boolean getRI() throws IOException {
            return false;
        }

        public boolean getRTS() throws IOException {
            return false;
        }

        public void setRTS(boolean value) throws IOException {
        }

        public boolean purgeHwBuffers(boolean purgeReadBuffers, boolean purgeWriteBuffers) throws IOException {
            int result;
            int result2;
            if (purgeReadBuffers && (result2 = getConnection().controlTransfer(FTDI_DEVICE_OUT_REQTYPE, 0, 1, 0, (byte[]) null, 0, USB_READ_TIMEOUT_MILLIS)) != 0) {
                throw new IOException("Flushing RX failed: result=" + result2);
            } else if (!purgeWriteBuffers || (result = getConnection().controlTransfer(FTDI_DEVICE_OUT_REQTYPE, 0, 2, 0, (byte[]) null, 0, USB_READ_TIMEOUT_MILLIS)) == 0) {
                return true;
            } else {
                throw new IOException("Flushing RX failed: result=" + result);
            }
        }
    }
}
