/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.DifferentialDrive;
import frc.robot.subsystems.Encoder;
import frc.robot.subsystems.Gyroscope;
import frc.robot.OI;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  public static Encoder m_encoder = new Encoder();
  public static Gyroscope gyroscope = new Gyroscope();
  public static OI m_oi;
  public static UsbCamera camera1;
  public static UsbCamera camera2;
  public static UsbCamera camera3;
  public static MjpegServer cameraswitch;
  public static Boolean switchingCameras = true;

  private static final int IMG_WIDTH = 320;
  private static final int IMG_HEIGHT = 240;

  public double centertapeOneX = 0.0;
  public double centertapeTwoX = 0.0;
  public static double turnAwayFromCenter = 0.0;

  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  public CANSparkMax frontLeftMotor = new CANSparkMax(1, MotorType.kBrushless);
  public CANSparkMax backLeftMotor = new CANSparkMax(3, MotorType.kBrushless);
  public CANSparkMax frontRightMotor = new CANSparkMax(2, MotorType.kBrushless);
  public CANSparkMax backRightMotor = new CANSparkMax(4, MotorType.kBrushless);

  public SpeedControllerGroup leftMotorGroup = new SpeedControllerGroup(frontLeftMotor, backLeftMotor);
  public SpeedControllerGroup rightMotorGroup = new SpeedControllerGroup(frontRightMotor, backRightMotor);

  public DifferentialDrive drive = new DifferentialDrive(leftMotorGroup, rightMotorGroup);
  

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_oi = new OI();
    m_chooser.setDefaultOption("Default Auto", new Command(){
    
      @Override
      protected boolean isFinished() {
        return false;
      }
    });
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);
    
    camera3 = CameraServer.getInstance().startAutomaticCapture("camera floor", 2);
    camera3.setBrightness(1);
    camera3.setResolution(IMG_WIDTH, IMG_HEIGHT);
    
    camera1 = CameraServer.getInstance().startAutomaticCapture("camera front", 0);
    camera1.setBrightness(1);
    camera2 = CameraServer.getInstance().startAutomaticCapture("camera back", 1);
    camera2.setBrightness(1);
    

    cameraswitch = CameraServer.getInstance().addSwitchedCamera("camera Switch");
    cameraswitch.setSource(camera2);
  
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    m_encoder.displayEncoders();
    drive.tankDrive(m_oi.getDriverXBox().getRawAxis(1), m_oi.getDriverXBox().getRawAxis(5), true);
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   * You can use it to reset any subsystem information you want to clear when
   * the robot is disabled.
   */
  @Override
  public void disabledInit() {
    m_encoder.resetEncoders();
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString code to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons
   * to the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();

    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector",
     * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
     * = new MyAutoCommand(); break; case "Default Auto": default:
     * autonomousCommand = new ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    /*double centertapeOneX;
    double centertapeTwoX;
    synchronized (imgLock) {
      
      centertapeOneX = this.centertapeOneX;
      centertapeTwoX = this.centertapeTwoX;
    }
    turnAwayFromCenter = (IMG_WIDTH / 2) - (centertapeOneX + centertapeTwoX)/2;
    System.out.println("turn away from center :" + turnAwayFromCenter);
    System.out.println("tape 1" + centertapeOneX);
    System.out.println("tape 2" + centertapeTwoX);*/


    Scheduler.getInstance().run();
    }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
