import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class Student {

    private String name;
    private float grade;

    public String toRecord(){
        return String.format("%s|%.2f",this.getName(), this.getGrade());
    }

    @Override
    public String toString(){
        return String.format("%s => %.2f",this.getName(), this.getGrade());
    }
}