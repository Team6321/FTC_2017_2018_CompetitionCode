package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
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
    private final double TICKS_PER_REV = 1120;
    private final double WHEEL_DIAMETER = 4.25; //in inches, of course
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

    private void initServo()
    {
        colorServo = hardwareMap.servo.get("colorServo");
    }

    private void initColorSensor()
    {
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
    }

    private void doAutonomous()
    {
        knockOffJewel();
        rotateRobot(108); //this makes it turn front straight ahead to facing the parking zone
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
            rotateRobot(-30.0);
        }
        if(! aheadIsRed) //if the ball in front is blue, turn 30 degrees clockwise to hit the red ball and then rotate back
        {
            rotateRobot(-30.0);
            rotateRobot(30.0);
        }
    }

    private void parkRobot()
    {
        drive(24,0.5, 0.5); //this is a placeholder - idk how many inches to drive
    }

    private boolean readColorSensor()
    {
        int redValue = colorSensor.red();
        int blueValue = colorSensor.blue();
        int greenValue = colorSensor.green(); //this is just a check to make sure the sensor is working properly

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
    private void rotateRobot(double turnDegrees)
    {
        boolean turnClockwise;
        ////////////////Turn motors respective directions so we don't turn more 180 degrees counterclockwise /////////
        if(turnDegrees >= 180)
        {
            turnClockwise = true;
        }
        else
        {
            turnClockwise = false;
        }

        //////////////Now do calculations for turning and stuff/////////////////////////////////
        double numOfInchesToTurn = calculateTurnDistance(turnDegrees);

        if(turnClockwise)
        {
            drive(numOfInchesToTurn, 0.5, -0.5); //goes until reaches distance to get to turn degree
        }
        else
        {
            drive(numOfInchesToTurn, -0.5, 0.5); //goes until reaches distance to get to turn degree
        }

    }

    private double calculateTurnDistance(double turnTheta)
    {
        /*this uses the fact that both the point you start off on and the point you end on are
        on a circle with the radius of the length or width of the robot

        we're assuming that the length (and width) of the robot is 18" here
        */

        double turnDistance = (double)(turnTheta / 360.0); //finds how much of circle it is cutting off
        turnDistance = turnDistance * (2 * Math.PI * (9 * Math.sqrt(2))); //9*rt(2) is placeholder, idk length/ width of robot

        return turnDistance;
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

    private void setPosition(double numOfInches)
    {
        int distance = (int)(numOfInches * TICKS_PER_INCH);

        frontLeft.setTargetPosition(distance);
        backLeft.setTargetPosition(distance);
        frontRight.setTargetPosition(distance);
        frontRight.setTargetPosition(distance);
    }

    private void runToPosition(double leftPower, double rightPower)
    {
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //set power to all motors
        setPower(leftPower,rightPower);


        while(frontLeft.isBusy() && frontRight.isBusy())
        {
            // let the motors keep running
        }

        //tells all motors to stop
        setPower(0,0);
    }

    private void stopUsingEncoders()
    {
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void setPower(double leftPower, double rightPower)
    {
        frontLeft.setPower(leftPower);
        backLeft.setPower(leftPower);
        frontRight.setPower(rightPower);
        frontRight.setPower(rightPower);
    }

}
