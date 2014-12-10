package com.tuohy.worldwindvr.input;

import de.hardcode.jxinput.Axis;
import de.hardcode.jxinput.event.JXInputAxisEvent;
import de.hardcode.jxinput.event.JXInputAxisEventListener;
import de.hardcode.jxinput.event.JXInputEventManager;

public class AxisListener implements JXInputAxisEventListener {

        public AxisListener( Axis axis )
        {
                JXInputEventManager.addListener( this, axis, 0.1 );
        }

        
        @Override
        public void changed(JXInputAxisEvent arg0) {
                System.out.println( "Axis " + arg0.getAxis().getName() + " changed : value=" + arg0.getAxis().getValue() + ", event causing delta=" + arg0.getDelta() );

        }

}