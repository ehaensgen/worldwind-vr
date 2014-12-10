package com.tuohy.worldwindvr.input;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.ViewInputAttributes;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.view.firstperson.BasicFlyView;

import java.io.File;

import de.hardcode.jxinput.JXInputManager;
import de.hardcode.jxinput.directinput.DirectInputDevice;
import de.hardcode.jxinput.event.JXInputAxisEvent;
import de.hardcode.jxinput.event.JXInputAxisEventListener;
import de.hardcode.jxinput.event.JXInputEventManager;

public class Xbox360Controller{

	//singleton
	public static final Xbox360Controller INSTANCE = new Xbox360Controller();

	DirectInputDevice xbox = null;

	private static long timeOfLastUpdate;

	//	double[] axisVals;

	private Xbox360Controller(){
		System.out.println(System.getProperty("java.library.path"));
		System.load(new File("jxinput.dll").getAbsolutePath());
		JXInputEventManager.setTriggerIntervall( 20 );
		for(int i = 0; i < JXInputManager.getNumberOfDevices(); i++){
			if(JXInputManager.getJXInputDevice(i).getName().equals("Controller (XBOX 360 For Windows)")){
				xbox = new DirectInputDevice(i);
				//				axisVals = new double[xbox.getNumberOfAxes()];
				//				for(int j = 0; j < xbox.getNumberOfAxes(); j++){
				//					final int axisIndex = j;
				//					if(xbox.getAxis(axisIndex) != null){
				//						JXInputEventManager.addListener(new JXInputAxisEventListener(){
				//							@Override
				//							public void changed(JXInputAxisEvent arg0) {
				//								axisVals[axisIndex] = arg0.getAxis().getValue();
				//								if(Math.abs(axisVals[axisIndex]) <= 0.1){
				//									axisVals[axisIndex] = 0;
				//								}
				////								System.out.println("Updated " + axisIndex + " to " + axisVals[axisIndex]);
				//							}
				//
				//						}, xbox.getAxis(j), 0.1);
				//					}
				//				}
				new ButtonListener(xbox.getButton(0));
			}
		}
	}

	public void updateViewPosition(View dcView) {
		if(xbox!=null){
			double forward = -xbox.getAxis(1).getValue();
			double side = -xbox.getAxis(0).getValue();
			if(Math.abs(forward)>0.25 || Math.abs(side)>0.25){
				onHorizontalTranslateRel(dcView,forward,side);
			}
			timeOfLastUpdate = System.currentTimeMillis();
		}
	}

	/**
	 * 
	 * @param view
	 * @param forwardInput
	 * @param sideInput
	 * @param totalForwardInput - between -1 and 1
	 * @param totalSideInput - between -1 and 1
	 * @param deviceAttributes
	 * @param actionAttributes
	 */
	protected void onHorizontalTranslateRel(View view, double forwardInput, double sideInput)
	{
		Angle forwardChange;
		Angle sideChange;

		//TODO: use the trigger to determine speed?
		double cameraTranslationSpeed = 1.0;
		long now = System.currentTimeMillis();
		if(timeOfLastUpdate>0){

			double timeElapsed = now - timeOfLastUpdate;
			double timeUnitsElapsed = timeElapsed/40;
			
			((VRFlyViewInputHandler)view.getViewInputHandler()).stopGoToAnimators();

			forwardChange = Angle.fromDegrees(timeUnitsElapsed * forwardInput * cameraTranslationSpeed * getScaleValueElevation(view));
			sideChange = Angle.fromDegrees(timeUnitsElapsed * sideInput * cameraTranslationSpeed * getScaleValueElevation(view));

			onHorizontalTranslateRel(view, forwardChange, sideChange);
		}
		//		System.out.println("moving at " + System.currentTimeMillis());
	}

	protected void onHorizontalTranslateRel(View view, Angle forwardChange, Angle sideChange)
	{
		if (view == null) // include this test to ensure any derived implementation performs it
		{
			return;
		}

		if (forwardChange.equals(Angle.ZERO) && sideChange.equals(Angle.ZERO))
		{
			return;
		}

		if (view instanceof BasicFlyView)
		{

			Vec4 forward = view.getForwardVector();
			Vec4 up = view.getUpVector();
			Vec4 side = forward.transformBy3(Matrix.fromAxisAngle(Angle.fromDegrees(90), up));

			forward = forward.multiply3(forwardChange.getDegrees());
			side = side.multiply3(sideChange.getDegrees());
			Vec4 eyePoint = view.getEyePoint();
			eyePoint = eyePoint.add3(forward.add3(side));
			Position newPosition = view.getGlobe().computePositionFromPoint(eyePoint);

			view.setEyePosition(newPosition);
			//                this.setEyePosition(this.uiAnimControl, view, newPosition, actionAttribs);
			view.firePropertyChange(AVKey.VIEW, null, view);
		}
	}

	protected double getScaleValueElevation(View view)
	{
		if (view == null)
		{
			return 0.0;
		}

		//max and min elevation?  not sure
		double[] range = new double[]{100.0,1000000.0};

		//the controller sensitivity?
		double sensitivity = 1.0;

		Position eyePos = view.getEyePosition();
		double radius = view.getGlobe().getRadius();
		double surfaceElevation = view.getGlobe().getElevation(eyePos.getLatitude(),
				eyePos.getLongitude());
		double t = getScaleValue(range[0], range[1],
				eyePos.getElevation() - surfaceElevation, 3.0 * radius, true);
		t *= sensitivity;

		return t;
	}    

	protected double getScaleValue(double minValue, double maxValue,
			double value, double range, boolean isExp)
	{
		double t = value / range;
		t = t < 0 ? 0 : (t > 1 ? 1 : t);
		if (isExp)
		{
			t = Math.pow(2.0, t) - 1.0;
		}
		return(minValue * (1.0 - t) + maxValue * t);
	}

	public static void main(String[] args){
		Xbox360Controller instance = Xbox360Controller.INSTANCE;
		while(true){

		}
	}

}