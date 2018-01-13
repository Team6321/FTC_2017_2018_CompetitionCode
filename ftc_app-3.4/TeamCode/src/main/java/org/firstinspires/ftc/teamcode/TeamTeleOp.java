package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/*
 * Created by Sriram on 10/29/2017.
 */

/*
    NOTES ABOUT THIS PROGRAM:
    1) All the buttons and what they do are on Google Drive under the Software folder
 */

@TeleOp(name = "TeamTeleOp", group = "Maybe") //random group name, can change if necessary

public class TeamTeleOp extends LinearOpMode
{
    private DcMotor frontLeft, frontRight, backLeft, backRight, armLeft, armRight, clawWristMotor;
    private Servo clawServo;
    private double clawPosition; //range: 0 to 1 (represents 0 to 1 pi radians)

    @Override
    public void runOpMode() throws InterruptedException
    {
        clawPosition = 0.0;

        //runs only once: when robot is initialized
        initHardware();

        //after start is pressed, continue to the while loop
        waitForStart();

        while(opModeIsActive())
        {
             moveArm();
             moveRobot();
        }

        //Serves same function as IterativeOpMode stop():
        //does anything that needs to be done after opMode is done
        finish();
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

    private void moveRobot()
    {
        //left joystick controls the power going to all 4 wheels (move up and down)
        //right joystick controls the direction the robot goes (move left and right)
        float wheelMotorsPower = -gamepad1.left_stick_y;
        float turn = gamepad1.right_stick_x;

        //makes sure we don't burn out the motors from too much/little power going to them
        //we subtract power from one side of the robot in order to turn.
        double leftMotorPower = restrictPower(wheelMotorsPower-turn);
        double rightMotorPower = restrictPower(wheelMotorsPower+turn);

        //applying power to the motors
        backLeft.setPower(leftMotorPower);
        frontLeft.setPower(leftMotorPower);
        backRight.setPower(rightMotorPower);
        frontRight.setPower(rightMotorPower);
    }

    private void moveArm()
    {
        moveArmUpAndDown();
        moveClawUpAndDown();
        openAndCloseClaw();
    }

    private void moveArmUpAndDown()
    {
        //Taking y-values from the left stick and restricting values
        double armMotorsPower = restrictPower(gamepad2.left_stick_y);

        //if the motors don't recieve any power, give them 10% power
        armMotorsPower = armMotorsPower == 0.0 ? 0.1: armMotorsPower;

        //scales power so that when arm goes up it goes faster than when it comes down
        if(armMotorsPower > 0) {armMotorsPower /= 2;}
        else if(armMotorsPower < 0) {armMotorsPower /= 4;}

        //Divide by 2 to not make the power too much
        armRight.setPower(armMotorsPower);
        armLeft.setPower(armMotorsPower);
    }

    private void moveClawUpAndDown()
    {
        //Taking y-values from the right stick and restricting values
        double clawMotorPower = restrictPower(gamepad2.right_stick_y);

        //Divide by 2 to not make the power too much
        clawWristMotor.setPower(clawMotorPower/2.0);
    }

    private void openAndCloseClaw()
    {
        float closeClawValue = gamepad2.left_trigger;
        float openClawValue = gamepad2.right_trigger;

        clawPosition = Range.clip(openClawValue - closeClawValue, -1, 1);
        clawServo.setPosition(clawPosition);
    }

    private double restrictPower(float power) { return scaleInput(Range.clip(power, -1, 1)); }

    public void finish()
    {
        clawServo.setPosition(0.5); //just for aesthetics? Robert put it here, so I won't touch it.
                                    //I also cannot think of anything else to put here.
    }

    //used for motor values, to make sure it doesn't go over or under range
    private double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.10, 0.15, 0.20, 0.25, 0.30, 0.35,
                0.40, 0.45, 0.50, 0.55, 0.60, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90, 0.95, 1.00, 1.00};

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 21.0);

        // index should be positive.
        index = Math.abs(index);

        //if input was negative, make scaleArray[index] negative. Otherwise, let it stay positive.
        double finalPower = dVal < 0 ? -scaleArray[index] : scaleArray[index];

        // return scaled value.
        return finalPower;
    }

}
