package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * Created by Dinesh on 11/28/2017.
 */

/*
    Notes:
    1) Each wheel is 4" in diameter
    2) 1120 ticks per rotation
    3) 1:3 gear ratio, so there are 3360 ticks per 1 rotation
    4) 1 rotation = 4*pi inches, or about 12"

    5) Robot has to orient color servo so that when the color sensor comes down, it is between
        the jewels

 */

@Autonomous(name = "BlueAutonomous", group = "TeamCode")

public class BlueAutonomous extends LinearOpMode
{
    private DcMotor frontLeft, frontRight, backLeft, backRight, armLeft, armRight, clawWristMotor;
    private Servo colorServo;
    private ColorSensor colorSensor;
    private GyroSensor gyro;
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
            doAutonomous();
        }
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
    private void doAutonomous()
    {
        knockOffJewel();
        parkRobot();
    }

    private void knockOffJewel()
    {
        //lower the color sensor to read it
        colorServo.setPosition(1);
        boolean aheadIsRed = readColorSensor();

        if(aheadIsRed) //if the ball in front is red, turn 30 degrees counterclockwise and then rotate back
        {
            rotateRobot(30.0);
        }
        if(! aheadIsRed) //if the ball in front is blue, turn 30 degrees clockwise to hit the red ball and then rotate back
        {
            rotateRobot(30.0);
        }
    }

    private void parkRobot()
    {
        driveForward(24); //this is a placeholder - idk how many inches to drive
    }

    private boolean readColorSensor()
    {
        int redValue = colorSensor.red();
        int blueValue = colorSensor.blue();
        int greenValue = colorSensor.green(); //this is just to make sure the sensor is working properly

        if(redValue > blueValue) //if we see a red ball, we hit it by rotating 30 degrees counterclockwise
        {
            return true;
        }
        else if(blueValue > redValue) //if we see a blue ball, we hit the red ball by rotating 30 degrees clockwise
        {
            return false;
        }
        else if( (greenValue > redValue) || (greenValue > blueValue) )
        {
            telemetry.addData("STATUS:","WE HAVE A PROBLEM, HOUSTON! COLOR SENSOR IS SEEING GREEN!");
            telemetry.update();
        }

        System.exit(1);
        return false; //just to pacify whiny compiler, code won't reach till here
    }

    //turns robot specified number of degrees, and then turns it back
    private void rotateRobot(double degrees)
    {
        double targetHeading = (degrees < 0) ? (359 - degrees) : degrees; //359 because gyro doesn't go till 360 (b/c 360 degrees = 0 degrees)

    }

    private void driveForward(double numOfInches)
    {

    }

}
