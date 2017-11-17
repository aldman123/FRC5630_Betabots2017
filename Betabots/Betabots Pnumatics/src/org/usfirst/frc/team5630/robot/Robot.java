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
	JoystickButton buttonA, buttonB, buttonX, buttonY, buttonBack, buttonStart;

	// Channels for the wheels
	final int kFrontLeftChannel = 2;
	final int kRearLeftChannel = 3;
	final int kFrontRightChannel = 1;
	final int kRearRightChannel = 0;

	// The channel on the driver station that the joystick is connected to
	final int kJoystickChannel = 0;
	
	boolean compressorOn, solenoidOn, compressorEnabled, pressureSwitch;
	double compressorCurrent;

	Joystick stick = new Joystick(kJoystickChannel);

	public Robot() {
		robotDrive = new RobotDrive(kFrontLeftChannel, kRearLeftChannel, kFrontRightChannel, kRearRightChannel);
		robotDrive.setInvertedMotor(MotorType.kFrontLeft, true); // invert the
																	// left side
																	// motors
		robotDrive.setInvertedMotor(MotorType.kRearLeft, true); // you may need
																// to change or
																// remove this
																// to match your
																// robot
		robotDrive.setExpiration(0.1);
		
		
		shooterCompressor = new Compressor();
		shooterSolenoid = new DoubleSolenoid(0,1);
	}

	/**
	 * Runs the motors with Mecanum drive.
	 */
	@Override
	
	public void getInputs() {
		buttonA = new JoystickButton(stick, 0);
		buttonB = new JoystickButton(stick, 1);
		buttonX = new JoystickButton(stick, 2);
		buttonY = new JoystickButton(stick, 3);
	}
	
	
	public void operatorControl() {
		robotDrive.setSafetyEnabled(true);
		while (isOperatorControl() && isEnabled()) {

			// Use the joystick X axis for lateral movement, Y axis for forward
			// movement, and Z axis for rotation.
			// This sample does not use field-oriented drive, so the gyro input
			// is set to zero.
			robotDrive.mecanumDrive_Cartesian(stick.getX(), stick.getY(), stick.getZ(), 0);
			
			
			
			//compressorOn = stick.getRawButton(8); //The START? Button
			solenoidOn = stick.getRawButton(7); //The BACK Button
			
					
			shooterCompressor.start();
			shooterCompressor.setClosedLoopControl(true); //Always goes after shooterCompressor.start();
			
			
			if (solenoidOn) {
				shooterSolenoid.set(DoubleSolenoid.Value.kForward);
			} else {
				shooterSolenoid.set(DoubleSolenoid.Value.kReverse);
			}
			
			compressorEnabled = shooterCompressor.enabled();					//Get Compressor Status
			pressureSwitch = shooterCompressor.getPressureSwitchValue();
			compressorCurrent = shooterCompressor.getCompressorCurrent();
			
			SmartDashboard.putBoolean("Compressor Enabled", compressorEnabled); //Displays Status of Compressor
			SmartDashboard.putBoolean("Pressure Switch", pressureSwitch);
			SmartDashboard.putNumber("Compressor Current", compressorCurrent);

			Timer.delay(0.005); // wait 5ms to avoid hogging CPU cycles
		}
	}
}
