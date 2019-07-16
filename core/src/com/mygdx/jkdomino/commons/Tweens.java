package com.mygdx.jkdomino.commons;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class Tweens {
    public static void Blink(Actor target, float aHigh, float aLow, float duration, int repeat, Runnable onComplete) {
        SequenceAction seq = new SequenceAction();
        seq.addAction(Actions.repeat(repeat,
                Actions.sequence(
                        Actions.alpha(0.3f, duration/repeat),
                        Actions.alpha(1f, duration/repeat)
                )
        ));
        if (onComplete != null)
            seq.addAction(Actions.run(onComplete));

        target.addAction(seq);
    }

    public static void setTimeout(Stage stage, float timeout, Runnable callback) {
        stage.addAction(Actions.sequence(
                Actions.delay(timeout),
                Actions.run(callback)
        ));
    }
    public static <T extends Actor> void action(T target, Action action, Runnable onComplete){
        SequenceAction seq = new SequenceAction();
        seq.addAction(action);
        if (onComplete != null)
            seq.addAction(Actions.run(onComplete));
        target.addAction(seq);
    }

    public static <T extends Actor> void seqActions(T target, Runnable onComplete, Action ...as) {
        SequenceAction seq = new SequenceAction();
        for (Action a : as) {
            seq.addAction(a);
        }

        if (onComplete != null)
            seq.addAction(Actions.run(onComplete));

        target.addAction(seq);
    }

    public static <T extends Actor> void palActions(T target, Runnable onComplete, Action ...as) {
        ParallelAction pal = new ParallelAction();
        SequenceAction seq = new SequenceAction();

        for (Action a : as) {
            pal.addAction(a);
        }

        if (onComplete != null) {
            seq.addAction(pal);
            seq.addAction(Actions.run(onComplete));
            target.addAction(seq);
        }
        else {
            target.addAction(pal);
        }
    }
}
