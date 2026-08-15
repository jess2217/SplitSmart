

import com.splitexpense.exception.StudentNotFoundException;
import com.splitexpense.model.Student;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private final List<Student> students = new ArrayList<>();
    private int nextId = 1;

    public Student addStudent(String name, String email, String college) {
        Student student = new Student(nextId++, name, email, college);
        students.add(student);
        return student;
    }

    public List<Student> getStudents() {
        return List.copyOf(students);
    }

    public Student findById(int id) {
        return students.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + id));
    }

    public void updateStudent(int id, String name, String email, String college) {
        Student student = findById(id);
        student.setName(name);
        student.setEmail(email);
        student.setCollege(college);
    }

    public void deleteStudent(int id) {
        Student student = findById(id);
        students.remove(student);
    }
}
