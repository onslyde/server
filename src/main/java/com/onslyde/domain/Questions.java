package com.onslyde.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by wesleyhales on 1/11/14.
 */
@Entity
@Table(name = "questions", catalog = "onslyde")
public class Questions {
  private int id;
  private String question;
  private Date created;
  private int attendeeId;
  private Slide slide;

  private Set<Answers> answers = new HashSet<Answers>(0);


  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Basic
  @Column(name = "question", nullable = false, insertable = true, updatable = true, length = 255, precision = 0)
  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "slide_id", nullable = false)
  public Slide getSlide() {
    return slide;
  }

  public void setSlide(Slide slide) {
    this.slide = slide;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @Basic
  @Column(name = "attendee_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
  public Integer getAttendeeId() {
    return attendeeId;
  }

  public void setAttendeeId(Integer attendeeId) {
    this.attendeeId = attendeeId;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
  public Set<Answers> getAnswers() {
    return answers;
  }

  public void setAnswers(Set<Answers> answers) {
    this.answers = answers;
  }
}
