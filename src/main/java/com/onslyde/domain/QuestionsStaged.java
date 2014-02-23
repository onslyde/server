package com.onslyde.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by wesleyhales on 1/11/14.
 */
@Entity
@Table(name = "questions_staged", catalog = "onslyde")
public class QuestionsStaged implements java.io.Serializable {
  private int id;
  private String question;
  private Date created;
  private int attendeeId;
  private Date timeStart;
  private Date timeEnd;
  private String identifier;
  private String answer1;
  private String answer2;


  public QuestionsStaged() {
  }

  public QuestionsStaged(int id, String question, Date created, int attendeeId, Date timeStart, Date timeEnd, String identifier, String answer1, String answer2) {
    this.id = id;
    this.question = question;
    this.created = created;
    this.attendeeId = attendeeId;
    this.timeStart = timeStart;
    this.timeEnd = timeEnd;
    this.identifier = identifier;
    this.answer1 = answer1;
    this.answer2 = answer2;
  }

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Column(name = "question", nullable = false, insertable = true, updatable = true, length = 255, precision = 0)
  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @Column(name = "attendee_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
  public Integer getAttendeeId() {
    return attendeeId;
  }

  public void setAttendeeId(Integer attendeeId) {
    try {
      this.attendeeId = attendeeId;
    } catch (Exception e) {
      System.out.println("----setter problem " + e.getCause());
    }
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "time_start", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
  public Date getTimeStart() {
    return timeStart;
  }

  public void setTimeStart(Date timeStart) {
    this.timeStart = timeStart;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "time_end", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
  public Date getTimeEnd() {
    return timeEnd;
  }

  public void setTimeEnd(Date timeEnd) {
    this.timeEnd = timeEnd;
  }

  @Column(name = "identifier", nullable = false, insertable = true, updatable = true, length = 255, precision = 0)
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Column(name = "answer1", nullable = false, insertable = true, updatable = true, length = 255, precision = 0)
  public String getAnswer1() {
    return answer1;
  }

  public void setAnswer1(String answer1) {
    this.answer1 = answer1;
  }

  @Column(name = "answer2", nullable = false, insertable = true, updatable = true, length = 255, precision = 0)
  public String getAnswer2() {
    return answer2;
  }

  public void setAnswer2(String answer2) {
    this.answer2 = answer2;
  }
}
