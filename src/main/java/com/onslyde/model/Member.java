/**
 * onslyde, Audience engagement
 * Copyright 2012, Hales Consulting, Inc., and individual contributors
*/
package com.onslyde.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@XmlRootElement
@Table(name = "Member_html5mobi", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Member implements Serializable {
   /** Default value included to remove warning. Remove or modify at will. **/
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Long id;

   @NotNull
   @Size(min = 1, max = 25, message = "1-25 letters and spaces")
   @Pattern(regexp = "[A-Za-z ]*", message = "Only letters and spaces")
   private String name;

   @NotNull
   @NotEmpty
   @Email(message = "Invalid format")
   private String email;

   @NotNull
   @Size(min = 8, message = "Must be greater than 8 characters")
   @Column(name = "password")
   private String password;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}