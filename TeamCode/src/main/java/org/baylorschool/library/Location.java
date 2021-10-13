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
        this.x = translation.get(0);
        this.y = translation.get(1);
        this.z = translation.get(2);

        this.roll = rotation.firstAngle;
        this.pitch = rotation.secondAngle;
        this.heading = rotation.thirdAngle;
    }

    public Location(float x, float y, float z, float roll, float pitch, float heading) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.roll = roll;
        this.pitch = pitch;
        this.heading = heading;
    }

    public void reportTelemtry(Telemetry telemetry) {
        telemetry.addData("Pos (inches)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                x, y, z);
        telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", roll, pitch, heading);
    }

    public static Location difference(Location location1, Location location2) {
        return new Location(
                Math.abs(location1.getX() - location2.getX()),
                Math.abs(location1.getY() - location2.getY()),
                Math.abs(location1.getZ() - location2.getZ()),
                Math.abs(location1.getRoll() - location2.getRoll()),
                Math.abs(location1.getPitch() - location2.getPitch()),
                Math.abs(location1.getHeading() - location2.getHeading())
        );
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
