package com.onslyde.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wesleyhales on 1/11/14.
 */
@Entity
@Table(name = "answers", catalog = "onslyde")
public class Answers {
  private int id;
  private Questions question;
  private String answer;
  private Integer attendeeId;
  private Date created;


  @Id
  @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", nullable = false)
  public Questions getQuestion() {
    return question;
  }

  public void setQuestion(Questions question) {
    this.question = question;
  }

  @Basic
  @Column(name = "answer", nullable = true, insertable = true, updatable = true, length = 255, precision = 0)
  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  @Basic
  @Column(name = "attendee_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
  public Integer getAttendeeId() {
    return attendeeId;
  }

  public void setAttendeeId(Integer attendeeId) {
    this.attendeeId = attendeeId;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

}
