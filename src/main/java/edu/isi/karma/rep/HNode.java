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
/**
 * 
 */
package edu.isi.karma.rep;

import java.io.PrintWriter;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author szekely
 * 
 */
public class HNode extends RepEntity implements Comparable<HNode> {

    @Override
    public String toString() {
	return "HNode [hTableId=" + hTableId + ", columnName=" + columnName
		+ ", nestedTable=" + nestedTable + "]";
    }

    // The HTable I belong to.
    private final String hTableId;

    // The name of the column I represent.
    private String columnName;

    // A nested table, possibly null.
    private HTable nestedTable = null;

    HNode(String id, String hTableId, String columnName) {
	super(id);
	this.hTableId = hTableId;
	this.columnName = columnName;
    }

    public String getColumnName() {
	return columnName;
    }

    public void setColumnName(String columnName) {
	this.columnName = columnName;
    }

    /**
     * @return the ID of the HTable containing this HNode.
     */
    public String getHTableId() {
	return hTableId;
    }

    public boolean hasNestedTable() {
	return nestedTable != null;
    }

    public HTable getNestedTable() {
	return nestedTable;
    }

    public void setNestedTable(HTable nestedTable) {
	this.nestedTable = nestedTable;
	// mariam
	nestedTable.setParentHNode(this);
    }

    public void removeNestedTable() {
	this.nestedTable = null;
    }

    public HTable addNestedTable(String tableName, Worksheet worksheet,
	    RepFactory factory) {
	nestedTable = factory.createHTable(tableName);
	// mariam
	nestedTable.setParentHNode(this);
	worksheet.addNestedTableToDataTable(this, factory);
	return nestedTable;
    }

    // mariam
    /**
     * Returns the HTable that this node belongs to.
     * 
     * @param f
     * @return the HTable that this node belongs to.
     */
    public HTable getHTable(RepFactory f) {
	return f.getHTable(hTableId);
    }

    // mariam
    /**
     * Returns the HNodePath for this node.
     * 
     * @param factory
     * @return the HNodePath for this node.
     */
    public HNodePath getHNodePath(RepFactory factory) {
	HNodePath p1 = new HNodePath(this);
	// get the table that it belongs to
	HTable t = factory.getHTable(hTableId);
	HNode parentNode = t.getParentHNode();
	if (parentNode != null) {
	    HNodePath p2 = parentNode.getHNodePath(factory);
	    p1 = HNodePath.concatenate(p2, p1);
	}
	return p1;
    }

    @Override
    public void prettyPrint(String prefix, PrintWriter pw, RepFactory factory) {
	pw.print(prefix + "- ");
	pw.print(columnName);
	pw.print("/" + id);
	if (nestedTable != null) {
	    pw.println(": ");
	    nestedTable.prettyPrint(prefix + "    ", pw, factory);
	} else {
	    pw.println();
	}

    }

    public int compareTo(HNode other) {
	return columnName.compareTo(other.getColumnName());
    }

    public JSONArray getJSONArrayRepresentation(RepFactory f)
	    throws JSONException {
	JSONArray arr = new JSONArray();
	Stack<HNode> st = new Stack<HNode>();
	st.push(this);
	HTable t = f.getHTable(hTableId);
	HNode parentNode = t.getParentHNode();
	while (parentNode != null) {
	    st.push(parentNode);
	    t = f.getHTable(parentNode.getHTableId());
	    parentNode = t.getParentHNode();
	}

	while (!st.isEmpty()) {
	    HNode node = st.pop();
	    JSONObject obj = new JSONObject();
	    obj.put("columnName", node.getColumnName());
	    arr.put(obj);
	}
	return arr;
    }

}
