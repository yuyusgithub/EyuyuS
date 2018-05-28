package documentApi;

import com.google.gson.Gson;
import db.model.Student;

/**
 * Created by 彦祖 .
 */
public class CommonMethod {


    public static  String getStudentJson() {
        Student student = new Student();
        student.setName("name666");
        student.setS1(1);
        student.setS2(2);
        student.setTime(321123L);
        Gson gson = new Gson();
        return gson.toJson(student);
    }
}
