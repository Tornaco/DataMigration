package lib;

import android.os.Message;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.lib.State;
import org.newstand.lib.StateMachine;

import static lib.StateMachineTest.HelloWorld.makeHelloWorld;

/**
 * Created by Nick@NewStand.org on 2017/3/15 14:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class StateMachineTest {

    @Test
    public void testHelloWorld() {
        HelloWorld hw = makeHelloWorld();

        hw.sendMessage(hw.obtainMessage());
    }

    static class HelloWorld extends StateMachine {

        State1 mState1 = new State1();
        State1 mState2 = new State1();

        protected HelloWorld(String name) {
            super(name);

            addState(mState1);
            addState(mState2);

            setInitialState(mState1);

            setDbg(true);
        }

        public static HelloWorld makeHelloWorld() {
            HelloWorld hw = new HelloWorld("hwm");
            hw.start();
            return hw;
        }

        class State1 extends State {

            @Override
            public void enter() {
                super.enter();
                log("State1 Enter");
            }

            @Override
            public void exit() {
                super.exit();
                log("State1 exit");
            }

            @Override
            public boolean processMessage(Message msg) {
                log("State1 Hello World");
                transitionTo(mState2);
                return HANDLED;
            }
        }

        class State2 extends State {

            @Override
            public void enter() {
                super.enter();
                log("State2 enter");
            }

            @Override
            public void exit() {
                super.exit();
                log("State2 exit");
            }

            @Override
            public boolean processMessage(Message msg) {
                log("State2 Hello World");
                return HANDLED;
            }
        }
    }


}
