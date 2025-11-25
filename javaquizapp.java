import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JavaQuizApp.java
 * <p>
 * A simple JavaFX-based multiple-choice quiz application converted from the console quiz.
 * - Displays one question at a time
 * - Shows four option buttons (A/B/C/D)
 * - 10-second countdown per question
 * - Tracks score and shows final result in a dialog
 *
 * NOTE: Requires JavaFX (OpenJFX). Run with Java 11+ and include JavaFX modules on the module path.
 */
public class JavaQuizApp extends Application {

    private static class Question {
        String text;
        String[] opts; // A,B,C,D
        String correct; // "A".."D"

        Question(String text, String a, String b, String c, String d, String correct) {
            this.text = text;
            this.opts = new String[]{a, b, c, d};
            this.correct = correct;
        }
    }

    private final List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;

    // UI
    private Label questionLabel;
    private Button[] optionButtons = new Button[4];
    private Label timerLabel;
    private Label scoreLabel;
    private Timeline timeline;
    private int timeLeft = 10;

    @Override
    public void start(Stage primaryStage) {
        loadQuestions();
        Collections.shuffle(questions);

        primaryStage.setTitle("Java Quiz App");

        questionLabel = new Label();
        questionLabel.setWrapText(true);
        questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox optionsBox = new VBox(10);
        optionsBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < 4; i++) {
            Button b = new Button();
            b.setMaxWidth(Double.MAX_VALUE);
            final int idx = i;
            b.setOnAction(e -> handleAnswer(idx));
            optionButtons[i] = b;
            optionsBox.getChildren().add(b);
        }

        timerLabel = new Label("Time left: 10s");
        scoreLabel = new Label("Score: 0");

        HBox topBar = new HBox(20, timerLabel, scoreLabel);
        topBar.setAlignment(Pos.CENTER);

        Button nextBtn = new Button("Skip / Next");
        nextBtn.setOnAction(e -> nextQuestion(false));

        Button restartBtn = new Button("Restart");
        restartBtn.setOnAction(e -> restartQuiz());

        HBox controlBar = new HBox(10, nextBtn, restartBtn);
        controlBar.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setTop(topBar);
        BorderPane.setAlignment(topBar, Pos.CENTER);
        root.setCenter(new VBox(12, questionLabel, optionsBox));
        root.setBottom(controlBar);

        Scene scene = new Scene(root, 600, 320);
        primaryStage.setScene(scene);
        primaryStage.show();

        showQuestion(currentIndex);
    }

    private void loadQuestions() {
        questions.clear();
        questions.add(new Question(
                "Which keyword is used to create a subclass in Java?",
                "A) implements", "B) extends", "C) inherits", "D) override", "B"
        ));

        questions.add(new Question(
                "What is the output of: System.out.println(10 + 20 + \"Java\")?",
                "A) 30Java", "B) Java1020", "C) Java30", "D) 1020Java", "A"
        ));

        questions.add(new Question(
                "Which of these is a valid way to create an object in Java?",
                "A) MyClass obj = new MyClass();", "B) obj = new MyClass();",
                "C) class obj = MyClass();", "D) new MyClass obj();", "A"
        ));

        questions.add(new Question(
                "What is the output of: System.out.println(10 > 5 ? 'Yes' : 'No');",
                "A) Yes", "B) No", "C) true", "D) false", "A"
        ));

        questions.add(new Question(
                "What will be the output of: System.out.println(\"Java\".charAt(2));",
                "A) a", "B) v", "C) J", "D) Exception", "B"
        ));
    }

    private void showQuestion(int index) {
        if (index >= questions.size()) {
            showResultDialog();
            return;
        }

        Question q = questions.get(index);
        questionLabel.setText((index + 1) + ". " + q.text);
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(q.opts[i]);
            optionButtons[i].setDisable(false);
            optionButtons[i].setStyle("");
        }

        scoreLabel.setText("Score: " + score);

        startTimer();
    }

    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        timeLeft = 10;
        timerLabel.setText("Time left: " + timeLeft + "s");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            timeLeft--;
            timerLabel.setText("Time left: " + timeLeft + "s");
            if (timeLeft <= 0) {
                timeline.stop();
                // Disable options and move to next
                for (Button b : optionButtons) b.setDisable(true);
                // show correct answer briefly
                showCorrectThenNext();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void handleAnswer(int selectedIdx) {
        if (timeline != null) timeline.stop();
        Question q = questions.get(currentIndex);
        String selected = switch (selectedIdx) {
            case 0 -> "A";
            case 1 -> "B";
            case 2 -> "C";
            default -> "D";
        };

        boolean correct = selected.equals(q.correct);
        if (correct) {
            score++;
            optionButtons[selectedIdx].setStyle("-fx-border-color: green; -fx-border-width: 2px;");
        } else {
            optionButtons[selectedIdx].setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            // highlight correct
            int correctIdx = q.correct.charAt(0) - 'A';
            optionButtons[correctIdx].setStyle("-fx-border-color: green; -fx-border-width: 2px;");
        }

        for (Button b : optionButtons) b.setDisable(true);

        // small delay then next question
        Timeline pause = new Timeline(new KeyFrame(Duration.seconds(1.2), e -> nextQuestion(true)));
        pause.play();
    }

    private void showCorrectThenNext() {
        Question q = questions.get(currentIndex);
        int correctIdx = q.correct.charAt(0) - 'A';
        optionButtons[correctIdx].setStyle("-fx-border-color: green; -fx-border-width: 2px;");
        Timeline pause = new Timeline(new KeyFrame(Duration.seconds(1.2), e -> nextQuestion(true)));
        pause.play();
    }

    private void nextQuestion(boolean answered) {
        currentIndex++;
        if (currentIndex < questions.size()) {
            showQuestion(currentIndex);
        } else {
            showResultDialog();
        }
    }

    private void restartQuiz() {
        Collections.shuffle(questions);
        currentIndex = 0;
        score = 0;
        showQuestion(currentIndex);
    }

    private void showResultDialog() {
        if (timeline != null) timeline.stop();
        Platform.runLater(() -> {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Quiz Result");

            Label msg = new Label("Your final score is: " + score + " / " + questions.size());
            msg.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Button ok = new Button("OK");
            ok.setOnAction(e -> dialog.close());

            Button restart = new Button("Play Again");
            restart.setOnAction(e -> {
                dialog.close();
                restartQuiz();
            });

            HBox buttons = new HBox(10, ok, restart);
            buttons.setAlignment(Pos.CENTER);

            VBox v = new VBox(12, msg, buttons);
            v.setAlignment(Pos.CENTER);
            v.setPadding(new Insets(12));

            Scene s = new Scene(v, 300, 120);
            dialog.setScene(s);
            dialog.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
