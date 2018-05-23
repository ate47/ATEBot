package fr.atesab.bot.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Evaluator {
	public static final String VARIABLE_CHAR = "a-zA-Z0-9";
	public static final Pattern VARIABLE_PATTERN = Pattern.compile("["+VARIABLE_CHAR+"]+");
	public static String getLastBlock(String expression) {
		int p = 0;
		String currentExpression = "";
		for (int i = expression.length()-1; 0 <= i; i--) {
			char c = expression.charAt(i);
			switch (c) {
			case '-':
				if(p==0) {
					return "-"+currentExpression;
				}
			case '+':
			case '*':
			case '%':
			case '/':
			case '^':
				if(p==0) {
					return currentExpression;
				}
			case '(':
				if(p<0)p++;
				else return currentExpression;
				currentExpression=c+currentExpression;
				if(p==0) {
					return currentExpression;
				}
				break;
			case ')':
				p--;
			default:
				currentExpression=c+currentExpression;
				break;
			}
		}
		return currentExpression;
	}
	public static String getNextBlock(String expression) {
		int p = 0;
		String currentExpression = "";
		int n = expression.length();
		boolean sexp = false;
		for (int i = 0; i < n; i++) {
			char c = expression.charAt(i);
			switch (c) {
			case ')':
				if(p>0) p--;
				else throw new IllegalArgumentException("Syntax error: unexpected ')'");
				currentExpression+=c;
				if(p==0) {
					return currentExpression;
				}
				break;
			case '(':
				p++;
				currentExpression+=c;
				break;
			case '-':
				if(sexp) {
					currentExpression+=c;
					break;
				}
			case '+':
			case '*':
			case '%':
			case '/':
			case '^':
				if(p==0) {
					return currentExpression;
				}
				currentExpression+=c;
			case ' ':
				break;
			default:
				sexp = true;
				currentExpression+=c;
				break;
			}
		}
		return currentExpression;
		
	}
	private Map<String, Double> variables;
	private List<String> operations;
	private Map<String, FunctionEvaluator> functions;
	private int deep = 0;
	public Evaluator() {
		this(new HashMap<String, Double>());
	}
	public Evaluator(Map<String, Double> variables) {
		this(variables, new HashMap<String, FunctionEvaluator>());
	}
	public Evaluator(Map<String, Double> variables, Map<String, FunctionEvaluator> functions) {
		this(variables, functions, new ArrayList<>());
	}
	public Evaluator(Map<String, Double> variables, Map<String, FunctionEvaluator> functions, List<String> operations) {
		this.variables = variables;
		this.operations = operations;
		this.functions = functions;
		this.variables.put("pi", Math.PI);
		this.variables.put("e", Math.E);
	}
	public Evaluator defineVariable(String name, double value) {
		return defineVariable(new String[] {name}, new double[] {value});
	}
	public Evaluator defineVariable(String[] names, double[] values) {
		if(names.length != values.length) throw new IllegalArgumentException("Different array size");
		for (int i = 0; i < values.length; i++)
			if(VARIABLE_PATTERN.matcher(names[i]).matches())
				variables.put(names[i], values[i]);
			else throw new IllegalArgumentException("Not valid variable name \""+names[i]+"\".");
		return this;
	}
	public double evaluate(String expression) {
		expression = " "+expression+" ";
		this.operations.clear();
		this.deep = 0;
		for (String var: variables.keySet())
			expression=expression.replaceAll("([^"+VARIABLE_CHAR+"]){1}"+var+"([^"+VARIABLE_CHAR+"]){2}", "$1"+String.valueOf(variables.get(var))+"$2");
		return evaluate(expression.substring(1, expression.length()-1), 0);
	}
	public double evaluate(String expression, int deep) {
		if(deep>this.deep)
			this.deep = deep;
		operations.add(getDeepLine(deep)+" "+expression);
		String currentExpression = "";
		int n = expression.length();
		for (int i = 0; i < n; i++) {
			char c = expression.charAt(i);
			switch (c) {
			case '(':
				String nb = getNextBlock(expression.substring(i));
				int n2 = nb.length()+2+i;
				return evaluate(currentExpression+
						String.valueOf(evaluate(nb.substring(1, nb.length()-1), deep + 1))+(n2<n?expression.substring(n2):""), deep + 1);
			case ')':
				throw new IllegalArgumentException("Syntax error: unexpected ')'");
			default:
				currentExpression+=c;
				break;
			}
		}
		currentExpression = "";
		for (int i = 0; i < n; i++) {
			char c = expression.charAt(i);
			if(c == '^') {
				if(i>n) throw new IllegalArgumentException("Expression expected after the exponent");
				String nextG = expression.substring(i+1);
				String last = getLastBlock(currentExpression);
				String next = getNextBlock(nextG);
				return evaluate(currentExpression.substring(0, i-last.length())+
						String.valueOf(Math.pow(evaluate(last, deep+1), evaluate(next, deep+1)))
						+nextG.substring(next.length()), deep+1);
			} else currentExpression+=c;
		}
		currentExpression = "";
		for (int i = 0; i < n; i++) {
			char c = expression.charAt(i);
			if(c == '*') {
				if(i>n) throw new IllegalArgumentException("Expression expected after the multiplication");
				String nextG = expression.substring(i+1);
				String last = getLastBlock(currentExpression);
				String next = getNextBlock(nextG);
				return evaluate(currentExpression.substring(0, i-last.length())+
						String.valueOf(evaluate(last, deep+1) * evaluate(next, deep+1))
						+nextG.substring(next.length()), deep+1);
			} else if(c == '%') {
				if(i>n) throw new IllegalArgumentException("Expression expected after the modulus");
				String nextG = expression.substring(i+1);
				String last = getLastBlock(currentExpression);
				String next = getNextBlock(nextG);
				return evaluate(currentExpression.substring(0, i-last.length())+
						String.valueOf(evaluate(last, deep+1) % evaluate(next, deep+1))
						+nextG.substring(next.length()), deep+1);
			} else if(c == '/') {
				if(i>n) throw new IllegalArgumentException("Expression expected after the division");
				String nextG = expression.substring(i+1);
				String last = getLastBlock(currentExpression);
				String next = getNextBlock(nextG);
				return evaluate(currentExpression.substring(0, i-last.length())+
						String.valueOf(evaluate(last, deep+1) / evaluate(next, deep+1))
						+nextG.substring(next.length()), deep+1);
			} else currentExpression+=c;
		}
		currentExpression = "";
		expression = expression.replaceAll("[+]?( )*-( )*", "-");
		n = expression.length();
		boolean sexp = false;
		for (int i = 0; i < n; i++) {
			char c = expression.charAt(i);
			if(c == '-' && sexp) {
				if(i>n) throw new IllegalArgumentException("Expression expected after the substraction");
				String nextG = expression.substring(i+1);
				String last = getLastBlock(currentExpression);
				String next = getNextBlock(nextG);
				return evaluate(currentExpression.substring(0, i-last.length())+
						String.valueOf(evaluate(last, deep+1) - evaluate(next, deep+1))
						+nextG.substring(next.length()), deep+1);
			} else if (c!=' ') sexp = true;
			currentExpression+=c;
		}
		currentExpression = "";
		for (int i = 0; i < n; i++) {
			char c = expression.charAt(i);
			if(c == '+') {
				if(i>n) throw new IllegalArgumentException("Expression expected after the addition");
				String nextG = expression.substring(i+1);
				String last = getLastBlock(currentExpression);
				String next = getNextBlock(nextG);
				return evaluate(currentExpression.substring(0, i-last.length())+
						String.valueOf(evaluate(last, deep+1) + evaluate(next, deep+1))
						+nextG.substring(next.length()), deep+1);
			} else currentExpression+=c;
		}
		return expression.isEmpty() ? 0 : Double.valueOf(expression);
	}
	public int getDeep() {
		return deep;
	}
	private String getDeepLine(int deep) {
		String s = "";
		for (int i = 0; i < deep; i++) s+="-";
		return s;
	}
	public List<String> getOperations() {
		return operations;
	}
	public double getVariable(String name) {
		return variables.getOrDefault(name, null);
	}
	public double runFunction(String name, String args, int deep) {
		if(functions.containsKey(name)) {
			if(args.startsWith("(") && args.endsWith(")"))args = args.substring(1, args.length()-1);
			List<Double> arguments = new ArrayList<Double>();
			int n = args.length();
			int p = 0;
			String currentArg = "";
			for (int i = 0; i < n; i++) {
				char c = args.charAt(i);
				switch (c) {
				case '(':
					p++;
					currentArg+=c;
					break;
				case ')':
					if(p<=0) throw new IllegalArgumentException("Syntax error: unexpected ')'");
					p--;
					currentArg+=c;
					break;
				case ',':
					if(p==0) {
						arguments.add(evaluate(currentArg, deep + 1));
						currentArg = "";
						break;
					}
				default:
					currentArg+=c;
				}
			}
			arguments.add(evaluate(currentArg, deep + 1));
			return functions.get(name).evaluate(this, arguments.toArray(new Double[arguments.size()]));
		} else throw new IllegalArgumentException("Unbound value \""+name+"\"");
	}
}
