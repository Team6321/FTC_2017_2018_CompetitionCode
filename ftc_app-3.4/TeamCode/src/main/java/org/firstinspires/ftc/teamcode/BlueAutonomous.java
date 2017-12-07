package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Dinesh on 11/28/2017.
 */

/*
    Notes:
    1) Each wheel is 4" in diameter
    2) 1120 ticks per rotation
    3) 1:3 gear ratio, so there are 3360 ticks per 1 rotation
    4) 1 rotation = 4*pi inches, or about 12"
 */

public class BlueAutonomous extends LinearOpMode
{
    //EACH WHEEL IS 4" in diameter
    //
    private DcMotor frontLeft, frontRight, backLeft, backRight, armLeft, armRight, clawWristMotor;
    private Servo clawServo;
    private double clawPosition; //range: 0 to 1 (represents 0 to 1 pi radians)
    private final double TICKS_PER_REV = 1120.0;
    private final double WHEEL_DIAMETER = 4.0 //in inches, of course
    private final double GEAR_RATIO = 3; //geared so that we have to go 3 times as many ticks/rotation
    private final double TICKS_PER_INCH = (TICKS_PER_REV * GEAR_RATIO) / (WHEEL_DIAMETER * Math.PI); //this is ticks in 1 rotation divided by circumference

    @Override
    public void runOpMode() throws InterruptedException
    {
        //runs only once when robot is initialized
        initHardware();

        //after start is pressed, continue to the while loop
        waitForStart();

        while(opModeIsActive())
        {

        }
    }
    private void initHardware()
    {
        initMotors();
        initServo();
        telemetry.addData("STATUS: ", "All hardware initialized successfully.");
        telemetry.update();
    }

    private void initMotors()
    {
        //maps all motors to the hardwareMap
        frontLeft = hardwareMap.dcMotor.get("FLeft");
        frontRight = hardwareMap.dcMotor.get("FRight");
        backLeft = hardwareMap.dcMotor.get("BLeft");
        backRight = hardwareMap.dcMotor.get("BRight");
        armLeft = hardwareMap.dcMotor.get("AMLeft");
        armRight = hardwareMap.dcMotor.get("AMRight");
        clawWristMotor = hardwareMap.dcMotor.get("mChain");

        //setting all motors to run with encoders
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //reversing previous configuration, due to guessing wrong
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);

        //tentatively reversing left motor for the 2 arm motors, as well
        armLeft.setDirection(DcMotor.Direction.REVERSE);
        armRight.setDirection(DcMotor.Direction.FORWARD);

        //also tentatively reversing the claw "wrist" motor
        clawWristMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    private void initServo()
    {
        clawServo = hardwareMap.servo.get("sClaw");
    }

    private void driveForward(double numOfInches)
    {

    }

}
