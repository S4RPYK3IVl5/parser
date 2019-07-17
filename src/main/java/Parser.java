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

        Pattern pattern = Pattern.compile(" [0-9]+");
        List<Integer> integerList = new LinkedList<>();
        int n = 1;
        int count = 0;

        PdfReader reader = new PdfReader("http://admission.sgmu.ru/sites/default/files/file/2019/List/2019-list-ped-budg.pdf");

        //Лечебное дело - http://admission.sgmu.ru/sites/default/files/file/2019/List/2019-list-lech-budg.pdf
        //Педиатрия - http://admission.sgmu.ru/sites/default/files/file/2019/List/2019-list-ped-budg.pdf

        for (int i = 1; i <= reader.getNumberOfPages(); ++i) {

            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            System.out.println(text);

            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                arrayList.add(text.substring(matcher.start(), matcher.end()));
            }
        }

        // удаляет номер студента
        for (int i = 0; i < arrayList.size(); i += 4) {
            check(i);

        }

        for (int i = 0; i < arrayList.size(); i++) {
            System.out.print(arrayList.get(i) + " ");
            count++;
            if (count%4 == 0)
                System.out.println();
        }

        count = 0;
        n = 0;

        for (int i = 0; i < arrayList.size(); i++) {
            n += Integer.valueOf(arrayList.get(i).trim());
            count++;
            if (count == 4){
                //System.out.println((i/4+1) + " : " + n);
                integerList.add(n);
                n = 0;
                count = 0;
            }
        }

        count = 0;

        Collections.sort(integerList);
        Collections.reverse(integerList);

        for (int i : integerList)
        {
            System.out.println(count++ + " : " + i);
        }

        reader.close();
    }

    private static void check(int i) {
        //У последнего не првоеряем
        if (i+3 >= arrayList.size()) {
            arrayList.remove(i);
            return;
        }

        if (Integer.valueOf(arrayList.get(i+2).trim()) <= 10) // П
        {
            for (int j = 0; j < 3; j++)
                arrayList.remove(i);

            check(i);
            return;
        }

        if (Integer.valueOf(arrayList.get(i+3).trim()) <= 10) { // без 1 экзамена
            for (int j = 0; j < 4; j++)
                arrayList.remove(i);

            check(i);
            return;
        }

        if (Integer.valueOf(arrayList.get(i+1).trim()) <= 10) { // 0 экзаменов
            for (int j = 0; j < 2; j++)
                arrayList.remove(i);

            check(i);
            return;
        }

        arrayList.remove(i);
    }
}
