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
package edu.isi.karma.cleaning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.isi.karma.cleaning.changed_grammar.RuleInterpreterLexer;
import edu.isi.karma.cleaning.changed_grammar.RuleInterpreterParser;
import edu.isi.karma.cleaning.changed_grammar.RuleInterpreterTree;
import edu.isi.karma.cleaning.features.Feature;
import edu.isi.karma.cleaning.features.RegularityFeatureSet;

public class Main {
    public void Evaluation() {
	String fpath = "/Users/bowu/Research/dataclean/data/rule.txt";
	String xline = "";
	String fpath0 = "/Users/bowu/Research/dataclean/data/30addresslist.txt";
	String fpath1 = "/Users/bowu/Research/dataclean/data/eval.txt";

	BufferedReader br = null;
	BufferedReader xbr = null;
	BufferedWriter bres = null;
	try {
	    bres = new BufferedWriter(new FileWriter(fpath1));
	    br = new BufferedReader(new FileReader(fpath));
	    String line = "";
	    while ((line = br.readLine()) != null) {
		if (line.compareTo("") == 0)
		    break;

		Evaluator eva = new Evaluator();
		xbr = new BufferedReader(new FileReader(fpath0));
		while (true) {
		    xline = xbr.readLine();
		    if (xline == null) {
			double d1 = eva.calShannonEntropy(eva.pos);
			double d2 = eva.calShannonEntropy(eva.cht);
			// String l =
			// "rule:::::::::"+line+"\n"+"epos:"+d1+" ecnt:"+d2;
			String l = d1 + "	" + d2 + " ";
			bres.write(l + "\n");
			bres.flush();
			System.out.println(d1 + " " + d2);
			break;
		    }
		    if (xline.compareTo("") == 0) {
			break;
		    }
		    Ruler r = new Ruler();
		    r.setNewInput(xline);
		    eva.setRuler(r);
		    CharStream cs = new ANTLRStringStream(line);
		    RuleInterpreterLexer lexer = new RuleInterpreterLexer(cs);
		    CommonTokenStream tokens = new CommonTokenStream(lexer);
		    RuleInterpreterParser parser = new RuleInterpreterParser(
			    tokens);
		    CommonTreeNodeStream nodes = new CommonTreeNodeStream(
			    parser.rule().getTree());
		    RuleInterpreterTree inter = new RuleInterpreterTree(nodes);
		    inter.setRuler(r);
		    inter.rule();
		    eva.addCnt(r.whats);
		    eva.addPos(r.positions);
		    // System.out.println(r.print());

		}
		// output evaluation result

	    }
	} catch (Exception ex) {
	    System.out.println("" + ex.toString());
	} finally {
	    try {
		if (br != null)
		    br.close();
		if (xbr != null)
		    xbr.close();
		if (bres != null)
		    bres.close();
	    } catch (IOException e) {
	    }
	}
    }

