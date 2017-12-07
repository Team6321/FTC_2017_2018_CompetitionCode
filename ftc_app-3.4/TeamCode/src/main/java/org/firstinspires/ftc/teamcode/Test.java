package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * Created by Sriram on 12/7/2017.
 */


@TeleOp(name = "TestCode", group = "TeamCode")


public class Test extends LinearOpMode
{
    private DcMotor frontLeft, frontRight, backLeft, backRight, armLeft, armRight, clawWristMotor;
    private Servo colorServo;
    private ColorSensor colorSensor;
    private GyroSensor gyro;
    private double clawPosition; //range: 0 to 1 (represents 0 to 1 pi radians)
    private final double TICKS_PER_REV = 1120;
    private final double WHEEL_DIAMETER = 4.25; //in inches, of course
    private final double GEAR_RATIO = 3; //geared so that we have to go 3 times as many ticks/rotation
    private final double TICKS_PER_INCH = (TICKS_PER_REV * GEAR_RATIO) / (WHEEL_DIAMETER * Math.PI); //this is ticks in 1 rotation divided by circumference


    public void runOpMode()
    {
        initHardware();

        waitForStart();

        testCode();
    }
    private void initHardware()
    {
        initMotors();
        initServo();
        initColorSensor();
        initGyro();
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
        colorServo = hardwareMap.servo.get("colorServo");
    }

    private void initColorSensor()
    {
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
    }

    private void initGyro()
    {
        gyro = hardwareMap.gyroSensor.get("gyro");
    }

    private void testCode()
    {
        //place whatever code to test here
        driveForward(24 );
    }

    private void driveForward(double numOfInches)
    {
        runUsingEncoders();
        resetEncoders();
        setPosition( (int)(TICKS_PER_INCH * numOfInches) );
        runToPosition();
        stopUsingEncoders();
    }

    private void resetEncoders()
    {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void runUsingEncoders()
    {
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void setPosition(int numOfTicks)
    {
        frontLeft.setTargetPosition(numOfTicks);
        backLeft.setTargetPosition(numOfTicks);
        frontRight.setTargetPosition(numOfTicks);
        frontRight.setTargetPosition(numOfTicks);
    }

    private void stopUsingEncoders()
    {
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void runToPosition()
    {
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while(frontLeft.isBusy() && frontRight.isBusy())
        {
            // let the motors keep running
        }
    }
}
