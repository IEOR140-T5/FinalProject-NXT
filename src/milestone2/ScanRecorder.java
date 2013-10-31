package milestone2;

import lejos.nxt.*;
import lejos.util.Datalogger;
import lejos.util.Delay;
/**
 * records the results of a 180 degree scan (angle and light intensity
 * to a dataLogger and plays it back over a USB port
 * @author owner.GLASSEY
 */

public class ScanRecorder
{


   public ScanRecorder(NXTRegulatedMotor theMotor, LightSensor eye, UltrasonicSensor ussensor)
   {
      motor = theMotor;
      _ussensor = ussensor;
      _eye = eye;
      _eye.setFloodlight(false);
   }

   /**
    * returns the angle at which the maximum light intensity was found
    * @return 
    */
   public int getTargetBearing()
   {
      return _targetBearing;
   }
/**
    * returns the maximum light intensity found during the scan
    * @return  light intensity
    */
   public int getLight()
   {
      return _maxLight;
   }
/**
    * returns the angle in which the light sensor is pointing
    * @return the angle
    */
   public int getHeadAngle()
   {
      return motor.getTachoCount();
   }
 /**
    * sets the motor sped in deg/sec
    * @param speed 
    */
   public void setSpeed(int speed)
   {
      motor.setSpeed(speed);
   }
/**
    * scan from current head angle to limit angle and write the angle and 
    * light sensor value to the datalog
    * @param limitAngle 
    */
   public void scanTo(int limitAngle)
   {
      int oldAngle = motor.getTachoCount();
      motor.rotateTo(limitAngle, true);
      int light = 0;
      while (motor.isMoving())
      {
         short angle = (short) motor.getTachoCount();
         if (angle != oldAngle)
         {
            light = _eye.getNormalizedLightValue();
            oldAngle = angle;
            dl.writeLog(angle);
            dl.writeLog(light);
         }

         Thread.yield();
      }
   }
   
   /**
    * scan from current head angle to limit angle and write the angle and 
    * light sensor value to the datalog
    */
   public void scanWall()
   {
      int distance1 = 0;
      int distance2 = 0;
      
      LCD.clear();
      motor.rotateTo(90, true);
      
      while (motor.isMoving())
      {
         
          distance1 = _ussensor.getDistance();
          LCD.drawInt(distance1, 0, 1);

         Thread.yield();
      }
      
      
      Delay.msDelay(500);
      
      motor.rotateTo(-90, true);
      
      while (motor.isMoving())
      {
         
    	  distance2 =  _ussensor.getDistance();
          LCD.drawInt(distance2, 0, 2);

         Thread.yield();
      }
      
      motor.rotateTo(0);
      Button.waitForAnyPress();
   }
   
/**
    * rotate the scanner head to the angle
    * @param angle
    * @param instantReturn if true, the method is non-blocking
    */
   public void rotateTo(int angle, boolean instantReturn)
   { System.out.println(" rotatet "+angle);
      motor.rotateTo(angle, instantReturn);
   }
/**
    * rotates the scaner head to angle;  returns when rotation is complete
    * @param angle 
    */
   public void rotateTo(int angle)
   {
      rotateTo(angle, false);
   }
/**
    * scan between -90 and 90 degrees
    * @param args 
    */
   public static void main(String[] args)
   {
     System.out.println(" go ");
      ScanRecorder s = new ScanRecorder(Motor.B, new LightSensor(SensorPort.S2), new UltrasonicSensor(SensorPort.S3));
      Motor.B.setSpeed(60);
      Button.waitForAnyPress();
      System.out.println(" pressed ");
      
      /* scan angle 
      int angle = 100;
      s.rotateTo(-angle);
      s.scanTo(angle);
      s.scanTo(-angle);
      s.rotateTo(0);
      s.dl.transmit();  // use usb
      */
      
      /* scan wall */
      s.scanWall();
   }
   /******* instance variabled ***************/
   NXTRegulatedMotor motor;
   LightSensor _eye;
   UltrasonicSensor _ussensor;
   int _targetBearing;
   int _maxLight;
   boolean _found = false;
   Datalogger dl = new Datalogger();
}
