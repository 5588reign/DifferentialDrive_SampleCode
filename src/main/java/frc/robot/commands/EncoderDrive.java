/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Encoder;

public class EncoderDrive extends Command {
    private double targetEncoderDistLeft;
    private double targetEncoderDistRight;
    private double speed = .3;
    private boolean isBackwards;

  public EncoderDrive(double distance, double speed) {
      this.speed = speed;
      targetEncoderDistLeft = Encoder.getLeftEncoderDistance() + distance;
      targetEncoderDistRight = Encoder.getRightEncoderDistance() + distance;
      isBackwards = distance < 0 ? true : false;
}

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    }

    // Called repeatedly when this Command is sched tuled to run
    @Override
    protected void execute() {
        if(isBackwards == true){
            speed *= -1;
        }
        Robot.drive.setSpeed(speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        double distanceLeft = targetEncoderDistLeft - Encoder.getRightEncoderDistance();
        double distanceRight = targetEncoderDistRight - Encoder.getRightEncoderDistance();
        boolean distance = (distanceLeft == 0 && distanceRight == 0) ? true : false;
        return distance;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }

}
