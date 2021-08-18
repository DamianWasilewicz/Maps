package edu.brown.cs.student.mock;


/**
 * A wrapper class that stores data about a MockPerson.
 */
public class MockPerson {

  private String datetime;
  private String firstname;
  private String lastname;
  private String email;
  private String gender;
  private String streetAddress;

  /**
   * Constructor.
   * @param timestamp
   *      MockPerson's timestamp
   * @param first
   *      MockPerson's first name
   * @param last
   *      MockPerson's last name
   * @param mail
   *      MockPerson's email
   * @param gen
   *      MockPerson's gender
   * @param address
   *      MockPerson's address
   */
  public MockPerson(String timestamp, String first, String last, String mail, String gen,
                    String address) {
    datetime = timestamp;
    firstname = first;
    lastname = last;
    email = mail;
    gender = gen;
    streetAddress = address;
  }

  /**
   * gets date.
   * @return
   *      MockPerson's date
   */
  public String getDatetime() {
    return datetime;
  }

  /**
   * gets first name.
   * @return
   *      MockPerson's first name
   */
  public String getFirstname() {
    return firstname;
  }

  /**
   * gets last name.
   * @return
   *      MockPerson's last name
   */
  public String getLastname() {
    return lastname;
  }

  /**
   * gets email.
   * @return
   *      MockPerson's email
   */
  public String getEmail() {
    return email;
  }

  /**
   * gets gender.
   * @return
   *      MockPerson's gender
   */
  public String getGender() {
    return gender;
  }
  /**
   * gets address.
   * @return
   *      MockPerson's address
   */
  public String getStreetAddress() {
    return streetAddress;
  }

  /* setters */
  /**
   * sets time.
   * @param str
   *      MockPerson's date and time
   */
  public void setDatetime(String str) {
    datetime = str;
  }

  /**
   * sets firstname.
   * @param str
   *      MockPerson's first name
   */
  public void setFirstname(String str) {
    firstname = str;
  }

  /**
   * sets last name.
   * @param str
   *      MockPerson's last name
   */
  public void setLastname(String str) {
    lastname = str;
  }

  /**
   * sets email.
   * @param str
   *      MockPerson's email
   */
  public void setEmail(String str) {
    email = str;
  }

  /**
   * sets gender.
   * @param str
   *      MockPerson's gender
   */
  public void setGender(String str) {
    gender = str;
  }

  /**
   * sets address.
   * @param str
   *      MockPerson's address
   */
  public void setStreetAddress(String str) {
    streetAddress = str;
  }
}
