package edu.brown.cs.student.main.parser.classes;

/** StudentRecord containing data on students. */
public record StudentRecord(int studentId, String name, String major) {
  /** Method which returns the studentID. */
  public int getStudentId() {
    return studentId;
  }

  /** Method which returns the name. */
  public String getName() {
    return name;
  }

  /** Method which returns the major. */
  public String getMajor() {
    return major;
  }
}
