package org.usfirst.frc.team5630.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.
 */
public class Robot extends SampleRobot {
	RobotDrive robotDrive;
	Compressor shooterCompressor;
	DoubleSolenoid shooterSolenoid;
	boolean buttonA, buttonB, buttonX, buttonY, buttonBack, buttonStart;
	boolean compressorEnabled, pressureSwitch;
	boolean toggleButtonA = false;
	boolean solenoidOn = false;
	double compressorCurrent;
	
	// Channels for the wheels
	final int kFrontLeftChannel = 2;
	final int kRearLeftChannel = 3;
	final int kFrontRightChannel = 1;
	final int kRearRightChannel = 0;

	// The channel on the driver station that the joystick is connected to
	final int kJoystickChannel = 0;

	Joystick stick = new Joystick(kJoystickChannel);

	public Robot() {
		robotDrive = new RobotDrive(kFrontLeftChannel, kRearLeftChannel, kFrontRightChannel, kRearRightChannel);
		//robotDrive.setInvertedMotor(MotorType.kFrontLeft, true); // invert the
																	// left side
																	// motors
		//robotDrive.setInvertedMotor(MotorType.kRearLeft, true); // you may need
																// to change or
																// remove this
																// to match your
																// robot
		robotDrive.setExpiration(0.1);
		
		shooterCompressor = new Compressor();
		shooterSolenoid = new DoubleSolenoid(0,1);
	}

	
	public void getInput() {
		
		buttonA = stick.getRawButton(1); //Buttons start at 1... [sadface]
		buttonB = stick.getRawButton(2);
		buttonX = stick.getRawButton(3);
		buttonY = stick.getRawButton(4);
		buttonBack = stick.getRawButton(7);
		buttonStart = stick.getRawButton(8);
		
		
		if (toggleButtonA && buttonA) {
			toggleButtonA = false;
			if (solenoidOn == true) {
				solenoidOn = false;
			} else {
				solenoidOn = true;
			}
		} else if (buttonA == false) { 
			toggleButtonA = true;
		}
		
	}
	
	@Override
	public void operatorControl() {
		robotDrive.setSafetyEnabled(true);
		while (isOperatorControl() && isEnabled()) {
			getInput();
			
			//Standard Right Joystick Forward/Backward
			//Left Joystick Right/Left
			robotDrive.arcadeDrive(stick);
			
					
			shooterCompressor.start();
			shooterCompressor.setClosedLoopControl(true); //Always goes after shooterCompressor.start();
			
			
			if (solenoidOn || buttonBack) {
				shooterSolenoid.set(DoubleSolenoid.Value.kForward);
			} else {
				shooterSolenoid.set(DoubleSolenoid.Value.kReverse);
			}
			
			compressorEnabled = shooterCompressor.enabled();	//Get Compressor Status
			pressureSwitch = shooterCompressor.getPressureSwitchValue();
			compressorCurrent = shooterCompressor.getCompressorCurrent();
			
			SmartDashboard.putBoolean("Compressor Enabled", compressorEnabled); //Displays Status of Compressor
			SmartDashboard.putBoolean("Pressure Switch", pressureSwitch);
			SmartDashboard.putNumber("Compressor Current", compressorCurrent);
			SmartDashboard.putBoolean("Button A", buttonA);
			SmartDashboard.putBoolean("toggle", toggleButtonA);

			Timer.delay(0.005); // wait 5ms to avoid hogging CPU cycles
		}
	}
}
