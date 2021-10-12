package org.baylorschool.library;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Location {

    private float x, y, z;
    private float roll, pitch, heading;

    public Location(OpenGLMatrix matrix) {
        VectorF translation = matrix.getTranslation();
        Orientation rotation = Orientation.getOrientation(matrix, EXTRINSIC, XYZ, DEGREES);
        x = translation.get(0);
        y = translation.get(1);
        z = translation.get(2);

        roll = rotation.firstAngle;
        pitch = rotation.secondAngle;
        heading = rotation.thirdAngle;
    }

    public void reportTelemtry(Telemetry telemetry) {
        telemetry.addData("Pos (inches)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                x, y, z);
        telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", roll, pitch, heading);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getRoll() {
        return roll;
    }

    public float getPitch() {
        return pitch;
    }

    public float getHeading() {
        return heading;
    }
}
