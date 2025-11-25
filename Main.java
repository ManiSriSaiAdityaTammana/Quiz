import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main{

    //--------------------------------------------------------------
    public static int checkAnswer(String userAnswer, String correctAnswer) {
        if (userAnswer != null && userAnswer.equals(correctAnswer)) {
            System.out.println("Answer is correct!");
            return 1;
        } else {
            System.out.println("Your answer is wrong! The correct answer is " + correctAnswer + ".");
            return 0;
        }
    }


    public static int updateScore(int score, int result) {
        if(result == 1)
            return score + 1;
        else
            return score;
    }
    //--------------------------------------------------------------

    public static ArrayList<String[]> getJavaQuestions() {
        ArrayList<String[]> questions = new ArrayList<>();

        questions.add(createQuestion(
                "Which keyword is used to create a subclass in Java?",
                "A) implements", "B) extends", "C) inherits", "D) override", "B"
        ));
        questions.add(createQuestion(
                "What is the output of: System.out.println(10 + 20 + \"Java\")?",
                "A) 30Java", "B) Java1020", "C) Java30", "D) 1020Java", "A"
        ));
        questions.add(createQuestion(
                "Which of these is a valid way to create an object in Java?",
                "A) MyClass obj = new MyClass();", "B) obj = new MyClass();",
                "C) class obj = MyClass();", "D) new MyClass obj();", "A"
        ));
        questions.add(createQuestion(
                "What is the output of: System.out.println(10 > 5 ? 'Yes' : 'No');",
                "A) Yes", "B) No", "C) true", "D) false", "A"
        ));
        questions.add(createQuestion(
                "What will be the output of: System.out.println(\"Java\".charAt(2));",
                "A) a", "B) v", "C) J", "D) Exception", "B"
        ));

        return questions;
    }

    public static String[] createQuestion(String text, String optA, String optB, String optC, String optD, String correctAns) {
        String[] question = new String[6];
        question[0] = text;
        question[1] = optA;
        question[2] = optB;
        question[3] = optC;
        question[4] = optD;
        question[5] = correctAns;
        return question;
    }

    public static void countdownTimer() {
        System.out.println("\nYou have 10 seconds to think...");
        try {
            for (int i = 10; i > 0; i--) {
                System.out.print("\rTime left: " + i + " seconds ");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Timer interrupted.");
        }
        System.out.println("\n");
    }

    public static void displayQuestion(String[] question, int questionNumber) {
        System.out.println("\n====================");
        System.out.println("Question " + questionNumber + ": " + question[0]);
        System.out.println(question[1]);
        System.out.println(question[2]);
        System.out.println(question[3]);
        System.out.println(question[4]);
    }

    public static String getUserAnswer(Scanner sc) {
        System.out.print("Now enter your answer (A, B, C, D): ");
        String answer = sc.nextLine().trim().toUpperCase();

        if (answer.equals("A") || answer.equals("B") || answer.equals("C") || answer.equals("D")) {
            return answer;
        }
        return null;
    }

    public static void playQuiz() {
        System.out.println("Welcome to Java Quiz!");
        ArrayList<String[]> questions = getJavaQuestions();
        Collections.shuffle(questions);
        Scanner sc = new Scanner(System.in);
        int score = 0;
        int question_number = 1;

        for (String[] question : questions) {
            displayQuestion(question, question_number);
            countdownTimer();
            String userAnswer = getUserAnswer(sc);
            int result = checkAnswer(userAnswer, question[5]);
            score = updateScore(score, result);
            question_number++;
        }

        System.out.println("\nYour final score is: " + score + " / " + questions.size());
        System.out.println("Thanks for playing!");
    }

    public static void main(String[] args) {
        playQuiz();
    }
}