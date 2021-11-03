// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package hardware;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Add your docs here. */
public class Limelight {
    /**
     * This class encapsulates the constants required to control the LED's on the Lime Light
     */
    private class LightStatus{
        /** Use the LED Mode set in the current pipeline */
        static public final double PipeLine = 0;
        /** Force the LEDs to off */
        static public final double ForceOff = 1;
        /** Force the LEDs to blink */
        static public final double ForceBlink = 2;
        /** Force the LEDs to on*/
        static public final double ForceOn = 3;
    }
    
    private double mDesiredTargetX;
    private NetworkTable mLimelightTable;
    private final double mTargetHeight;
    private final double mLimelightHeight;
    private final double mLimelightMountingAngle;
    private final double mLimelightDrivebaseTolerance;

    /**
     * Creates a new Limelight. 
     * @param targetHeight the height of the target from the ground (inches)
     * @param limelightHeight the height of the limelight from the ground (inches)
     * @param limelightMountingAngle The angle that the limelight is mounted at, adjacent to the horizontal (degrees)
     * @param limelightDrivebaseTolerance this is the desired angle of the target from the limelight's field of view (degrees)
     */
    public Limelight(double targetHeight, double limelightHeight, double limelightMountingAngle, double limelightDrivebaseTolerance, double shooterDesiredTargetLocation) {
        mLimelightTable = NetworkTableInstance.getDefault().getTable("limelight");
        mDesiredTargetX = shooterDesiredTargetLocation;
        mTargetHeight = targetHeight;
        mLimelightHeight = limelightHeight;
        mLimelightMountingAngle = limelightMountingAngle;
        mLimelightDrivebaseTolerance = limelightDrivebaseTolerance;
        setLightStatus(false);
        
    }

    /**
     * Gets the current horizontal distance from the limelight to the goal.
     * @return horizontal distance from goal (in)
     */
    public double getDistanceFromGoal() {
        // distanceFromGoal is the following formula: (targetHeight - limelightHeight) / tan(limelightMountingAngle + limelightAngleToTarget)
        return (mTargetHeight - mLimelightHeight) / 
            Math.tan(Math.toRadians(mLimelightMountingAngle + mLimelightTable.getEntry("ty").getDouble(0.0))); 
    }
    
    /**
     * Gets the Vision Target's current X position in the camera's field of view.
     * This function assumes that the Vision Target is currently in the camera's field of view.
     * The lime light 2 has a field of view X range of -27.5 to 27.5.
     * @return the Vision Target's current x
     */
    public double getVisionCurrentX() {
       return mLimelightTable.getEntry("tx").getDouble(0.0);
    }
    
    /**
     * Gets a constant value for the Deseried X position of the Vision Target's X position.
     * <p>IE where you want the Vision Target to end up in the camera's field of view. 
     * The lime light 2 has a field of view X range of -27.5 to 27.5, so your value should be in this range.
     * </p>
     * @return the deseried x position
     */
    public double getDesiredTargetX() {
        return mDesiredTargetX;
    }
    
    /**
     * Returns if the Vision Target is aligned.
     * <p>The target is aligned if the magnitude of the difference in angles is below a certain threshold.</p>
     * @return true if aligned
     */
    public boolean isTargetXAligned() {
        return isVisionTargetInCameraFrame() && (Math.abs(getVisionCurrentX() - getDesiredTargetX()) < mLimelightDrivebaseTolerance);
    }

    /**
     * Sets the Lime Light's LED to the desired state
     * @param ledsOn the state
     */
    public void setLightStatus(boolean ledsOn) {
        if (ledsOn) {
            mLimelightTable.getEntry("ledMode").setNumber(LightStatus.ForceOn);
        }
        else {
            mLimelightTable.getEntry("ledMode").setNumber(LightStatus.ForceOff);
        }
    }

    /**
     * Returns if the Vision Target is located within the camera's current field of view.
     * @return true if Vision Target is located within the camera's current field of view
     */
    public boolean isVisionTargetInCameraFrame() {
        return mLimelightTable.getEntry("tv").getDouble(0.0) > 0.0;
    }

    /**
     * Sending values to the SmartDashboard.
     */
    public void updateSmartDashboard() {
        mDesiredTargetX = SmartDashboard.getNumber("Desired Target X", mDesiredTargetX);
        SmartDashboard.putBoolean("Target in Camera Frame", isVisionTargetInCameraFrame());
        SmartDashboard.putBoolean("Target X Aligned", isTargetXAligned());
        SmartDashboard.putNumber("Distance From Goal", getDistanceFromGoal());
    }
}
