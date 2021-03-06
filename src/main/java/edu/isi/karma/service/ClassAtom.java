/*******************************************************************************
 * Copyright 2012 University of Southern California
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This code was developed by the Information Integration Group as part 
 * of the Karma project at the Information Sciences Institute of the 
 * University of Southern California.  For more information, publications, 
 * and related projects, please see: http://www.isi.edu/integration
 ******************************************************************************/

package edu.isi.karma.service;

import edu.isi.karma.modeling.alignment.URI;

public class ClassAtom extends Atom {

    private URI classPredicate;
    private Argument argument1;

    public ClassAtom(URI classPredicate, Argument argument1) {
	this.classPredicate = classPredicate;
	this.argument1 = argument1;
    }

    public URI getClassPredicate() {
	return classPredicate;
    }

    public Argument getArgument1() {
	return argument1;
    }

    public void setClassPredicate(URI classPredicate) {
	this.classPredicate = classPredicate;
    }

    public void setArgument1(Argument argument1) {
	this.argument1 = argument1;
    }

    public void print() {
	System.out.println("class predicate uri: "
		+ classPredicate.getUriString());
	System.out.println("class predicate ns: " + classPredicate.getNs());
	System.out.println("class predicate prefix: "
		+ classPredicate.getPrefix());
	System.out.println("argument1: " + argument1.getId());
    }

}
