package org.firstinspires.ftc.teamcode.drive.OGCode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

/*
 * This is a simple routine to test translational drive capabilities.
 */
@Config
@Autonomous(group = "Testers")
public class DLSColectareEncoderBabyTest extends LinearOpMode {
    public static double DISTANCE = 60; // in
    public static double Kp = 0.0015;
    public static double Ki = 0.02;
    public static double Kd = 0.00003;
    public static double maxSpeed = 0.75;
    public static double RetractedPosition = -75 , ExtendedPosition = 2020;
    int TargetLift = 0;
    ElapsedTime timerPID = new ElapsedTime();

    @Override

    public void runOpMode() throws InterruptedException {
        List <LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
        ElapsedTime changePositions = new ElapsedTime();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        RobotMap robot = new RobotMap(hardwareMap);
        SimplePIDController hello = new SimplePIDController(Kp,Ki,Kd);
        robot.left4Bar.setPosition(0.4);
        robot.right4Bar.setPosition(0.4);
        waitForStart();

        if (isStopRequested()) return;


        telemetry.update();
        hello.targetValue = RetractedPosition;
        while (!isStopRequested() && opModeIsActive())
        {
            int ColectarePosition = robot.encoderMotorColectare.getCurrentPosition();
            double powerColectare = hello.update(ColectarePosition);
            powerColectare = Math.max(-1,Math.min(powerColectare,1));
            robot.motorColectare.setPower(powerColectare);
            if (changePositions.seconds()>4)
            {
                if (hello.targetValue == RetractedPosition )
                {
                    hello.targetValue = ExtendedPosition;
                }
                else
                {
                    hello.targetValue = RetractedPosition;
                }
                changePositions.reset();
            }

            telemetry.addData("ColectareEncoder", ColectarePosition);
            telemetry.addData("powerColectare", powerColectare);
            telemetry.addData("TargetLift",hello.targetValue);
            telemetry.addData("Error", hello.measuredError(ColectarePosition));
            if (Kp!=hello.p || Kd!=hello.d || Ki!=hello.i || maxSpeed !=hello.maxOutput )
            {
                hello.p = Kp;
                hello.d = Kd;
                hello.i = Ki;
                hello.maxOutput = maxSpeed;
            }

            telemetry.update();
        }
    }
}