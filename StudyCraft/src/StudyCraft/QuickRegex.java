package StudyCraft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuickRegex {
	public static void main(String[] args){
		System.out.println(expressionPresent("xyz.png", ".png"));		
	}
	
	public static int expressionPresent(String source, String expression){
		Pattern pattern = Pattern.compile(expression);
		Matcher match = pattern.matcher(source);
		if(match.find()){
			return match.start();
		}
		return -1;
		//return match.find();
	}
	
	public static int expressionPresentEnd(String source, String expression){
		Pattern pattern = Pattern.compile(expression);
		Matcher match = pattern.matcher(source);
		if(match.find()){
			return match.end();
		}
		return -1;
		//return match.find();
	}
	
	public static int[] expressionPresentStartEnd(String source, String expression){
		int[] result = {-1, -1};
		
		Pattern pattern = Pattern.compile(expression);
		Matcher match = pattern.matcher(source);
		if(match.find()){
			result[0] = match.start();
			result[1] = match.end();
			return result;
		}
		return result;
	}
}
