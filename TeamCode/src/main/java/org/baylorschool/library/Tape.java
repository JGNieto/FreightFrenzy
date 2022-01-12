package org.baylorschool.library;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;

public class Tape {
    CRServo servoExtend;
    Servo servoTilt;

    long lastTiltUpdate = 0;
    final double tiltMultiplier = 0.7;
    double currentTilt = 0;

    public Tape(CRServo servoExtend, Servo servoTilt) {
        this.servoExtend = servoExtend;
        this.servoTilt = servoTilt;
    }

    public Tape(HardwareMap hardwareMap) {
        this.servoExtend = hardwareMap.get(CRServo.class, Globals.tapeExtend);
        this.servoTilt = hardwareMap.get(Servo.class, Globals.tapeTilt);
    }

    public void setExtendPower(double power) {
        power = power < -1 ? -1 : (power > 1 ? 1 : power);
        servoExtend.setPower(power);
    }

    public void setTiltPower(double power) {
        long thisTime = System.currentTimeMillis();
        if (lastTiltUpdate != 0) {
            currentTilt += power * tiltMultiplier * (lastTiltUpdate - thisTime) / 1000;
        }
        lastTiltUpdate = thisTime;
        servoTilt.setPosition(currentTilt);
    }
}
