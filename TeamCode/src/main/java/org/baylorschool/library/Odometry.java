package org.baylorschool.library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Odometry {
    private DcMotor encoderLeft;
    private DcMotor encoderRight;
    private DcMotor encoderMid;

    private int previousLeft = 0;
    private int previousRight = 0;
    private int previousMid = 0;

    private ElapsedTime elapsedTime;

    private long lastMeasurement = -1;

    public Odometry(DcMotor encoderLeft, DcMotor encoderRight, DcMotor encoderMid, ElapsedTime elapsedTime) {
        this.encoderLeft = encoderLeft;
        this.encoderRight = encoderRight;
        this.encoderMid = encoderMid;
        this.elapsedTime = elapsedTime;
    }

    public Location calculateNewLocation(Location currentLocation) {
        long measurementTime = elapsedTime.nanoseconds();
        int measureLeft = encoderLeft.getCurrentPosition();
        int measureRight = encoderRight.getCurrentPosition();
        int measureMid = encoderMid.getCurrentPosition();

        int diffLeft = measureLeft - previousLeft;
        int diffRight = measureRight - previousRight;
        int diffMid = measureMid - previousMid;

        long diff = measurementTime - lastMeasurement;

        previousLeft = measureLeft;
        previousRight = measureRight;
        previousMid = measureMid;

        // First time it's called, we can't do much.
        if (lastMeasurement == -1) {
            lastMeasurement = measurementTime;
            return currentLocation;
        }

        lastMeasurement = measurementTime;

        // FIXME: WRITE ALGORITHM
        return currentLocation;
    }

}
