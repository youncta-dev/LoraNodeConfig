package com.youncta.loranodeconfig;



import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Locale;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


public class NfcManager implements Runnable, OnTagDetected {

    private static byte[] NFC_IDENTIFIER = { 0x55, 0x01 };


    private NdefMessage lastReadNdefMessage = null;
    String message = null;

    private Tag lastTag;

    Thread thread = new Thread(NfcManager.this);

    private static volatile NfcManager instance = null;


    public static NfcManager getInstance() {
        NfcManager localInstance = instance;
        if (localInstance == null) {
            synchronized (NfcManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new NfcManager();
                }
            }
        }
        return localInstance;
    }

    void setTextMessage(String message) {
        this.message = message;
    }

    public NfcManager()  {
        instance = this;
    }

    public void start() {
        thread.start();
    }

    public void readTag() {


    }

    @Override
    public void run()  {

        while (true) {
            try {
                Thread.sleep(5000);

            } catch (InterruptedException e) {
            }
        }
    }

    public NdefMessage createTextMessage(String content) {
        try {
            // Get UTF-8 byte
            byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] text = content.getBytes("UTF-8"); // Content in UTF-8

            int langSize = lang.length;
            int textLength = text.length;

            ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
            payload.write((byte) (langSize & 0x1F));
            payload.write(lang, 0, langSize);
            payload.write(text, 0, textLength);
            NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT, new byte[0],
                    payload.toByteArray());
            return new NdefMessage(new NdefRecord[]{record});
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public void onTagDetected(Context context, Tag tag, Parcelable[] data) {


        if ((tag == null) || (data == null)) {
            Toast.makeText(context, R.string.nfcDetectionError, Toast.LENGTH_LONG).show();
            return;
        }

        if (lastTag != null) {
            //Toast.makeText(context, R.string.nfcTagAlreadyRead, Toast.LENGTH_LONG).show();
        }
        else {
            // Getting all records from tag
            lastTag = tag;

            lastReadNdefMessage = (NdefMessage) data[0];
        }
        try {


            // Check if we have to write
            if (message != null)  {

                NdefMessage ndefMessage = createTextMessage(message);

                writeTag(context, tag, ndefMessage);

                message = null;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  boolean writeTag(Context context, Tag tag, NdefMessage ndefMessage) {


        try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);

            if(ndef != null) {
                ndef.connect();

                // Make sure the tag is writable
                if(!ndef.isWritable()) {

                    Toast.makeText(context, R.string.nfcReadOnlyError, Toast.LENGTH_LONG).show();
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = ndefMessage.toByteArray().length;
                if(ndef.getMaxSize() < size) {
                    Toast.makeText(context, R.string.nfcBadSpaceError, Toast.LENGTH_LONG).show();
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(ndefMessage);

                    Toast.makeText(context, R.string.nfcWritten, Toast.LENGTH_LONG).show();
                    return true;
                } catch (TagLostException tle) {
                    Toast.makeText(context, R.string.nfcTagLostError, Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException ioe) {
                    Toast.makeText(context, R.string.nfcFormattingError, Toast.LENGTH_LONG).show();
                    return false;
                } catch (FormatException fe) {
                    Toast.makeText(context, R.string.nfcFormattingError, Toast.LENGTH_LONG).show();
                    return false;
                }
                // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if(format != null) {
                    try {
                        format.connect();
                        format.format(ndefMessage);

                        Toast.makeText(context, R.string.nfcWritten, Toast.LENGTH_LONG).show();
                        return true;
                    } catch (TagLostException tle) {
                        Toast.makeText(context, R.string.nfcTagLostError, Toast.LENGTH_LONG).show();
                        return false;
                    } catch (IOException ioe) {
                        Toast.makeText(context, R.string.nfcFormattingError, Toast.LENGTH_LONG).show();
                        return false;
                    } catch (FormatException fe) {
                        Toast.makeText(context, R.string.nfcFormattingError, Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(context, R.string.nfcNoNdefError,Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } catch(Exception e) {
            Toast.makeText(context, R.string.nfcUnknownError, Toast.LENGTH_LONG).show();
        }

        return false;
    }

}