import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.*;

import java.io.*;
import java.util.*;

@Data
public class Subject {

    private String name;
    private String toMail;
    private ArrayList<Student> students;

    public Subject(String name){
        this.setName(name);
    }

    public void addStudent(String name, float grade){
        if (students == null) students = new ArrayList<>();

        students.add(new Student(name, grade));
        System.out.println("Record added.");
    }

    public void addStudentsFromFile(String fileName){
        try {
            File f = new File(fileName);
            @Cleanup Scanner s = new Scanner(f);
            String line;
            String[] student;
            int added = 0;

            if (s.hasNextLine()) setToMail(s.nextLine());
            if (students == null) students = new ArrayList<>();

            while (s.hasNextLine()){
                line = s.nextLine();

                if (!line.isEmpty()) {
                    student = line.split("\\|");
                    students.add(new Student(student[0],Float.parseFloat(student[1])));
                    added++;
                }
            }
            System.out.printf("File read successfully. %d records were added.\n",added);
        } catch (FileNotFoundException e) {
            System.out.println("-> File not found. Check filename and try again.");
        }
    }

    private float getMax(){
        float max = 0;

        for (Student x: students)
            if (x.getGrade() > max) max = x.getGrade();

        return max;
    }

    private float getMin(){
        float min = 10;

        for (Student x: students)
            if (x.getGrade() < min) min = x.getGrade();

        return min;
    }

    private float getAverage(){
        float sum = 0;

        for (Student x: students)
            sum += x.getGrade();

        return sum / students.size();
    }

    private Map.Entry<Integer, ArrayList<Float>> getMostRepeated(){
        LinkedHashMap<Float,Integer> counts = new LinkedHashMap<>();
        ArrayList<Float> mostRepeated = new ArrayList<>();

        int count = 0;
        int currentCount = 0;

        for (Student currentStudent : students) {
            //Si el valor actual ya fue contado, se pasa al siguiente valor
            if (!counts.containsKey(currentStudent.getGrade())){
                //contando las repeticiones
                for (Student student : students)
                    if (currentStudent.getGrade() == student.getGrade()) currentCount++;

                //Se guarda el valor y su cuenta
                counts.put(currentStudent.getGrade(), currentCount);

                //Evaluando el resultado
                if (currentCount > count) count = currentCount;

                //Se reinicia la cuenta
                currentCount = 0;
            }
        }

        //Se compara que valores coinciden con la cuenta más repetida
        for (Map.Entry<Float,Integer> entry : counts.entrySet())
            if (Objects.equals(count, entry.getValue()))
                mostRepeated.add(entry.getKey());

        return new AbstractMap.SimpleEntry<>(count,mostRepeated);
    }

    private ArrayList<Student> getSubList(float x){
        ArrayList<Student> subList = new ArrayList<>();

        for (Student student : students)
            if (student.getGrade() == x) subList.add(student);

        return subList;
    }

    @Override
    public String toString(){
        if (students == null || students.size() == 0) return "There are NO records stored.";

        StringBuilder sb = new StringBuilder();

        for (Student student : students)
            sb.append(student.toRecord()).append("\n");

        float val = getMax();
        List<Student> subList = getSubList(val);
        sb.append(String.format("\n\nMax value: %.2f with %d repetitions.", val, subList.size()));
        for (Student student : subList)
            sb.append("\n\t").append(student.getName());

        val = getMin();
        subList = getSubList(val);
        sb.append(String.format("\n\nMin value: %.2f with %d repetitions.", val, subList.size()));
        for (Student student : subList)
            sb.append("\n\t").append(student.getName());

        sb.append(String.format("\n\nAvg value: %.2f",getAverage()));

        Map.Entry<Integer,ArrayList<Float>> mostRepeated = getMostRepeated();
        val = mostRepeated.getKey();
        sb.append(String.format("\n\nMost repeated value(s): %s with %d repetitions each",Arrays.toString(mostRepeated.getValue().toArray()),(int)val));
        for (float x : mostRepeated.getValue())
            for (Student student : getSubList(x))
                sb.append("\n\t").append(student.toString());

        return sb.toString();
    }

    public void printToTextFile(){
        if (students != null && students.size() > 0){
            try {
                @Cleanup PrintStream fileStream = new PrintStream("Reports\\" + this.getName()+".txt");
                fileStream.println(this);
                System.out.println("TXT file generated successfully");
            } catch (IOException e){
                System.out.println("An error has occurred while generating the TXT file: ");
                e.printStackTrace();
            }
        } else {
            System.out.println("Unable to generate TXT file because the students list is empty.");
        }

    }

    public void printToPDF(){
        if (students != null && students.size() > 0){
            try {
                @Cleanup Document doc = new Document();
                PdfWriter.getInstance(doc,new FileOutputStream("Reports\\" + this.getName()+".pdf"));

                doc.open();
                Paragraph paragraph = new Paragraph(this.toString());
                doc.add(paragraph);

                System.out.println("PDF file generated successfully");
            } catch (Exception e){
                System.out.println("An error has occurred while generating the PDF file: ");
                e.printStackTrace();
            }
        } else {
            System.out.println("Unable to generate PDF file because the students list is empty.");
        }
    }

    public void sendReport(){
        String path = "Reports\\" + this.getName()+".pdf";
        if (new File(path).exists()) {
            if (this.getToMail() != null && !this.getToMail().isBlank()){
                Mail mail = new Mail();
                mail.sendMail(
                        this.getToMail()
                        ,this.getName().toUpperCase()+" grades report."
                        ,"Students list and statistics for " + this.getName().toUpperCase()
                        ,new String[]{path}
                );
            } else {
                System.out.println("Unable to send email. Recipient email address has not been set.");
            }
        } else {
            System.out.println("Unable to send email. The report file does not exist.");
        }

    }
}
