package ssp_01_solution;

import java.util.HashMap;
import java.util.Scanner;

public class P4SortAggregation2 {

	public static void main(String[] args) {
		
		String deptFile = "data/ssp_01/dept.txt";
		String empFile = "data/ssp_01/emp.txt";
		
		HashMap<String, Dept> dept = EmpUtil.readDeptFile(deptFile);
		HashMap<String, Emp> emp = EmpUtil.readEmpFile(empFile);
		
		Scanner sc = new Scanner(System.in);
		String line;
		while((line=sc.nextLine())!= null) {
			if("Q".equals(line)) {
				break;
			} else {
				Emp e = emp.get(line);
				if(e == null) continue;
				
				String deptNm = EmpUtil.getEntireDept(dept, e.deptNo);
				
				System.out.println(deptNm + " " + e.name + " " + e.position);
			}
		}
	}
}
