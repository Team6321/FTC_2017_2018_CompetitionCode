package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Dinesh on 12/14/2017.
 */

//@TeleOp(name = "TestCode", group = "Team Code")

public class TestCode extends LinearOpMode
{
    private DcMotor frontLeft, frontRight, backLeft, backRight, armLeft, armRight, clawWristMotor;
    private Servo colorServo;
    private ColorSensor colorSensor;
    private final double TICKS_PER_REV = 1120.0;
    private final double WHEEL_DIAMETER = 4.25; //in inches, of course
    private final double GEAR_RATIO = 3.0; //geared so that we have to go 3 times as many ticks/rotation
    private final double TICKS_PER_INCH = (TICKS_PER_REV * GEAR_RATIO) / (WHEEL_DIAMETER * Math.PI); //this is ticks in 1 rotation divided by circumference

    @Override
    public void runOpMode() throws InterruptedException
    {
        //runs only once when robot is initialized
        initHardware();

        //after start is pressed, continue to the while loop
        waitForStart();

        doCrap();
    }

    public void doCrap()
    {

        knockOffJewel();
    }

    private void knockOffJewel() {
        //lower the color sensor to read it
        colorServo.setPosition(-50.00);

        boolean ballAheadIsBlue = isBlue();

        if (ballAheadIsBlue) //if the ball in front is red, turn 30 degrees counterclockwise and then rotate back
        {
            rotateRobot(30.0);
            rotateRobot(-30.0);
        }
        if (!ballAheadIsBlue) //if the ball in front is blue, turn 30 degrees clockwise to hit the red ball and then rotate back
        {
            rotateRobot(-30.0);
            rotateRobot(30.0);
        }

        colorServo.setPosition(0);
    }

    //turns robot specified number of degrees, and then turns it back
    private void rotateRobot(double turnDegrees)
    {
        boolean turnClockwise = (turnDegrees >= 180);

        //////////////Now do calculations for turning and stuff/////////////////////////////////
        double numOfInchesToTurn = calculateTurnDistance(turnDegrees);

        if(turnClockwise)
        {
            drive(numOfInchesToTurn, -0.5, 0.5); //goes until reaches distance to get to turn degree
        }
        else
        {
            drive(numOfInchesToTurn, 0.5, -0.5); //goes until reaches distance to get to turn degree
        }

    }

    private boolean isBlue()
    {
        int redValue = colorSensor.red();
        int blueValue = colorSensor.blue();
        return (blueValue > redValue);
    }


    private double calculateTurnDistance(double turnTheta)
    {
        /*
        this uses the fact that both the point you start off on and the point you end on are
        on a circle with the radius of sqrt(18^2 + 17.5^2)/2
        robotLength = 17"
        robotWidth = 18.5"

        */
        double diameterOfCircle = Math.sqrt(Math.pow(18.0, 2) + Math.pow(17.5, 2));
        double turnDistance = (turnTheta / 360.0) * (diameterOfCircle * Math.PI); //finds how much of circle it is cutting off
        return turnDistance;
    }

    private void initHardware()
    {
        initMotors();
        initServo();
        initColorSensor();
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

        //reversing previous configuration, due to guessing wrong
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);

        //tentatively reversing left motor for the 2 arm motors, as well
        armLeft.setDirection(DcMotor.Direction.REVERSE);
        armRight.setDirection(DcMotor.Direction.FORWARD);

        //also tentatively reversing the claw "wrist" motor
        clawWristMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    private void drive(double numOfInches, double leftPower, double rightPower)
    {
        runUsingEncoders();
        resetEncoders();
        setPosition(numOfInches);
        runToPosition(leftPower, rightPower);
        stopUsingEncoders();
    }

    private void resetEncoders()
    {
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void runUsingEncoders()
    {
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void setPosition(double numOfInches)
    {
        int distance = (int)Math.round(numOfInches * TICKS_PER_INCH);

        backLeft.setTargetPosition(distance);
        backRight.setTargetPosition(distance);
        frontLeft.setTargetPosition(distance);
        frontRight.setTargetPosition(distance);
    }

    private void runToPosition(double leftPower, double rightPower)
    {
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //set power to all motors
        setPower(leftPower, rightPower);


        while(frontLeft.isBusy() && frontRight.isBusy())
        {
            // let the motors keep running
        }

        //tells all motors to stop
        setPower(0,0);
    }

    private void stopUsingEncoders()
    {
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void setPower(double leftPower, double rightPower)
    {
        backLeft.setPower(leftPower);
        backRight.setPower(rightPower);
        frontLeft.setPower(leftPower);
        frontRight.setPower(rightPower);
    }

    private void initServo()
    {
        colorServo = hardwareMap.servo.get("colorServo");
    }

    private void initColorSensor()
    {
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
    }

}


