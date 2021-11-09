package org.baylorschool.library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Sensors {
    private Vuforia vuforia;
    private IMU imu;
    private Mecanum mecanum;

    public Sensors(HardwareMap hardwareMap, boolean useVuforia) {
        this.imu = new IMU();
        this.mecanum = new Mecanum(hardwareMap);
        this.vuforia = useVuforia ? new Vuforia(hardwareMap) : null;
    }

    public void initialize(HardwareMap hardwareMap, double initialHeading) {
        imu.initializeImu(hardwareMap);
        imu.forceValue(initialHeading);

        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        if (vuforia != null) {
            vuforia.initializeParamers(false);
            vuforia.startTracking();
        }
    }

    public void end() {
        mecanum.stop();
        vuforia.stopTracking();
    }

    public Vuforia getVuforia() {
        return vuforia;
    }

    public IMU getImu() {
        return imu;
    }

    public Mecanum getMecanum() {
        return mecanum;
    }
}
