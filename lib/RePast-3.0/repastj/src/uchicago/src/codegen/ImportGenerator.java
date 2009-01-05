/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.codegen;

//import java.util.ArrayList;

public class ImportGenerator implements CodeGenerator {

  private String id;
  private String name;

  public ImportGenerator(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public void add(String name, Object o) {
    throw new UnsupportedOperationException("method add(...) not supported for ImportGenerator");
  }

  public String generate(int ident) {
    StringBuffer b = new StringBuffer("import ");
    b.append(name);
    b.append(";\n");

    return b.toString();
  }
  
  public String getId(){
  	return id;
  }
  
  public String getName(){
  	return name;
  }
  
  public boolean equals(Object o){
  	if(!(o instanceof ImportGenerator)){
  		return false;
  	}
  	ImportGenerator other = (ImportGenerator) o;
  	if(!other.getId().equals(id) || !other.getName().equals(name)){
  		return false;
  	}
  	return true;
  }
  
  public int hashCode(){
  	int result = 17;
  	result = name.hashCode() *37 + result;
  	result = id.hashCode() * 37 + result;
  	return result;
  }
}