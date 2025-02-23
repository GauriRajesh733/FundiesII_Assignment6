import tester.*;
import java.util.function.*;

interface IList<T> {
  int length();

  <R> IList<R> map(Function<T, R> mapFunc);

  boolean contains(T elem, BiFunction<T, T, Boolean> sameAsFunc);

  boolean elemsContainedIn(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc);

  boolean sameSet(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc);

  // QUESTION: should add be a void method??
  IList<T> addToSet(T elem, BiFunction<T, T, Boolean> sameAsFunc);

  // oneExists: check if at least one element in this list is contained in the
  // other
  boolean oneContainedIn(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc);

  IList<T> commonElements(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc);
}

class MtList<T> implements IList<T> {

  public int length() {
    return 0;
  }

  public boolean contains(T elem, BiFunction<T, T, Boolean> sameAsFunc) {
    return false;
  }

  public boolean elemsContainedIn(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc) {
    return otherList.length() == 0;
  }

  public boolean sameSet(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc) {
    return otherList.length() == 0;
  }

  public IList<T> addToSet(T elem, BiFunction<T, T, Boolean> sameAsFunc) {
    return new ConsList<T>(elem, new MtList<T>());
  }

  public boolean oneContainedIn(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc) {
    return false;
  }

  public IList<T> commonElements(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc) {
    return this;
  }

  public <R> IList<R> map(Function<T, R> mapFunc) {
    return new MtList<R>();
  }

}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  public boolean contains(T elem, BiFunction<T, T, Boolean> sameAsFunc) {
    if (sameAsFunc.apply(this.first, elem)) {
      return true;
    }
    return this.rest.contains(elem, sameAsFunc);
  }

  public int length() {
    return 1 + this.rest.length();
  }

  public boolean elemsContainedIn(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc) {
    return otherList.contains(this.first, sameAsFunc)
        && this.rest.elemsContainedIn(otherList, sameAsFunc);
  }

  public boolean sameSet(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc) {
    return otherList.elemsContainedIn(this, sameAsFunc)
        && this.elemsContainedIn(otherList, sameAsFunc);
  }

  public IList<T> addToSet(T elem, BiFunction<T, T, Boolean> sameAsFunc) {
    if (this.contains(elem, sameAsFunc)) {
      return this;
    }
    return new ConsList<T>(elem, this);
  }

  public boolean oneContainedIn(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc) {
    return otherList.contains(this.first, sameAsFunc)
        || this.rest.oneContainedIn(otherList, sameAsFunc);

  }

  public IList<T> commonElements(IList<T> otherList, BiFunction<T, T, Boolean> sameAsFunc) {
    if (otherList.contains(this.first, sameAsFunc)) {
      return new ConsList<T>(this.first, this.rest.commonElements(otherList, sameAsFunc));
    }
    return this.rest.commonElements(otherList, sameAsFunc);
  }

  public <R> IList<R> map(Function<T, R> mapFunc) {
    return new ConsList<R>(mapFunc.apply(this.first), this.rest.map(mapFunc));
  }

}

/*
 * class Utils {
 * 
 * // Checks if professor exists and teaches any course public Boolean
 * instructorExists(Instructor prof) { if (!prof.name.equals("")) { return true;
 * } else { throw new IllegalArgumentException("Error"); } }
 * 
 * }
 */

// Function Objects

// Represents if courses are the same
class SameCourse implements BiFunction<Course, Course, Boolean> {
  // Course name must be the same, but it is not the same course
  // without the same set of students
  // Same set of students and same course also implies the same Instructor
  public Boolean apply(Course course1, Course course2) {
    return course1.name.equals(course2.name)
        && course1.students.sameSet(course2.students, new SameStudent());

  }
}

// Represents if instructors are the same
class SameInstructor implements BiFunction<Instructor, Instructor, Boolean> {
  // Checks if the professor name is the same and the course lists are the same
  // professors could have the same name, but be different ppl
  // therefore courses must be the same too
  public Boolean apply(Instructor prof1, Instructor prof2) {
    return prof1.name.equals(prof2.name) && prof1.courses.sameSet(prof2.courses, new SameCourse());
  }
}

// Represents if students are the same
class SameStudent implements BiFunction<Student, Student, Boolean> {
  // All ids are unique, even if an ID is recycled,
  // the student that had it before would not exist
  public Boolean apply(Student student1, Student student2) {
    return student1.id == student2.id;
  }
}

class CourseToName implements Function<Course, String> {
  public String apply(Course c) {
    return c.name;
  }
}

class SameString implements BiFunction<String, String, Boolean> {
  // All ids are unique, even if an ID is recycled,
  // the student that had it before would not exist
  public Boolean apply(String str1, String str2) {
    return str1.equals(str2);
  }
}

// Instructor: Represents an instructor
class Instructor {
  String name;
  IList<Course> courses;

  // Constructor for Instructor
  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }

  void addCourse(Course c) {
    this.courses = new ConsList<Course>(c, this.courses);
  }

  boolean dejavu(Student s) {
    return this.courses.commonElements(s.courses, new SameCourse()).length() > 1;
  }

}

// Student:
class Student {
  String name;
  int id;
  IList<Course> courses;

  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<Course>();
  }

  void enroll(Course c) {
    this.courses = this.courses.addToSet(c, new SameCourse());
    c.addStudent(this);
  }

  boolean classmates(Student s) {
    return this.courses.oneContainedIn(s.courses, new SameCourse());
  }

}

// Course:
class Course {
  String name;
  Instructor prof;
  IList<Student> students;

  Course(String name, Instructor prof) {
    this.prof = prof;
    this.name = name;
    this.students = new MtList<Student>();

    this.prof.addCourse(this);
  }
  
  void addStudent(Student s) {
    this.students = this.students.addToSet(s, new SameStudent());
  }

}

class ExamplesRegistrar {
  // STUDENT EXAMPLES

  // TEACHER EXAMPLES

  // COURSE EXAMPLES
  boolean testClassmates(Tester t) {
    return t.checkExpect(new Student("charlie", 3)
        .classmates(new Student("carly", 2)), true);
  }
}
