import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static List<String> arrayList = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile(" [0-9]+|(подлинник)|(копия)");
        // [0-9]+|(подлинник)|(копия)
        // [0-9]+
        List<Integer> integerList = new LinkedList<>();
        List<String> documentType = new LinkedList<>();
        Map<String, String> linksMap = new HashMap<>();
        int n = 0;
        int count = 0;

        linksMap.put("Лечебное дело", "http://admission.sgmu.ru/sites/default/files/file/2019/List/2019-list-lech-budg.pdf");
        linksMap.put("Педиатрия", "http://admission.sgmu.ru/sites/default/files/file/2019/List/2019-list-ped-budg.pdf");

        System.out.println("Выберете список, который выхотите запарсить (Педиатрия, Лечебное дело) !!! Точно как указано в скобочках !!! :");
        String key = scanner.nextLine();
        String value = linksMap.get(key);
        System.out.println(value);

        PdfReader reader = new PdfReader(value);

        for (int i = 1; i <= reader.getNumberOfPages(); ++i) {

            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            System.out.println(text);

            Matcher matcher = pattern.matcher(text);
            while (matcher.find())
                arrayList.add(text.substring(matcher.start(), matcher.end()));
        }

        // удаляет номер студента
        for (int i = 0; i < arrayList.size(); i += 5)
            check(i);

        showMessage("/////////////// Баллы - тип документа ///////////////");

        for (int i = 0; i < arrayList.size(); i++) {
            System.out.print(arrayList.get(i) + " ");
            count++;
            if (count%5 == 0)
                System.out.println();
        }

        count = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            count++;
            if (count == 5){
                documentType.add(arrayList.get(i));
                count = 0;
                continue;
            }
            n += Integer.valueOf(arrayList.get(i).trim());
            if (count == 4){
                //System.out.println((i/4+1) + " : " + n);
                integerList.add(n);
                n = 0;
            }
        }

        showMessage("/////////////// Результат - тип документа ///////////////");

        for (int i = 0; i < integerList.size(); i++) {
            System.out.println(integerList.get(i) + " : " + documentType.get(i));
        }

        showMessage("/////////////// Отсортированные баллы ///////////////");

        Collections.sort(integerList);
        Collections.reverse(integerList);

        count = 1;
        for (int i : integerList)
            System.out.println(count++ + " : " + i);

        showMessage("/////////////// Число копий и подлинников ///////////////");

        int countOfCopy = 0, countOfOriginal = 0;
        for (String string : documentType) {
            if (string.equals("копия"))
                countOfCopy++;
            else
                countOfOriginal++;
        }
        System.out.println("Копий: " + countOfCopy + ", Оригиналов: " + countOfOriginal);

        showMessage("Если есть предлодения или вопросы, обращайтесь по адрессу электронной почты: andreysaprykin@mail.ru");

        reader.close();
    }

    private static void showMessage(String message) {
        System.out.println();
        System.out.println(message);
        System.out.println();
    }

    private static void check(int i) {
        //У последнего не првоеряем
        if (i+4 >= arrayList.size()) {
            arrayList.remove(i);
            return;
        }

        if (Integer.valueOf(arrayList.get(i+1).trim()) <= 10) { // 0 экзаменов
            for (int j = 0; j < 3; j++)
                arrayList.remove(i);

            check(i);
            return;
        }

        if (Integer.valueOf(arrayList.get(i+2).trim()) <= 10) // П
        {
            for (int j = 0; j < 4; j++)
                arrayList.remove(i);

            check(i);
            return;
        }

        if (Integer.valueOf(arrayList.get(i+3).trim()) <= 10) { // без 1 экзамена
            for (int j = 0; j < 5; j++)
                arrayList.remove(i);

            check(i);
            return;
        }

        arrayList.remove(i);
    }
}
