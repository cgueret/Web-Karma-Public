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
package edu.isi.karma.cleaning.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;

import edu.isi.karma.cleaning.Ruler;
import edu.isi.karma.cleaning.TNode;
import edu.isi.karma.cleaning.Tokenizer;

public class RegularityFeatureSet implements FeatureSet {

    public ArrayList<Vector<TNode>> tokenseqs;
    public ArrayList<Vector<TNode>> otokenseqs;
    public Vector<String> fnames;
    public static String[] targets = { "#", ";", ",", "!", "~", "@", "$", "%",
	    "^", "&", "*", "(", ")", "_", "-", "{", "}", "[", "]", "\"", "\'",
	    ":", "?", "<", ">", ".", "bnk", "syb", "wrd", "num" };

    public RegularityFeatureSet() {
	tokenseqs = new ArrayList<Vector<TNode>>();
	otokenseqs = new ArrayList<Vector<TNode>>();
	fnames = new Vector<String>();
    }

    public Vector<TNode> tokenizer(String Org) {
	CharStream cs = new ANTLRStringStream(Org);
	Tokenizer tk = new Tokenizer(cs);
	Token t;
	t = tk.nextToken();
	Vector<TNode> x = new Vector<TNode>();
	while (t.getType() != -1) {
	    int mytype = -1;
	    if (t.getType() == 12) {
		mytype = TNode.WRDTYP;
	    } else if (t.getType() == 4) {
		mytype = TNode.BNKTYP;
	    } else if (t.getType() == 8) {
		mytype = TNode.NUMTYP;
	    } else if (t.getType() == 9) {
		mytype = TNode.SYBSTYP;
	    }
	    TNode tx = new TNode(mytype, t.getText());
	    x.add(tx);
	    // System.out.println("cnt: "+t.getText()+" type:"+t.getType());
	    t = tk.nextToken();
	}
	return x;
    }

    @Override
    public Collection<Feature> computeFeatures(Collection<String> examples,
	    Collection<String> oexamples) {
	Vector<Feature> r = new Vector<Feature>();

	for (String s : examples) {
	    Vector<TNode> x = this.tokenizer(s);
	    this.tokenseqs.add(x);
	}
	for (String s : oexamples) {
	    Vector<TNode> x = this.tokenizer(s);
	    this.otokenseqs.add(x);
	}
	// counting feature
	String[] symbol = { "#", ";", ",", "!", "~", "@", "$", "%", "^", "&",
		"*", "(", ")", "_", "-", "{", "}", "[", "]", "\"", "'", ":",
		"?", "<", ">", "." };
	Vector<CntFeature> cntfs = new Vector<CntFeature>(symbol.length);
	// moving feature
	Vector<MovFeature> movfs = new Vector<MovFeature>(symbol.length);
	for (int i = 0; i < symbol.length; i++) {
	    TNode t = new TNode(TNode.SYBSTYP, symbol[i]);
	    Vector<TNode> li = new Vector<TNode>();
	    li.add(t);
	    cntfs.add(i, new CntFeature(this.otokenseqs, this.tokenseqs, li));
	    cntfs.get(i).setName("entr_cnt_" + symbol[i]);
	    movfs.add(i, new MovFeature(this.otokenseqs, this.tokenseqs, li));
	    movfs.get(i).setName("entr_mov" + symbol[i]);
	}
	// count the blank, symbol wrd and number token
	TNode t = new TNode(TNode.BNKTYP, TNode.ANYTOK);
	Vector<TNode> li = new Vector<TNode>();
	li.add(t);
	CntFeature cf = new CntFeature(this.otokenseqs, this.tokenseqs, li);
	cf.setName("entr_cnt_bnk");
	TNode t1 = new TNode(TNode.SYBSTYP, TNode.ANYTOK);
	Vector<TNode> li1 = new Vector<TNode>();
	li1.add(t1);
	CntFeature cf1 = new CntFeature(this.otokenseqs, this.tokenseqs, li1);
	cf1.setName("entr_cnt_syb");
	TNode t2 = new TNode(TNode.WRDTYP, TNode.ANYTOK);
	Vector<TNode> li2 = new Vector<TNode>();
	li2.add(t2);
	CntFeature cf2 = new CntFeature(this.otokenseqs, this.tokenseqs, li2);
	cf2.setName("entr_cnt_wrd");
	TNode t3 = new TNode(TNode.NUMTYP, TNode.ANYTOK);
	Vector<TNode> li3 = new Vector<TNode>();
	li3.add(t3);
	CntFeature cf3 = new CntFeature(this.otokenseqs, this.tokenseqs, li3);
	cf3.setName("entr_cnt_num");
	cntfs.add(cf);
	cntfs.add(cf1);
	cntfs.add(cf2);
	cntfs.add(cf3);
	r.addAll(cntfs);
	r.addAll(movfs);
	for (int i = 0; i < r.size(); i++) {
	    fnames.add(r.get(i).getName());
	}
	return r;
    }

