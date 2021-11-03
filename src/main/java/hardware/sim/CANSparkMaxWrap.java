/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package hardware.sim;

import com.revrobotics.*;


/**
 * Add your docs here.
 */
public class CANSparkMaxWrap extends CANSparkMax {

    private final boolean simulated = Robot.isSimulation();
    private net.thefletcher.revrobotics.CANSparkMax simDevice; 

    public class CANEncoderSim extends CANEncoder {
        public CANEncoderSim(CANSparkMax device) {
            super(device);
        }

        @Override
        public double getVelocity() {
            if (simulated) {
                return simDevice.getEncoder().getVelocity();
            } 
            else {
                return super.getVelocity();
            }
        }

        @Override
        public double getPosition() {
            if (simulated) {
                return simDevice.getEncoder().getPosition();
            } 
            else {
                return super.getPosition();
            }
        }
    }

    public class CANPIDControllerSim extends CANPIDController {
        public CANPIDControllerSim(CANSparkMax device) {
            super(device);
        }

        @Override
        public CANError setReference(double value, ControlType ctrl) {
            if (simulated) {
                return CANError.fromInt(simDevice.getPIDController().setReference(value, net.thefletcher.revrobotics.enums.ControlType.values()[ctrl.ordinal()]).ordinal());
            } 
            else {
                return super.setReference(value, ctrl);
            }
        }
    }

    public CANSparkMaxWrap(int deviceID, MotorType type) {
        super(deviceID, type);
        simDevice = new net.thefletcher.revrobotics.CANSparkMax(deviceID, getMotorTypeSim(type));
    }

    @Override
    public void setInverted(boolean isInverted) {
        if (simulated) {
            simDevice.setInverted(isInverted);
        } 
        else {
            super.setInverted(isInverted);
        }
    }

    @Override
    public boolean getInverted() {
        if (simulated) {
            return simDevice.getInverted();
        } 
        else {
            return super.getInverted();
        }
    }

    @Override
    public void set(double speed) {
        if (simulated) {
            simDevice.set(speed);
        } 
        else {
            super.set(speed);
        }
    }

    @Override
    public double get() {
        if (simulated) {
            return simDevice.get();
        } 
        else {
            return super.get();
        }
    }

    @Override
    public void stopMotor() {
        if (simulated) {
            simDevice.stopMotor();
        } 
        else {
            super.stopMotor();
        }
    }

    @Override
    public double getAppliedOutput() {
        if (simulated) {
		    return simDevice.getAppliedOutput();
        } 
        else {
            return super.getAppliedOutput();
        }
    }
    
    public net.thefletcher.revrobotics.CANSparkMax getSimDevice() {
        return simDevice;
    }
    
    private CANError getCanErrorFromSim(net.thefletcher.revrobotics.enums.CANError e) {
        return CANError.fromInt(e.ordinal());
    }

    private net.thefletcher.revrobotics.enums.CANError getCanErrorSim(CANError e) {
        return net.thefletcher.revrobotics.enums.CANError.fromInt(e.ordinal());
    } 

    private net.thefletcher.revrobotics.enums.ControlType getControlTypeSim(ControlType t) {
        return  net.thefletcher.revrobotics.enums.ControlType.values()[t.ordinal()];
    }

    private ControlType getControlTypeFromSim(net.thefletcher.revrobotics.enums.ControlType t) {
        return  ControlType.values()[t.ordinal()];
    }

    private net.thefletcher.revrobotics.enums.MotorType getMotorTypeSim(MotorType t) {
      return net.thefletcher.revrobotics.enums.MotorType.fromId(t.ordinal());  
    }

    private MotorType getMotorTypeFromSim(net.thefletcher.revrobotics.enums.MotorType t) {
      return MotorType.fromId(t.ordinal());  
    }
    
    @Override
	public CANError follow(final CANSparkMax leader) {
        if (simulated && leader instanceof CANSparkMaxWrap) {
		    return getCanErrorFromSim(simDevice.follow(((CANSparkMaxWrap) leader).getSimDevice()));
        } 
        else {
            return super.follow(leader);
        }

    }

    @Override
	public CANError follow(ExternalFollower leader, int deviceID) {
        if (simulated) {
		    return getCanErrorFromSim(simDevice.follow(net.thefletcher.revrobotics.enums.ExternalFollower.kFollowerSparkMax, deviceID));
        } 
        else {
            return super.follow(leader, deviceID);
        }

    }

    @Override
    public CANEncoderSim getEncoder() {
        return new CANEncoderSim(this);
    }

    @Override
    public CANPIDControllerSim getPIDController() {
        return new CANPIDControllerSim(this);
    }
}
