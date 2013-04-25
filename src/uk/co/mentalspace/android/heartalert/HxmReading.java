package uk.co.mentalspace.android.heartalert;

import java.io.Serializable;

import android.util.Log;

/**
 * HrmReading
 * 
 * This class holds the information corresponding to a single message from 
 * the Zephyr HxM Heart Rate Monitor
 * 
 * The constructor HrmReading(byte[]) will fill the member fields from the bytes presumably 
 * read from a connected Zephyr HxM Heart Rate Monitor.  Because Java does not support 
 * signed/unsigned variants of numbers, we sometimes put the fields extracted from the 
 * HxM message into fields larger than is necessary.
 */
public class HxmReading implements Serializable {
	private static final long serialVersionUID = 5014453244685498788L;
	private static final String LOGNAME = "HrmReading";

	public final int STX = 0x02;
    public final int MSGID = 0x26;
    public final int DLC = 55;
    public final int ETX = 0x03;
	
	public int serial;
	public byte stx;
	public byte msgId;
	public byte dlc;
	public int firmwareId;
	public int firmwareVersion;
	public int hardWareId;
	public int hardwareVersion;
	public int batteryIndicator;
	public int heartRate;
	public int heartBeatNumber;
	public long hbTime1;
	public long hbTime2;
	public long hbTime3;
	public long hbTime4;
	public long hbTime5;
	public long hbTime6;
	public long hbTime7;
	public long hbTime8;
	public long hbTime9;
	public long hbTime10;
	public long hbTime11;
	public long hbTime12;
	public long hbTime13;
	public long hbTime14;
	public long hbTime15;
	public long reserved1;
	public long reserved2;
	public long reserved3;
	public long distance;
	public long speed;
	public byte strides;    
	public byte reserved4;
	public long reserved5;
	public byte crc;
	public byte etx;

    public HxmReading (byte[] buffer) {
    	int bufferIndex = 0;

    	if (Preferences.enableDebugLogging) Log.d (LOGNAME, "HrmReading being built from byte buffer");
    	
        try {
			stx 				= buffer[bufferIndex++];
			msgId 				= buffer[bufferIndex++];
			dlc 				= buffer[bufferIndex++];
			firmwareId 			= (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			firmwareVersion 	= (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hardWareId 			= (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hardwareVersion		= (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			batteryIndicator  	= (int)(0x000000FF & (int)(buffer[bufferIndex++]));
			heartRate  			= (int)(0x000000FF & (int)(buffer[bufferIndex++]));
			heartBeatNumber  	= (int)(0x000000FF & (int)(buffer[bufferIndex++]));
			hbTime1				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime2				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime3				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime4				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime5				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime6				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime7				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime8				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime9				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime10			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime11			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime12			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime13			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime14			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			hbTime15			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			reserved1			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			reserved2			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			reserved3			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			distance			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			speed				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			strides 			= buffer[bufferIndex++];    
			reserved4 			= buffer[bufferIndex++];
			reserved5 			= (long)(int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
			crc 				= buffer[bufferIndex++];
			etx 				= buffer[bufferIndex];
		} catch (Exception e) {
			// An exception should only happen if the buffer is too short and we walk off the end of the bytes,
			// because of the way we read the bytes from the device this should never happen, but just in case
			// we'll catch the exception
	        if (Preferences.enableWarningLogging) Log.w(LOGNAME, "Failure building HrmReading from byte buffer, probably an incopmplete or corrupted buffer", e);
		}

        if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Building HrmReading from byte buffer complete, consumed [" + bufferIndex + "] bytes in the process");
        
        // One simple check to see if we parsed the bytes properly is to check if the ETX 
        // character was found where we expected it,  a more robust implementation would be
        // to calculate the CRC from the message contents and compare it to the CRC from 
        // the packet.  
        if ( etx != ETX ) if (Preferences.enableErrorLogging) Log.e(LOGNAME, "...ETX mismatch!  The HxM message was not parsed properly");
        
        // log the contents of the HrmReading, use logcat to watch the data as it arrives  
        dump();
	}
        
    /*
     * dump() sends the contents of the HrmReading object to the log, use 'logcat' to view
     */    
    public void dump() {
    	if (!Preferences.enableVerboseLogging) return;
    	
		Log.d(LOGNAME,"HrmReading Dump");
		Log.d(LOGNAME,"...serial "+ ( serial ));
		Log.d(LOGNAME,"...stx "+ ( stx ));
		Log.d(LOGNAME,"...msgId "+( msgId ));
		Log.d(LOGNAME,"...dlc "+ ( dlc ));
		Log.d(LOGNAME,"...firmwareId "+ ( firmwareId ));
		Log.d(LOGNAME,"...sfirmwareVersiontx "+ (  firmwareVersion ));
		Log.d(LOGNAME,"...hardWareId "+ (  hardWareId ));
		Log.d(LOGNAME,"...hardwareVersion "+ (  hardwareVersion ));
		Log.d(LOGNAME,"...batteryIndicator "+ ( batteryIndicator ));
		Log.d(LOGNAME,"...heartRate "+ ( heartRate ));
		Log.d(LOGNAME,"...heartBeatNumber "+ ( heartBeatNumber ));
		Log.d(LOGNAME,"...shbTime1tx "+ (  hbTime1 ));
		Log.d(LOGNAME,"...hbTime2 "+ (  hbTime2 ));
		Log.d(LOGNAME,"...hbTime3 "+ (  hbTime3 ));
		Log.d(LOGNAME,"...hbTime4 "+ (  hbTime4 ));
		Log.d(LOGNAME,"...hbTime4 "+ (  hbTime5 ));
		Log.d(LOGNAME,"...hbTime6 "+ (  hbTime6 ));
		Log.d(LOGNAME,"...hbTime7 "+ (  hbTime7 ));
		Log.d(LOGNAME,"...hbTime8 "+ (  hbTime8 ));
		Log.d(LOGNAME,"...hbTime9 "+ (  hbTime9 ));
		Log.d(LOGNAME,"...hbTime10 "+ (  hbTime10 ));
		Log.d(LOGNAME,"...hbTime11 "+ (  hbTime11 ));
		Log.d(LOGNAME,"...hbTime12 "+ (  hbTime12 ));
		Log.d(LOGNAME,"...hbTime13 "+ (  hbTime13 ));
		Log.d(LOGNAME,"...hbTime14 "+ (  hbTime14 ));
		Log.d(LOGNAME,"...hbTime15 "+ (  hbTime15 ));
		Log.d(LOGNAME,"...reserved1 "+ (  reserved1 ));
		Log.d(LOGNAME,"...reserved2 "+ (  reserved2 ));
		Log.d(LOGNAME,"...reserved3 "+ (  reserved3 ));
		Log.d(LOGNAME,"...distance "+ (  distance ));
		Log.d(LOGNAME,"...speed "+ (  speed ));
		Log.d(LOGNAME,"...strides "+ ( strides ));
		Log.d(LOGNAME,"...reserved4 "+ ( reserved4 ));
		Log.d(LOGNAME,"...reserved5 "+ ( reserved5 ));
		Log.d(LOGNAME,"...crc "+ ( crc ));
		Log.d(LOGNAME,"...etx "+ ( etx ));    	    	    	
    }    
}   
