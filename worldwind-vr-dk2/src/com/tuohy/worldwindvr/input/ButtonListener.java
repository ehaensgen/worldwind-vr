package com.tuohy.worldwindvr.input;

import de.hardcode.jxinput.Button;
import de.hardcode.jxinput.event.JXInputButtonEvent;
import de.hardcode.jxinput.event.JXInputButtonEventListener;
import de.hardcode.jxinput.event.JXInputEventManager;

public class ButtonListener implements JXInputButtonEventListener {

        public ButtonListener( Button button )
        {
                JXInputEventManager.addListener( this, button );
        }

        
        @Override
        public void changed(JXInputButtonEvent arg0) {
                System.out.println( "Button " + arg0.getButton().getName() + " changed : state=" + arg0.getButton().getState() );

        }

}