    public static void buildEntropy(double a, int[] buk) {
	int buks[] = buk;
	if (a >= 0.0 && a < 0.1) {
	    buks[0] += 1;
	} else if (a >= 0.1 && a < 0.2) {
	    buks[1] += 1;
	} else if (a >= 0.2 && a < 0.3) {
	    buks[2] += 1;
	} else if (a >= 0.3 && a < 0.4) {
	    buks[3] += 1;
	} else if (a >= 0.4 && a < 0.5) {
	    buks[4] += 1;
	} else if (a >= 0.5 && a < 0.6) {
	    buks[5] += 1;
	} else if (a >= 0.6 && a < 0.7) {
	    buks[6] += 1;
	} else if (a >= 0.7 && a < 0.8) {
	    buks[7] += 1;
	} else if (a >= 0.8 && a < 0.9) {
	    buks[8] += 1;
	} else if (a >= 0.9 && a <= 1.0) {
	    buks[9] += 1;
	}
    }

    public static double calShannonEntropy(int[] a) {
	int cnt = 0;
	for (int c : a) {
	    cnt += c;
	}
	if (cnt == 0)
	    return Math.log(10);//
	double entropy = 0.0;
	for (int i = 0; i < a.length; i++) {
	    double freq = a[i] * 1.0 / cnt;
	    if (freq == 0)
		continue;
	    entropy -= freq * Math.log(freq);
	}
	return entropy;
    }

    @Override
    public Collection<String> getFeatureNames() {

	return fnames;

    }
}

class CntFeature implements Feature {
    String name = "";
    double score = 0.0;
    Vector<TNode> pa;

    public void setName(String name) {
	this.name = name;
    }

    public CntFeature(ArrayList<Vector<TNode>> v, ArrayList<Vector<TNode>> n,
	    Vector<TNode> t) {
	pa = t;
	score = calFeatures(v, n);
    }

    // x is the old y is the new example
    public double calFeatures(ArrayList<Vector<TNode>> x,
	    ArrayList<Vector<TNode>> y) {
	HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
	for (int i = 0; i < x.size(); i++) {
	    int cnt = 0;
	    Vector<TNode> z = x.get(i);
	    Vector<TNode> z1 = y.get(i);
	    int bpos = 0;
	    int p = 0;
	    int bpos1 = 0;
	    int p1 = 0;
	    int cnt1 = 0;
	    while (p != -1) {
		p = Ruler.Search(z, pa, bpos);
		if (p == -1)
		    break;
		bpos = p + 1;
		cnt++;
	    }
	    while (p1 != -1) {
		p1 = Ruler.Search(z1, pa, bpos1);
		if (p1 == -1)
		    break;
		bpos1 = p1 + 1;
		cnt1++;
	    }
	    // use the minus value to compute homogenenity
	    cnt = cnt - cnt1;
	    if (tmp.containsKey(cnt)) {
		tmp.put(cnt, tmp.get(cnt) + 1);
	    } else {
		tmp.put(cnt, 1);
	    }
	}
	Integer a[] = new Integer[tmp.keySet().size()];
	tmp.values().toArray(a);
	int b[] = new int[a.length];
	for (int i = 0; i < a.length; i++) {
	    b[i] = a[i].intValue();
	}
	return RegularityFeatureSet.calShannonEntropy(b) * 1.0
		/ Math.log(x.size());
    }

    @Override
    public String getName() {

	return this.name;
    }

    @Override
    public double getScore() {

	return score;
    }
}
