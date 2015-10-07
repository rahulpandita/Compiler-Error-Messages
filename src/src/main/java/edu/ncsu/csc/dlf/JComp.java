package edu.ncsu.csc.dlf;


import java.net.URI;
import java.util.Arrays;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

@SuppressWarnings("restriction")
public class JComp 
{
	public static void main(String[] args) {
		JComp jc = new JComp();
		String src= " abstract class Base { \n abstract void m(); \n}\n\n\nclass AbstractCantBeAccessed extends Base {\n\n void m() {\n\n super.m(); \n } \n}";
		jc.compile("AbstractCantBeAccessed", src);
		
	}
	
	public String compile(String fileName, String src)
	{
		StringBuffer errStr = new StringBuffer();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	    JavaFileObject file = new JavaSourceFromString(fileName, src);
	    
	    Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
	    CompilationTask  task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

	    task.call();
	    for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
	      /*System.out.println(diagnostic.getCode());
	      System.out.println(diagnostic.getKind());
	      System.out.println(diagnostic.getPosition());
	      System.out.println(diagnostic.getStartPosition());
	      System.out.println(diagnostic.getEndPosition());
	      System.out.println(diagnostic.getSource());
	      System.out.println(diagnostic.getMessage(null));*/
	      errStr.append(diagnostic.getMessage(null));

	    }
	    return errStr.toString();
	}
	
	class JavaSourceFromString extends SimpleJavaFileObject {
		  final String code;

		  JavaSourceFromString(String name, String code) {
		    super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
		    this.code = code;
		  }

		  @Override
		  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		    return code;
		  }
	}
}
