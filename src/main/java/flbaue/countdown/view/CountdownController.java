/*
 * Copyright 2015 Florian Bauer, florian.bauer@posteo.de
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package flbaue.countdown.view;

import flbaue.countdown.Countdown;
import flbaue.countdown.model.Time;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * Created by Florian Bauer on 17.01.15.
 */
public class CountdownController implements ChangeListener<Number> {


    private static final String RED = "#F60101";
    private static final String BLACK = "#000000";
    private Time timeRemaining;
    private Countdown countdownApp;

    @FXML
    private Button plus1;

    @FXML
    private Button plus10;

    @FXML
    private Button plus60;

    @FXML
    private Button start;

    @FXML
    private Button stop;

    @FXML
    private Label time;
    private Thread audioThread;


    public CountdownController() {
        this.timeRemaining = new Time(this);
    }

    @FXML
    private void handleplus1() {
        timeRemaining.addMinutes(1);
    }

    @FXML
    private void handleplus10() {
        timeRemaining.addMinutes(10);
    }

    @FXML
    private void handleplus60() {
        timeRemaining.addMinutes(60);
    }

    @FXML
    private void handleStart() {
        countdownApp.startCountdown(timeRemaining);
    }

    @FXML
    private void handleStop() {
        countdownApp.stopCountdown();
        stopAudio();
    }

    @FXML
    private void handleReset() {
        countdownApp.stopCountdown();
        timeRemaining.reset();
        stopAudio();
    }

    @FXML
    private void handleSub1() {
        timeRemaining.subMinutes(1);
    }

    @FXML
    private void handleSub5() {
        timeRemaining.subMinutes(5);
    }


    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        time.setText(timeRemaining.toString());

        if (newValue.intValue() <= 0) {
            time.setTextFill(Paint.valueOf(RED));
            audioThread = new Thread(() -> {
                Clip clip = null;
                AudioInputStream ais = null;
                try {
                    clip = AudioSystem.getClip();
                    ais = AudioSystem.getAudioInputStream(Countdown.class.getResource("/sound/ring.wav"));
                    clip.open(ais);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    clip.start();
                    while (!Thread.interrupted()) {
                        Thread.sleep(500);
                    }
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    if (clip != null) {
                        clip.stop();
                        clip.close();
                    }
                    if (ais != null) {
                        try {
                            ais.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            audioThread.start();

        } else {
            time.setTextFill(Paint.valueOf(BLACK));
        }
    }

    public void stop() {
        stopAudio();
    }

    private void stopAudio() {
        if (audioThread != null) {
            audioThread.interrupt();
            audioThread = null;
        }
    }

    public void setCountdownApp(Countdown countdownApp) {
        this.countdownApp = countdownApp;
    }
}
