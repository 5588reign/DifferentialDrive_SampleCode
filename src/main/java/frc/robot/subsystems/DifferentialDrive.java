/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.RobotDriveBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpiutil.math.MathUtil;


/**
 * An example subsystem. You can replace me with your own Subsystem.
 */
public class DifferentialDrive extends RobotDriveBase implements Sendable, AutoCloseable {
  public static final double kDefaultQuickStopThreshold = 0.2;
  public static final double kDefaultQuickStopAlpha = 0.1;

  private final SpeedController m_leftMotor;
  private final SpeedController m_rightMotor;

  private double m_rightSideInvertMultiplier = -1.0;
  private boolean m_reported;
  //private double m_deadband = RobotDriveBase.kDefaultDeadband;

  public DifferentialDrive(SpeedController leftMotor, SpeedController rightMotor) {
    m_leftMotor = leftMotor;
    m_rightMotor = rightMotor;
  }

  @Override
  public void close() throws Exception {
    //This is where you would get the SendableRegistry involved to keep track of sendables
  }

   /**
   * Tank drive method for differential drive platform.
   *
   * @param leftSpeed     The robot left side's speed along the X axis [-1.0..1.0]. Forward is
   *                      positive.
   * @param rightSpeed    The robot right side's speed along the X axis [-1.0..1.0]. Forward is
   *                      positive.
   * @param squareInputs If set, decreases the input sensitivity at low speeds.
   */
  public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
    if (!m_reported) {
      m_reported = true;
    }

    leftSpeed = MathUtil.clamp(leftSpeed, -1.0, 1.0);
    leftSpeed = applyDeadband(leftSpeed, m_deadband);

    rightSpeed = MathUtil.clamp(rightSpeed, -1.0, 1.0);
    rightSpeed = applyDeadband(rightSpeed, m_deadband);

    // Square the inputs (while preserving the sign) to increase fine control
    // while permitting full power.
    if (squareInputs) {
      leftSpeed = Math.copySign(leftSpeed * leftSpeed, leftSpeed);
      rightSpeed = Math.copySign(rightSpeed * rightSpeed, rightSpeed);
    }

    m_leftMotor.set(leftSpeed * m_maxOutput);
    m_rightMotor.set(rightSpeed * m_maxOutput * m_rightSideInvertMultiplier);

    feed();
  }

  public void setSpeed(double leftSpeed, double rightSpeed) {
    m_leftMotor.set(leftSpeed * m_maxOutput);
    m_rightMotor.set(rightSpeed * m_maxOutput * m_rightSideInvertMultiplier);
  }

  public void setSpeed(double speed) {
    m_leftMotor.set(speed * m_maxOutput);
    m_rightMotor.set(speed * m_maxOutput * m_rightSideInvertMultiplier);
  }

  /**
   * Gets if the power sent to the right side of the drivetrain is multipled by -1.
   *
   * @return true if the right side is inverted
   */
  public boolean isRightSideInverted() {
    return m_rightSideInvertMultiplier == -1.0;
  }

  /**
   * Sets if the power sent to the right side of the drivetrain should be multipled by -1.
   *
   * @param rightSideInverted true if right side power should be multipled by -1
   */
  public void setRightSideInverted(boolean rightSideInverted) {
    m_rightSideInvertMultiplier = rightSideInverted ? -1.0 : 1.0;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("DifferentialDrive");
    builder.setActuator(true);
    builder.setSafeState(this::stopMotor);
    builder.addDoubleProperty("Left Motor Speed", m_leftMotor::get, m_leftMotor::set);
    builder.addDoubleProperty(
        "Right Motor Speed",
        () -> m_rightMotor.get() * m_rightSideInvertMultiplier,
        x -> m_rightMotor.set(x * m_rightSideInvertMultiplier));
  }
  

  @Override
  public void stopMotor() {
    m_leftMotor.stopMotor();
    m_rightMotor.stopMotor();
    feed();
  }

  @Override
  public String getDescription() {
    return "DifferentialDrive";
  }

  public void motorStart(CANSparkMax frontLeft, CANSparkMax backLeft, CANSparkMax frontRight, CANSparkMax backRight){
    frontLeft.restoreFactoryDefaults();
    frontRight.restoreFactoryDefaults();
    backLeft.restoreFactoryDefaults();
    backRight.restoreFactoryDefaults();

    frontRight.setInverted(true);
    backRight.setInverted(true);

    backLeft.follow(frontLeft);
    backRight.follow(frontRight);

  }
}