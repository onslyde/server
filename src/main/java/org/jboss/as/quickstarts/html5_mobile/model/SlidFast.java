/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.quickstarts.html5_mobile.model;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ejb.Stateful;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */

@Singleton
@Stateful
public class SlidFast {

    private List<String> activeOptions;
    private String activeOption;
    private List<String> currentVotes;

    @PostConstruct
    public void initialize() {
        System.out.println("_____________postconstruct");
        this.activeOption = "";
        this.activeOptions = new ArrayList<String>();
        this.currentVotes = new ArrayList<String>();
    }

    public List<String> getActiveOptions() {
        return activeOptions;
    }

    public void setActiveOptions(List<String> activeOptions) {
        this.activeOptions = activeOptions;
    }

    public String getActiveOption() {
        return activeOption;
    }

    public void setActiveOption(String activeOption) {
        this.activeOption = activeOption;
    }

    public List<String> getCurrentVotes() {
        return currentVotes;
    }

    public void setCurrentVotes(List<String> currentVotes) {
        this.currentVotes = currentVotes;
    }
}
