package org.baylorschool.library;

import android.content.Context;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.io.File;

public class Sounds {
    private Context context;

    private final File successFile;

    private final String soundPath = "/FIRST/blocks/sounds";

    public Sounds(HardwareMap hardwareMap) throws Exception {
        context = hardwareMap.appContext;

        successFile = new File("/sdcard" + soundPath + "/successone.wav");
        if (!successFile.exists())
            throw new Exception();
    }

    public void playSuccess() {
        SoundPlayer.getInstance().startPlaying(context, successFile);
    }
}
