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
package edu.isi.karma.rep.semantictypes;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import edu.isi.karma.modeling.alignment.URI;
import edu.isi.karma.util.Jsonizable;

public class SemanticType implements Jsonizable {
    private final String hNodeId;
    private final URI type;
    private final URI domain;
    private final Origin origin;
    private final boolean isPartOfKey;
    private final ConfidenceLevel confidenceLevel;

    public enum Origin {
	User, CRFModel
    }

    public enum ConfidenceLevel {
	High, Medium, Low
    }

    public enum ClientJsonKeys {
	isPrimary, Domain, FullType
    }

    public SemanticType(String hNodeId, URI type, URI domain, Origin origin,
	    Double probability, boolean isPartOfKey) {
	this.hNodeId = hNodeId;
	this.type = type;
	this.origin = origin;
	this.domain = domain;
	this.isPartOfKey = isPartOfKey;

	if (probability > 0.8)
	    confidenceLevel = ConfidenceLevel.High;
	else if (probability < 0.8 && probability > 0.4)
	    confidenceLevel = ConfidenceLevel.Medium;
	else
	    confidenceLevel = ConfidenceLevel.Low;
    }

    public String getHNodeId() {
	return hNodeId;
    }

    public URI getDomain() {
	return domain;
    }

    public URI getType() {
	return type;
    }

    public Origin getOrigin() {
	return origin;
    }

    @Override
    public String toString() {
	if (isClass())
	    return "SemanticType [hNodeId=" + hNodeId + ", type="
		    + type.getUriString() + ", origin=" + origin
		    + ", isPartOfKey=" + isPartOfKey + ", confidenceLevel="
		    + confidenceLevel + "]";
	else
	    return "SemanticType [hNodeId=" + hNodeId + ", type="
		    + type.getUriString() + ", domain=" + domain.getUriString()
		    + ", origin=" + origin + ", isPartOfKey=" + isPartOfKey
		    + ", confidenceLevel=" + confidenceLevel + "]";
    }

    // mariam
    /**
     * Returns true if this type is a Class; false if it is a data property.
     * 
     * @return true if this type is a Class; false if it is a data property.
     */
    public boolean isClass() {
	if (domain == null || domain.getUriString().trim().isEmpty())
	    return true;
	// it is a data property
	return false;
    }

    public ConfidenceLevel getConfidenceLevel() {
	return confidenceLevel;
    }

    public boolean isPartOfKey() {
	return isPartOfKey;
    }

    public JSONObject getJSONArrayRepresentation() throws JSONException {
	JSONObject typeObj = new JSONObject();
	typeObj.put(ClientJsonKeys.FullType.name(), type.getUriString());
	if (isClass())
	    typeObj.put(ClientJsonKeys.Domain.name(), "");
	else
	    typeObj.put(ClientJsonKeys.Domain.name(), domain.getUriString());
	typeObj.put(ClientJsonKeys.isPrimary.name(), isPartOfKey);
	return typeObj;
    }

    @Override
    public void write(JSONWriter writer) throws JSONException {
	writer.object();
	writer.key("HNodeId").value(hNodeId);
	writer.key("Type").value(type);
	writer.key("Origin").value(origin.name());
	writer.key("ConfidenceLevel").value(confidenceLevel.name());
	writer.endObject();
    }
}
