package pkg;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class C {

	public static void main(String[] args) throws Exception {
		
		CtClass<?> l =
			Launcher.parseClass(
				"class A { void m() { System.out.println(\"yeah\");} }");
		
		System.out.println(l);
	}
}
