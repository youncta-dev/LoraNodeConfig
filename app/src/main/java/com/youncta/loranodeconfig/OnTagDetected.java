package com.youncta.loranodeconfig;

import android.content.Context;
import android.nfc.Tag;
import android.os.Parcelable;

/**
 * Created by adibacco on 26/01/2017.
 */

public interface OnTagDetected {
    void onTagDetected(Context context, Tag tag, Parcelable[] data);

}
