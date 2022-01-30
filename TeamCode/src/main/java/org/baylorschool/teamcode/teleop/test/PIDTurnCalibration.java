package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.ftclib.PIDFController;
import org.baylorschool.library.localization.Odometry;

@TeleOp(name = "PIDTurnCalibration", group = "Test")
public class PIDTurnCalibration extends LinearOpMode {

    Location currentLocation = new Location(Places.middle(-1), Places.middle(0), -180);
    Odometry odometry;
    Mecanum mecanum;
    IMU imu;

    double targetAngle = 0;

    enum Variables {
        PROPORTIONAL,
        INTEGRAL,
        DERIVATIVE,
        FORWARD;
        public Variables next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    @Override
    public void runOpMode() {
        mecanum = new Mecanum(hardwareMap);
        imu = new IMU(hardwareMap);
        odometry = new Odometry(mecanum, hardwareMap, imu, true);

        waitForStart();

        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Variables modifying = Variables.PROPORTIONAL;

        boolean running = false;
        int divisor = 60;

        PIDFController pid = Globals.rotationPIDFController();
        pid.setSetPoint(0);
        pid.setTolerance(3);

        double angleDiff = Location.angleTurn(targetAngle, currentLocation.getHeading());
        double rotPower = pid.calculate(angleDiff);

        while (opModeIsActive()) {
            boolean hasModified = false;
            currentLocation = odometry.calculateNewLocation(currentLocation);
            angleDiff = Location.angleTurn(targetAngle, currentLocation.getHeading());
            rotPower = pid.calculate(angleDiff) / divisor;

            rotPower = Math.min(rotPower, .2);

            if (running)
                mecanum.moveCustomScaling(0, 0, rotPower, 1);
            else {
                mecanum.moveMecanum(0,0,gamepad1.left_stick_x);
                if (gamepad1.x)
                    currentLocation.setHeading(-180);
            }

            if (gamepad1.left_trigger > 0.7) {
                divisor += 1;
                hasModified = true;
            }

            if (gamepad1.right_trigger > 0.7) {
                divisor -= 1;
                hasModified = true;
            }

            if (gamepad1.y)
                running = true;
            else if (gamepad1.a)
                running = false;

            if (gamepad1.b) {
                modifying = modifying.next();
                hasModified = true;
            }

            double change = 0;

            if (gamepad1.right_bumper)
                change += .1;

            if (gamepad1.left_bumper)
                change -= .1;

            if (gamepad1.dpad_up)
                change += .01;

            if (gamepad1.dpad_down)
                change -= .01;

            if (gamepad1.dpad_right)
                change += .001;

            if (gamepad1.dpad_left)
                change -= .001;

            if (change != 0) {
                hasModified = true;
                switch (modifying) {
                    case PROPORTIONAL:
                        pid.setP(pid.getP() + change);
                        break;
                    case INTEGRAL:
                        pid.setI(pid.getI() + change);
                        break;
                    case DERIVATIVE:
                        pid.setD(pid.getD() + change);
                        break;
                    case FORWARD:
                        pid.setF(pid.getF() + change);
                        break;
                }
            }

            telemetry.addData("KP", pid.getP());
            telemetry.addData("KI", pid.getI());
            telemetry.addData("KD", pid.getD());
            telemetry.addData("KF", pid.getF());
            telemetry.addData("Modifying", modifying.name());
            telemetry.addData("Running", running);
            telemetry.addData("Angle diff", angleDiff);
            telemetry.addData("Divisor", divisor);
            telemetry.addData("Power", rotPower);
            telemetry.update();

            if (hasModified) sleep(200);
        }
        mecanum.stop();
    }
}
