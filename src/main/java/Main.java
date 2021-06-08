import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String subjectOpt;

        Subject sub1 = new Subject("Math");
        Subject sub2 = new Subject("Chemistry");
        Subject sub3 = new Subject("English");

        do {
            System.out.println("""
                    
                    =============== Academic Record System ===============
                    Subject menu:
                    1. Math.
                    2. Chemistry.
                    3. English.
                    4. EXIT
                    """);
            System.out.print("Option: ");
            subjectOpt = scan.nextLine();

            switch (subjectOpt) {
                case "1" -> displaySubjectMenu(sub1, scan);
                case "2" -> displaySubjectMenu(sub2, scan);
                case "3" -> displaySubjectMenu(sub3, scan);
                case "4" -> System.out.println("END!");
                default -> System.out.println("-> Please select a valid option!");
            }
        } while (!subjectOpt.equals("4"));
    }

    public static void displaySubjectMenu(Subject subject, Scanner sc){
        String menu;

        do {
            System.out.printf("""
                    
                    *************** %s management menu ***************
                    1. Load file.
                    2. Add single student.
                    3. Create & send report.
                    4. Show subject records on screen
                    5. BACK.
                    
                    """, subject.getName().toUpperCase());
            System.out.print("Option: ");
            menu = sc.nextLine();

            switch (menu) {
                case "1":
                    System.out.print("\n\tEnter file name: ");
                    subject.addStudentsFromFile(sc.nextLine());
                    break;

                case "2":
                    System.out.print("\tName: ");
                    String name = sc.nextLine();
                    float grade;
                    while (true){
                        System.out.print("\tGrade: ");
                        try {
                            grade = sc.nextFloat();

                            if (grade >= 0 && grade <= 10)
                                break;
                            else
                                System.out.println("\t-> Input a value between 0 & 10");
                        } catch (InputMismatchException e) {
                            System.out.println("\t-> Input a numeric value!");
                        } finally {
                            sc.nextLine();
                        }
                    }
                    subject.addStudent(name, grade);
                    break;

                case "3":
                    subject.printToTextFile();
                    subject.printToPDF();
                    subject.sendReport();
                    break;

                case "4":
                    System.out.println(subject);
                    break;

                case "5":
                    break;

                default:
                    System.out.println("-> Please select a valid option!");
            }
        } while (!menu.equals("5"));
    }
}