    public void autogeneratetestFeaturesFile() {
	String fpath = "/Users/bowu/Research/dataclean/data/testrule.txt";
	new ResultViewer();
	new Vector<CommonTree>();
	try {
	    BufferedReader br = new BufferedReader(new FileReader(fpath));
	    String line = "";
	    Vector<String> row = new Vector<String>();
	    row.add("rows");
	    HashMap<String, Vector<String>> hm = new HashMap<String, Vector<String>>();

	    while ((line = br.readLine()) != null) {
		if (line.compareTo("") == 0)
		    break;

		// CommonTree t = (CommonTree) parser.rule().getTree();
		String[] xline;
		String fpath0 = "/Users/bowu/Research/dataclean/data/RuleData/50_address_pair.csv";
		CSVReader xbr = new CSVReader(new FileReader(fpath0), '\t');
		xbr.readNext();
		String s = "";
		while ((xline = xbr.readNext()) != null) {
		    Ruler r = new Ruler();
		    Vector<String> xrow = new Vector<String>();
		    xrow.add(xline[0]);
		    r.setNewInput(xline[0]);
		    CharStream cs = new ANTLRStringStream(line);
		    RuleInterpreterLexer lexer = new RuleInterpreterLexer(cs);
		    CommonTokenStream tokens = new CommonTokenStream(lexer);
		    RuleInterpreterParser parser = new RuleInterpreterParser(
			    tokens);
		    CommonTreeNodeStream nodes = new CommonTreeNodeStream(
			    (CommonTree) parser.rule().getTree());
		    RuleInterpreterTree evaluator = new RuleInterpreterTree(
			    nodes);
		    evaluator.setRuler(r);
		    evaluator.rule();
		    s += r.toString() + "\n";
		    if (r.toString().compareTo(xline[1]) == 0) {
			xrow.add(r.toString());
		    } else {
			xrow.add("<font color='#FF0000'>" + r.toString()
				+ "</font>");
		    }
		}
		if (hm.containsKey(s)) {
		    hm.get(s).add(line);
		} else {
		    Arrays.asList(s.split("\n"));
		    // RegularityClassifer.Add2FeatureFile(examples, "", isrit);
		    Vector<String> vr = new Vector<String>();
		    vr.add(line);
		    hm.put(s, vr);
		}

	    }
	} catch (Exception ex) {
	    System.out.println("" + ex.toString());
	}
    }

    /**
     * @param fpath
     *            rule file location
     * @param fpath0
     *            ground through file location
     * @param ofpath
     *            output location
     * @return
     */
    public static String[] exper1_cluster(String fpath, String fpath0,
	    String ofpath) {
	new Vector<CommonTree>();
	String orgin = "";
	BufferedReader br = null;
	CSVReader xbr = null;
	try {
	    br = new BufferedReader(new FileReader(fpath));
	    String line = "";
	    Vector<String> row = new Vector<String>();
	    row.add("rows");
	    HashMap<String, Vector<String>> hm = new HashMap<String, Vector<String>>();

	    while ((line = br.readLine()) != null) {
		if (line.compareTo("") == 0)
		    break;

		// CommonTree t = (CommonTree) parser.rule().getTree();
		String[] xline;
		xbr = new CSVReader(new FileReader(fpath0), '\t');
		// xbr.readNext();
		String s = "";// the string contain all the data
		orgin = ""; // the string contain all the original data
		while ((xline = xbr.readNext()) != null) {
		    Ruler r = new Ruler();
		    Vector<String> xrow = new Vector<String>();
		    xrow.add(xline[0]);
		    r.setNewInput(xline[0]);
		    CharStream cs = new ANTLRStringStream(line);
		    RuleInterpreterLexer lexer = new RuleInterpreterLexer(cs);
		    CommonTokenStream tokens = new CommonTokenStream(lexer);
		    RuleInterpreterParser parser = new RuleInterpreterParser(
			    tokens);
		    CommonTreeNodeStream nodes = new CommonTreeNodeStream(
			    (CommonTree) parser.rule().getTree());
		    RuleInterpreterTree evaluator = new RuleInterpreterTree(
			    nodes);
		    evaluator.setRuler(r);
		    evaluator.rule();
		    s += r.toString() + "\n";
		    orgin += xline[0] + "\n";
		    if (r.toString().compareTo(xline[1]) == 0) {
			xrow.add(r.toString());
		    } else {
			xrow.add("<font color='#FF0000'>" + r.toString()
				+ "</font>");
		    }
		}
		// hm is the data to rules
		if (hm.containsKey(s)) {
		    hm.get(s).add(line);
		} else {
		    Vector<String> vr = new Vector<String>();
		    vr.add(line);
		    hm.put(s, vr);
		}
	    }
	    int cnt = 0;
	    // ResultViewer rx = new ResultViewer();
	    boolean isfirstRun = true;
	    Vector<String> rawAddr = new Vector<String>();
	    for (String ks : orgin.split("\n")) {
		rawAddr.add(ks);
	    }
	    double lowest = 10000;
	    String result = "";
	    for (String xs : hm.keySet()) {
		hm.get(xs);
		RegularityFeatureSet rf = new RegularityFeatureSet();
		Vector<String> addr = new Vector<String>();
		for (String is : xs.split("\n")) {
		    addr.add(is);
		}
		Collection<Feature> cf = rf.computeFeatures(rawAddr, addr);
		Feature[] x = new Feature[cf.size()];
		cf.toArray(x);
		Vector<String> xrow = new Vector<String>();
		if (isfirstRun) {
		    xrow.add("Featurename");
		    for (int l = 0; l < x.length; l++) {
			xrow.add(x[l].getName());
		    }
		    isfirstRun = false;
		    // rx.addRow(xrow);
		    xrow = new Vector<String>();
		}
		if (!isfirstRun) {
		    xrow.add(String.valueOf(cnt));
		    double sc = 0;
		    for (int k = 0; k < cf.size(); k++) {
			sc += x[k].getScore();
			xrow.add(String.valueOf(x[k].getScore()));
		    }
		    if (sc < lowest) {
			lowest = sc;
			result = xs;
		    }
		}
		// rx.addRow(xrow);
		// write2file(vs1,"/Users/bowu/Research/dataclean/data/cluster_rset"+cnt+".txt");
		cnt++;
	    }
	    String[] res = visualResult(result, fpath0);
	    // rx.print(ofpath);
	    return res;
	} catch (Exception ex) {
	    System.out.println("" + ex.toString());
	    return null;
	} finally {
	    try {
		if (br != null)
		    br.close();
		if (xbr != null)
		    xbr.close();
	    } catch (IOException e) {
	    }
	}
    }

