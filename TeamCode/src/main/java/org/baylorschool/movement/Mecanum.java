package org.baylorschool.movement;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Mecanum {

    private DcMotor frMotor;
    private DcMotor brMotor;
    private DcMotor flMotor;
    private DcMotor blMotor;

    /**
     *
     * @param frMotor
     * @param brMotor
     * @param flMotor
     * @param blMotor
     */
    public Mecanum(DcMotor frMotor, DcMotor brMotor, DcMotor flMotor, DcMotor blMotor) {
        this.frMotor = frMotor;
        this.brMotor = brMotor;
        this.flMotor = flMotor;
        this.blMotor = blMotor;
    }

    /**
     *
     * @param angle in degrees (forward is 0, right is 90, back is 180, and left is 270.
     * @param speed Speed (from 0 to 1)
     */
    public void move(double angle, double speed) {

    }

}
