/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.*;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * An example subsystem.  You can replace me with your own Subsystem.
 */
public class Encoder extends Subsystem {
  //distance per pulse = pi * the wheel diameter in inches / pulse per revolution * fudge factor
  private static final double DISTANCE_PER_PULSE_INCHES = (Math.PI * 6) / 10.5 * 1;
  private Robot m_robot = new Robot();

  public CANEncoder frontLeftEncoder = new CANEncoder((CANSparkMax)m_robot.frontLeftMotor);
  public CANEncoder backLeftEncoder = new CANEncoder((CANSparkMax)m_robot.backLeftMotor);
  public CANEncoder frontRightEncoder = new CANEncoder((CANSparkMax)m_robot.frontRightMotor);
  public CANEncoder backRightEncoder = new CANEncoder((CANSparkMax)m_robot.backRightMotor);

  public void resetEncoders() {
    frontLeftEncoder.setPosition(0.0);
    backLeftEncoder.setPosition(0.0);
    frontRightEncoder.setPosition(0.0);
    backRightEncoder.setPosition(0.0);
  }

  public Encoder() {
    frontLeftEncoder.setPositionConversionFactor(DISTANCE_PER_PULSE_INCHES);
    backLeftEncoder.setPositionConversionFactor(DISTANCE_PER_PULSE_INCHES);
    frontRightEncoder.setPositionConversionFactor(DISTANCE_PER_PULSE_INCHES);
    backRightEncoder.setPositionConversionFactor(DISTANCE_PER_PULSE_INCHES);
    resetEncoders();
  } 

  public double getRightEncoderDistance(){
    return (frontRightEncoder.getPosition() + backRightEncoder.getPosition())/2;
  }

  public double getLeftEncoderDistance(){
    return (frontLeftEncoder.getPosition() + backLeftEncoder.getPosition())/2;
  }

  public void displayEncoders(){
  SmartDashboard.putNumber("Right Encoder Avg", (frontRightEncoder.getPosition() + backRightEncoder.getPosition())/2);
  SmartDashboard.putNumber("Left Encoder Avg", (frontLeftEncoder.getPosition() + backLeftEncoder.getPosition())/2);
  }

  // Put methods for controlling this subsystem here. Call these from Commands.
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

}
