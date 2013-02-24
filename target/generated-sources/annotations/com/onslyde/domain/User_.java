package com.onslyde.domain;

import java.util.Date;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(User.class)
public abstract class User_ {

	public static volatile SingularAttribute<User, Integer> id;
	public static volatile SetAttribute<User, Session> sessions;
	public static volatile SingularAttribute<User, Date> created;
	public static volatile SingularAttribute<User, String> email;
	public static volatile SingularAttribute<User, String> fullName;
	public static volatile SingularAttribute<User, String> password;

}

