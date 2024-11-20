package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.parser.Parser;
import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.parser.classes.Star;
import edu.brown.cs.student.main.parser.classes.StudentRecord;
import edu.brown.cs.student.main.parser.creators.StarCreator;
import edu.brown.cs.student.main.parser.creators.StudentRecordCreator;
import edu.brown.cs.student.main.parser.creators.TrivialCreator;
import java.io.*;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the Parser class */
public class ParserTest {
  Parser<List<String>> incomeByRaceParser;
  Parser<List<String>> malformedParser;
  Parser<List<String>> stringParser;
  Parser<List<String>> charArrayParser;
  Parser<StudentRecord> erroringStudentRecordParser;
  Parser<StudentRecord> studentRecordParser;
  Parser<Star> erroringStarParser;
  Parser<Star> starParser;
  TrivialCreator trivialCreator = new TrivialCreator();
  StudentRecordCreator studentRecordCreator = new StudentRecordCreator();
  StarCreator starCreator = new StarCreator();

  /** ================== Tests with TrivialCreator and different Reader Objects ================= */
  // FileReader test parsing uniformed CSV
  @Test
  public void testParseRegCSV() {
    try {
      incomeByRaceParser =
          new Parser<List<String>>(
              new FileReader("data/csv/census/income_by_race.csv"), trivialCreator, false, true);
      incomeByRaceParser.parse();
    } catch (IOException | IllegalArgumentException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    assertEquals(324, incomeByRaceParser.getParsedContent().size());
    assertEquals(9, incomeByRaceParser.getParsedContent().get(223).size());
    assertEquals(9, incomeByRaceParser.getParsedContent().get(0).size());
    assertEquals(
        List.of(
            "7",
            "Two Or More",
            "2017",
            "2017",
            "44000",
            "11831",
            "\"Kent County, RI\"",
            "05000US44003",
            "kent-county-ri"),
        incomeByRaceParser.getParsedContent().get(143));
    assertFalse(
        incomeByRaceParser.getParsedContent().contains(List.of("Gemini", "Roberto", "Nick")));
  }

  // FileReader test parsing malformed data
  @Test
  public void testParseMalformedCSV() {
    try {
      malformedParser =
          new Parser<List<String>>(
              new FileReader("data/csv/malformed/malformed_signs.csv"),
              trivialCreator,
              false,
              false);
      malformedParser.parse();
    } catch (IOException | IllegalArgumentException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    assertEquals(13, malformedParser.getParsedContent().size());
    assertEquals(2, malformedParser.getParsedContent().get(0).size());
    assertEquals(List.of("Aquarius"), malformedParser.getParsedContent().get(11));
    assertEquals(List.of("Gemini", "Roberto", "Nick"), malformedParser.getParsedContent().get(3));
  }

  // StringReader test parsing a string
  @Test
  public void testParseStringReader() {
    try {
      stringParser =
          new Parser<List<String>>(
              new StringReader(
                  """
                      There,was,a,great,big,moose!
                      He,liked,to,drink,a,lot,of,juice,
                      Singing,way,oh,way,oh,
                      way,oh,way,oh,way,oh,way,oh,"""),
              trivialCreator,
              false,
              false);
      stringParser.parse();
    } catch (IOException | IllegalArgumentException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    assertEquals(4, stringParser.getParsedContent().size());
    assertEquals(6, stringParser.getParsedContent().get(0).size());
    assertEquals(8, stringParser.getParsedContent().get(1).size());
    assertEquals(5, stringParser.getParsedContent().get(2).size());
    assertEquals(8, stringParser.getParsedContent().get(3).size());
    assertEquals(
        List.of("Singing", "way", "oh", "way", "oh"), stringParser.getParsedContent().get(2));
    assertEquals(
        List.of("There", "was", "a", "great", "big", "moose!"),
        stringParser.getParsedContent().get(0));
  }

  // A more unusual StringReader test parsing a string
  @Test
  public void testWeirdParseStringReader() {
    try {
      stringParser =
          new Parser<List<String>>(
              new StringReader(
                  """
                      Lots of

                      strangethings
                      ,,,,,
                      h,p,enning,here
                      like
                      'quotes',in,quotes,?,"""),
              trivialCreator,
              false,
              false);
      stringParser.parse();
    } catch (IOException | IllegalArgumentException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    assertEquals(7, stringParser.getParsedContent().size());
    assertEquals(List.of("Lots of"), stringParser.getParsedContent().get(0));
    assertEquals(List.of(""), stringParser.getParsedContent().get(1));
    assertEquals(List.of(), stringParser.getParsedContent().get(3));
    assertEquals(List.of("h", "p", "enning", "here"), stringParser.getParsedContent().get(4));
    assertEquals(List.of("'quotes'", "in", "quotes", "?"), stringParser.getParsedContent().get(6));
  }

  // test Parser on a CharArrayReader
  @Test
  public void testParseCharArrayReader() {
    try {
      charArrayParser =
          new Parser<List<String>>(
              new CharArrayReader(
                  new char[] {
                    'w', 'h', 'o', ',', 'e', 'v', 'e', 'n', ',', '\n', 'u', 's', 'e', 's', ',', 't',
                    'h', 'i', 's'
                  }),
              trivialCreator,
              false,
              false);
      charArrayParser.parse();
    } catch (IOException | IllegalArgumentException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    assertEquals(2, charArrayParser.getParsedContent().size());
    assertEquals(List.of("who", "even"), charArrayParser.getParsedContent().get(0));
    assertEquals(List.of("uses", "this"), charArrayParser.getParsedContent().get(1));
  }

  /**
   * ========================= Testing other classes of creators ==================================
   */
  // test StudentRecordCreator with headerrow set to true
  @Test
  public void testStudentRecordParsing() {
    try {
      studentRecordParser =
          new Parser<StudentRecord>(
              new FileReader("data/csv/students/students.csv"), studentRecordCreator, true, true);
      studentRecordParser.parse();
    } catch (IOException | IllegalArgumentException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    assertEquals(10, studentRecordParser.getParsedContent().size());
    assertEquals(1001, studentRecordParser.getParsedContent().get(0).getStudentId());
    assertEquals("Alice Johnson", studentRecordParser.getParsedContent().get(0).getName());
    assertEquals("Computer Science", studentRecordParser.getParsedContent().get(0).getMajor());
    assertEquals(1008, studentRecordParser.getParsedContent().get(7).getStudentId());
    assertEquals("Hank Martin", studentRecordParser.getParsedContent().get(7).getName());
    assertEquals("Physics", studentRecordParser.getParsedContent().get(7).getMajor());
    assertEquals(1010, studentRecordParser.getParsedContent().get(9).getStudentId());
    assertEquals("Jack Wilson", studentRecordParser.getParsedContent().get(9).getName());
    assertEquals("Philosophy", studentRecordParser.getParsedContent().get(9).getMajor());
  }

  // explicit test to ensure that headerrow variable is working as intended by using a
  // "should-error" first row
  @Test
  public void testHeaderRowVar() {
    try {
      studentRecordParser =
          new Parser<StudentRecord>(
              new StringReader(
                  "thisdefisntanint,john,science"
                      + "\n1001,Sally,Pysch"
                      + "\n1002,Sally's boy,Sally"),
              studentRecordCreator,
              true,
              true);
      studentRecordParser.parse();
    } catch (IOException | IllegalArgumentException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    assertEquals(2, studentRecordParser.getParsedContent().size());
  }

  @Test
  public void testStarCreator() {
    try {
      starParser =
          new Parser<Star>(new FileReader("data/csv/stars/ten-star.csv"), starCreator, true, true);
      starParser.parse();
    } catch (IOException | IllegalArgumentException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    assertEquals(10, starParser.getParsedContent().size());
    assertEquals(0, starParser.getParsedContent().get(0).getStarId());
    assertEquals("Sol", starParser.getParsedContent().get(0).getProperName());
    assertEquals(0, starParser.getParsedContent().get(0).getCoordinates()[0]);
    assertEquals(0, starParser.getParsedContent().get(0).getCoordinates()[1]);
    assertEquals(0, starParser.getParsedContent().get(0).getCoordinates()[2]);
    assertEquals(3, starParser.getParsedContent().get(0).getCoordinates().length);

    assertEquals(118721, starParser.getParsedContent().get(9).getStarId());
    assertEquals("", starParser.getParsedContent().get(9).getProperName());
    assertEquals(-2.28262, starParser.getParsedContent().get(9).getCoordinates()[0]);
    assertEquals(0.64697, starParser.getParsedContent().get(9).getCoordinates()[1]);
    assertEquals(0.29354, starParser.getParsedContent().get(9).getCoordinates()[2]);
    assertEquals(3, starParser.getParsedContent().get(9).getCoordinates().length);
  }

  /**
   * =========================== Testing errors and exceptions ====================================
   */
  // test Parser for a file not found with a FileReader, example for exception testing
  @Test
  public void testFileNotFoundParse() throws IOException {
    Exception exception =
        assertThrows(
            FileNotFoundException.class,
            () ->
                new Parser<List<String>>(
                    new FileReader("data/csv/census/housing.csv"), trivialCreator, false, true));
  }

  // test Parser for null input
  @Test
  public void testNullInputParser() throws IllegalArgumentException {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Parser<List<String>>(null, trivialCreator, false, false));
    exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new Parser<List<String>>(new StringReader(""), null, false, false));
  }

  // reader throws the exception
  @Test
  public void testReaderException() throws IllegalArgumentException {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new Parser<List<String>>(
                    new CharArrayReader(new char[] {'a'}, 0, -1), trivialCreator, false, false));
  }

  // test StudentRecordCreator for an incorrect data form
  @Test
  public void testIncorrectDataStudentRecord() throws FactoryFailureException {
    erroringStudentRecordParser =
        new Parser<StudentRecord>(
            new StringReader("A12B,Johnny,Astrophysics"), studentRecordCreator, false, true);
    Exception exception =
        assertThrows(FactoryFailureException.class, () -> erroringStudentRecordParser.parse());
    erroringStudentRecordParser =
        new Parser<StudentRecord>(
            new StringReader("1234,Johnny,Astrophysics\n1235,Alice,BioMed\nthree,Jo,Math"),
            studentRecordCreator,
            false,
            true);
    exception =
        assertThrows(FactoryFailureException.class, () -> erroringStudentRecordParser.parse());
    erroringStudentRecordParser =
        new Parser<StudentRecord>(
            new StringReader("1234,Johnny,Astrophysics\n1235,Alice\nthree,Jo,Math"),
            studentRecordCreator,
            false,
            false);
    exception =
        assertThrows(FactoryFailureException.class, () -> erroringStudentRecordParser.parse());
  }

  // test starcreator for incorrect data forms or too many coordinates
  @Test
  public void testIncorrectDataStar() throws FactoryFailureException {
    erroringStarParser =
        new Parser<Star>(
            new StringReader("1,Andreas,282.43485,0.00449,5.36884,0"), starCreator, false, true);
    Exception exception =
        assertThrows(FactoryFailureException.class, () -> erroringStarParser.parse());
    erroringStarParser =
        new Parser<Star>(
            new StringReader("0,Sol,0,0,0\n1,Andreas,282.43485,0.00449\n"),
            starCreator,
            false,
            true);
    exception = assertThrows(FactoryFailureException.class, () -> erroringStarParser.parse());
  }

  // test for inconsistent number of columns and setNumColumns boolean functionality
  @Test
  public void testWeirdColumns() throws FactoryFailureException {
    try {
      malformedParser =
          new Parser<List<String>>(
              new FileReader("data/csv/malformed/malformed_signs.csv"),
              trivialCreator,
              false,
              true);

      Exception exception =
          assertThrows(FactoryFailureException.class, () -> malformedParser.parse());

      malformedParser =
          new Parser<List<String>>(
              new FileReader("data/csv/malformed/malformed_signs.csv"),
              trivialCreator,
              false,
              false);

      // should not error when setNumColumns is false
      malformedParser.parse();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
