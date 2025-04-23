package ssp_01_solution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class EmpUtil{
	public static HashMap<String, Dept> readDeptFile(String filename){
		HashMap<String, Dept> dept = new HashMap<>();
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			String line;
			while((line = br.readLine()) != null){
				String[] d = line.split("#");
				dept.put(d[0], new Dept(d[0], d[1], d[2]));
			}
		} catch (Exception e) {}
		return dept;
	}
	
	public static HashMap<String, Emp> readEmpFile(String filename){
		HashMap<String, Emp> emp = new HashMap<>();
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			String line;
			while((line = br.readLine()) != null){
				String[] e = line.split("#");
				emp.put(e[0], new Emp(e[0], e[1], e[2], e[3]));
			}
		} catch (Exception e) {}
		return emp;
	}
	
	public static String getEntireDept(HashMap<String, Dept> dept, String deptNo) {
		Dept d = dept.get(deptNo);
		if("0".equals(d.upperNo)) return d.name;
		else return getEntireDept(dept, d.upperNo) + " " + d.name;
	}
}