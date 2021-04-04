import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

//d:/example_Java/xzzx/ff.txt

public class Rectangle {
    private int maxPoint;
    private ArrayList<Integer> coordinates;
    private int[] dividers = new int[]{2, 3, 5, 7, 11};
    private int deltaFirst;
    private int deltaSecond;
    private String filePath;
    private ArrayList<String> coordinateRectangles;
    private static final String TITLE = "<!DOCTYPE html>\n<html>\n<head>\n" +
            "\t<title>rectangles</title>\n</head>\n<table cellspacing=\"0\">\n<tbody>\n";
    private static final String EPILOGUE = "</tbody>\n</table>\n</html>\n";
    private static final String EMPTY_CELL = "<td style=\"height: 10px; width: 10px;\" ></td>\n";
    private static final String CELL = "<td style=\"height: 10px; width: 10px;\" bgcolor=\"#FFC000\"></td>\n";
    private static final String START_STR = "<tr>\n";
    private static final String END_STR = "</tr>\n";


    public static void main(String[] args) {
        Rectangle rectangle = new Rectangle();
        if (args.length > 0) {
            File file = new File(args[0]);
            rectangle.filePath = file.getParent();

            rectangle.readFile(args[0]);
            rectangle.findDelta();
            int bestDivide = rectangle.findBestDivide();
            int[][] coordinateList = rectangle.getCoordinateList(bestDivide);
            String[][] list = rectangle.createMatrixPositionRectangles(coordinateList);
            rectangle.writeHtml(list);

            System.out.println("File was successful created in " + rectangle.filePath);

        } else {
            System.out.println("Please provide the file path");
        }
    }

    private void readFile(String filePath) {
        coordinates = new ArrayList<Integer>();
        coordinateRectangles = new ArrayList<String>();
        maxPoint = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.ready()) {
                String str = reader.readLine();

                coordinateRectangles.add(str.trim());

                String[] strCoordinates = str.split(",");

                for (String s : strCoordinates) {
                    int point = Integer.parseInt(s.trim());
                    if (maxPoint < point) {
                        maxPoint = point;
                    }
                    coordinates.add(point);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // отсортируем все координаты
    // найдем все дельты между соседними координатами
    // выбереме из всех дельт две наименьшие
    private void findDelta() {
        Collections.sort(coordinates);
        ArrayList<Integer> deltaList = new ArrayList<>();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            int delta = coordinates.get(i + 1) - coordinates.get(i);
            if (delta > 0) {
                deltaList.add(delta);
            }
        }

        Collections.sort(deltaList);

        deltaFirst = deltaList.get(0);
        deltaSecond = deltaList.get(1);
    }

    // найдем НОД
    private int findGcd(int a, int b) {
        while (b != 0) {
            int tmp = a % b;
            a = b;
            b = tmp;
        }
        return a;
    }

    // найдем наилучший делитель
    private int findBestDivide() {
        int deltaDivide = findGcd(deltaFirst, deltaSecond);
        deltaFirst = deltaFirst / deltaDivide;
        deltaSecond = deltaSecond / deltaDivide;
        maxPoint = maxPoint / deltaDivide;

        int bestDivide = deltaDivide;

        while (maxPoint > 200) {
            int divide = findDivideMinRemainder();
            deltaFirst = deltaFirst / divide;
            deltaSecond = deltaSecond / divide;
            maxPoint = maxPoint / divide;
            bestDivide = bestDivide * divide;
        }

        return bestDivide;
    }

    // найдем такой делитель остатки от деления на который у двух дельт будет наименьшим
    private int findDivideMinRemainder() {
        int minRemainder = 99999;
        int divideResult = 0;
        for (int divide : dividers) {
            int reminderFirst = deltaFirst % divide;
            int reminderSecond = deltaSecond % divide;

            if (reminderFirst > reminderSecond) {
                if (minRemainder > reminderFirst) {
                    minRemainder = reminderFirst;
                    divideResult = divide;
                }
            } else {
                if (minRemainder > reminderSecond) {
                    minRemainder = reminderSecond;
                    divideResult = divide;
                }
            }
        }
        return divideResult;
    }

    // отсортируем кооринаты прямоугольников по возрастанию начальных координат Y и X
    // поделим каждую координату на найденный делитель
    private int[][] getCoordinateList(int bestDivide) {
        int[][] coordinateList = new int[coordinateRectangles.size()][4];

        Collections.sort(coordinateRectangles);

        for (int i = 0; i < coordinateRectangles.size(); i++) {
            String[] coordinates = coordinateRectangles.get(i).split(",");
            coordinateList[i][0] = Integer.parseInt(coordinates[0].trim()) / bestDivide;
            coordinateList[i][1] = Integer.parseInt(coordinates[1].trim()) / bestDivide;
            coordinateList[i][2] = Integer.parseInt(coordinates[2].trim()) / bestDivide;
            coordinateList[i][3] = Integer.parseInt(coordinates[3].trim()) / bestDivide;
        }

        return coordinateList;
    }

    // создадим двумерный массив
    // пройдемся по всем прямоугольникам
    // в каждую ячейку принадлежащую прямоугольнку добавим в массив
    private String[][] createMatrixPositionRectangles(int[][] coordinateList) {
        String[][] list = new String[maxPoint][];
        for (int numberRectangle = 0; numberRectangle < coordinateList.length; numberRectangle++) {
            for (int i = coordinateList[numberRectangle][0]; i < coordinateList[numberRectangle][2]; i++) {
                for (int j = coordinateList[numberRectangle][1]; j < coordinateList[numberRectangle][3]; j++) {

                    if (list[i] == null) {
                        list[i] = new String[maxPoint];
                    }

                    list[i][j] = CELL;
                }
            }
        }

        return list;
    }


    //создадим и запишем файл HTML
    private void writeHtml(String[][] list) {
        try (FileWriter writer = new FileWriter(filePath + "/Rectangle.html")) {
            writer.write(TITLE);

            for (int i = 0; i < list.length; i++) {
                writer.write(START_STR);
                if (list[i] == null) {
                    writer.write(EMPTY_CELL);
                } else {
                    for (int j = 0; j < list[i].length; j++) {
                        if (list[i][j] == null) {
                            writer.write(EMPTY_CELL);
                        } else {
                            writer.write((String) list[i][j]);
                        }

                    }
                }
                writer.write(END_STR);
            }

            writer.write(EPILOGUE);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
