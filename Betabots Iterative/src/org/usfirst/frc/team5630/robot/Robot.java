package org.usfirst.frc.team5630.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	RobotDrive robotDrive;
	Talon flywheel;
	Compressor shooterCompressor;
	DoubleSolenoid shooterSolenoid;
	boolean buttonA, buttonB, buttonX, buttonY, buttonBack, buttonStart;
	boolean compressorEnabled, pressureSwitch;
	boolean toggleButtonA = false;
	boolean toggleButtonB = false;
	boolean solenoidOn = false;
	boolean flywheelOn = false;
	double compressorCurrent;
	final double flywheelSpeed = 0.35;
	final double flywheelSpeedAuto = 0.4;
	int autoTime = 0;

	// Channels for the wheels
	final int kFrontLeftChannel = 2;
	final int kRearLeftChannel = 3;
	final int kFrontRightChannel = 1;
	final int kRearRightChannel = 0;

	// The channel on the driver station that the joystick is connected to
	final int kJoystickChannel = 0;

	Joystick stick = new Joystick(kJoystickChannel);

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);

		robotDrive = new RobotDrive(kFrontLeftChannel, kRearLeftChannel, kFrontRightChannel, kRearRightChannel);
		robotDrive.setSafetyEnabled(true);
		
		robotDrive.setExpiration(1);

		shooterCompressor = new Compressor();
		shooterSolenoid = new DoubleSolenoid(0, 1);

		flywheel = new Talon(7);

	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);

		autoTime = 0;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto: // if we already have compressed air
		default:
			// Put default auto code here
			if (autoTime < 60) {
				shooterSolenoid.set(DoubleSolenoid.Value.kReverse);
				robotDrive.arcadeDrive(-0.5, 0);
			} else if (autoTime < 120) {
				robotDrive.arcadeDrive(0, -0.35);
			} else if (autoTime < 180) {
				robotDrive.arcadeDrive(0, 0);
				flywheel.set(flywheelSpeedAuto);
			} else if (autoTime < 240) {
				shooterSolenoid.set(DoubleSolenoid.Value.kForward);
			} else if (autoTime < 280) {
				shooterSolenoid.set(DoubleSolenoid.Value.kReverse);
				robotDrive.arcadeDrive(0, 0.2);
				//flywheel.set(0);
			} else if (autoTime < 300) {
				shooterSolenoid.set(DoubleSolenoid.Value.kForward);
			} else if (autoTime < 330) {
				shooterSolenoid.set(DoubleSolenoid.Value.kReverse);
			} else if (autoTime < 360) {
				shooterSolenoid.set(DoubleSolenoid.Value.kForward);
			} else if (autoTime < 390) {
				shooterSolenoid.set(DoubleSolenoid.Value.kReverse);
			} else if (autoTime < 410) {
				shooterSolenoid.set(DoubleSolenoid.Value.kForward);
			} else if (autoTime < 440) {
				shooterSolenoid.set(DoubleSolenoid.Value.kReverse);
			}
			
			autoTime += 1;
			System.out.print(autoTime);
			
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		getInput();

		//CameraServer.getInstance().startAutomaticCapture();

		// Standard Right Joystick Forward/Backward
		// Left Joystick Right/Left
		robotDrive.arcadeDrive(stick.getRawAxis(1), -stick.getRawAxis(4));

		if (shooterCompressor.getPressureSwitchValue()) {
			shooterCompressor.stop();
		} else {
			shooterCompressor.start();
		}
		shooterCompressor.start();
		shooterCompressor.setClosedLoopControl(true); // Always goes after shooterCompressor.start();

		if (solenoidOn || buttonBack) {
			shooterSolenoid.set(DoubleSolenoid.Value.kForward);
		} else {
			shooterSolenoid.set(DoubleSolenoid.Value.kReverse);
		}

		if (flywheelOn) {
			flywheel.set(flywheelSpeed);
		} else {
			flywheel.set(0);
		}

		compressorEnabled = shooterCompressor.enabled(); // Get Compressor Status
		pressureSwitch = shooterCompressor.getPressureSwitchValue();
		compressorCurrent = shooterCompressor.getCompressorCurrent();

		SmartDashboard.putBoolean("Compressor Enabled", compressorEnabled); // Displays Status of Compressor
		SmartDashboard.putBoolean("Pressure Switch", pressureSwitch);
		SmartDashboard.putNumber("Compressor Current", compressorCurrent);
		SmartDashboard.putBoolean("Button A", buttonA);
		SmartDashboard.putBoolean("Flywheel On", flywheelOn);
		SmartDashboard.putNumber("Flywheel Speed", flywheel.getSpeed());
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}

	private void getInput() {

		buttonA = stick.getRawButton(1); // Buttons start at 1... [sadface]
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

		if (toggleButtonB && buttonB) {
			toggleButtonB = false;
			if (flywheelOn == true) {
				flywheelOn = false;
			} else {
				flywheelOn = true;
			}
		} else if (buttonB == false) {
			toggleButtonB = true;
		}

	}

}
