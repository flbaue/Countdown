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

package flbaue.countdown;

import flbaue.countdown.model.Time;
import flbaue.countdown.view.CountdownController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Florian Bauer on 17.01.15.
 */
public class Countdown extends Application {

    private Thread countdownThread;
    private Stage primaryStage;
    private CountdownController countdownController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        countdownController.stop();
    }

    public void startCountdown(final Time timeRemaining) {
        countdownThread = new Thread(() -> {
            while (timeRemaining.hasTimeLeft() && !Thread.interrupted()) {
                Platform.runLater(() -> timeRemaining.subSeconds(1));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        countdownThread.start();
    }

    public void stopCountdown() {
        if (countdownThread != null) {
            countdownThread.interrupt();
            countdownThread = null;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Countdown");

        initLayout();
    }

    private void initLayout() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Countdown.class.getResource("/view/countdown.fxml"));
        Pane layout;
        try {
            layout = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        countdownController = loader.getController();
        countdownController.setCountdownApp(this);
    }
}
