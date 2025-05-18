package com.example.hello;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private Label attempsUser;
    @FXML
    private TextField inputAttemp;
    @FXML
    private Button checkAttempUser;
    @FXML
    private Label userInputNumber;
    @FXML
    private Label currentBulls;
    @FXML
    private Label currentCows;
    @FXML
    private Label gameRules;
    @FXML
    private Tab help;
    private List<Integer> secretNumber;//загаданное число комьпютером
    private int countAttempts = 0;//количество попыток игрока
    private int[] result;//массив значений быков и коров при текущей попытке
    private List<Integer> user = new ArrayList<>();//попытка введеного числа пользователем
    //проверка введенного числа пользователем
    @FXML
    protected void onButtonClickCheck() {
        String text = inputAttemp.getText();
        //валидность введоного числа пользователем
        if (text.length() != 4 || !text.matches("\\d+") || !hasDups(text)) {
            //System.out.println("Ошибка! Введите четыре цифры без пробелов.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setContentText("Введите четыре цифры без пробелов.");
            alert.show();
            inputAttemp.setText("");
        }
        //проверка введенного числа
        else{
            for (int i = 0; i < 4; i++) {
                //посимвольное преобразование введеного числа в лист user
                user.add(Integer.parseInt(String.valueOf(text.charAt(i))));
            }
            result = checkBullsAndCows(secretNumber, user);

            //System.out.println("Ваша текущая попытка: " + user.toString());
            //System.out.println("Результат: " + result[0] + " бык(ов) и " + result[1] + " коров(а).");

            //запись текущей попытки
            attempsUser.setText(attempsUser.getText() + inputAttemp.getText() + "\n");
            currentBulls.setText(currentBulls.getText() + result[0] + "\n");
            currentCows.setText(currentCows.getText() + result[1] + "\n");

            //очистка поля ввода
            inputAttemp.setText("");

            countAttempts++;

            if (result[0] == 4) {
                String listString = secretNumber.stream().map(Object::toString)
                        .collect(Collectors.joining(""));
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION, "Поздравляем! Хотите начать заново ?", ButtonType.YES, ButtonType.NO);
                alert2.setTitle("Поздравляем! Хотите начать заново ?");
                alert2.setContentText("Вы угадали число: " + listString + "\n" +
                        "Ваше количество попыток: " + countAttempts);
                alert2.showAndWait();

                if (alert2.getResult() == ButtonType.YES) {
                    onButtonClickStart();
                }
                if (alert2.getResult() == ButtonType.NO){
                    Platform.exit();
                }
            }
            if (countAttempts == 13){
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION, "Вы проиграли! Хотите начать заново ?", ButtonType.YES, ButtonType.NO);
                alert2.setTitle("Вы проиграли! Хотите начать заново ?");
                alert2.setContentText("Попытки закончились!");
                alert2.showAndWait();

                if (alert2.getResult() == ButtonType.YES) {
                    onButtonClickStart();
                }
                if (alert2.getResult() == ButtonType.NO){
                    Platform.exit();
                }
            }
            user.clear();
        }
    }
    //запуск новой игры
    @FXML
    protected void onButtonClickStart() {
        welcomeText.setText("Компьютер\nзагадал число!");
        //генерация загаданного числа
        secretNumber = generateSecretNumber();
        //обнуление количества попыток
        countAttempts = 0;
        //очистка поля ввода
        inputAttemp.setText("");
        //очистка попыток
        attempsUser.setText("");
        currentBulls.setText("");
        currentCows.setText("");
        //активация кноки и поля ввода, выделение черным цветом текст
        inputAttemp.setDisable(false);
        checkAttempUser.setDisable(false);
        userInputNumber.setTextFill(Color.BLACK);

    }
    //правила игры
    @FXML
    protected void onSelectChangeGR() {
        gameRules.setText("Компьютер загадает 4х-значное\n" +
                          "число с неповторяющимися\n" +
                          "цифрами в диапазоне от 0 до 9\n" +
                          "включительно. Игрок должен\n" +
                          "угадать загаданное число.\n" +
                          "Чтобы проверить свою \n" +
                          "догадку, игрок вводит в поле \n" +
                          "число. Компьютер сообщает,\n" +
                          "сколько цифр угадано без\n" +
                          "совпадения с их позициями в\n" +
                          "загаданном числе (количество\n" +
                          "\"коров\") и сколько угадано \n" +
                          "вплоть до позиции в загаданном\n" +
                          "числе (количество \"быков\").\n" +
                          "Количество попыток - 13.\n" +
                          "Пример:\n" +
                          "Загадано число - 6973\n" +
                          "Попытка игрока - 6539\n" +
                          "Результат - 1 бык и 2 коровы");
    }
    //генерация загаданного числа
    private static List<Integer> generateSecretNumber() {
        //создание списка от 0 до 9
        List<Integer> numbers = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        //пустой список для результата
        List<Integer> secretNumber = new ArrayList<>();
        //генерация 4х уникальных цифр
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(numbers.size());//генерация случайного индекса
            secretNumber.add(numbers.get(index));//добавление цифры в результат
            numbers.remove(index);//удаление использованной цифры
        }
        Collections.shuffle(secretNumber);//перемешивание результата для дополнительной случайности
        //System.out.println("Загаданное число: " + secretNumber);
        return secretNumber;
    }
    //проверка совпадения быков и коров
    private static int[] checkBullsAndCows(List<Integer> secret, List<Integer> user) {
        int bulls = 0;//цифра есть и совпадает с положением
        int cows = 0;//цифра есть в загаданном числе

        for (int i = 0; i < 4; i++) {
            if (user.get(i).equals(secret.get(i))) {
                bulls++;
            } else if (secret.contains(user.get(i))) {
                cows++;
            }
        }
        return new int[]{bulls, cows};
    }
    //проверка на неповторяющиеся символы
    public static boolean hasDups(String s) {
        //разделение строки на массив строк
        String[] ar = s.split("");
        //сортируем массив
        Arrays.sort(ar);
        boolean noDups = true;
        for (int i = 1; i < ar.length && noDups; i++) {
            //если текущий символ равен предыдущему
            if (ar[i].equals(ar[i-1])) {
                noDups = false;
            }
        }
        return noDups;
    }
}