package com.onslyde.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

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
public class Questions implements java.io.Serializable {
  private int id;
  private String question;
  private Date created;
  private int attendeeId;
  private Slide slide;

  private Set<Answers> answers = new HashSet<Answers>(0);

  public Questions() {
  }

  public Questions(int id, String question, Date created, int attendeeId, Slide slide, Set<Answers> answers) {
    this.id = id;
    this.question = question;
    this.created = created;
    this.attendeeId = attendeeId;
    this.slide = slide;
    this.answers = answers;
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

  @JsonIgnore
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

  @Column(name = "attendee_id", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
  public Integer getAttendeeId() {
    return attendeeId;
  }

  public void setAttendeeId(Integer attendeeId) {
    this.attendeeId = attendeeId;
  }

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "question")
  public Set<Answers> getAnswers() {
    return answers;
  }

  public void setAnswers(Set<Answers> answers) {
    this.answers = answers;
  }
}
