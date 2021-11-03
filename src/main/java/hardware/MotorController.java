/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package hardware;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class MotorController {
    private String mName;
    private CANSparkMax mSparkMax;
    private CANEncoder mEncoder;
    private CANPIDController mPIDController = null;
    private double mP;
    private double mI;
    private double mD;
    
    /**
    * Creates a SparkMax and resets it to default.
    * @param name the prefix for all the variables in SmartDashboard 
    * @param deviceID the Device ID for the new SparkMax
    */
    public MotorController(String name, int deviceID) {
        mName = name;
        mSparkMax = new CANSparkMax(deviceID, MotorType.kBrushless);
        //Initializing encoder
        //We can only call .getEncoder once because of a bug in spark max api
        //which causes encoder initialization to occur on every call of .getEncoder() this breaks things
        mEncoder = mSparkMax.getEncoder();
        mSparkMax.restoreFactoryDefaults();
    }

    /**
     * Creates the PID for the controller and calls motor controller
     * @see MotorController(String name, int deviceID)
     * @param name the prefix for all the variables in SmartDashboard 
     * @param deviceID the Device ID for the new SparkMax
     * @param smartCurrentLimit the current limit for prevention of brown outs
     * @param enablePid finds whether we are enabling pid
     */
    //Todo: we must add constants for PID values once we have found the right values
    public MotorController(String name, int deviceID, int smartCurrentLimit, boolean... enablePid) {
        this(name, deviceID); //calls the constructor for correct arguements
        //Current limiting is required to prevent brown outs
        mSparkMax.setSmartCurrentLimit(smartCurrentLimit);
        //If enablePid has any number of booleans greater than 0 we are enabling pid
        if (enablePid.length > 0)
        {
            mP = SmartDashboard.getNumber(mName + " P Value", 1.0);
            mI = SmartDashboard.getNumber(mName + " I Value", 0.0);
            mD = SmartDashboard.getNumber(mName + " D Value", 0.0);
            mPIDController = mSparkMax.getPIDController();
            setPID();
        }
        mSparkMax.setOpenLoopRampRate(.1);
    }

    /**
     * Gets the Spark Max
     * @return returns Spark Max
     */
    public CANSparkMax getSparkMax() {
        return mSparkMax;
    }

    /**
     * Gets the Encoder
     * @return returns Encoder
     */
    public CANEncoder getEncoder() {
        return mEncoder;
    }

    /**
     * Gets the PID
     * @return returns PID controller
     */
    public CANPIDController getPID() {
        return mPIDController;
    }

    /**
     * Sets the PID for the controller and puts the PID values in Smart Dashboard
     */
    public void setPID() {
        mPIDController.setP(mP);
        mPIDController.setI(mI);
        mPIDController.setD(mD);
        SmartDashboard.putNumber(mName+" P Value", mP);
        SmartDashboard.putNumber(mName+" I Value", mI);
        SmartDashboard.putNumber(mName+" D Value", mD);
    }

    /**
     * Updates the Smart Dashboard and checks the PID values to determine if update is needed
     */
    public void updateSmartDashboard() {
        //The simulation crashes whenever .getEncoder() is called
        if(mPIDController != null) {
            if (SmartDashboard.getNumber(mName + " P Value", mP) != mP) {
                mP = SmartDashboard.getNumber(mName + " P Value", mP);
                mPIDController.setP(mP);
            }
            if (SmartDashboard.getNumber(mName + " I Value", mI) != mI) {
                mI = SmartDashboard.getNumber(mName + " I Value", mI);
                mPIDController.setI(mI);
            }
            if (SmartDashboard.getNumber(mName + " D Value", mD) != mD) {
                mD = SmartDashboard.getNumber(mName + " D Value", mD);
                mPIDController.setD(mD);
            }
        }
    }
    
}