    public void write2file(Collection<String> x, String fname) {
	try {
	    BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
		    fname)));
	    for (String s : x) {
		bw.write(s + "\n");
	    }
	    bw.close();
	} catch (Exception ex) {
	    System.out.println("" + ex.toString());
	}
    }

    public void applyRule(String rule, String fpath) {
	ResultViewer rv = new ResultViewer();
	BufferedReader xbr = null;
	try {

	    Ruler r = new Ruler();
	    String xline = "";
	    File f = new File(fpath);
	    xbr = new BufferedReader(new FileReader(f));
	    while ((xline = xbr.readLine()) != null) {
		Vector<String> xrow = new Vector<String>();
		if (xline.compareTo("") == 0)
		    break;
		// xrow.add(xline);
		CharStream cs = new ANTLRStringStream(rule);
		RuleInterpreterLexer lexer = new RuleInterpreterLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		RuleInterpreterParser parser = new RuleInterpreterParser(tokens);
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(
			(CommonTree) parser.rule().getTree());
		RuleInterpreterTree evaluator = new RuleInterpreterTree(nodes);
		r.setNewInput(xline);
		evaluator.setRuler(r);
		evaluator.rule();
		System.out.println(r.toString());
		xrow.add(xline);
		xrow.add(r.toString());
		// xrow.add(r.toString());
		rv.addRow(xrow);
	    }
	    rv.print(f.getAbsolutePath() + "_res.csv");
	} catch (Exception ex) {
	    System.out.println("" + ex.toString());
	} finally {
	    try {
		if (xbr != null)
		    xbr.close();
	    } catch (IOException e) {
	    }
	}
    }

    // for generating training data
    public void genTrainingdata() {
	File dir = new File("/Users/bowu/Research/dataclean/data/RuleData");
	File[] flist = dir.listFiles();
	try {
	    for (int i = 0; i < flist.length; i++) {
		if (flist[i].getName().contains(".csv")) {
		    CSVReader cr = new CSVReader(new FileReader(flist[i]), '\t');
		    BufferedWriter bw = new BufferedWriter(new FileWriter(
			    new File("/Users/bowu/Research/dataclean/data/"
				    + flist[i].getName() + "_res.txt")));
		    String[] eles = cr.readNext();
		    while ((eles = cr.readNext()) != null) {
			bw.write(eles[1] + "\n");
		    }
		    bw.close();
		}
	    }
	} catch (Exception ex) {
	    System.out.println("" + ex.toString());
	}
    }

    static Vector<Integer> ruleNum1 = new Vector<Integer>();
    static Vector<Integer> ruleNum2 = new Vector<Integer>();

    // output for generating all the results of all rules
    // fpath rule file path fpath0 data file
    public static String[] output(String fpath, String fpath0) {
	// ResultViewer rv = new ResultViewer();
	Vector<CommonTree> ps = new Vector<CommonTree>();
	CSVReader xbr = null;
	BufferedReader br = null;
	try {
	    br = new BufferedReader(new FileReader(fpath));
	    String line = "";
	    Vector<String> row = new Vector<String>();
	    row.add("rows");
	    while ((line = br.readLine()) != null) {
		if (line.compareTo("") == 0)
		    break;
		row.add(line);
		CharStream cs = new ANTLRStringStream(line);
		RuleInterpreterLexer lexer = new RuleInterpreterLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		RuleInterpreterParser parser = new RuleInterpreterParser(tokens);
		// CommonTree t = (CommonTree) parser.rule().getTree();
		ps.add((CommonTree) parser.rule().getTree());
	    }
	    // rv.addRow(row);
	    // System.out.println("hello");
	    // apply the rule
	    String[] wrongExample = null;
	    HashSet<Integer> hs = new HashSet<Integer>();
	    String[] xline;
	    xbr = new CSVReader(new FileReader(fpath0), '\t');
	    xbr.readNext();
	    while ((xline = xbr.readNext()) != null) {
		Ruler r = new Ruler();
		Vector<String> xrow = new Vector<String>();
		xrow.add(xline[0]);
		for (int k = 0; k < ps.size(); k++) {
		    r.setNewInput(xline[0]);
		    CommonTreeNodeStream nodes = new CommonTreeNodeStream(
			    ps.get(k));
		    RuleInterpreterTree evaluator = new RuleInterpreterTree(
			    nodes);
		    evaluator.setRuler(r);
		    evaluator.rule();
		    System.out.println("function output " + k);
		    if (r.toString().compareTo(xline[1]) == 0) {
			xrow.add(r.toString());
		    } else {
			xrow.add("<font color='#FF0000'>" + r.toString()
				+ "</font>");
			if (!hs.contains(k)) {
			    hs.add(k);
			    wrongExample = xline;
			}
		    }

		}
		// rv.addRow(xrow);
	    }
	    // rv.print("xx.csv");
	    // rv.publishHTML("/Users/bowu/Research/dataclean/data/ResVisual.htm");
	    System.out.println("Right Rules: " + (ps.size() - hs.size())
		    + "   Total Rules: " + ps.size());
	    ruleNum1.add(ps.size());
	    return wrongExample;
	} catch (Exception ex) {
	    System.out.println("" + ex.toString());
	    return null;
	} finally {
	    try {
		if (br != null)
		    br.close();
		if (xbr != null)
		    xbr.close();
	    } catch (IOException e) {
	    }
	}
    }

    public void visualFile() {
	CSVReader cr = null;
	CSVReader cr1 = null;
	try {
	    String gtruth = "/Users/bowu/Research/dataclean/data/RuleData/50_address_pair.csv";
	    String file = "/Users/bowu/Research/dataclean/data/cluster1.csv";
	    cr = new CSVReader(new FileReader(new File(gtruth)), '\t');
	    List<String[]> lines = cr.readAll();
	    lines.remove(0);
	    cr1 = new CSVReader(new FileReader(new File(file)), '\t');
	    String[] ds = cr1.readNext();
	    ResultViewer rv = new ResultViewer();
	    for (int p = 0; p < ds.length; p++) {
		String[] res = ds[p].split("\n");
		Vector<String> tr = new Vector<String>();
		for (int k = 0; k < res.length; k++) {
		    if (res[k].compareTo(lines.get(k)[1]) == 0) {
			tr.add(res[k]);
		    } else {
			tr.add("<font color='#FF0000'>" + res[k] + "</font>");
		    }
		}
		rv.addColumn(tr);
	    }
	    rv.publishHTML("/Users/bowu/Research/dataclean/data/cluster1.htm");
	} catch (Exception e) {
	    // TODO: handle exception
	} finally {
	    try {
		if (cr != null)
		    cr.close();
		if (cr1 != null)
		    cr1.close();
	    } catch (IOException e) {
	    }
	}
    }

    public static String[] visualResult(String xs, String gtruth) {
	try {
	    CSVReader cr = new CSVReader(new FileReader(new File(gtruth)), '\t');
	    List<String[]> lines = cr.readAll();
	    // lines.remove(0);
	    ResultViewer rv = new ResultViewer();
	    String[] res = xs.split("\n");
	    String[] wrongExample = null;// use for experiment to record
					 // incorrect transoformated result
	    Vector<String> tr = new Vector<String>();
	    Vector<String> org = new Vector<String>();
	    for (String[] tmp : lines) {
		org.add(tmp[0]);
	    }
	    rv.addColumn(org);
	    for (int k = 0; k < res.length; k++) {
		if (res[k].compareTo(lines.get(k)[1]) == 0) {
		    tr.add(res[k]);
		} else {
		    tr.add("<font color='#FF0000'>" + res[k] + "</font>");
		    wrongExample = lines.get(k);
		}
	    }
	    rv.addColumn(tr);
	    rv.publishHTML("/Users/bowu/Research/dataclean/data/cluster1.htm");
	    return wrongExample;
	} catch (Exception e) {
	    System.out.println("error in the visualResult");
	    return null;
	}

    }

    public void genNegRes() {
	String dpath = "/Users/bowu/Research/dataclean/data/RuleData/rawdata";
	try {
	    BufferedReader br = new BufferedReader(
		    new FileReader(
			    new File(
				    "/Users/bowu/Research/dataclean/data/RuleData/rawdata/negarules.in")));
	    File f = new File(dpath);
	    File[] fs = f.listFiles();
	    for (File fe : fs) {
		if (fe.getName().contains("txt")) {
		    String rule = br.readLine();
		    this.applyRule(rule, fe.getAbsolutePath());
		}
	    }

	} catch (Exception e) {
	    // TODO: handle exception
	}

    }

    public static void fun1() {
	try {
	    BufferedReader br1 = new BufferedReader(
		    new FileReader(
			    new File(
				    "/Users/bowu/Research/dataclean/data/RuleData/rawdata/trans/50_data2.txt")));
	    BufferedReader br2 = new BufferedReader(
		    new FileReader(
			    new File(
				    "/Users/bowu/Research/dataclean/data/RuleData/rawdata/trans/50_data2_res.txt")));
	    CSVWriter cw = new CSVWriter(
		    new FileWriter(
			    new File(
				    "/Users/bowu/Research/dataclean/data/RuleData/rawdata/trans/50_data2.csv")),
		    '\t');
	    String line1 = "";
	    String line2 = "";
	    while (((line1 = br1.readLine()) != null)
		    && ((line2 = br2.readLine()) != null)) {
		line2.replaceAll("", "\"");
		String[] l = { line1, line2 };
		cw.writeNext(l);
	    }
	    cw.close();
	} catch (Exception ex) {
	    System.out.println("" + ex.toString());
	}
    }

    public void exper_2(String dirpath) {
	File nf = new File(dirpath);
	File[] allfiles = nf.listFiles();
	// statistics
	Vector<String> names = new Vector<String>();
	Vector<Integer> exampleCnt = new Vector<Integer>();
	Vector<Double> timeleng = new Vector<Double>();
	new Vector<Integer>();
	Vector<Vector<String>> ranks = new Vector<Vector<String>>();
	Vector<Vector<Integer>> consisRules = new Vector<Vector<Integer>>();
	Vector<String> cRules = new Vector<String>();
	// list all the csv file under the dir
	for (File f : allfiles) {
	    String cx = "";

	    Vector<String[]> examples = new Vector<String[]>();
	    Vector<String[]> entries = new Vector<String[]>();
	    Vector<String> rank = new Vector<String>();
	    Vector<Integer> consRule = new Vector<Integer>();
	    try {
		if (f.getName().indexOf(".csv") == (f.getName().length() - 4)) {
		    CSVReader cr = new CSVReader(new FileReader(f), '\t');
		    String[] pair;
		    String corrResult = "";
		    while ((pair = cr.readNext()) != null) {
			pair[0] = "<_START>" + pair[0] + "<_END>";
			entries.add(pair);
			corrResult += pair[1] + "\n";
		    }
		    HashMap<Integer, Boolean> indicators = new HashMap<Integer, Boolean>();
		    examples.add(entries.get(0));
		    boolean isend = false;
		    double timespan = 0.0;
		    int Ranktermin = -1;
		    while (Ranktermin == -1) // repeat as no correct answer
					     // appears.
		    {
			cx = "";
			HashMap<String, Integer> dic = new HashMap<String, Integer>();
			long st = System.currentTimeMillis();
			Vector<String> pls = RuleUtil.genRule(examples);
			System.out.println("Consistent Rules :" + pls.size());
			for (int k = 0; k < examples.size(); k++) {
			    System.out.println(examples.get(k)[0] + "    "
				    + examples.get(k)[1]);
			}
			int corrNum = 0;
			String[] wexam = null;
			if (pls.size() == 0)
			    continue;
			for (int i = 0; i < pls.size(); i++) {
			    String tranresult = "";
			    cx += "\n\n" + pls.get(i);
			    String[] rules = pls.get(i).split("<RULESEP>");
			    // System.out.println(""+s1);
			    Vector<String> xr = new Vector<String>();
			    for (int t = 0; t < rules.length; t++) {
				if (rules[t].length() != 0)
				    xr.add(rules[t]);
			    }
			    isend = true;
			    for (int j = 0; j < entries.size(); j++) {
				String s = RuleUtil.applyRule(xr,
					entries.get(j)[0]);
				if (s == null || s.length() == 0) {
				    isend = false;
				    wexam = entries.get(j);
				    s = entries.get(j)[0];
				    // break;
				}
				if (s.compareTo(entries.get(j)[1]) != 0) {
				    isend = false;
				    wexam = entries.get(j);
				    // break;
				}
				tranresult += s + "\n";
			    }
			    if (dic.containsKey(tranresult)) {
				dic.put(tranresult, dic.get(tranresult) + 1);
			    } else {
				dic.put(tranresult, 1);
			    }
			    if (isend)
				corrNum++;
			}
			long ed = System.currentTimeMillis();
			timespan = (ed - st) * 1.0 / 60000;

			String trainPath = "./grammar/features.arff";
			int trnk = UtilTools.rank(dic, corrResult, trainPath);
			rank.add(trnk + "/" + dic.keySet().size());
			Ranktermin = trnk;
			if (!indicators.containsKey(examples.size())) {
			    if (!recdic.containsKey(f.getName())) {
				HashMap<String, Vector<Double>> tmp = new HashMap<String, Vector<Double>>();
				if (tmp.containsKey("" + examples.size())) {
				    Vector<Double> x = tmp.get(""
					    + examples.size());
				    x.set(0, x.get(0) + RuleUtil.sgsnum);
				    x.set(1, x.get(1) + pls.size());
				    x.set(2, x.get(2) + corrNum);
				    if (trnk <= 3 && trnk >= 0) {
					x.set(3, x.get(3) + 1);
					x.set(4, x.get(4) + dic.keySet().size());
				    }
				    x.set(5, x.get(5) + 1);
				} else {
				    Vector<Double> x = new Vector<Double>();
				    x.add(1.0 * RuleUtil.sgsnum);
				    x.add(1.0 * pls.size());
				    x.add(1.0 * corrNum);
				    if (trnk <= 3 && trnk >= 0) {
					x.add(1.0);
					x.add(1.0 * dic.keySet().size());
				    } else {
					x.add(0.0);
					x.add(0.0);
				    }
				    x.add(1.0);
				    tmp.put("" + examples.size(), x);
				}
				recdic.put(f.getName(), tmp);
			    } else {
				HashMap<String, Vector<Double>> tmp = recdic
					.get(f.getName());
				if (tmp.containsKey("" + examples.size())) {
				    Vector<Double> x = tmp.get(""
					    + examples.size());
				    x.set(0, x.get(0) + RuleUtil.sgsnum);
				    x.set(1, x.get(1) + pls.size());
				    x.set(2, x.get(2) + corrNum);
				    if (trnk <= 3 && trnk >= 0) {
					x.set(3, x.get(3) + 1);
					x.set(4, x.get(4) + dic.keySet().size());
				    }
				    x.set(5, x.get(5) + 1);
				} else {
				    Vector<Double> x = new Vector<Double>();
				    x.add(1.0 * RuleUtil.sgsnum);
				    x.add(1.0 * pls.size());
				    x.add(1.0 * corrNum);
				    if (trnk <= 3 && trnk >= 0) {
					x.add(1.0);
					x.add(1.0 * dic.keySet().size());
				    } else {
					x.add(0.0);
					x.add(0.0);
				    }

				    x.add(1.0);
				    tmp.put("" + examples.size(), x);
				}
			    }
			}
			indicators.put(examples.size(), true);
			String[] choice = UtilTools.results
				.get(UtilTools.index).split("\n");
			for (int n = 0; n < choice.length; n++) {
			    if (choice[n].compareTo(entries.get(n)[1]) != 0) {
				wexam = entries.get(n);
				break;
			    }
			}
			if (wexam != null) {
			    // if(examples.size()<=3)
			    // {
			    examples.add(wexam);
			    // }
			}
			names.add(f.getName());
			exampleCnt.add(examples.size());
			timeleng.add(timespan);
			cRules.add(cx);
			ranks.add(rank);
			consisRules.add(consRule);
		    }
		}
	    } catch (Exception ex) {
		System.out.println("" + ex.toString());
	    }
	}
	new Random();
	try {
	    BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
		    "/Users/bowu/mysoft/xx/logx.txt")));
	    for (int x = 0; x < names.size(); x++) {
		bw.write(names.get(x) + ":" + exampleCnt.get(x) + ","
			+ timeleng.get(x));
		bw.write("\n");
		System.out.println(names.get(x) + ":" + exampleCnt.get(x) + ","
			+ timeleng.get(x));
		bw.write("\n");
		// System.out.println(consisRules.get(x));
	    }
	    bw.flush();
	} catch (Exception ex) {
	    System.out.println("" + ex.toString());
	}

    }

    public HashMap<String, HashMap<String, Vector<Double>>> recdic = new HashMap<String, HashMap<String, Vector<Double>>>();

    public void write2CSV() {
	try {
	    CSVWriter cw = new CSVWriter(
		    new FileWriter(new File("./exper.csv")));
	    Set<String> sy = recdic.keySet();
	    Iterator<String> iter = sy.iterator();
	    while (iter.hasNext()) {
		String key = iter.next();
		System.out.println("" + key);
		HashMap<String, Vector<Double>> values = recdic.get(key);
		Set<String> ks = values.keySet();
		Iterator<String> iter1 = ks.iterator();
		while (iter1.hasNext()) {
		    String[] row = new String[8];
		    String expcnt = iter1.next();
		    Vector<Double> vs = values.get(expcnt);
		    for (int j = 0; j < vs.size(); j++) {
			row[j + 2] = vs.get(j) + "";
		    }
		    row[0] = key;
		    row[1] = expcnt;
		    System.out.println("" + row);
		    cw.writeNext(row);
		}
	    }
	    cw.flush();
	    cw.close();
	} catch (Exception e) {
	    System.out.println("" + e.toString());
	}
    }

    public static void main(String[] args) {
	Main m = new Main();
	Vector<Double> xy = new Vector<Double>();
	for (int x = 0; x < 1; x++) {
	    double st = System.currentTimeMillis();
	    m.exper_2("/Users/bowu/Research/testdata/TestSingleFile");
	    double ed = System.currentTimeMillis();
	    xy.add((ed - st) * 1.0 / 60000);

	}
	m.write2CSV();
	for (int i = 0; i < xy.size(); i++) {
	    System.out.println("" + xy.get(i));
	}
	System.out.println("hello");
    }
}
