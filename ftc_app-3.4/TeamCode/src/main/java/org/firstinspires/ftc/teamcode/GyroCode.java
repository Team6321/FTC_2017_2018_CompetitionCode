package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by killi on 12/6/2017.
 */

public class GyroCode extends LinearOpMode
{
    @Override
    public void runOpMode() throws InterruptedException
    {
        GyroSensor gyro = hardwareMap.gyroSensor.get("gyro");
        double currentHeading = gyro.getHeading();
        currentHeading = gyro.getHeading();
        if (currentHeading > 180){
            currentHeading = currentHeading -360
        }

    } }
