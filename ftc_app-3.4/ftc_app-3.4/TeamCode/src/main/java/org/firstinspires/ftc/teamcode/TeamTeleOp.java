package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Sriram on 10/29/2017.
 */

/*
    THINGS THAT NEED TO BE ADDRESSED IN THIS PROGRAM:
    1) Which motors do we need to reverse?
    2) Do the drivers like the button configurations and the whole modes idea?
    3) The arm doesn't move 360 degrees right? Only up and down?
    4) Does the subtraction for the motor powers work?
 */

/*
    NOTES ABOUT THIS PROGRAM IN GENERAL:
    1) All the buttons and what they do are on Google Drive under the Software folder
    2) There are two "modes" or states the robot could be in:
           State 1 is where the robot as a whole moves
           State 2 is where the arm and the claw are the only ones that can move
           This (hopefully) helps the drivers be more precise and accurate when they pick up things
 */

@TeleOp(name = "TeamTeleOp", group = "Team Code") //random group name, can change if necessary

public class TeamTeleOp extends LinearOpMode
{
    private DcMotor frontLeft, frontRight, backLeft, backRight, armLeft, armRight, clawWristMotor;
    private Servo clawServo;
    private double clawPosition; //range: 0 to 1 (represents 0 to 1 pi radians)
    private boolean mode2IsActive; //if true, mode 1 is active. If false, mode 2 is active.

    @Override
    public void runOpMode() throws InterruptedException
    {
        mode2IsActive = false;
        clawPosition = 0.0;

        //runs only once: when robot is initialized
        initHardware();

        //after start is pressed, continue to the while loop
        waitForStart();

        while(opModeIsActive())
        {
            if(gamepad1.back)
                mode2IsActive = !mode2IsActive;  //assigns the opposite state to mode2IsActive

            if(mode2IsActive)
                moveArm();              //you only move the claw in mode 2
            else
                moveRobot();            // you only move the robot in mode 1, when mode2IsActive is false
        }

        //Serves same function as IterativeOpMode stop():
        //does anything that needs to be done after opMode is done
        finish();
    }

    private void initHardware()
    {
       initMotors();
       initServo();
    }

    //only used in mode 1
    private void moveRobot()
    {
        //left joystick controls the power going to all 4 wheels (move up and down)
        //right joystick controls the direction the robot goes (move left and right)
        float wheelMotorsPower = -gamepad1.left_stick_y;
        float turn = gamepad1.right_stick_x;

        //makes sure we don't burn out the motors from too much/little power going to them
        //we subtract power from one side of the robot in order to turn.
        double leftMotorPower = restrictValue(wheelMotorsPower-turn);
        double rightMotorPower = restrictValue(wheelMotorsPower+turn);

        //applying power to the motors
        backLeft.setPower(leftMotorPower);
        frontLeft.setPower(leftMotorPower);
        backRight.setPower(rightMotorPower);
        frontRight.setPower(rightMotorPower);
    }

    //only used in mode 2
    private void moveArm()
    {
        moveArmUpAndDown();
        moveClawUpAndDown();
        openAndCloseClaw();
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

        //tentatively reversing left wheel motors because idk which motors are reversed
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
        clawServo = hardwareMap.servo.get("sClaw");
    }

    //only used in mode 2
    private void moveArmUpAndDown()
    {
        //Taking y-values from the left stick and restricting values
        double armMotorsPower = restrictValue(gamepad1.left_stick_y);

        armRight.setPower(armMotorsPower);
        armLeft.setPower(armMotorsPower);
    }

    //only used in mode 2
    private void moveClawUpAndDown()
    {
        //Taking y-values from the right stick and restricting values
        double clawMotorPower = restrictValue(gamepad1.right_stick_y);

        clawWristMotor.setPower(clawMotorPower);
    }

    //only used in mode 2
    private void openAndCloseClaw()
    {
        float closeClawValue = gamepad1.left_trigger;
        float openClawValue = gamepad1.right_trigger;

        clawPosition = Range.clip(openClawValue - closeClawValue, -1, 1);
        clawServo.setPosition(clawPosition);
    }

    private double restrictValue(float decimal)
    {
        return scaleInput(Range.clip(decimal, -1, 1));
    }

    public void finish()
    {
        clawServo.setPosition(0.5); //just for aesthetics? Robert put it here, so I won't touch it.
        //I also cannot think of anything else to put here, so yeah.
    }


    //used for motor values, to make sure it doesn't go over or under range
    private double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // get value from the array.
        double dScale = 0.0;

        //if input was negative, make scaleArray[index] negative. Otherwise, let it stay positive.
        dScale = dVal < 0 ? -scaleArray[index] : scaleArray[index];

        // return scaled value.
        return dScale;
    }


}
